package com.arwin.bigmusic.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arwin.bigmusic.data.model.MusicTrack
import com.arwin.bigmusic.data.network.ApiService
import com.arwin.bigmusic.data.network.RetrofitInstance
import com.arwin.bigmusic.data.repository.MusicRepository
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MusicRepository
    private val _musicTracks = MutableLiveData<List<MusicTrack>>()
    val musicTracks: LiveData<List<MusicTrack>> get() = _musicTracks
    val isPlaying = MutableLiveData<Boolean>()
    val currentTrackIndex = MutableLiveData<Int>()

    init {
        val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        repository = MusicRepository(apiService)
    }

    fun searchMusic(term: String) {
        viewModelScope.launch {
            repository.searchMusic(term) { tracks ->
                _musicTracks.postValue(tracks)
            }
        }
    }
}
