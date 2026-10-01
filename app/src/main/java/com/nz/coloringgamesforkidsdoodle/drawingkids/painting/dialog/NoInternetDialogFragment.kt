package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.DialogNoInternetBinding
import android.view.KeyEvent
import androidx.fragment.app.FragmentManager


class NoInternetDialogFragment : DialogFragment() {

    private var _binding: DialogNoInternetBinding? = null
    private val binding get() = _binding!!
    var onRetryClick: (() -> Unit)? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogNoInternetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Allow back press to close the dialog and finish the activity safely
        isCancelable = true
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismiss()
                requireActivity().finish() // Takes user back to the previous/main screen
                true
            } else {
                false
            }
        }

        binding.btnRetry.setOnClickListener {
            onRetryClick?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NoInternetDialog"
        fun show(fragmentManager: FragmentManager, onRetry: () -> Unit) {
            val dialog = NoInternetDialogFragment()
            dialog.onRetryClick = onRetry
            dialog.show(fragmentManager, TAG)
        }
    }
}