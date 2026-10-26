package com.kampplus.hava.ui.state

import com.kampplus.hava.data.model.CityLocation
import com.kampplus.hava.data.model.WeatherDetails

sealed interface LoadState {
    data object Loading : LoadState
    data class Success(
        val currentWeather: WeatherDetails,
        val searchResults: List<CityLocation> = emptyList(),
        val searchQuery: String = ""
    ) : LoadState
    data object Empty : LoadState
    data class Error(val message: String) : LoadState
}
