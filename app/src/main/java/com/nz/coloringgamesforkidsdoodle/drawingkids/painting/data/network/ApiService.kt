package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.network


import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.data.model.JsonFileModel
import com.nz.coloringgamesforkidsdoodle.drawingkids.painting.util.Constant
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET(Constant.GITHUB_JSON_ENDPOINT)
    suspend fun fetchData(): Response<JsonFileModel>


    //  Dedicated endpoint for Color By Number data
    @GET("CodeCraftX-bySana/my-coloring-game-data/main/color_by_number_data.json")
    suspend fun fetchColorByNumberData(): Response<JsonFileModel>


}

//interface ApiService {
//    @GET("Tatto112/color-by-number-game/main/kids_data.json")
//    suspend fun fetchData(): Response<JsonFileModel>
//
//}
