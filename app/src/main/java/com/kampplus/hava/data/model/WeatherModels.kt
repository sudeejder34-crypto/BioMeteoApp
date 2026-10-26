package com.kampplus.hava.data.model

data class GeoResponse(
    val results: List<GeoResult>?
)

data class GeoResult(
    val id: Long?,
    val name: String?,
    val country: String?,
    val latitude: Double?,
    val longitude: Double?
)

data class WeatherResponse(
    val latitude: Double?,
    val longitude: Double?,
    val current: CurrentWeather?
)

data class CurrentWeather(
    val temperature_2m: Double?,
    val relative_humidity_2m: Int?,
    val surface_pressure: Double?,
    val weather_code: Int?,
    val wind_speed_10m: Double?
)

data class CityLocation(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

data class BioclimaticInsights(
    val medicalInsight: String,
    val agriculturalInsight: String,
    val animalInsight: String
)

data class WeatherDetails(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val humidity: Int,
    val pressure: Double,
    val windSpeed: Double,
    val weatherCode: Int,
    val description: String,
    val canGoOutside: Boolean,
    val insights: BioclimaticInsights,
    val humanScore: Int,
    val agriculturalScore: Int,
    val animalScore: Int,
    val coachingTips: List<String>
)
