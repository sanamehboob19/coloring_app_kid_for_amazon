package com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.util

import android.graphics.Color
import com.nextguidance.coloringgamesforkidsdoodle.drawingkids.painting.data.model.ColorItem


object ColorPaletteProvider {

    /**
     * Curated list of vibrant colors for kids.
     * You can easily add more colors to this list anytime.
     */
    val allColors: List<ColorItem> by lazy {
        listOf(

            // ─────────────────────────────
            // BRIGHT REDS & PINKS
            // ─────────────────────────────
            ColorItem(Color.parseColor("#FF1744"), "Bright Red"),
            ColorItem(Color.parseColor("#D50000"), "Ruby Red"),
            ColorItem(Color.parseColor("#FF5252"), "Coral Red"),
            ColorItem(Color.parseColor("#FF3D00"), "Flame Red"),
            ColorItem(Color.parseColor("#F50057"), "Fuchsia"),
            ColorItem(Color.parseColor("#E91E63"), "Berry Pink"),
            ColorItem(Color.parseColor("#FF4081"), "Hot Pink"),
            ColorItem(Color.parseColor("#FF80AB"), "Bubblegum"),
            ColorItem(Color.parseColor("#C51162"), "Deep Rose"),
            ColorItem(Color.parseColor("#FF6090"), "Rose Pink"),
            ColorItem(Color.parseColor("#EC407A"), "Rose Berry"),
            ColorItem(Color.parseColor("#AD1457"), "Raspberry"),
            ColorItem(Color.parseColor("#F06292"), "Soft Pink"),
            ColorItem(Color.parseColor("#FF1493"), "Deep Pink"),
            ColorItem(Color.parseColor("#FF69B4"), "Candy Pink"),
            ColorItem(Color.parseColor("#D81B60"), "Magenta Rose"),

            // ─────────────────────────────
            // ORANGE & PEACH
            // ─────────────────────────────
            ColorItem(Color.parseColor("#FF6D00"), "Tangerine"),
            ColorItem(Color.parseColor("#FF9100"), "Amber Orange"),
            ColorItem(Color.parseColor("#FF7043"), "Peach"),
            ColorItem(Color.parseColor("#FF6E40"), "Flamingo"),
            ColorItem(Color.parseColor("#FF8A65"), "Apricot"),
            ColorItem(Color.parseColor("#FF5722"), "Vivid Orange"),
            ColorItem(Color.parseColor("#F4511E"), "Burnt Orange"),
            ColorItem(Color.parseColor("#EF6C00"), "Pumpkin"),
            ColorItem(Color.parseColor("#FB8C00"), "Orange Gold"),
            ColorItem(Color.parseColor("#FFA726"), "Mango"),
            ColorItem(Color.parseColor("#FFB74D"), "Peach Orange"),
            ColorItem(Color.parseColor("#FFCC80"), "Soft Apricot"),
            ColorItem(Color.parseColor("#FFAB40"), "Golden Orange"),
            ColorItem(Color.parseColor("#FF9800"), "Bright Orange"),
            ColorItem(Color.parseColor("#E65100"), "Deep Orange"),
            ColorItem(Color.parseColor("#FFCA28"), "Mango Yellow"),

            // ─────────────────────────────
            // YELLOWS & GOLD
            // ─────────────────────────────
            ColorItem(Color.parseColor("#FFD600"), "Sunny Yellow"),
            ColorItem(Color.parseColor("#FFEA00"), "Canary"),
            ColorItem(Color.parseColor("#FFF176"), "Pastel Yellow"),
            ColorItem(Color.parseColor("#FFD54F"), "Buttercup"),
            ColorItem(Color.parseColor("#FFAB00"), "Marigold"),
            ColorItem(Color.parseColor("#FDD835"), "Lemon Gold"),
            ColorItem(Color.parseColor("#FBC02D"), "Golden Yellow"),
            ColorItem(Color.parseColor("#F9A825"), "Honey Gold"),
            ColorItem(Color.parseColor("#FFF59D"), "Soft Lemon"),
            ColorItem(Color.parseColor("#FFF9C4"), "Cream Yellow"),
            ColorItem(Color.parseColor("#FFEE58"), "Lemon"),
            ColorItem(Color.parseColor("#FFC107"), "Amber"),
            ColorItem(Color.parseColor("#FFE082"), "Golden Cream"),
            ColorItem(Color.parseColor("#FFD740"), "Bright Gold"),
            ColorItem(Color.parseColor("#FFB300"), "Sun Gold"),
            ColorItem(Color.parseColor("#F57F17"), "Deep Gold"),

            // ─────────────────────────────
            // GREENS
            // ─────────────────────────────
            ColorItem(Color.parseColor("#76FF03"), "Electric Lime"),
            ColorItem(Color.parseColor("#00E676"), "Bright Green"),
            ColorItem(Color.parseColor("#64DD17"), "Apple Green"),
            ColorItem(Color.parseColor("#00C853"), "Emerald"),
            ColorItem(Color.parseColor("#AEEA00"), "Chartreuse"),
            ColorItem(Color.parseColor("#1DE9B6"), "Fresh Mint"),
            ColorItem(Color.parseColor("#00BFA5"), "Caribbean Sea"),
            ColorItem(Color.parseColor("#81C784"), "Sage Green"),
            ColorItem(Color.parseColor("#AED581"), "Pistachio"),
            ColorItem(Color.parseColor("#43A047"), "Forest Green"),
            ColorItem(Color.parseColor("#2E7D32"), "Deep Green"),
            ColorItem(Color.parseColor("#66BB6A"), "Meadow Green"),
            ColorItem(Color.parseColor("#9CCC65"), "Kiwi Green"),
            ColorItem(Color.parseColor("#8BC34A"), "Lime Green"),
            ColorItem(Color.parseColor("#CDDC39"), "Lime Yellow"),
            ColorItem(Color.parseColor("#00A152"), "Jade Green"),

            // ─────────────────────────────
            // CYAN & TURQUOISE
            // ─────────────────────────────
            ColorItem(Color.parseColor("#00E5FF"), "Aqua Cyan"),
            ColorItem(Color.parseColor("#00B8D4"), "Turquoise"),
            ColorItem(Color.parseColor("#4DD0E1"), "Ice Blue"),
            ColorItem(Color.parseColor("#80D8FF"), "Baby Blue"),
            ColorItem(Color.parseColor("#00BFA5"), "Caribbean Teal"),
            ColorItem(Color.parseColor("#26C6DA"), "Ocean Cyan"),
            ColorItem(Color.parseColor("#00ACC1"), "Deep Cyan"),
            ColorItem(Color.parseColor("#00838F"), "Dark Teal"),
            ColorItem(Color.parseColor("#80CBC4"), "Seafoam"),
            ColorItem(Color.parseColor("#4DB6AC"), "Aqua Green"),
            ColorItem(Color.parseColor("#009688"), "Teal"),
            ColorItem(Color.parseColor("#B2EBF2"), "Ice Mint"),
            ColorItem(Color.parseColor("#84FFFF"), "Electric Aqua"),
            ColorItem(Color.parseColor("#18FFFF"), "Neon Aqua"),
            ColorItem(Color.parseColor("#00BCD4"), "Bright Teal"),
            ColorItem(Color.parseColor("#006064"), "Deep Ocean"),

            // ─────────────────────────────
            // BLUES
            // ─────────────────────────────
            ColorItem(Color.parseColor("#2979FF"), "Royal Blue"),
            ColorItem(Color.parseColor("#304FFE"), "Cobalt Blue"),
            ColorItem(Color.parseColor("#3D5AFE"), "Sapphire"),
            ColorItem(Color.parseColor("#0091EA"), "Cerulean"),
            ColorItem(Color.parseColor("#4FC3F7"), "Glacier"),
            ColorItem(Color.parseColor("#5C6BC0"), "Denim"),
            ColorItem(Color.parseColor("#42A5F5"), "Sky Blue"),
            ColorItem(Color.parseColor("#1E88E5"), "Ocean Blue"),
            ColorItem(Color.parseColor("#1565C0"), "Deep Blue"),
            ColorItem(Color.parseColor("#0D47A1"), "Navy Blue"),
            ColorItem(Color.parseColor("#64B5F6"), "Soft Blue"),
            ColorItem(Color.parseColor("#90CAF9"), "Cloud Blue"),
            ColorItem(Color.parseColor("#82B1FF"), "Electric Blue"),
            ColorItem(Color.parseColor("#448AFF"), "Bright Sapphire"),
            ColorItem(Color.parseColor("#536DFE"), "Indigo Blue"),
            ColorItem(Color.parseColor("#03A9F4"), "Bright Sky"),

            // ─────────────────────────────
            // PURPLE & VIOLET
            // ─────────────────────────────
            ColorItem(Color.parseColor("#AA00FF"), "Neon Violet"),
            ColorItem(Color.parseColor("#E040FB"), "Neon Purple"),
            ColorItem(Color.parseColor("#651FFF"), "Deep Indigo"),
            ColorItem(Color.parseColor("#7C4DFF"), "Lavender"),
            ColorItem(Color.parseColor("#9C27B0"), "Grape"),
            ColorItem(Color.parseColor("#BA68C8"), "Orchid"),
            ColorItem(Color.parseColor("#9575CD"), "Lilac"),
            ColorItem(Color.parseColor("#6A1B9A"), "Deep Purple"),
            ColorItem(Color.parseColor("#8E24AA"), "Royal Purple"),
            ColorItem(Color.parseColor("#AB47BC"), "Violet"),
            ColorItem(Color.parseColor("#CE93D8"), "Soft Lavender"),
            ColorItem(Color.parseColor("#E1BEE7"), "Light Lilac"),
            ColorItem(Color.parseColor("#B388FF"), "Purple Glow"),
            ColorItem(Color.parseColor("#D500F9"), "Electric Purple"),
            ColorItem(Color.parseColor("#6200EA"), "Ultra Violet"),
            ColorItem(Color.parseColor("#512DA8"), "Deep Violet"),

            // ─────────────────────────────
            // BROWN & EARTH TONES
            // ─────────────────────────────
            ColorItem(Color.parseColor("#8D6E63"), "Cocoa Brown"),
            ColorItem(Color.parseColor("#5D4037"), "Dark Chocolate"),
            ColorItem(Color.parseColor("#A1887F"), "Caramel"),
            ColorItem(Color.parseColor("#BCAAA4"), "Sand"),
            ColorItem(Color.parseColor("#795548"), "Mocha"),
            ColorItem(Color.parseColor("#6D4C41"), "Coffee Brown"),
            ColorItem(Color.parseColor("#4E342E"), "Espresso"),
            ColorItem(Color.parseColor("#3E2723"), "Dark Cocoa"),
            ColorItem(Color.parseColor("#A0522D"), "Sienna"),
            ColorItem(Color.parseColor("#D2691E"), "Chocolate"),
            ColorItem(Color.parseColor("#C19A6B"), "Tan"),
            ColorItem(Color.parseColor("#DEB887"), "Warm Sand"),
            ColorItem(Color.parseColor("#F4A460"), "Sandy Brown"),
            ColorItem(Color.parseColor("#BC8F8F"), "Dusty Rose"),
            ColorItem(Color.parseColor("#CD853F"), "Clay"),
            ColorItem(Color.parseColor("#8B4513"), "Saddle Brown"),

            // ─────────────────────────────
            // SOFT / NEUTRAL / DARK
            // ─────────────────────────────
            ColorItem(Color.parseColor("#78909C"), "Slate Gray"),
            ColorItem(Color.parseColor("#607D8B"), "Steel Gray"),
            ColorItem(Color.parseColor("#455A64"), "Charcoal"),
            ColorItem(Color.parseColor("#37474F"), "Dark Slate"),
            ColorItem(Color.parseColor("#212121"), "Midnight Black"),
            ColorItem(Color.parseColor("#424242"), "Graphite"),
            ColorItem(Color.parseColor("#757575"), "Classic Gray"),
            ColorItem(Color.parseColor("#9E9E9E"), "Silver Gray"),
            ColorItem(Color.parseColor("#BDBDBD"), "Light Gray"),
            ColorItem(Color.parseColor("#EEEEEE"), "Cloud Gray"),
            ColorItem(Color.parseColor("#F5F5F5"), "Soft White"),
            ColorItem(Color.parseColor("#FAFAFA"), "Snow White"),
            ColorItem(Color.parseColor("#CFD8DC"), "Blue Gray"),
            ColorItem(Color.parseColor("#B0BEC5"), "Cool Gray"),
            ColorItem(Color.parseColor("#90A4AE"), "Storm Gray"),
            ColorItem(Color.parseColor("#263238"), "Night Gray")
        )
    }

    fun getDefaultColor(): ColorItem = allColors.first()
}