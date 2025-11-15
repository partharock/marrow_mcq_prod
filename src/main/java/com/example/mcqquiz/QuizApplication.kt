package com.example.mcqquiz

import android.app.Application
import com.example.mcqquiz.di.AppContainer

class QuizApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }

}
