package com.example.mcqquiz

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mcqquiz.database.Converters

@Entity(tableName = "questions")
@TypeConverters(Converters::class)
data class Question(
    @PrimaryKey
    val id: Int,
    val question: String,
    val options: List<String>,
    val answerIndex: Int
) {
    @Ignore
    var shuffledOptions: List<String> = emptyList()
    @Ignore
    var shuffledAnswerIndex: Int = -1

    fun shuffleOptions() {
        if (shuffledOptions.isEmpty()) { // Shuffle only once
            val originalAnswer = options[answerIndex]
            val shuffled = options.shuffled()
            shuffledOptions = shuffled
            shuffledAnswerIndex = shuffled.indexOf(originalAnswer)
        }
    }
}
