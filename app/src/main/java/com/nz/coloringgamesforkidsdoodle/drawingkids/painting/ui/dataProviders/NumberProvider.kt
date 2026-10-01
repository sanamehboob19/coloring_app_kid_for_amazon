package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders

import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.NumberModel

object NumberProvider {

    val numbers: List<NumberModel> by lazy {
        listOf(
            NumberModel(1, "1", "One", "#E91E63"),     // Hot Pink
            NumberModel(2, "2", "Two", "#9C27B0"),     // Purple
            NumberModel(3, "3", "Three", "#00BCD4"),   // Cyan
            NumberModel(4, "4", "Four", "#4CAF50"),    // Green
            NumberModel(5, "5", "Five", "#FF9800"),    // Orange
            NumberModel(6, "6", "Six", "#E91E63"),     // Pink
            NumberModel(7, "7", "Seven", "#3F51B5"),   // Indigo
            NumberModel(8, "8", "Eight", "#FF4081"),   // Rose Pink
            NumberModel(9, "9", "Nine", "#7C4DFF"),    // Deep Violet
            NumberModel(10, "10", "Ten", "#00B0FF"),   // Sky Blue
            NumberModel(11, "11", "Eleven", "#00E676"),// Bright Green
            NumberModel(12, "12", "Twelve", "#FFD600"),// Sunny Gold
            NumberModel(13, "13", "Thirteen", "#FF6D00"),// Tangerine
            NumberModel(14, "14", "Fourteen", "#651FFF"),// Royal Violet
            NumberModel(15, "15", "Fifteen", "#00E5FF"),// Aqua
            NumberModel(16, "16", "Sixteen", "#FF1744"),// Bright Red
            NumberModel(17, "17", "Seventeen", "#FFAB00"),// Amber
            NumberModel(18, "18", "Eighteen", "#F50057"),// Deep Pink
            NumberModel(19, "19", "Nineteen", "#2979FF"),// Electric Blue
            NumberModel(20, "20", "Twenty", "#00C853") // Emerald
        )
    }
}