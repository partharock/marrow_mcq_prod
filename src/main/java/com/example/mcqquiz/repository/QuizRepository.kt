package com.example.mcqquiz.repository

import com.example.mcqquiz.Question
import com.example.mcqquiz.database.QuestionDao
import com.example.mcqquiz.network.QuizApiService

class QuizRepository(
    private val quizApiService: QuizApiService,
    private val questionDao: QuestionDao
) {

    suspend fun getQuestions(): List<Question> {
        val cachedQuestions = questionDao.getQuestions()
//        return if (cachedQuestions.isNotEmpty()) {
//            cachedQuestions
//        } else {
            val networkQuestions = quizApiService.getQuestions()
            questionDao.insertAll(networkQuestions)
            return networkQuestions
//        }
    }

    suspend fun refreshQuestions() {
        val networkQuestions = quizApiService.getQuestions()
        questionDao.clearAll()
        questionDao.insertAll(networkQuestions)
    }
}
