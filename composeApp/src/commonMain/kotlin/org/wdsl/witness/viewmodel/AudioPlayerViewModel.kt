package org.wdsl.witness.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.wdsl.witness.PlatformContext
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.util.AudioPlayer
import org.wdsl.witness.util.getAudioPlayer

class AudioPlayerViewModel(
    private val audioPlayer: AudioPlayer = getAudioPlayer(),
    private val defaultDispatchers: CoroutineDispatcher = Dispatchers.Default,
): ViewModel() {
    private var _audioPlayerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Idle)
    val audioPlayerState = _audioPlayerState.asStateFlow()

    val audioCurrentPosition = audioPlayer.audioCurrentPosition

    fun loadRecording(platformContext: PlatformContext, recording: Recording) {
        audioPlayer.loadRecording(platformContext, recording)
        _audioPlayerState.value = AudioPlayerState.RecordingReady
    }

    fun observeAudioPlayerState() {
        viewModelScope.launch(defaultDispatchers) {
            audioPlayer.observeAudioPlayerState()
        }
    }

    fun observeAudioCurrentPosition() {
        viewModelScope.launch(defaultDispatchers) {
            audioPlayer.observeAudioCurrentPosition()
        }
    }

    fun playRecording() {
        audioPlayer.play()
    }

    fun pauseRecording() {
        audioPlayer.pause()
    }

    override fun onCleared() {
        audioPlayer.release()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel()
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
