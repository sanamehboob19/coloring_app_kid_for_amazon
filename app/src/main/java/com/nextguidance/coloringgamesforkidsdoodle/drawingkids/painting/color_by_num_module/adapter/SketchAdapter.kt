package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.signature.ObjectKey
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.model.ImageData
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.util.PreferencesManager
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.ItemSketchBinding
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util.Constant

class SketchAdapter(private val onItemClick: (model: ImageData.Image) -> Unit,
                    private val showNumberLabel: Boolean = false) :
    ListAdapter<ImageData.Image, SketchAdapter.ViewHolder>(SketchDiffCallback())
{

    private var isPremiumUser: Boolean = PreferencesManager.isPremiumUser()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSketchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until itemCount) { // Ensure position is within bounds
            holder.bind(getItem(position),  position + 1)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPremium(isPremium: Boolean) {
        if (this.isPremiumUser == isPremium) return
        this.isPremiumUser = isPremium
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSketchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ImageData.Image, itemNumber: Int) {

            PreferencesManager.getFavList().contains(model.id)

            Log.d("IMAGE_URL_DEBUG", "Loading Sketch #${itemNumber}: ${model.sketchUrl}")

            Glide.with(binding.imgSketch.context)
                .asBitmap()
                .load(model.originalUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .thumbnail(0.1f)
                .signature(ObjectKey(PreferencesManager.getInt(Constant.KEY_DATA_VERSION)))
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Bitmap>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("GLIDE_ERROR", "FAILED URL: $model")
                        Log.e("GLIDE_ERROR", "REASON: ${e?.message}")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Bitmap>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .placeholder(R.drawable.img_place_holder)
                .into(binding.imgSketch)


            binding.btnNew.visibility = if (model.premium) View.VISIBLE else View.GONE

            // Show or hide number label based on flag
            if (showNumberLabel) {
                binding.tvItemNumber.visibility = View.GONE
                binding.tvItemNumber.text = itemNumber.toString()
            } else {
                binding.tvItemNumber.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onItemClick(model)
            }

        }
    }

    class SketchDiffCallback : DiffUtil.ItemCallback<ImageData.Image>() {
        override fun areItemsTheSame(oldItem: ImageData.Image, newItem: ImageData.Image): Boolean {
            return oldItem.sketchUrl == newItem.sketchUrl
        }

        override fun areContentsTheSame(oldItem: ImageData.Image, newItem: ImageData.Image): Boolean {
            return oldItem == newItem
        }
    }

}