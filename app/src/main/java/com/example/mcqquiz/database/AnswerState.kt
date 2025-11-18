package com.example.mcqquiz.database

import androidx.room.Entity

/**
 * Represents the saved state of a single question within a module.
 *
 * @param moduleId The ID of the module this question belongs to.
 * @param questionId A unique identifier for the question within the module (using the question text's hashcode).
 * @param selectedIndex The index of the answer the user selected.
 * @param revealed Whether the answer has been revealed to the user.
 */
@Entity(tableName = "answer_state", primaryKeys = ["moduleId", "questionId"])
data class AnswerState(
    val moduleId: String,
    val questionId: String,
    val selectedIndex: Int,
    val revealed: Boolean
)
