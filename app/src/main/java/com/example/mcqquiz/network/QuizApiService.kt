package com.example.mcqquiz.network

import com.example.mcqquiz.Question
import com.example.mcqquiz.QuizOption
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * A Retrofit service interface for network operations related to the quiz.
 * This interface defines the API endpoints that the application will use to communicate
 * with the remote server.
 */
interface QuizApiService {

    /**
     * Fetches a list of available quiz categories from the main quiz options URL.
     *
     * @return A list of [QuizOption] objects, each representing a selectable quiz topic.
     */
    @GET("https://gist.githubusercontent.com/dr-samrat/ee986f16da9d8303c1acfd364ece22c5/raw")
    suspend fun getQuizOptions(): List<QuizOption>

    /**
     * Fetches a list of quiz questions from a dynamically provided URL.
     *
     * This is a suspend function, which means it's designed to be called from a coroutine.
     * This allows the network request to be made in the background without blocking the main thread,
     * preventing the app from freezing.
     *
     * @param url The full URL from which to fetch the questions. This is provided by the selected
     *            [QuizOption].
     * @return A [List] of [Question] objects that are parsed from the JSON response body.
     */
    @GET
    suspend fun getQuestions(@Url url: String): List<Question>

}
