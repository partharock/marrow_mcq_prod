package com.example.mcqquiz

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mcqquiz.repository.QuizRepository
import com.example.mcqquiz.ui.Module
import com.example.mcqquiz.ui.ModuleProgress
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiQuestion(
    val question: Question,
    val selectedIndex: Int? = null,
    val revealed: Boolean = false,
    val shuffledOptions: List<String>,
    val shuffledAnswerIndex: Int
)

data class UiState(
    val loading: Boolean = false,
    val questions: List<QuizUiQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val currentModuleId: String = "",
    val correctAnswers: Int = 0,
    val skippedAnswers: Int = 0,
    val streak: Int = 0,
    val longestStreak: Int = 0,
    val showResults: Boolean = false,
    val totalQuestions: Int = 0,
    val remainingTime: Int = 2,
    val isClosing: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val quizStarted: Boolean = false,
    val showOptions: Boolean = false,
    val showAdvanceTimer: Boolean = false,
    val showQuitDialog: Boolean = false,
    val visitedPages: Set<Int> = emptySet()
)

class QuizViewModel(application: Application, private val repository: QuizRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules = _modules.asStateFlow()

    private var advanceJob: Job? = null
    private val soundManager: SoundManager

    companion object {
        private const val ADVANCE_DELAY_SECONDS = 2
    }

    init {
        soundManager = SoundManager(application)
        viewModelScope.launch {
            soundManager.loadSounds()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }

    fun onQuitAttempt() {
        _uiState.value = _uiState.value.copy(showQuitDialog = true)
    }

    fun dismissQuitDialog() {
        _uiState.value = _uiState.value.copy(showQuitDialog = false)
    }

    fun startQuiz() {
        _uiState.value = _uiState.value.copy(quizStarted = true, showOptions = true, loading = true)
        viewModelScope.launch {
            fetchModules()
        }
    }

    fun onOptionSelected(moduleId: String, questionsUrl: String) {
        _uiState.value = _uiState.value.copy(
            showOptions = false,
            loading = true,
            currentModuleId = moduleId
        )
        viewModelScope.launch {
            loadQuestions(questionsUrl)
        }
    }

    fun onPageChanged(page: Int) {
        advanceJob?.cancel()
        _uiState.value = _uiState.value.copy(
            currentIndex = page,
            showAdvanceTimer = false,
            visitedPages = _uiState.value.visitedPages + page
        )
    }

    fun endTest() {
        viewModelScope.launch {
            calculateAndSaveResults(completed = true)
        }

    }

    fun saveProgressAndReturn() {
        viewModelScope.launch {
            calculateAndSaveResults(completed = false)
            fetchModules()
            _uiState.value = _uiState.value.copy(showOptions = true, loading = false)
        }
    }

    fun toggleSound() {
        soundManager.toggleSound()
        _uiState.value = _uiState.value.copy(isSoundEnabled = soundManager.isSoundEnabled())
    }

    fun initiateAppClose() {
        _uiState.value = _uiState.value.copy(isClosing = true)
    }

    private suspend fun fetchModules() {
        val quizOptions = repository.getQuizOptions()
        val progress = repository.getAllProgress().associateBy { it.moduleId }
        _modules.value = quizOptions.map { Module(it, progress[it.id]) }
        _uiState.value = _uiState.value.copy(loading = false)
    }

    private fun getAdvanceJob(): Job {
        return viewModelScope.launch {
            _uiState.value = _uiState.value.copy(showAdvanceTimer = true)
            for (i in ADVANCE_DELAY_SECONDS downTo 1) {
                _uiState.value = _uiState.value.copy(remainingTime = i)
                delay(1000)
            }
            advance()
        }
    }

    private suspend fun loadQuestions(questionsUrl: String) {
        val questions = repository.getQuestions(questionsUrl).map { question ->
            val originalAnswer = question.options[question.answerIndex]
            val shuffled = question.options.shuffled()
            val newAnswerIndex = shuffled.indexOf(originalAnswer)
            QuizUiQuestion(
                question = question,
                shuffledOptions = shuffled,
                shuffledAnswerIndex = newAnswerIndex
            )
        }
        _uiState.value = _uiState.value.copy(
            loading = false,
            questions = questions,
            totalQuestions = questions.size,
            visitedPages = setOf(0)
        )
    }

    fun selectAnswer(idx: Int) {
        val s = _uiState.value
        val quizQuestion = s.questions.getOrNull(s.currentIndex) ?: return
        if (quizQuestion.revealed) return

        val correct = (idx == quizQuestion.shuffledAnswerIndex)
        var newStreak = s.streak
        var longest = s.longestStreak

        if (correct) {
            newStreak++
            if (newStreak > longest) {
                longest = newStreak
            }
            soundManager.playCorrectSound()
        } else {
            newStreak = 0
            soundManager.playIncorrectSound()
        }

        val updatedQuestions = s.questions.toMutableList()
        updatedQuestions[s.currentIndex] = quizQuestion.copy(selectedIndex = idx, revealed = true)

        _uiState.value =
            s.copy(questions = updatedQuestions, streak = newStreak, longestStreak = longest)

        val allAnswered = updatedQuestions.all { it.revealed }
        if (allAnswered) {
            advanceJob?.cancel()
            advanceJob = getAdvanceJob()
        } else {
            val isLastQuestion = s.currentIndex == s.questions.size - 1
            if (!isLastQuestion) {
                advanceJob?.cancel()
                advanceJob = getAdvanceJob()
            }
        }
    }

    fun skip() {
        advanceJob?.cancel()
        advance()
    }

    private fun advance() {
        val s = _uiState.value
        val next = s.currentIndex + 1
        if (next >= s.questions.size) {
            viewModelScope.launch {
                calculateAndSaveResults(completed = true)
            }
        } else {
            _uiState.value = s.copy(
                currentIndex = next,
                remainingTime = ADVANCE_DELAY_SECONDS,
                showAdvanceTimer = false,
                visitedPages = s.visitedPages + next
            )
        }
    }

    private suspend fun calculateAndSaveResults(completed: Boolean) {
        val s = _uiState.value

        var correctCount = 0
        s.questions.forEach { q ->
            if (q.revealed && q.selectedIndex == q.shuffledAnswerIndex) {
                correctCount++
            }
        }

        val skippedCount =
            s.visitedPages.count { s.questions.getOrNull(it)?.revealed?.not() ?: false }

        val moduleId = s.currentModuleId
        if (moduleId.isNotEmpty()) {
            repository.insertProgress(
                ModuleProgress(
                    moduleId,
                    correctCount,
                    s.totalQuestions,
                    completed,
                    s.visitedPages.size
                )
            )
        }

        if (completed) {
            _uiState.value = s.copy(
                showResults = true,
                correctAnswers = correctCount,
                skippedAnswers = skippedCount
            )
        }
    }

    fun restart() {
        _uiState.value = UiState(isSoundEnabled = _uiState.value.isSoundEnabled)
        startQuiz()
    }
}

class QuizViewModelFactory(
    private val application: Application,
    private val repository: QuizRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
