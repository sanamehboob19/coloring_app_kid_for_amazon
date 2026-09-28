package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemSavedArtCardBinding

class SavedArtAdapter(
    private val artList: List<Bitmap>,
    private val onItemClick: (Int) -> Unit // pass position or file path
) : RecyclerView.Adapter<SavedArtAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSavedArtCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bitmap = artList[position]
        holder.binding.ivSavedThumb.setImageBitmap(bitmap)

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = artList.size

    class ViewHolder(val binding: ItemSavedArtCardBinding) : RecyclerView.ViewHolder(binding.root)
}