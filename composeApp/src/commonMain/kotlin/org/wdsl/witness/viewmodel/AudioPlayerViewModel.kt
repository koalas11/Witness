package org.wdsl.witness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.wdsl.witness.module.audio.AudioPlayerModule
import org.wdsl.witness.storage.room.Recording
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel for managing audio playback functionality.
 *
 * @param audioPlayer The AudioPlayerModule for handling audio playback.
 */
class AudioPlayerViewModel(
    private val audioPlayer: AudioPlayerModule,
): ViewModel() {
    private var _audioPlayerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Idle)
    val audioPlayerState = _audioPlayerState.asStateFlow()

    private var _audioCurrentPosition = MutableStateFlow(0L)
    val audioCurrentPosition = _audioCurrentPosition.asStateFlow()

    private var _audioDurationMsState = MutableStateFlow(0L)
    val audioDurationMsState = _audioDurationMsState.asStateFlow()

    private var _jobAudioCurrentPosition: Job? = null
    private var _jobAudioState: Job? = null

    fun loadRecording(recording: Recording) {
        _audioPlayerState.value = AudioPlayerState.RecordingLoading
        viewModelScope.launch {
            audioPlayer.loadAudio(recording.recordingFileName)
                .onSuccess {
                    _audioDurationMsState.value = it ?: 0L
                    _audioPlayerState.value = AudioPlayerState.RecordingReady
                    observeAudioCurrentPosition()
                }
                .onError {
                    _audioPlayerState.value = AudioPlayerState.Idle
                }
        }
    }

    fun CoroutineScope.observeAudioCurrentPosition() {
        _jobAudioCurrentPosition = launch {
            while (isActive) {
                delay(50.milliseconds)
                _audioCurrentPosition.value = audioPlayer.getCurrentPosition()
            }
        }
        _jobAudioState = launch {
            while (isActive) {
                delay(100.milliseconds)
                val pos = audioPlayer.getCurrentPosition()
                val isPlaying = audioPlayer.isPlaying()

                if (_audioDurationMsState.value in 1..pos) {
                        audioPlayer.pauseAudio()
                        audioPlayer.seekTo(0)
                    _audioPlayerState.value = AudioPlayerState.Paused
                    continue
                }

                when {
                    isPlaying && _audioPlayerState.value != AudioPlayerState.Playing -> {
                        _audioPlayerState.value = AudioPlayerState.Playing
                    }
                    !isPlaying && _audioPlayerState.value == AudioPlayerState.Playing -> {
                        _audioPlayerState.value = AudioPlayerState.Paused
                    }
                }
            }
        }
    }

    fun playRecording() {
        viewModelScope.launch {
            _audioPlayerState.value = AudioPlayerState.Playing
            audioPlayer.resumeAudio()
        }
    }

    fun pauseRecording() {
        viewModelScope.launch {
            _audioPlayerState.value = AudioPlayerState.Paused
            audioPlayer.pauseAudio()
        }
    }

    fun seekTo(milliseconds: Long) {
        viewModelScope.launch {
            audioPlayer.seekTo(milliseconds)
            _audioCurrentPosition.value = milliseconds
        }
    }

    override fun onCleared() {
        _jobAudioCurrentPosition?.cancel()
        _jobAudioCurrentPosition = null
        audioPlayer.releasePlayer()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val audioPlayerModule = witnessAppContainer().audioPlayerModule
                AudioPlayerViewModel(audioPlayerModule)
            }
        }
    }
}

sealed interface AudioPlayerState {
    object Idle : AudioPlayerState
    object RecordingLoading : AudioPlayerState
    object RecordingReady : AudioPlayerState
    object Playing : AudioPlayerState
    object Paused : AudioPlayerState
}
