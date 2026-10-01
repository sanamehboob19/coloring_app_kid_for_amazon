package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog


import android.R
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.DialogSaveShareBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.extension.setOnAnimateClickListener
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.ImageExportUtil
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.ToastUtils

class SaveShareDialogFragment : DialogFragment() {

    private var _binding: DialogSaveShareBinding? = null
    private val binding get() = _binding!!

    private var isSaved = false

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            setLayout(width, LayoutParams.WRAP_CONTENT)
//            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
//            val height = (resources.displayMetrics.heightPixels * 0.90).toInt()
//            setLayout(width, height)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSaveShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bitmap = currentDrawingBitmap
        if (bitmap == null) {
            dismiss()
            return
        }

        //  Show canvas preview
        binding.ivArtPreview.setImageBitmap(bitmap)

        //  Close button
        binding.btnClose.setOnAnimateClickListener {
            dismiss()
        }

        //  Share button
        binding.btnShare.setOnAnimateClickListener {
            ImageExportUtil.shareBitmap(requireContext(), bitmap)
        }

        //  Save to Gallery button
        binding.btnSaveGallery.setOnAnimateClickListener {
            if (isSaved) return@setOnAnimateClickListener

            val success = ImageExportUtil.saveToGallery(requireContext(), bitmap)
            if (success) {
                isSaved = true
                ToastUtils.show("Saved to Gallery! 🎨")

                binding.btnSaveGallery.apply {
                    setBackgroundResource(R.color.transparent)
//                    setImageResource(R.drawable.bubb)
                    isEnabled = false
                }
            } else {
                ToastUtils.show("Failed to save image")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        currentDrawingBitmap = null
    }


    companion object {
        private const val TAG = "SaveShareDialogFragment"
        private var currentDrawingBitmap: Bitmap? = null

        /**
         * Safe launcher that avoids TransactionTooLargeException with large bitmaps.
         */
        fun show(fragmentManager: FragmentManager, bitmap: Bitmap) {
            currentDrawingBitmap = bitmap
            SaveShareDialogFragment().show(fragmentManager, TAG)
        }
    }
}