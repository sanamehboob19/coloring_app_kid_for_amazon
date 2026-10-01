package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.AlphabetModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemAlphabetCardBinding

class AlphabetSelectionAdapter(
    private val letters: List<AlphabetModel>,
    private val onLetterClick: (AlphabetModel) -> Unit
) : RecyclerView.Adapter<AlphabetSelectionAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlphabetCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(letters[position])
        // Inside your Adapter's ViewHolder bind method:
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

    override fun getItemCount(): Int = letters.size

    inner class ViewHolder(
        private val binding: ItemAlphabetCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AlphabetModel) {
            binding.tvLetter.text = item.letter
            binding.tvWord.text = item.word
            binding.cardLetterTile.setCardBackgroundColor(Color.parseColor(item.glitterColorHex))

            binding.cardLetterTile.setOnClickListener {
                onLetterClick(item)
            }
        }
    }
}