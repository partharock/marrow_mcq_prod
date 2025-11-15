package com.example.mcqquiz

import android.content.Context
import android.media.SoundPool

class SoundManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(2).build()
    private var isSoundEnabled = true

    private val correctSoundId: Int =
        soundPool.load(context, R.raw.correct_sfx, 1)
    private val incorrectSoundId: Int =
        soundPool.load(context, R.raw.incorrect_sfx, 1)

    fun playCorrectSound() {
        if (isSoundEnabled) {
            soundPool.play(correctSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playIncorrectSound() {
        if (isSoundEnabled) {
            soundPool.play(incorrectSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun toggleSound() {
        isSoundEnabled = !isSoundEnabled
    }

    fun isSoundEnabled(): Boolean {
        return isSoundEnabled
    }

    fun release() {
        soundPool.release()
    }
}
