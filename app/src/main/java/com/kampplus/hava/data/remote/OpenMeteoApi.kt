package com.kampplus.hava.data.remote

import com.kampplus.hava.data.model.GeoResponse
import com.kampplus.hava.data.model.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface OpenMeteoApi {
    @GET
    suspend fun searchCities(
        @Url url: String = "https://geocoding-api.open-meteo.com/v1/search",
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "tr",
        @Query("format") format: String = "json"
    ): GeoResponse

    @GET
    suspend fun getWeather(
        @Url url: String = "https://api.open-meteo.com/v1/forecast",
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,surface_pressure,weather_code,wind_speed_10m",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse

    companion object {
        val instance: OpenMeteoApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenMeteoApi::class.java)
        }
    }
}
