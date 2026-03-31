package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import dev.samandar.walletapp.R

object SoundManager {
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = -1

    fun init(context: Context) {
        if (soundPool == null) {
            soundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .build()
                )
                .build()

            // Ovozni yuklash
            clickSoundId = soundPool?.load(context, R.raw.button_sound, 1) ?: -1
        }
    }

    // Ovozni ijro etish funksiyasi
    fun playClick() {
        soundPool?.play(clickSoundId, 0.07f, 0.07f, 1, 0, 1.8f)
    }

    // Xotirani bo'shatish
    fun release() {
        soundPool?.release()
        soundPool = null
    }
}