package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter


import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.ColorItem
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemColorSwatchBinding


class ColorPaletteAdapter(
    private val colors: List<ColorItem>,
    private val onColorSelected: (ColorItem) -> Unit
) : RecyclerView.Adapter<ColorPaletteAdapter.ColorViewHolder>()
{

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorSwatchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = colors.size

    inner class ColorViewHolder(
        private val binding: ItemColorSwatchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ColorItem, isSelected: Boolean) {
            // Set circle background to the paint color
            binding.cardColorSwatch.setCardBackgroundColor(ColorStateList.valueOf(item.colorInt))

            // Show indicator checkmark on active color
            binding.ivSelectedIndicator.visibility = if (isSelected) View.VISIBLE else View.GONE

            // Handle color selection
            binding.cardColorSwatch.setOnClickListener {
                val previousIndex = selectedPosition
                val currentIndex = bindingAdapterPosition
                if (currentIndex != RecyclerView.NO_POSITION && previousIndex != currentIndex) {
                    selectedPosition = currentIndex
                    notifyItemChanged(previousIndex)
                    notifyItemChanged(selectedPosition)
                    onColorSelected(item)
                }
            }
        }
    }
}