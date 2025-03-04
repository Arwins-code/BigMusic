package com.arwin.bigmusic.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arwin.bigmusic.data.model.MusicTrack
import com.arwin.bigmusic.databinding.ItemMusicBinding
import com.bumptech.glide.Glide

class MusicAdapter(
    private val musicTracks: List<MusicTrack>,
    private val onClick: (MusicTrack) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {
    private var music: MutableList<MusicTrack> = mutableListOf()

    class MusicViewHolder(val binding: ItemMusicBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateData(newData: List<MusicTrack>) {
        music.clear()
        music.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val track = musicTracks[position]
        holder.binding.trackName.text = track.trackName
        holder.binding.artistName.text = track.artistName
        Glide
            .with(holder.itemView.context)
            .load(track.collectionName)
            .into(holder.binding.albumImage)
        holder.itemView.setOnClickListener {
            onClick(track)
        }
    }

    override fun getItemCount(): Int = musicTracks.size
}