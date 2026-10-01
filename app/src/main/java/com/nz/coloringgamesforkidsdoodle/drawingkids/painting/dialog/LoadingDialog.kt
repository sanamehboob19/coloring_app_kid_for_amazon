package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.DialogLoadingBinding

class LoadingDialog private constructor(
    private val message: String
) : DialogFragment()
{

    private var _binding: DialogLoadingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.tvLoadingMessage.text = message
    }

//    override fun onStart() {
//        super.onStart()
//        dialog?.window?.apply {
//            setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT
//            )
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            setDimAmount(0.7f)
//            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//        }
//    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }

    companion object {
        const val TAG = "LoadingDialog"

        fun newInstance(message: String = "Saving..."): LoadingDialog =
            LoadingDialog(message)
    }
}