package com.example.mcqquiz.repository

import com.example.mcqquiz.Question
import com.example.mcqquiz.QuizOption
import com.example.mcqquiz.database.QuestionDao
import com.example.mcqquiz.network.QuizApiService
import com.example.mcqquiz.ui.ModuleProgress
import com.example.mcqquiz.ui.ModuleProgressDao

class QuizRepository(
    private val quizApiService: QuizApiService,
    private val questionDao: QuestionDao,
    private val moduleProgressDao: ModuleProgressDao
) {
    suspend fun getQuizOptions(): List<QuizOption> {
        return quizApiService.getQuizOptions()
    }

    suspend fun getQuestions(url: String): List<Question> {
        // For simplicity, we are not caching questions from dynamic URLs in this example.
        // A more robust implementation might involve a more sophisticated caching strategy.
        return quizApiService.getQuestions(url)
    }

    suspend fun refreshQuestions(url: String) {
        val networkQuestions = quizApiService.getQuestions(url)
        questionDao.clearAll()
        questionDao.insertAll(networkQuestions)
    }

    suspend fun getAllProgress(): List<ModuleProgress> {
        return moduleProgressDao.getAll()
    }

    suspend fun insertProgress(moduleProgress: ModuleProgress) {
        moduleProgressDao.insert(moduleProgress)
    }
}
