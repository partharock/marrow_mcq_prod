package com.example.mcqquiz

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mcqquiz.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class QuizViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: QuizViewModel
    private val mockRepository: QuizRepository = mock()
    private val mockApplication: Application = mock()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = QuizViewModel(mockApplication, mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `selectAnswer with correct answer updates state correctly`() = testDispatcher.runBlockingTest {
        // Given
        val questions = listOf(
            QuizUiQuestion(Question(1, "Question 1", listOf("A", "B"), 0)),
            QuizUiQuestion(Question(2, "Question 2", listOf("C", "D"), 1))
        )
        viewModel.setQuestions(questions)

        // When
        viewModel.selectAnswer(0) // Select the correct answer for the first question

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(1, uiState.correctAnswers)
        assertEquals(1, uiState.streak)
        assertEquals(1, uiState.longestStreak)
        assertEquals(true, uiState.questions[0].revealed)
        assertEquals(0, uiState.questions[0].selectedIndex)
    }
}
