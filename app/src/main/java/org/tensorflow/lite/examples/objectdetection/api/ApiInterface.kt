package org.tensorflow.lite.examples.objectdetection.api

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.Query
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiInterface {
    @Headers("Content-Type: application/json")
    @GET("/search.json")
    fun getSerpApi(@Query("engine") engine: String, @Query("q") q: String, @Query("location") location: String, @Query("hl") hl: String, @Query("gl") gl: String, @Query("api_key") api_key: String):
            Call<JsonElement>
}