package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.dataProviders

import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.PixelArtModel

object PixelArtProvider {

    val models: List<PixelArtModel> by lazy {
        listOf(
            // 1. 😊 Yellow Smiley Face
            PixelArtModel(
                id = 1,
                title = "Smiley Face 😊",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#FFD600", "#2979FF", "#FF1744"),
                matrix = listOf(
                    0, 0, 1, 1, 1, 1, 1, 1, 0, 0,
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                    1, 1, 2, 1, 1, 1, 1, 2, 1, 1,
                    1, 1, 2, 1, 1, 1, 1, 2, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 3, 1, 1, 1, 1, 1, 1, 3, 1,
                    1, 1, 3, 1, 1, 1, 1, 3, 1, 1,
                    0, 1, 1, 3, 3, 3, 3, 1, 1, 0,
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                    0, 0, 1, 1, 1, 1, 1, 1, 0, 0
                )
            ),

            // 2. 🤖 Friendly Robot
            PixelArtModel(
                id = 2,
                title = "Friendly Robot 🤖",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#FF1744", "#2979FF", "#00E676"),
                matrix = listOf(
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 2, 2, 2, 2, 2, 2, 2, 2, 1,
                    1, 2, 0, 3, 2, 2, 0, 3, 2, 1,
                    1, 2, 0, 0, 2, 2, 0, 0, 2, 1,
                    1, 2, 2, 2, 2, 2, 2, 2, 2, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 0, 1, 1, 1, 1, 1, 1, 0, 1,
                    1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
                    1, 1, 0, 0, 0, 0, 0, 0, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1
                )
            ),

            // 3. 🪲 Blue Beetle Bug
            PixelArtModel(
                id = 3,
                title = "Blue Bug 🪲",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#2979FF", "#FFD600", "#FF1744"),
                matrix = listOf(
                    0, 0, 1, 0, 0, 0, 0, 1, 0, 0,
                    0, 0, 0, 1, 1, 1, 1, 0, 0, 0,
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                    1, 0, 1, 2, 1, 1, 2, 1, 0, 1,
                    1, 0, 1, 1, 1, 1, 1, 1, 0, 1,
                    0, 1, 1, 1, 3, 3, 1, 1, 1, 0,
                    1, 0, 1, 1, 1, 1, 1, 1, 0, 1,
                    1, 0, 0, 1, 1, 1, 1, 0, 0, 1,
                    0, 0, 1, 0, 0, 0, 0, 1, 0, 0,
                    0, 1, 0, 0, 0, 0, 0, 0, 1, 0
                )
            ),

            // 4. ❤️ Pixel Heart
            PixelArtModel(
                id = 4,
                title = "Pixel Heart ❤️",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#FF1744", "#FF4081"),
                matrix = listOf(
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 1, 1, 0, 0, 0, 0, 1, 1, 0,
                    1, 2, 2, 1, 0, 0, 1, 2, 2, 1,
                    1, 2, 2, 2, 1, 1, 2, 2, 2, 1,
                    1, 2, 2, 2, 2, 2, 2, 2, 2, 1,
                    0, 1, 2, 2, 2, 2, 2, 2, 1, 0,
                    0, 0, 1, 2, 2, 2, 2, 1, 0, 0,
                    0, 0, 0, 1, 2, 2, 1, 0, 0, 0,
                    0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                )
            ),

            // 5. 🍎 Sweet Apple
            PixelArtModel(
                id = 5,
                title = "Sweet Apple 🍎",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#E53935", "#43A047", "#6D4C41"),
                matrix = listOf(
                    0, 0, 0, 0, 2, 2, 0, 0, 0, 0,
                    0, 0, 0, 0, 3, 2, 0, 0, 0, 0,
                    0, 1, 1, 1, 0, 0, 1, 1, 1, 0,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                    0, 0, 1, 1, 1, 1, 1, 1, 0, 0,
                    0, 0, 0, 1, 1, 1, 1, 0, 0, 0
                )
            ),

            // 6. 🐥 Cute Chick
            PixelArtModel(
                id = 6,
                title = "Cute Chick 🐥",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#FFEB3B", "#FF9800", "#212121"),
                matrix = listOf(
                    0, 0, 0, 1, 1, 1, 0, 0, 0, 0,
                    0, 0, 1, 1, 3, 1, 1, 0, 0, 0,
                    0, 2, 2, 1, 1, 1, 1, 0, 0, 0,
                    0, 0, 0, 1, 1, 1, 0, 0, 0, 0,
                    0, 0, 1, 1, 1, 1, 1, 0, 0, 0,
                    0, 1, 1, 1, 1, 1, 1, 1, 0, 0,
                    0, 1, 1, 1, 1, 1, 1, 1, 0, 0,
                    0, 0, 1, 1, 1, 1, 1, 0, 0, 0,
                    0, 0, 0, 2, 0, 2, 0, 0, 0, 0,
                    0, 0, 2, 2, 0, 2, 2, 0, 0, 0
                )
            ),

            // 7. 🍄 Magic Mushroom
            PixelArtModel(
                id = 7,
                title = "Mushroom 🍄",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#E53935", "#FFF8E1", "#212121"),
                matrix = listOf(
                    0, 0, 0, 1, 1, 1, 1, 0, 0, 0,
                    0, 0, 1, 1, 1, 1, 1, 1, 0, 0,
                    0, 1, 0, 0, 1, 1, 0, 0, 1, 0,
                    1, 1, 0, 0, 1, 1, 0, 0, 1, 1,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    0, 0, 2, 2, 2, 2, 2, 2, 0, 0,
                    0, 0, 2, 3, 2, 2, 3, 2, 0, 0,
                    0, 0, 2, 3, 2, 2, 3, 2, 0, 0,
                    0, 0, 2, 2, 2, 2, 2, 2, 0, 0,
                    0, 0, 0, 2, 2, 2, 2, 0, 0, 0
                )
            ),

            // 8. 🎮 Retro Game Boy
            PixelArtModel(
                id = 8,
                title = "Retro Console 🎮",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#7C4DFF", "#80D8FF", "#37474F", "#FF1744"),
                matrix = listOf(
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                    0, 1, 2, 2, 2, 2, 2, 2, 1, 0,
                    0, 1, 2, 2, 2, 2, 2, 2, 1, 0,
                    0, 1, 2, 2, 2, 2, 2, 2, 1, 0,
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                    0, 1, 0, 3, 0, 0, 0, 4, 1, 0,
                    0, 1, 3, 3, 3, 0, 4, 0, 1, 0,
                    0, 1, 0, 3, 0, 0, 0, 0, 1, 0,
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 0,
                    0, 0, 1, 1, 1, 1, 1, 1, 0, 0
                )
            ),

            // 9. 🌸 Pretty Flower
            PixelArtModel(
                id = 9,
                title = "Flower 🌸",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#F06292", "#FDD835", "#43A047"),
                matrix = listOf(
                    0, 0, 0, 1, 1, 0, 0, 0, 0, 0,
                    0, 1, 1, 0, 0, 1, 1, 0, 0, 0,
                    0, 1, 1, 2, 2, 1, 1, 0, 0, 0,
                    0, 0, 2, 2, 2, 2, 0, 0, 0, 0,
                    0, 1, 1, 2, 2, 1, 1, 0, 0, 0,
                    0, 1, 1, 0, 0, 1, 1, 0, 0, 0,
                    0, 0, 0, 3, 3, 0, 0, 0, 0, 0,
                    0, 3, 3, 3, 3, 0, 0, 0, 0, 0,
                    0, 0, 0, 3, 3, 3, 3, 0, 0, 0,
                    0, 0, 0, 3, 3, 0, 0, 0, 0, 0
                )
            ),

            // 10. ⛵ Sailboat

            PixelArtModel(
                id = 10,
                title = "Sailboat ⛵",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#0288D1", "#E53935", "#795548"),
                matrix = listOf(
                    0, 0, 0, 0, 3, 0, 0, 0, 0, 0,
                    0, 0, 0, 1, 3, 0, 0, 0, 0, 0,
                    0, 0, 1, 1, 3, 2, 0, 0, 0, 0,
                    0, 1, 1, 1, 3, 2, 2, 0, 0, 0,
                    1, 1, 1, 1, 3, 2, 2, 2, 0, 0,
                    0, 0, 0, 0, 3, 0, 0, 0, 0, 0,
                    0, 3, 3, 3, 3, 3, 3, 3, 0, 0,
                    0, 0, 3, 3, 3, 3, 3, 0, 0, 0,
                    0, 0, 0, 3, 3, 3, 0, 0, 0, 0,
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1
                )
            ),

            // 11. 🐱 Cute Kitty
            PixelArtModel(
                id = 11,
                title = "Cute Kitty 🐱",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#FB8C00", "#F48FB1", "#212121"),
                matrix = listOf(
                    0, 1, 0, 0, 0, 0, 0, 1, 0, 0,
                    0, 1, 2, 0, 0, 0, 2, 1, 0, 0,
                    0, 1, 1, 1, 1, 1, 1, 1, 0, 0,
                    0, 1, 3, 1, 1, 1, 3, 1, 0, 0,
                    0, 1, 1, 1, 2, 1, 1, 1, 0, 0,
                    0, 0, 1, 1, 1, 1, 1, 0, 0, 0,
                    0, 1, 1, 1, 1, 1, 1, 1, 0, 1,
                    0, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    0, 1, 1, 1, 1, 1, 1, 1, 0, 1,
                    0, 1, 0, 1, 0, 1, 0, 1, 0, 0
                )
            ),

            // 12. 🚀 Rocket Ship
            PixelArtModel(
                id = 12,
                title = "Rocket Ship 🚀",
                gridSize = 10,
                paletteColorsHex = listOf("#FFFFFF", "#E53935", "#00E5FF", "#FFD600", "#CFD8DC"),
                matrix = listOf(
                    0, 0, 0, 0, 1, 1, 0, 0, 0, 0,
                    0, 0, 0, 1, 4, 4, 1, 0, 0, 0,
                    0, 0, 0, 4, 2, 2, 4, 0, 0, 0,
                    0, 0, 0, 4, 2, 2, 4, 0, 0, 0,
                    0, 0, 1, 4, 4, 4, 4, 1, 0, 0,
                    0, 1, 1, 4, 4, 4, 4, 1, 1, 0,
                    1, 1, 0, 4, 4, 4, 4, 0, 1, 1,
                    1, 0, 0, 1, 1, 1, 1, 0, 0, 1,
                    0, 0, 0, 3, 3, 3, 3, 0, 0, 0,
                    0, 0, 0, 0, 3, 3, 0, 0, 0, 0
                )
            )
        )
    }
}