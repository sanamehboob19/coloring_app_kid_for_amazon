package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.CollectionModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemCollectionCardBinding

class CollectionAdapter(
    private val onCollectionClick: (CollectionModel) -> Unit
) : ListAdapter<CollectionModel, CollectionAdapter.CollectionViewHolder>(CollectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = ItemCollectionCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
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

    inner class CollectionViewHolder(
        private val binding: ItemCollectionCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CollectionModel) {
            binding.ivCollectionPreview.setImageResource(item.previewRes)
            binding.tvCollectionTitle.text = item.title
            binding.layoutBottomSpine.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, item.spineColorRes)
            )
            binding.tvNewBadge.visibility = if (item.isNew) View.VISIBLE else View.GONE
            binding.cardNotebookRoot.setOnClickListener {
                onCollectionClick(item)
            }
        }
    }

    private class CollectionDiffCallback : DiffUtil.ItemCallback<CollectionModel>() {
        override fun areItemsTheSame(oldItem: CollectionModel, newItem: CollectionModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CollectionModel, newItem: CollectionModel): Boolean {
            return oldItem == newItem
        }
    }
}