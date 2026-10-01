package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.dialog


import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.WindowManager
import android.widget.GridLayout
import androidx.fragment.app.FragmentManager
import com.intuit.sdp.R
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.databinding.DialogParentalGateBinding
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.extension.setOnAnimateClickListener
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.activities.settings.SettingsActivity
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.ToastUtils

class ParentalGateDialogFragment : DialogFragment() {

    private var _binding: DialogParentalGateBinding? = null
    private val binding get() = _binding!!

    private var targetNumber = 0

    private val numberWords = mapOf(
        1 to "One",
        2 to "Two",
        3 to "Three",
        4 to "Four",
        5 to "Five",
        6 to "Six",
        7 to "Seven",
        8 to "Eight",
        9 to "Nine",
        10 to "Ten"
    )

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            //  Dim the background
            setDimAmount(0.8f)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogParentalGateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateRandomQuestion()
        setupNumberButtons()

        binding.btnClose.setOnAnimateClickListener {
            dismiss()
        }
    }

    private fun generateRandomQuestion() {
        targetNumber = Random.nextInt(3, 11)
        val word = numberWords[targetNumber] ?: targetNumber.toString()

        val fullText = "Please tap number $word"
        val spannable = SpannableString(fullText)

        val startIndex = fullText.indexOf(word)
        val endIndex = startIndex + word.length

        // Make target word bold and green
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#00C853")), // Green color
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvQuestion.text = spannable
    }

    private fun setupNumberButtons() {
        binding.gridLayoutNumbers.removeAllViews()

        for (i in 1..10) {
            val button = MaterialButton(requireContext()).apply {
                text = i.toString()
                textSize = 18f
                isAllCaps = false
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#FF6F00"))

                // Set width to _52sdp and height to _42sdp so "10" fits easily in 1 line
                val params = GridLayout.LayoutParams().apply {
                    width = resources.getDimensionPixelSize(R.dimen._52sdp)
                    height = resources.getDimensionPixelSize(R.dimen._42sdp)
                    setMargins(8, 8, 8, 8)
                }

                // If it's the 10th button, make it span across columns or center nicely if layout allows,
                // but with 3 columns, 10 will wrap naturally to the 4th row on the left.
                if (i == 10) {
                    params.columnSpec = GridLayout.spec(1, 1) // Centers it under column 2
                }

                layoutParams = params

                setOnClickListener {
                    checkAnswer(i)
                }
            }
            binding.gridLayoutNumbers.addView(button)
        }
    }



    private fun checkAnswer(selectedNumber: Int) {
        if (selectedNumber == targetNumber) {
            ToastUtils.show("Access Granted! ✅")
            dismiss()
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        } else {
            ToastUtils.show("Oops! Try again ❌")
            generateRandomQuestion()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ParentalGateDialog"

        fun show(fragmentManager: FragmentManager) {
            ParentalGateDialogFragment().show(fragmentManager, TAG)
        }
    }
}