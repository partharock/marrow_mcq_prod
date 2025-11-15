package com.example.mcqquiz

import android.content.Context
import android.media.SoundPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var correctSoundId: Int = 0
    private var incorrectSoundId: Int = 0
    private var isSoundEnabled = true
    private var areSoundsLoaded = false

    suspend fun loadSounds() {
        withContext(Dispatchers.IO) {
            soundPool = SoundPool.Builder().setMaxStreams(2).build()
            correctSoundId = soundPool?.load(context, R.raw.correct_sfx, 1) ?: 0
            incorrectSoundId = soundPool?.load(context, R.raw.incorrect_sfx, 1) ?: 0
            areSoundsLoaded = true
        }
    }

    fun playCorrectSound() {
        if (isSoundEnabled && areSoundsLoaded) {
            soundPool?.play(correctSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playIncorrectSound() {
        if (isSoundEnabled && areSoundsLoaded) {
            soundPool?.play(incorrectSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun toggleSound() {
        isSoundEnabled = !isSoundEnabled
    }

    fun isSoundEnabled(): Boolean {
        return isSoundEnabled
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
