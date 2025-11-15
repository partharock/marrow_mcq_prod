package com.example.mcqquiz.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcqquiz.UiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuizScreen(
    state: UiState,
    onSelect: (Int) -> Unit,
    onSkip: () -> Unit,
    onToggleSound: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onEndTest: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { state.questions.size })

    LaunchedEffect(state.currentIndex) {
        if (pagerState.currentPage != state.currentIndex) {
            pagerState.animateScrollToPage(state.currentIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (state.currentIndex != pagerState.currentPage) {
            onPageChanged(pagerState.currentPage)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "fire-animation")
    val fireAnimationScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f, animationSpec = infiniteRepeatable(
            animation = tween(700), repeatMode = RepeatMode.Reverse
        ), label = "fire-scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onEndTest,
                modifier = Modifier.align(Alignment.CenterStart),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("End Test")
            }
            Text(
                text = "Quiz",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(onClick = onToggleSound, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(
                    imageVector = if (state.isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = "Toggle Sound"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .clearAndSetSemantics { },
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val fireEmojis = listOf(3, 4, 5, 6)
            fireEmojis.forEach { streakLevel ->
                val isActive = state.streak >= streakLevel
                Text(
                    text = "🔥",
                    modifier = Modifier
                        .alpha(if (isActive) 1f else 0.2f)
                        .scale(if (isActive) fireAnimationScale else 1f),
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${state.streak} questions streak achieved !!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .alpha(if (state.streak >= 3) 1f else 0f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Next in ${state.remainingTime}s",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .alpha(if (state.showAdvanceTimer) 1f else 0f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Question ${state.currentIndex + 1} of ${state.totalQuestions}",
            style = MaterialTheme.typography.bodyLarge
        )
        LinearProgressIndicator(
            progress = { (state.currentIndex + 1) / state.totalQuestions.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
            trackColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPager(
            state = pagerState, modifier = Modifier.weight(1f)
        ) { page ->
            val quizQuestion = state.questions.getOrNull(page)
            if (quizQuestion != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        quizQuestion.question.question,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    quizQuestion.shuffledOptions.forEachIndexed { idx, opt ->
                        val isSelected = quizQuestion.selectedIndex == idx
                        val isCorrect = if (quizQuestion.revealed) idx == quizQuestion.shuffledAnswerIndex else null
                        OptionRow(text = opt, isSelected = isSelected, isCorrect = isCorrect) {
                            if (!quizQuestion.revealed) {
                                onSelect(idx)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth()
        ) {
            val buttonText = if (state.currentIndex == state.questions.size - 1) "End Test" else "Skip"
            Icon(Icons.Default.SkipNext, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(buttonText)
        }
    }
}
