package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.vm


import androidx.lifecycle.ViewModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.enumms.GameMode
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.CategoryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val list = listOf(
            CategoryModel(GameMode.COLORING_BOOK, "Coloring Book 🎨", R.drawable.ic_cat_coloring_book, R.color.kids_primary),
            CategoryModel(GameMode.BLANK_CANVAS, "Blank Canvas ✏️", R.drawable.ic_cat_blank_canvas, R.color.kids_secondary),
            CategoryModel(GameMode.GLOW_PEN, "Glow Magic 🪄", R.drawable.ic_cat_glow_pen, R.color.kids_accent),
            CategoryModel(GameMode.MAGIC_PAINTING, "Magic Paint ✨", R.drawable.ic_cat_magic_color, R.color.paint_purple),
            CategoryModel(GameMode.NUMBER_TRACING, "123 Numbers 🔢", R.drawable.ic_cat_number, R.color.paint_blue),
            CategoryModel(GameMode.ALPHABET_TRACING, "ABC Letters 🔤", R.drawable.ic_cat_abc, R.color.paint_pink),
            CategoryModel(GameMode.PIXEL_ART, "Pixel Blocks 👾", R.drawable.ic_cat_pixel, R.color.paint_orange),
            CategoryModel(GameMode.COLOR_BY_NUMBER, "Number Paint 🔢", R.drawable.ic_cat_color_by_num_book, R.color.kids_secondary)
        )
        _categories.value = list
    }
}