package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model



import com.google.gson.annotations.SerializedName

data class JsonFileModel(
    @SerializedName("dataVersion") val dataVersion: Int,
    @SerializedName("images") val images: List<JsonImageItem>
)

data class JsonImageItem(
    @SerializedName("category") val category: String,
    @SerializedName("total") val total: Int,
    @SerializedName("premium") val premium: List<Int>?
)




//@Keep
//data class JsonFileModel(
//    val dataVersion: Int = 0,
//    val images: List<ImageItem> = emptyList()
//) {
//    @Keep
//    data class ImageItem(
//        val category: String = "",
//        val label: String = "",
//        val total: Int = 0,
//        val premium: List<Int> = emptyList()
//    )
//}