package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemSavedArtCardBinding

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

        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            val scale = if (hasFocus) 1.1f else 1.0f
            v.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(150)
                .start()
            v.elevation = if (hasFocus) 12f else 2f
        }

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = artList.size

    class ViewHolder(val binding: ItemSavedArtCardBinding) : RecyclerView.ViewHolder(binding.root)
}