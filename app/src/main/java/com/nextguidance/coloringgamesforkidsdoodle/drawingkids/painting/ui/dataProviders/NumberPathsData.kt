package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders

object NumberPathsData {

    fun getStrokes(number: Int): List<String> {
        return when (number) {
            1 -> listOf("M 35 30 L 50 15 L 50 85")
            2 -> listOf("M 25 32 C 25 14, 75 14, 75 35 C 75 55, 25 68, 25 85 L 75 85")
            3 -> listOf("M 25 28 C 30 14, 72 14, 72 34 C 72 47, 50 48, 50 48 C 50 48, 74 49, 74 66 C 74 86, 28 86, 25 72")

            // 4️⃣ FIXED: Split into 2 distinct strokes so the vertical line never auto-fills!
            4 -> listOf(
                "M 65 15 L 25 60 L 80 60", // Stroke 1: Down-left and across
                "M 65 35 L 65 85"          // Stroke 2: Vertical cross down
            )

            5 -> listOf("M 72 16 L 34 16 L 30 44 C 38 38, 74 38, 74 65 C 74 86, 35 88, 26 78")
            6 -> listOf("M 65 18 C 30 38, 25 60, 25 68 C 25 86, 75 86, 75 66 C 75 48, 25 48, 25 68")
            7 -> listOf("M 24 16 L 76 16 L 38 85")
            8 -> listOf("M 50 48 C 25 36, 25 15, 50 15 C 75 15, 75 36, 50 48 C 22 60, 22 85, 50 85 C 78 85, 78 60, 50 48 Z")
            9 -> listOf("M 70 48 C 70 20, 30 20, 30 48 C 30 65, 70 65, 70 48 L 70 70 C 70 85, 45 88, 32 82")

            // 🔟 FIXED: Perfect smooth oval loop for '0'
            10 -> listOf(
                "M 22 28 L 34 18 L 34 85", // Stroke 1: '1'
                "M 68 18 C 50 18, 50 85, 68 85 C 86 85, 86 18, 68 18 Z" // Stroke 2: Clean '0'
            )

            11 -> listOf(
                "M 24 28 L 36 18 L 36 85",
                "M 64 28 L 76 18 L 76 85"
            )

            12 -> listOf(
                "M 18 28 L 28 18 L 28 85",
                "M 50 32 C 50 14, 85 14, 85 35 C 85 55, 50 68, 50 85 L 85 85"
            )

            13 -> listOf(
                "M 18 28 L 28 18 L 28 85",
                "M 50 28 C 54 14, 85 14, 85 34 C 85 47, 68 48, 68 48 C 68 48, 86 49, 86 66 C 86 86, 52 86, 50 72"
            )

            // 14: Stroke 1 ('1'), Stroke 2 ('4' L-shape), Stroke 3 ('4' vertical stem)
            14 -> listOf(
                "M 18 28 L 28 18 L 28 85",
                "M 74 20 L 46 58 L 86 58",
                "M 74 40 L 74 85"
            )

            15 -> listOf(
                "M 18 28 L 28 18 L 28 85",
                "M 84 18 L 52 18 L 50 44 C 56 38, 86 38, 86 65 C 86 86, 54 88, 48 78"
            )

            16 -> listOf(
                "M 18 28 L 28 18 L 28 85",
                "M 80 20 C 54 38, 50 60, 50 68 C 50 86, 86 86, 86 66 C 86 48, 50 48, 50 68"
            )

            17 -> listOf(
                "M 18 28 L 28 18 L 28 85",
                "M 48 18 L 86 18 L 58 85"
            )

            18 -> listOf(
                "M 18 28 L 28 18 L 28 85",
                "M 68 48 C 50 36, 50 15, 68 15 C 86 15, 86 36, 68 48 C 48 60, 48 85, 68 85 C 88 85, 88 60, 68 48 Z"
            )

            19 -> listOf(
                "M 18 28 L 28 18 L 28 85",
                "M 82 48 C 82 20, 52 20, 52 48 C 52 65, 82 65, 82 48 L 82 70 C 82 85, 60 88, 52 82"
            )

            // 20: Stroke 1 ('2'), Stroke 2 ('0')
            20 -> listOf(
                "M 20 32 C 20 14, 50 14, 50 35 C 50 55, 20 68, 20 85 L 50 85",
                "M 74 18 C 58 18, 58 85, 74 85 C 90 85, 90 18, 74 18 Z"
            )

            else -> listOf("M 35 30 L 50 15 L 50 85")
        }
    }
}