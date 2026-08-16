package com.example.inklings

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlin.random.Random

class TypewriterSoundManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val ks1Id = soundPool.load(context, R.raw.typewriter_ks1, 1)
    private val ks2Id = soundPool.load(context, R.raw.typewriter_ks2, 1)
    private val spaceId = soundPool.load(context, R.raw.typewriter_space, 1)

    private var lastSoundId = -1
    private var consecutiveCount = 0

    /**
     * Requirement 14: Play random KS1 or KS2 with anti-repetition rule.
     */
    fun playKeySound() {
        var selectedId = if (Random.nextBoolean()) ks1Id else ks2Id

        // Anti-repetition: Do not allow the same sound more than 2 consecutive times.
        if (selectedId == lastSoundId) {
            consecutiveCount++
            if (consecutiveCount >= 2) {
                // Force the other sound
                selectedId = if (selectedId == ks1Id) ks2Id else ks1Id
                consecutiveCount = 0
            }
        } else {
            consecutiveCount = 0
        }
        
        lastSoundId = selectedId
        playSound(selectedId)
    }

    /**
     * Requirement 14: Always use typewriter_space.wav for spaces.
     */
    fun playSpaceSound() {
        playSound(spaceId)
        lastSoundId = -1 // Reset anti-repetition for normal keys
        consecutiveCount = 0
    }

    /**
     * Requirement 14: Always use typewriter_ks2.wav for backspace.
     */
    fun playBackspaceSound() {
        playSound(ks2Id)
        lastSoundId = -1 // Reset anti-repetition
        consecutiveCount = 0
    }

    private fun playSound(soundId: Int) {
        if (soundId != -1) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
