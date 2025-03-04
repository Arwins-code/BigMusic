package com.arwin.bigmusic.ui.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]

        musicAdapter = MusicAdapter { track ->
            playAudio(track.previewUrl, track.trackTimeMillis)
            isPlaying = true
            binding.playerControls.root.visibility = View.VISIBLE
        }

        binding.musicRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.musicRecyclerView.adapter = musicAdapter

        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = p0.toString()
                if (query.isNotEmpty()) {
                    viewModel.searchMusic(query)
                } else {
                    musicAdapter.updateData(emptyList())
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        viewModel.musicTracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                tracks[currentTrackIndex].isPlaying = isPlaying
            }
            musicTracks = tracks
            musicAdapter.updateData(tracks)
        }

        binding.playerControls.playPauseButton.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                pauseAudio()
            } else {
                playAudio(url = musicTracks.getOrNull(currentTrackIndex)?.previewUrl ?: "", time = musicTracks.getOrNull(currentTrackIndex)?.trackTimeMillis ?: 0)
            }
        }

        binding.playerControls.nextButton.setOnClickListener {
            playNextTrack()
        }

        binding.playerControls.previousButton.setOnClickListener {
            playPreviousTrack()
        }

        return binding.root
    }

    private fun setupSeekBar(time: Int) {
        binding.playerControls.seekBar.max = mediaPlayer?.duration ?: 0

        // Update SeekBar in real-time
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    binding.playerControls.seekBar.progress = it.currentPosition
                    handler.postDelayed(this, 1000) // Update every second
                }
            }
        }
        handler.post(runnable)

        // SeekBar listener
        binding.playerControls.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    if (seekBar != null) {
                        mediaPlayer!!.seekTo(seekBar.progress)
                    };
                }
            }
        })
    }

    private fun playAudio(url: String, time: Int) {
        if (url.isEmpty()) return

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepareAsync()
            mediaPlayer?.isLooping = false
            mediaPlayer?.setOnPreparedListener {
                it.start()
                setupSeekBar(time)
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
            playAudio(url = musicTracks[currentTrackIndex].previewUrl, time = musicTracks[currentTrackIndex].trackTimeMillis)
        } else {
            Toast.makeText(requireContext(), "No more tracks", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playPreviousTrack() {
        if (currentTrackIndex > 0) {
            currentTrackIndex--
            playAudio(url = musicTracks[currentTrackIndex].previewUrl, time = musicTracks[currentTrackIndex].trackTimeMillis)
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