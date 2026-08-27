package com.example.inklings

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class TypewriterSoundManager(private val context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(10) // Increased to support simultaneous pops if needed
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

    // Requirement 18: Load pop.mp3 from assets.
    private var popId = -1

    init {
        try {
            val assetFileDescriptor = context.assets.openFd("pop.wav")
            popId = soundPool.load(assetFileDescriptor, 1)
            android.util.Log.d("SoundManager", "pop.wav requested for loading, assigned ID: $popId")
            
            soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                android.util.Log.d("SoundManager", "Sound loaded: ID=$sampleId, Status=$status")
            }
        } catch (e: Exception) {
            android.util.Log.e("SoundManager", "Failed to load pop.wav from assets", e)
        }
    }

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

    fun playPopThreeTimes(scope: CoroutineScope) {
        android.util.Log.e("SoundManager", "playPopThreeTimes requested. popId: $popId")
        if (popId == -1) {
            android.util.Log.e("SoundManager", "popId is -1, sound might still be loading or failed to load")
            return
        }
        
        scope.launch {
            repeat(3) { i ->
                android.util.Log.e("SoundManager", "Playing pop #$i")
                playSound(popId)
                delay(1000) // Requirement 18: 1 second delay between pops
            }
        }
    }

    private fun playSound(soundId: Int) {
        if (soundId != -1) {
            android.util.Log.e("SoundManager", "Playing sound: $soundId")
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        } else {
            android.util.Log.e("SoundManager", "Attempted to play invalid sound ID (-1)")
        }
    }

    fun release() {
        soundPool.release()
    }
}
