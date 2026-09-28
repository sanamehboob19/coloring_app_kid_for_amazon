package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.AlphabetModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemAlphabetCardBinding

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