package com.arwin.bigmusic.ui.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.arwin.bigmusic.R
import com.arwin.bigmusic.data.model.MusicTrack
import com.arwin.bigmusic.databinding.FragmentMusicListBinding
import com.arwin.bigmusic.ui.adapter.MusicAdapter
import com.arwin.bigmusic.ui.viewmodel.MusicViewModel

class MusicListFragment : Fragment() {

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MusicViewModel
    private lateinit var musicAdapter: MusicAdapter

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackIndex = 0
    private var musicTracks: List<MusicTrack> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]

        musicAdapter = MusicAdapter { track ->
            playAudio(track.previewUrl)
        }

        binding.musicRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.musicRecyclerView.adapter = musicAdapter

        binding.searchEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val searchTerm = binding.searchEditText.text.toString()
                if (searchTerm.isNotEmpty()) {
                    viewModel.searchMusic(searchTerm)
                } else {
                    musicAdapter.updateData(emptyList())
                }
                true
            } else {
                false
            }
        }

        viewModel.musicTracks.observe(viewLifecycleOwner) { tracks ->
            musicTracks = tracks
            musicAdapter.updateData(tracks)
        }

        binding.playerControls.playPauseButton.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                pauseAudio()
            } else {
                playAudio(musicTracks.getOrNull(currentTrackIndex)?.previewUrl ?: "")
            }
        }

        binding.playerControls.nextButton.setOnClickListener {
            playNextTrack()
        }

        binding.playerControls.previousButton.setOnClickListener {
            playPreviousTrack()
        }

        binding.playerControls.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        return binding.root
    }

    private fun playAudio(url: String) {
        if (url.isEmpty()) return

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener {
                it.start()
                binding.playerControls.seekBar.max = it.duration
                binding.playerControls.playPauseButton.setImageResource(R.drawable.ic_pause)
            }
            mediaPlayer?.setOnCompletionListener {
                playNextTrack()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error playing audio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        binding.playerControls.playPauseButton.setImageResource(R.drawable.ic_play)
    }

    private fun playNextTrack() {
        if (currentTrackIndex < musicTracks.size - 1) {
            currentTrackIndex++
            playAudio(musicTracks[currentTrackIndex].previewUrl)
        } else {
            Toast.makeText(requireContext(), "No more tracks", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playPreviousTrack() {
        if (currentTrackIndex > 0) {
            currentTrackIndex--
            playAudio(musicTracks[currentTrackIndex].previewUrl)
        } else {
            Toast.makeText(requireContext(), "No previous tracks", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}