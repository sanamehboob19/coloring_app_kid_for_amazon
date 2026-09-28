package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper.NumberPopAnimator
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.ColorModel
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemColorBinding

class ColorAdapter(
    private val onItemClick: (ColorModel) -> Unit
) : ListAdapter<ColorModel, ColorAdapter.ViewHolder>(DiffCallback())
{

    private var selectedColor: Int? = null

    companion object {
        private const val PAYLOAD_PROGRESS = "payload_progress"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemColorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    // Full bind — only called on first load or selection change
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Partial bind — called when payload is present (NO blink, no animation)
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads.all { it == PAYLOAD_PROGRESS }) {
            holder.bindProgress(getItem(position))
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    inner class ViewHolder(private val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Full bind: color, scale animation, click, text
        fun bind(model: ColorModel) {

            if (model.isCompleted && binding.root.isGone) return

            val isSelected = model.color == selectedColor

            binding.imgColor.setBackgroundColor(model.color)
            binding.root.translationZ = if (isSelected) 10f else 0f

            // Scale animation only on full bind (selection change)
            val scaleValue = if (isSelected) 1.2f else 1.0f
            binding.root.scaleX = scaleValue
            binding.root.scaleY = scaleValue
//            binding.root.animate()
//                .scaleX(scaleValue)
//                .scaleY(scaleValue)
//                .setDuration(200)
//                .start()

            binding.root.setOnClickListener {
                if (selectedColor == model.color || model.isCompleted) return@setOnClickListener

                val prevIndex = currentList.indexOfFirst { it.color == selectedColor }
                selectedColor = model.color

                if (prevIndex != -1) notifyItemChanged(prevIndex)
                notifyItemChanged(absoluteAdapterPosition)

                onItemClick(model)
            }

            bindProgress(model) // Reuse progress logic
        }

        // Partial bind: only progress bar + completion text (no animation, no blink)
        fun bindProgress(model: ColorModel) {
            val isSelected = model.color == selectedColor

            if (model.isCompleted) {
                binding.txtNumber.text = "✓"
                binding.txtNumber.setTextColor(Color.GREEN)
            } else {
                binding.txtNumber.text = model.number.toString()
//                binding.txtNumber.setTextColor(if (isSelected) Color.WHITE else Color.BLACK)
                if (isSelected) {
                    if (isColorDark(model.color)) {
                        binding.txtNumber.setTextColor(Color.WHITE)
                    } else {
                        binding.txtNumber.setTextColor(Color.BLACK)
                    }
                } else {
                    binding.txtNumber.setTextColor(Color.BLACK)
                }


            }

            binding.pb.max = model.totalCount
            binding.pb.setProgress(model.filledCount.toFloat(), false)
        }

        fun playCompletionAnimation(model: ColorModel, onAnimationEnd: () -> Unit) {
            NumberPopAnimator.play(
                itemView = binding.root,
                numberView = binding.txtNumber,
                color = model.color,
                label = model.number.toString(),
                onRemove = {
                    // GONE + layout reflow already handled inside NumberPopAnimator.
                    // Just post the callback so it runs after the layout pass.
                    binding.root.post {
                        onAnimationEnd()
                    }
                }
            )
        }
    }

    fun setSelectedColor(color: Int) {
        val oldIndex = currentList.indexOfFirst { it.color == selectedColor }
        val newIndex = currentList.indexOfFirst { it.color == color }
        selectedColor = color
        if (oldIndex != -1) notifyItemChanged(oldIndex)
        if (newIndex != -1) notifyItemChanged(newIndex)
    }

    // Call this for progress-only updates — no blink
    fun updateProgress(newList: List<ColorModel>) {
        val oldList = currentList

        newList.forEachIndexed { index, newModel ->
            val oldModel = oldList.getOrNull(index) ?: return@forEachIndexed
            val progressChanged = oldModel.filledCount != newModel.filledCount
                    || oldModel.totalCount != newModel.totalCount
                    || oldModel.isCompleted != newModel.isCompleted

            if (progressChanged) {
                // Update the internal list reference without triggering DiffUtil animation
                notifyItemChanged(index, PAYLOAD_PROGRESS)
            }
        }

        // Silently update the backing list so currentList stays accurate
        super.submitList(newList)
    }

    private fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5 // if greater than 0.5 darker
    }

    class DiffCallback : DiffUtil.ItemCallback<ColorModel>() {
        override fun areItemsTheSame(oldItem: ColorModel, newItem: ColorModel) =
            oldItem.color == newItem.color

        override fun areContentsTheSame(oldItem: ColorModel, newItem: ColorModel) =
            oldItem == newItem
    }
}