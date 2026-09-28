package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.NumberModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemNumberCardBinding

class NumberSelectionAdapter(
    private val numbers: List<NumberModel>,
    private val onNumberClick: (NumberModel) -> Unit
) : RecyclerView.Adapter<NumberSelectionAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNumberCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(numbers[position])
    }

    override fun getItemCount(): Int = numbers.size

    inner class ViewHolder(
        private val binding: ItemNumberCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NumberModel) {
            binding.tvNumber.text = item.text
            // Set dynamic vibrant color for each number card
            binding.cardNumberTile.setCardBackgroundColor(Color.parseColor(item.glitterColorHex))

            binding.cardNumberTile.setOnClickListener {
                onNumberClick(item)
            }
        }
    }
}