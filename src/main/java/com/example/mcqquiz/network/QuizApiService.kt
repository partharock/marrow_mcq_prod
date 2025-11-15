package com.example.mcqquiz.network

import com.example.mcqquiz.Question
import retrofit2.http.GET

interface QuizApiService {
    @GET("https://gist.githubusercontent.com/dr-samrat/53846277a8fcb034e482906ccc0d12b2/raw")
    suspend fun getQuestions(): List<Question>
}
