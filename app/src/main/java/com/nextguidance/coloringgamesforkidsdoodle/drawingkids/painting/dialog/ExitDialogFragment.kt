package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.databinding.DialogExitBinding

class ExitDialogFragment : DialogFragment() {

    private var _binding: DialogExitBinding? = null
    private val binding get() = _binding!!

    var onExitConfirmed: (() -> Unit)? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogExitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true

        binding.btnStay.setOnClickListener {
            dismiss()
        }

        binding.btnExit.setOnClickListener {
            dismiss()
            onExitConfirmed?.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ExitDialog"

        fun show(fragmentManager: androidx.fragment.app.FragmentManager, onExit: () -> Unit) {
            val dialog = ExitDialogFragment()
            dialog.onExitConfirmed = onExit
            dialog.show(fragmentManager, TAG)
        }
    }
}