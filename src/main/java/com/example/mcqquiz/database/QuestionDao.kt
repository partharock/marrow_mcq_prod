package com.example.mcqquiz.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mcqquiz.Question

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM questions")
    suspend fun getQuestions(): List<Question>

    @Query("DELETE FROM questions")
    suspend fun clearAll()
}
