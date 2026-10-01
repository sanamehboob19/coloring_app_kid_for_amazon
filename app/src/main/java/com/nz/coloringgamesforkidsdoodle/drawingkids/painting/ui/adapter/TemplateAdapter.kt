package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter



import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.ColoringTemplate
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemTemplateCardBinding
import com.bumptech.glide.Glide
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R




class TemplateAdapter(
    private val onTemplateClick: (ColoringTemplate) -> Unit
) : ListAdapter<ColoringTemplate, TemplateAdapter.TemplateViewHolder>(TemplateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val binding = ItemTemplateCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TemplateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            val scale = if (hasFocus) 1.1f else 1.0f
            v.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(150)
                .start()
            v.elevation = if (hasFocus) 12f else 2f
        }

    }

    inner class TemplateViewHolder(
        private val binding: ItemTemplateCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ColoringTemplate) {
            // Load remote GitHub image URL using Glide seamlessly
            Glide.with(binding.ivTemplatePreview.context)
                .load(item.previewUrl) // Passing the direct GitHub string URL
                .placeholder(R.drawable.a1)
                .error(R.drawable.a1)
                .into(binding.ivTemplatePreview)

            binding.cardTemplateRoot.setOnClickListener {
                onTemplateClick(item)
            }
        }
    }

    private class TemplateDiffCallback : DiffUtil.ItemCallback<ColoringTemplate>() {
        override fun areItemsTheSame(oldItem: ColoringTemplate, newItem: ColoringTemplate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ColoringTemplate, newItem: ColoringTemplate): Boolean {
            return oldItem == newItem
        }
    }
}