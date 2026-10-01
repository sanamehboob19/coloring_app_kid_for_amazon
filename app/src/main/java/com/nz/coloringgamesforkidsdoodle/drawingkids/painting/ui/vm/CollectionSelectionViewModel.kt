package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.ui.vm


import androidx.lifecycle.ViewModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.R
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.CollectionModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class CollectionSelectionViewModel @Inject constructor() : ViewModel() {

    private val _collections = MutableStateFlow<List<CollectionModel>>(emptyList())
    val collections: StateFlow<List<CollectionModel>> = _collections.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        val list = listOf(
            CollectionModel(
                id = "ANIMALS",
                title = "Animals",
                previewRes = R.drawable.a1,
                spineColorRes = R.color.paint_pink,
                categoryKey = "animals"
            ),
            CollectionModel(
                id = "ADVANCED",
                title = "Advanced Art",
                previewRes = R.drawable.ad1,
                spineColorRes = R.color.paint_purple,
                isNew = true,
                categoryKey = "advanced"
            ),
            CollectionModel(
                id = "MUSIC",
                title = "Fruits & Fun",
                previewRes = R.drawable.fruit1,
                spineColorRes = R.color.paint_blue,
                categoryKey = "music"
            ),
            CollectionModel(
                id = "VEHICLES",
                title = "Vehicles & Sky",
                previewRes = R.drawable.sp2,
                spineColorRes = R.color.paint_orange,
                isNew = true,
                categoryKey = "vehicles"
            )
        )
        _collections.value = list
    }
}