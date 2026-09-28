package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.PixelArtModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemPixelCardBinding


class PixelSelectionAdapter(
    private val models: List<PixelArtModel>,
    private val onModelClick: (PixelArtModel) -> Unit
) : RecyclerView.Adapter<PixelSelectionAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPixelCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(models[position])
    }

    override fun getItemCount(): Int = models.size

    inner class ViewHolder(
        private val binding: ItemPixelCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PixelArtModel) {
            binding.tvTitle.text = item.title
            // Render non-interactive target preview
            binding.previewGridView.loadModel(item, isTargetPreview = true)

            binding.cardPixelRoot.setOnClickListener {
                onModelClick(item)
            }
        }
    }
}