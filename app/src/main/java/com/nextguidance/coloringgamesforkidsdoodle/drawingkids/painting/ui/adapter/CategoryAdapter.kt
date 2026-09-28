package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.CategoryModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemCategoryCardBinding

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