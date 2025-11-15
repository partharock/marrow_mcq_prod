package com.example.mcqquiz

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.mcqquiz.repository.QuizRepository
import com.example.mcqquiz.ui.ClosingOverlay
import com.example.mcqquiz.ui.LoadingPopup
import com.example.mcqquiz.ui.QuizScreen
import com.example.mcqquiz.ui.ResultsScreen
import com.example.mcqquiz.ui.StartScreen
import com.example.mcqquiz.ui.theme.McqQuizTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as QuizApplication).appContainer
        val repository =
            QuizRepository(appContainer.quizApiService, appContainer.quizDatabase.questionDao())
        val factory = QuizViewModelFactory(application, repository)

        val vm: QuizViewModel by viewModels { factory }

        setContent {
            McqQuizTheme {
                val state by vm.uiState.collectAsState()
                val context = LocalContext.current
                val activity = (context as? Activity)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when {
                            !state.quizStarted -> {
                                BackHandler { activity?.finish() }
                                StartScreen(onPlayQuiz = { vm.startQuiz() })
                            }

                            state.loading -> LoadingPopup()
                            state.showResults -> ResultsScreen(
                                total = state.totalQuestions,
                                correct = state.correctAnswers,
                                skipped = state.skippedAnswers,
                                longestStreak = state.longestStreak,
                                onRestart = { vm.restart() },
                                onClose = { vm.initiateAppClose() }
                            )

                            else -> QuizScreen(
                                state = state,
                                onSelect = { idx -> vm.selectAnswer(idx) },
                                onSkip = { vm.skip() },
                                onToggleSound = { vm.toggleSound() },
                                onPageChanged = { page -> vm.onPageChanged(page) },
                                onEndTest = { vm.endTest() }
                            )
                        }

                        if (state.isClosing) {
                            var progress by remember { mutableStateOf(0f) }
                            val animatedProgress by animateFloatAsState(
                                targetValue = progress,
                                animationSpec = tween(durationMillis = 2500)
                            )

                            ClosingOverlay(progress = { animatedProgress })

                            LaunchedEffect(Unit) {
                                progress = 1f
                                delay(2500)
                                activity?.finish()
                            }
                        }
                    }
                }
            }
        }
    }
}
