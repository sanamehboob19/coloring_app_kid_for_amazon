package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders

import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.AlphabetModel

object AlphabetProvider {

    val letters: List<AlphabetModel> by lazy {
        listOf(
            AlphabetModel("A", "Apple", "#FF1744"),   // Red
            AlphabetModel("B", "Ball", "#2979FF"),    // Blue
            AlphabetModel("C", "Cat", "#00E676"),     // Green
            AlphabetModel("D", "Dog", "#FF9100"),     // Orange
            AlphabetModel("E", "Elephant", "#AA00FF"),// Purple
            AlphabetModel("F", "Fish", "#00E5FF"),    // Cyan
            AlphabetModel("G", "Giraffe", "#FFD600"), // Yellow
            AlphabetModel("H", "Horse", "#FF4081"),   // Pink
            AlphabetModel("I", "Ice Cream", "#00B0FF"),// Sky Blue
            AlphabetModel("J", "Juice", "#FF6D00"),   // Amber
            AlphabetModel("K", "Kite", "#7C4DFF"),    // Violet
            AlphabetModel("L", "Lion", "#64DD17"),    // Lime
            AlphabetModel("M", "Monkey", "#D50000"),  // Crimson
            AlphabetModel("N", "Nest", "#00BFA5"),    // Teal
            AlphabetModel("O", "Orange", "#FF6D00"),  // Tangerine
            AlphabetModel("P", "Parrot", "#E040FB"),  // Magenta
            AlphabetModel("Q", "Queen", "#651FFF"),   // Indigo
            AlphabetModel("R", "Rabbit", "#FF5252"),  // Coral
            AlphabetModel("S", "Sun", "#FFD600"),     // Gold
            AlphabetModel("T", "Tiger", "#FF3D00"),   // Deep Orange
            AlphabetModel("U", "Umbrella", "#304FFE"),// Royal Blue
            AlphabetModel("V", "Violin", "#7C4DFF"),  // Lavender
            AlphabetModel("W", "Watch", "#00C853"),   // Emerald
            AlphabetModel("X", "Xylophone", "#FF4081"),// Hot Pink
            AlphabetModel("Y", "Yacht", "#FFAB00"),   // Mustard
            AlphabetModel("Z", "Zebra", "#212121")    // Dark Slate
        )
    }
}