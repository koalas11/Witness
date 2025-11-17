package org.wdsl.witness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.module.audio.AudioPlayerModule
import org.wdsl.witness.storage.room.Recording

class AudioPlayerViewModel(
    private val audioPlayer: AudioPlayerModule,
    private val defaultDispatchers: CoroutineDispatcher = Dispatchers.Default,
): ViewModel() {
    private var _audioPlayerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Idle)
    val audioPlayerState = _audioPlayerState.asStateFlow()

    private var _audioCurrentPosition = MutableStateFlow(0L)
    val audioCurrentPosition = _audioCurrentPosition.asStateFlow()

    private var _jobAudioCurrentPosition: Job? = null

    fun loadRecording(recording: Recording) {
        audioPlayer.loadAudio(recording.recordingFileName)
        _audioPlayerState.value = AudioPlayerState.RecordingReady
        observeAudioCurrentPosition()
    }

    fun observeAudioCurrentPosition() {
        viewModelScope.launch {
            _jobAudioCurrentPosition = launch {
                while (audioPlayer.isPlaying()) {
                    _audioCurrentPosition.value = audioPlayer.getCurrentPosition()
                }
            }
        }
    }

    fun playRecording() {
        audioPlayer.resumeAudio()
    }

    fun pauseRecording() {
        audioPlayer.pauseAudio()
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
