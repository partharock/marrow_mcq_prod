package com.example.mcqquiz

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mcqquiz.repository.QuizRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiQuestion(
    val question: Question,
    val selectedIndex: Int? = null,
    val revealed: Boolean = false
)

data class UiState(
    val loading: Boolean = false,
    val questions: List<QuizUiQuestion> = emptyList(),
    val currentIndex: Int = 0,
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
    val showAdvanceTimer: Boolean = false,
    val endTestButtonVisible: Boolean = false // New state for button visibility
)

class QuizViewModel(application: Application, private val repository: QuizRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private var advanceJob: Job? = null
    private val soundManager = SoundManager(application)

    companion object {
        private const val ADVANCE_DELAY_SECONDS = 2
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }

    fun startQuiz() {
        _uiState.value = _uiState.value.copy(quizStarted = true, loading = true)
        viewModelScope.launch {
            loadQuestions()
        }
    }

    fun onPageChanged(page: Int) {
        advanceJob?.cancel()
        val currentState = _uiState.value
        val shouldShow = currentState.endTestButtonVisible || (page == currentState.questions.size - 1 && currentState.questions.isNotEmpty())
        _uiState.value = currentState.copy(
            currentIndex = page,
            showAdvanceTimer = false,
            endTestButtonVisible = shouldShow
        )
    }

    fun endTest() {
        _uiState.value = _uiState.value.copy(showResults = true)
    }

    fun toggleSound() {
        soundManager.toggleSound()
        _uiState.value = _uiState.value.copy(isSoundEnabled = soundManager.isSoundEnabled())
    }

    fun initiateAppClose() {
        _uiState.value = _uiState.value.copy(isClosing = true)
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

    private suspend fun loadQuestions() {
        val questions = repository.getQuestions().map { QuizUiQuestion(it) }
        _uiState.value = _uiState.value.copy(
            loading = false,
            questions = questions,
            totalQuestions = questions.size,
        )
    }

    @VisibleForTesting
    fun setQuestions(questions: List<QuizUiQuestion>) {
        _uiState.value = _uiState.value.copy(
            questions = questions,
            totalQuestions = questions.size
        )
    }

    fun selectAnswer(idx: Int) {
        val s = _uiState.value
        val quizQuestion = s.questions.getOrNull(s.currentIndex) ?: return
        if (quizQuestion.revealed) return

        val correct = (idx == quizQuestion.question.answerIndex)
        var newStreak = s.streak
        var longest = s.longestStreak
        var correctCount = s.correctAnswers
        if (correct) {
            newStreak += 1
            correctCount += 1
            if (newStreak > longest) longest = newStreak
            soundManager.playCorrectSound()
        } else {
            newStreak = 0
            soundManager.playIncorrectSound()
        }

        val updatedQuestions = s.questions.toMutableList()
        updatedQuestions[s.currentIndex] = quizQuestion.copy(selectedIndex = idx, revealed = true)

        val allAnswered = updatedQuestions.all { it.revealed }

        _uiState.value = s.copy(
            questions = updatedQuestions,
            streak = newStreak,
            longestStreak = longest,
            correctAnswers = correctCount,
            showResults = if (allAnswered) true else s.showResults
        )

        if (!allAnswered) {
            advanceJob?.cancel()
            advanceJob = getAdvanceJob()
        }
    }

    fun skip() {
        advanceJob?.cancel()
        val s = _uiState.value
        val quizQuestion = s.questions.getOrNull(s.currentIndex) ?: return
        if (quizQuestion.revealed) {
            _uiState.value = _uiState.value.copy(showAdvanceTimer = false)
            advance()
            return
        }
        _uiState.value = s.copy(skippedAnswers = s.skippedAnswers + 1, showAdvanceTimer = false)
        advance()
    }

    private fun advance() {
        val s = _uiState.value
        val next = s.currentIndex + 1
        if (next >= s.questions.size) {
            _uiState.value = s.copy(showResults = true, showAdvanceTimer = false)
        } else {
            val shouldShow = s.endTestButtonVisible || (next == s.questions.size - 1)
            _uiState.value = s.copy(
                currentIndex = next,
                remainingTime = ADVANCE_DELAY_SECONDS,
                showAdvanceTimer = false,
                endTestButtonVisible = shouldShow
            )
        }
    }

    fun restart() {
        _uiState.value = UiState(isSoundEnabled = _uiState.value.isSoundEnabled)
    }
}

class QuizViewModelFactory(private val application: Application, private val repository: QuizRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
