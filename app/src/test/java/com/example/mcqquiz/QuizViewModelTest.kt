package com.example.mcqquiz

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mcqquiz.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class QuizViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: QuizViewModel
    private val mockRepository: QuizRepository = mock()
    private val mockApplication: Application = mock()

    private fun createTestQuestions(): List<Question> {
        return listOf(
            Question(1, "Question 1", listOf("A", "B", "C"), 0),
            Question(2, "Question 2", listOf("D", "E", "F"), 1),
            Question(3, "Question 3", listOf("G", "H", "I"), 2)
        )
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // We mock the SoundManager constructor call inside the ViewModel's init block
        whenever(mockApplication.applicationContext).thenReturn(mockApplication)
        viewModel = QuizViewModel(mockApplication, mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startQuiz loads and shuffles questions`() = runTest {
        val questions = createTestQuestions()
        whenever(mockRepository.getQuestions()).thenReturn(questions)

        viewModel.startQuiz()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse("Loading should be false", state.loading)
        assertEquals("Should have loaded all questions", questions.size, state.questions.size)
        assertTrue("Visited pages should include the first page", state.visitedPages.contains(0))
    }

    @Test
    fun `selectAnswer with correct answer updates streak`() = runTest {
        whenever(mockRepository.getQuestions()).thenReturn(createTestQuestions())
        viewModel.startQuiz()
        advanceUntilIdle()

        val firstQuestion = viewModel.uiState.value.questions[0]
        viewModel.selectAnswer(firstQuestion.shuffledAnswerIndex)

        val state = viewModel.uiState.value
        assertEquals("Streak should be 1", 1, state.streak)
        assertEquals("Longest streak should be 1", 1, state.longestStreak)
        assertTrue("Question should be revealed", state.questions[0].revealed)
    }

    @Test
    fun `selectAnswer with incorrect answer resets streak`() = runTest {
        whenever(mockRepository.getQuestions()).thenReturn(createTestQuestions())
        viewModel.startQuiz()
        advanceUntilIdle()

        // Answer correctly first
        val firstQuestion = viewModel.uiState.value.questions[0]
        viewModel.selectAnswer(firstQuestion.shuffledAnswerIndex)

        // Move to next question
        viewModel.onPageChanged(1)

        // Answer incorrectly
        val secondQuestion = viewModel.uiState.value.questions[1]
        val incorrectIndex = (secondQuestion.shuffledAnswerIndex + 1) % secondQuestion.shuffledOptions.size
        viewModel.selectAnswer(incorrectIndex)

        val state = viewModel.uiState.value
        assertEquals("Streak should be reset to 0", 0, state.streak)
        assertEquals("Longest streak should remain 1", 1, state.longestStreak)
    }

    @Test
    fun `endTest calculates and shows correct results`() = runTest {
        whenever(mockRepository.getQuestions()).thenReturn(createTestQuestions())
        viewModel.startQuiz()
        advanceUntilIdle()

        // Correctly answer the first question
        val q1 = viewModel.uiState.value.questions[0]
        viewModel.selectAnswer(q1.shuffledAnswerIndex)
        advanceUntilIdle()

        // Skip the second question (advances to it automatically)
        advanceUntilIdle() // Let the auto-advance finish
        viewModel.skip() // Now on Q3

        // When endTest is called
        viewModel.endTest()

        // Then results should be calculated
        val state = viewModel.uiState.value
        assertTrue("showResults should be true", state.showResults)
        assertEquals("There should be 1 correct answer", 1, state.correctAnswers)
        assertEquals("There should be 1 skipped answer", 1, state.skippedAnswers)
    }

    @Test
    fun `restart resets quiz to initial state`() = runTest {
        whenever(mockRepository.getQuestions()).thenReturn(createTestQuestions())
        viewModel.startQuiz()
        advanceUntilIdle()
        viewModel.selectAnswer(0) // Answer a question

        viewModel.restart()

        val state = viewModel.uiState.value
        assertFalse("quizStarted should be false", state.quizStarted)
        assertEquals("Current index should be 0", 0, state.currentIndex)
        assertEquals("Questions should be empty", 0, state.questions.size)
    }

    @Test
    fun `quit dialog state is managed correctly`() = runTest {
        assertFalse("Quit dialog should be hidden initially", viewModel.uiState.value.showQuitDialog)

        // When an attempt to quit is made
        viewModel.onQuitAttempt()
        assertTrue("Quit dialog should now be visible", viewModel.uiState.value.showQuitDialog)

        // When the dialog is dismissed
        viewModel.dismissQuitDialog()
        assertFalse("Quit dialog should be hidden again", viewModel.uiState.value.showQuitDialog)
    }
}
