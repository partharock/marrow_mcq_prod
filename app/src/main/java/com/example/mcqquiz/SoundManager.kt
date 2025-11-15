package com.example.mcqquiz

import android.content.Context
import android.media.SoundPool

class SoundManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(2).build()
    private var isSoundEnabled = true

    private val correctSoundId: Int =
        soundPool.load(context, R.raw.feedback_correct_right_answer_sba_preview, 1)
    private val incorrectSoundId: Int =
        soundPool.load(context, R.raw.feedback_incorrect_sba_preview, 1)

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
