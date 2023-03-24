package com.example.weatherapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") api_key: String
    ): Call<WeatherResponseModel>

    fun getCityWeather(
        @Query("q") cityName: String,
        @Query("APP_ID") api_key: String
    ): Call<WeatherResponseModel>
}