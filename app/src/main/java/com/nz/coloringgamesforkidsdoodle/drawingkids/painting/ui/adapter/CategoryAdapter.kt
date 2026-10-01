package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.CategoryModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemCategoryCardBinding

class CategoryAdapter(
    private val onCategoryClick: (CategoryModel) -> Unit
) : ListAdapter<CategoryModel, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
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

    inner class CategoryViewHolder(
        private val binding: ItemCategoryCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryModel) {
            binding.ivCategoryIcon.setImageResource(item.iconRes)
            binding.cardRoot.strokeColor = ContextCompat.getColor(
                binding.root.context,
                item.borderColorRes
            )
            binding.cardRoot.setOnClickListener {
                onCategoryClick(item)
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryModel>() {
        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem == newItem
        }
    }
}