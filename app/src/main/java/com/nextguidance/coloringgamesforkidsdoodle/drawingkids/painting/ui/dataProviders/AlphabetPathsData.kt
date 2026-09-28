package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders

object AlphabetPathsData {

    fun getStrokes(letter: String): List<String> {
        return when (letter.uppercase()) {
            // A: Left slant, right slant, middle bar
            "A" -> listOf(
                "M 50 15 L 20 85",
                "M 50 15 L 80 85",
                "M 32 58 L 68 58"
            )

            // B: Straight stem, top bubble, bottom bubble
            "B" -> listOf(
                "M 28 15 L 28 85",
                "M 28 15 C 65 15, 65 48, 28 48",
                "M 28 48 C 70 48, 70 85, 28 85"
            )

            // C: Smooth open curve
            "C" -> listOf(
                "M 75 28 C 70 15, 30 15, 30 50 C 30 85, 70 85, 75 72"
            )

            // D: Straight stem, rounded outer loop
            "D" -> listOf(
                "M 30 15 L 30 85",
                "M 30 15 C 75 15, 75 85, 30 85"
            )

            // E: Vertical stem, top bar, middle bar, bottom bar
            "E" -> listOf(
                "M 32 15 L 32 85",
                "M 32 15 L 75 15",
                "M 32 50 L 65 50",
                "M 32 85 L 75 85"
            )

            // F: Vertical stem, top bar, middle bar
            "F" -> listOf(
                "M 32 15 L 32 85",
                "M 32 15 L 75 15",
                "M 32 50 L 65 50"
            )

            // G: Curved body, inner horizontal bar
            // G: Continuous smooth stroke (around loop and directly into crossbar)
            "G" -> listOf(
                "M 75 28 C 70 14, 28 14, 28 50 C 28 86, 75 86, 75 52 L 50 52"
            )

            // H: Left stem, right stem, middle crossbar
            "H" -> listOf(
                "M 28 15 L 28 85",
                "M 72 15 L 72 85",
                "M 28 50 L 72 50"
            )

            // I: Clean vertical center stroke
            "I" -> listOf(
                "M 50 15 L 50 85"
            )

            // J: Downward hook
            "J" -> listOf(
                "M 70 15 L 70 65 C 70 85, 35 85, 35 70"
            )

            // K: Vertical stem, top diagonal, bottom diagonal
            "K" -> listOf(
                "M 30 15 L 30 85",
                "M 75 20 L 30 55",
                "M 42 45 L 75 85"
            )

            // L: Continuous downward and right bar
            "L" -> listOf(
                "M 32 15 L 32 85 L 75 85"
            )

            // M: Continuous valley stroke
            "M" -> listOf(
                "M 22 85 L 22 15 L 50 55 L 78 15 L 78 85"
            )

            // N: Left stem, diagonal down, right stem
            "N" -> listOf(
                "M 26 85 L 26 15",
                "M 26 15 L 74 85",
                "M 74 85 L 74 15"
            )

            // O: Full circular loop
            "O" -> listOf(
                "M 50 15 C 25 15, 25 85, 50 85 C 75 85, 75 15, 50 15 Z"
            )

            // P: Vertical stem, top loop
            "P" -> listOf(
                "M 30 15 L 30 85",
                "M 30 15 C 75 15, 75 52, 30 52"
            )

            // Q: Outer O loop, tail tick
            "Q" -> listOf(
                "M 50 15 C 25 15, 25 85, 50 85 C 75 85, 75 15, 50 15 Z",
                "M 60 65 L 78 85"
            )

            // R: Vertical stem, top bubble, leg kick
            "R" -> listOf(
                "M 30 15 L 30 85",
                "M 30 15 C 75 15, 75 50, 30 50",
                "M 48 50 L 75 85"
            )

            // S: Smooth flowing serpent curve (Matches video 01:56)
            "S" -> listOf(
                "M 72 28 C 65 15, 32 15, 32 34 C 32 50, 68 50, 68 66 C 68 85, 30 85, 26 70"
            )

            // T: Top horizontal bar, center vertical stem (Matches video 02:07)
            "T" -> listOf(
                "M 22 18 L 78 18",
                "M 50 18 L 50 85"
            )

            // U: Smooth rounded cup (Matches video 02:15)
            "U" -> listOf(
                "M 28 15 L 28 65 C 28 85, 72 85, 72 65 L 72 15"
            )

            // V: Diagonal down and up
            "V" -> listOf(
                "M 24 15 L 50 85 L 76 15"
            )

            // W: Double valley curve
            "W" -> listOf(
                "M 18 15 L 34 85 L 50 45 L 66 85 L 82 15"
            )

            // X: Diagonal slash 1, diagonal slash 2
            "X" -> listOf(
                "M 26 18 L 74 82",
                "M 74 18 L 26 82"
            )

            // Y: Top-left branch, top-right branch, bottom trunk
            "Y" -> listOf(
                "M 24 18 L 50 50",
                "M 76 18 L 50 50",
                "M 50 50 L 50 85"
            )

            // Z: Top horizontal, diagonal, bottom horizontal
            "Z" -> listOf(
                "M 26 20 L 74 20 L 26 80 L 74 80"
            )

            else -> listOf("M 50 15 L 20 85", "M 50 15 L 80 85", "M 32 58 L 68 58")
        }
    }
}