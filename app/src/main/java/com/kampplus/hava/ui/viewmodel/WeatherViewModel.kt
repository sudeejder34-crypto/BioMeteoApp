package com.kampplus.hava.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.data.model.BioclimaticInsights
import com.kampplus.hava.data.model.CityLocation
import com.kampplus.hava.data.model.WeatherDetails
import com.kampplus.hava.data.repository.WeatherRepository
import com.kampplus.hava.ui.state.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MetropolisWeather(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val description: String,
    val weatherCode: Int,
    val location: CityLocation
)

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    private val _state = MutableStateFlow<LoadState>(LoadState.Loading)
    val state = _state.asStateFlow()

    private val _metropolisesState = MutableStateFlow<List<MetropolisWeather>>(emptyList())
    val metropolisesState = _metropolisesState.asStateFlow()

    private var currentSelectedCity = CityLocation("İstanbul", "Türkiye", 41.0082, 28.9784)

    init {
        loadDefaultCity()
        loadMetropolises()
    }

    private fun loadDefaultCity() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            try {
                val weather = repository.fetchWeather(
                    currentSelectedCity.latitude,
                    currentSelectedCity.longitude,
                    currentSelectedCity.name,
                    currentSelectedCity.country
                )
                _state.value = LoadState.Success(currentWeather = weather)
            } catch (e: Exception) {
                _state.value = LoadState.Error(e.localizedMessage ?: "Bağlantı hatası oluştu.")
            }
        }
    }

    private fun loadMetropolises() {
        viewModelScope.launch {
            try {
                val list = listOf(
                    CityLocation("London", "UK", 51.5074, -0.1278),
                    CityLocation("Tokyo", "Japan", 35.6762, 139.6503),
                    CityLocation("Berlin", "Germany", 52.5200, 13.4050),
                    CityLocation("New York", "USA", 40.7128, -74.0060)
                )
                val results = mutableListOf<MetropolisWeather>()
                for (city in list) {
                    try {
                        val w = repository.fetchWeather(city.latitude, city.longitude, city.name, city.country)
                        results.add(MetropolisWeather(city.name, city.country, w.temperature, w.description, w.weatherCode, city))
                    } catch (_: Exception) {}
                }
                _metropolisesState.value = results
            } catch (_: Exception) {}
        }
    }

    fun searchCity(query: String) {
        val currentState = _state.value
        if (currentState is LoadState.Success) {
            _state.value = currentState.copy(searchQuery = query)
        } else if (currentState is LoadState.Empty || currentState is LoadState.Error) {
            _state.value = LoadState.Success(
                currentWeather = WeatherDetails(
                    cityName = "İstanbul",
                    country = "Türkiye",
                    temperature = 20.0,
                    humidity = 50,
                    pressure = 1013.0,
                    windSpeed = 10.0,
                    weatherCode = 0,
                    description = "Güneşli",
                    canGoOutside = true,
                    insights = BioclimaticInsights("", "", ""),
                    humanScore = 85,
                    agriculturalScore = 80,
                    animalScore = 82,
                    coachingTips = listOf("Atmosferik basınç ideal, odaklanma için uygun.")
                ),
                searchResults = emptyList(),
                searchQuery = query
            )
        }

        if (query.isBlank()) {
            val stateVal = _state.value
            if (stateVal is LoadState.Success) {
                _state.value = stateVal.copy(searchResults = emptyList())
            }
            return
        }

        viewModelScope.launch {
            try {
                val results = repository.searchCities(query.trim())
                val stateVal = _state.value
                if (stateVal is LoadState.Success) {
                    _state.value = stateVal.copy(searchResults = results)
                }
            } catch (_: Exception) {
                val stateVal = _state.value
                if (stateVal is LoadState.Success) {
                    _state.value = stateVal.copy(searchResults = emptyList())
                }
            }
        }
    }

    fun selectCity(city: CityLocation, onNavigate: () -> Unit) {
        currentSelectedCity = city
        viewModelScope.launch {
            _state.value = LoadState.Loading
            try {
                val weather = repository.fetchWeather(
                    city.latitude,
                    city.longitude,
                    city.name,
                    city.country
                )
                _state.value = LoadState.Success(
                    currentWeather = weather,
                    searchResults = emptyList(),
                    searchQuery = ""
                )
                onNavigate()
            } catch (e: Exception) {
                _state.value = LoadState.Error(e.localizedMessage ?: "Şehir verisi alınamadı.")
            }
        }
    }

    fun refreshCurrentWeather() {
        loadDefaultCity()
        loadMetropolises()
    }
}
