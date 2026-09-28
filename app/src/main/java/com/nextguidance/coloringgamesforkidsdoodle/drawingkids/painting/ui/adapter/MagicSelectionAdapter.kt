package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.entity.MagicTemplateEntity
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemMagicTemplateBinding
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter




class MagicSelectionAdapter(
    private val onTemplateClick: (MagicTemplateEntity) -> Unit
) : ListAdapter<MagicTemplateEntity, MagicSelectionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMagicTemplateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemMagicTemplateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MagicTemplateEntity) {
            binding.tvTemplateTitle.text = item.title

            // 🌟 Apply Grayscale / Black & White Outline Filter
            val colorMatrix = ColorMatrix().apply {
                setSaturation(0f)
            }
            val scale = 1.5f
            val offset = -128f * (scale - 1f)
            val contrastMatrix = ColorMatrix(floatArrayOf(
                scale, 0f, 0f, 0f, offset,
                0f, scale, 0f, 0f, offset,
                0f, 0f, scale, 0f, offset,
                0f, 0f, 0f, 1f, 0f
            ))
            colorMatrix.postConcat(contrastMatrix)
            binding.ivTemplatePreview.colorFilter = ColorMatrixColorFilter(colorMatrix)

            // Load remote GitHub preview image URL using Glide
            Glide.with(binding.ivTemplatePreview.context)
                .load(item.previewUrl)
                .placeholder(R.drawable.a1)
                .error(R.drawable.a1)
                .into(binding.ivTemplatePreview)

            binding.cardTemplateRoot.setOnClickListener {
                onTemplateClick(item)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<MagicTemplateEntity>() {
        override fun areItemsTheSame(oldItem: MagicTemplateEntity, newItem: MagicTemplateEntity): Boolean {
            return oldItem.templateId == newItem.templateId
        }

        override fun areContentsTheSame(oldItem: MagicTemplateEntity, newItem: MagicTemplateEntity): Boolean {
            return oldItem == newItem
        }
    }
}