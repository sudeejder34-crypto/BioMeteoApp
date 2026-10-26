package com.kampplus.hava.data.repository

import android.util.Log
import com.kampplus.hava.data.model.BioclimaticInsights
import com.kampplus.hava.data.model.CityLocation
import com.kampplus.hava.data.model.WeatherDetails
import com.kampplus.hava.data.remote.OpenMeteoApi

class WeatherRepository {
    private val api = OpenMeteoApi.instance

    suspend fun searchCities(query: String): List<CityLocation> {
        return try {
            val response = api.searchCities(name = query)
            response.results?.mapNotNull { item ->
                val cityName = item.name
                val lat = item.latitude
                val lon = item.longitude
                if (cityName != null && lat != null && lon != null) {
                    CityLocation(
                        name = cityName,
                        country = item.country ?: "Dünya",
                        latitude = lat,
                        longitude = lon
                    )
                } else null
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Search error", e)
            emptyList()
        }
    }

    suspend fun fetchWeather(lat: Double, lon: Double, cityName: String, country: String): WeatherDetails {
        val response = api.getWeather(lat = lat, lon = lon)
        val current = response.current ?: throw Exception("Hava verisi bulunamadı.")

        val temp = current.temperature_2m ?: 20.0
        val humidity = current.relative_humidity_2m ?: 50
        val pressure = current.surface_pressure ?: 1013.0
        val windSpeed = current.wind_speed_10m ?: 10.0
        val code = current.weather_code ?: 0

        val desc = getWeatherDescription(code)
        val canGoOutside = code !in 51..67 && code !in 80..86 && code !in 95..99 && windSpeed <= 30.0

        val medical = when {
            pressure < 1005.0 -> "Düşük Basınç: Bilişsel adaptasyon baskısı yüksek, hafif yorgunluk görülebilir."
            humidity > 80 && temp > 28 -> "Yüksek Nem & Sıcaklık: Zihinsel yorgunluk ve dikkat dağınıklığı riski artar."
            else -> "Atmosferik Denge Optimum: Nörolojik odaklanma ve bilişsel verimlilik en üst düzeyde."
        }

        val agricultural = when {
            humidity > 85 && temp in 15.0..25.0 -> "Yüksek Fungal Risk: Nem dengesizliği bitki yapraklarında mantari hastalıklara yol açabilir."
            temp < 2.0 -> "Don Stresi Uyarısı: Düşük sıcaklık bitki metabolizmasında osmotik stres oluşturabilir."
            else -> "Agrometeorolojik Durum Stabil: Bitki fizyolojik stresi düşük, tarımsal faaliyetler uygun."
        }

        val thi = 0.8 * temp + (humidity / 100.0) * (temp - 14.4) + 46.4
        val animal = when {
            thi > 79.0 -> "Kritik Isı Stresi (THI > 79): Çiftlik hayvanlarında acil gölgelendirme ve soğutma şart."
            thi in 72.0..79.0 -> "Hafif Isı Stresi: Süt veriminde düşüş riski, tedbirler gözden geçirilmeli."
            else -> "Termal Konfor Bölgesi: Hayvancılık açısından ısı stresi bulunmuyor."
        }

        // Calculate visual scores (0 - 100)
        val humanScore = (100.0 - Math.abs(temp - 22.0) * 3.0 - Math.abs(pressure - 1013.0) * 0.4).coerceIn(25.0, 98.0).toInt()
        val agriculturalScore = (100 - if (humidity > 85) 35 else 10).coerceIn(30, 95)
        val animalScore = (100.0 - Math.max(0.0, (thi - 72.0) * 4.0)).coerceIn(40.0, 98.0).toInt()

        // Generate Bio-Coach daily tips
        val tips = mutableListOf<String>()
        if (pressure < 1005.0) {
            tips.add("Barometrik basınç düşük: Zihinsel yorgunluğa karşı bugün mola sürelerini 10 dakika uzatın.")
        } else {
            tips.add("Atmosferik basınç ideal: Odaklanma gerektiren analitik işler için sabah saatlerini değerlendirin.")
        }
        if (humidity > 80) {
            tips.add("Yüksek nem oranı: Ev ve sera bitkilerinin sulama miktarını yarı yarıya azaltın.")
        } else {
            tips.add("Nem seviyesi dengeli: Tarımsal sulama ve açık hava faaliyetleri için verimli bir gün.")
        }
        if (temp > 28.0) {
            tips.add("Sıcaklık yüksek: Günün en sıcak saatlerinde (12:00 - 15:00) doğrudan güneş altında kalmaktan kaçının.")
        } else if (temp < 5.0) {
            tips.add("Soğuk hava uyarısı: Açık alan bitkilerini dona karşı koruyun, dışarıda katmanlı giyinin.")
        } else {
            tips.add("Termal konfor alanı: Yürüyüş ve dış mekan aktiviteleri için mükemmel bir sıcaklık aralığı.")
        }
        tips.add("Bioklimatik İpucu: Günlük su tüketiminizi hava nem oranına göre 2.5 litreye sabitleyin.")

        return WeatherDetails(
            cityName = cityName,
            country = country,
            temperature = temp,
            humidity = humidity,
            pressure = pressure,
            windSpeed = windSpeed,
            weatherCode = code,
            description = desc,
            canGoOutside = canGoOutside,
            insights = BioclimaticInsights(
                medicalInsight = medical,
                agriculturalInsight = agricultural,
                animalInsight = animal
            ),
            humanScore = humanScore,
            agriculturalScore = agriculturalScore,
            animalScore = animalScore,
            coachingTips = tips
        )
    }

    private fun getWeatherDescription(code: Int): String {
        return when (code) {
            0 -> "Güneşli ☀️"
            1, 2 -> "Parçalı Bulutlu ⛅"
            3 -> "Bulutlu ☁️"
            45, 48 -> "Sisli 🌫️"
            51, 53, 55, 56, 57 -> "Çisintili Yağmur 🌦️"
            61, 63, 65, 66, 67 -> "Yağmurlu 🌧️"
            71, 73, 75, 77 -> "Karlı ❄️"
            80, 81, 82 -> "Sağanak Yağış 🌦️"
            85, 86 -> "Kar Sağanaklı 🌨️"
            95, 96, 99 -> "Gök Gürültülü Fırtına ⛈️"
            else -> "Mevsim Normali 🌡️"
        }
    }
}
