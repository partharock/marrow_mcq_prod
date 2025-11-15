package com.example.mcqquiz

import androidx.room.Entity
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
    val answerIndex: Int,
)
