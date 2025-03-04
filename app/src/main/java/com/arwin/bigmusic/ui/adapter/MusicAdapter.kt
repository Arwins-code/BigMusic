package com.arwin.bigmusic.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arwin.bigmusic.data.model.MusicTrack
import com.arwin.bigmusic.databinding.ItemMusicBinding
import com.bumptech.glide.Glide

class MusicAdapter(
    private val onClick: (MusicTrack) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {
    private var musicTracks: MutableList<MusicTrack> = mutableListOf()

    class MusicViewHolder(val binding: ItemMusicBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateData(newData: List<MusicTrack>) {
        musicTracks.clear()
        musicTracks.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val track = musicTracks[position]
        holder.binding.songName.text = track.trackName
        holder.binding.artistName.text = track.artistName
        holder.binding.albumName.text = track.collectionName
        Log.e("Arwin", "onBindViewHolder: ${track.isPlaying}, ${track.artistName}")
        holder.binding.animationPlay.visibility = if (track.isPlaying) View.VISIBLE else View.GONE // this lottie naimation show and play based on selected item
        Glide
            .with(holder.itemView.context)
            .load(track.artworkUrl60)
            .into(holder.binding.albumImage)
        holder.itemView.setOnClickListener {
            onClick(track)
            track.isPlaying = !track.isPlaying
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = musicTracks.size
}