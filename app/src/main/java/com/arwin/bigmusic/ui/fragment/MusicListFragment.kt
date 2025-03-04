package com.arwin.bigmusic.ui.fragment

import android.view.KeyEvent
import com.arwin.bigmusic.R
import com.arwin.bigmusic.ui.adapter.MusicAdapter
import com.arwin.bigmusic.ui.viewmodel.MusicViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MusicListFragment : Fragment() {

    private lateinit var viewModel: MusicViewModel
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music_list, container, false)

        viewModel = ViewModelProvider(this)[MusicViewModel::class.java]
        musicRecyclerView = view.findViewById(R.id.musicRecyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)

        musicAdapter = MusicAdapter(emptyList()) { track ->
            playAudio(track.previewUrl)
        }

        musicRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        musicRecyclerView.adapter = musicAdapter

        searchEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val searchTerm = searchEditText.text.toString()
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
            musicAdapter.updateData(tracks)
        }

        return view
    }

    private fun playAudio(url: String) {
        // Implement audio playback logic here
        Toast.makeText(requireContext(), "Playing audio from $url", Toast.LENGTH_SHORT).show()
    }
}