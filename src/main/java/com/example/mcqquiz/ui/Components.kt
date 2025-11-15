package com.example.mcqquiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mcqquiz.ui.theme.CorrectGreen
import com.example.mcqquiz.ui.theme.IncorrectRed
import com.example.mcqquiz.ui.theme.SelectedGray

@Composable
fun OptionRow(text: String, isSelected: Boolean, isCorrect: Boolean?, onClick: () -> Unit) {
    // if isCorrect is null => not revealed yet
    val bg = when {
        isCorrect == true -> CorrectGreen // Correct answer is always green after reveal
        isSelected && isCorrect == false -> IncorrectRed // Selected incorrect answer is red
        isSelected -> SelectedGray // Selected answer before reveal
        else -> MaterialTheme.colorScheme.primary
    }
    val accessibilityDescription = when {
        isCorrect == true -> "$text, correct answer"
        isSelected && isCorrect == false -> "$text, incorrect answer"
        isSelected -> "$text, selected"
        else -> text
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Reduced vertical padding
            .background(bg, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // No ripple effect
                onClick = onClick,
                role = Role.Button
            )
            .semantics { contentDescription = accessibilityDescription }
            .padding(horizontal = 16.dp, vertical = 10.dp), // Fine-tuned padding
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text, 
            fontSize = 16.sp, 
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.clearAndSetSemantics { }
        )
    }
}
