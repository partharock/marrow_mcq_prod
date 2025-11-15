package com.example.mcqquiz.di

import android.content.Context
import com.example.mcqquiz.database.QuizDatabase
import com.example.mcqquiz.network.QuizApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(private val context: Context) {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/") // Base URL for gists
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val quizApiService: QuizApiService by lazy {
        retrofit.create(QuizApiService::class.java)
    }

    val quizDatabase: QuizDatabase by lazy {
        QuizDatabase.getDatabase(context)
    }
}
