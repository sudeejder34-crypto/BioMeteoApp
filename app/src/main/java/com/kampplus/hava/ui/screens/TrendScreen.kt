package com.kampplus.hava.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.ui.state.LoadState

data class DayTrend(
    val dayName: String,
    val temp: Int,
    val condition: String,
    val emoji: String,
    val score: Int
)

@Composable
fun TrendScreen(state: LoadState) {
    val context = LocalContext.current
    val gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF311042))

    val mockTrends = listOf(
        DayTrend("Pazartesi", 24, "Güneşli", "☀️", 88),
        DayTrend("Salı", 22, "Parçalı Bulutlu", "⛅", 82),
        DayTrend("Çarşamba", 18, "Yağmurlu", "🌧️", 64),
        DayTrend("Perşembe", 19, "Bulutlu", "☁️", 75),
        DayTrend("Cuma", 23, "Güneşli", "☀️", 90),
        DayTrend("Cumartesi", 26, "Açık", "☀️", 95),
        DayTrend("Pazar", 25, "Parçalı Bulutlu", "⛅", 88)
    )

    fun exportCsv() {
        val success = state as? LoadState.Success
        val cityName = success?.currentWeather?.cityName ?: "İstanbul"
        val temp = success?.currentWeather?.temperature ?: 24.0
        val hum = success?.currentWeather?.humidity ?: 50
        val pressure = success?.currentWeather?.pressure ?: 1013.0

        val csvBuilder = StringBuilder().apply {
            appendLine("Tarih,Sehir,Sicaklik_C,Nem_Yuzde,Basinc_hPa,Bilissel_Skor")
            appendLine("2025-05-18,$cityName,$temp,$hum,$pressure,88")
            appendLine("2025-05-19,$cityName,22.0,52,1014.0,82")
            appendLine("2025-05-20,$cityName,18.0,80,1005.0,64")
            appendLine("2025-05-21,$cityName,19.0,75,1008.0,75")
            appendLine("2025-05-22,$cityName,23.0,50,1013.0,90")
            appendLine("2025-05-23,$cityName,26.0,42,1016.0,95")
            appendLine("2025-05-24,$cityName,25.0,46,1015.0,88")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Bioclimatic Intelligence - Veri Seti (CSV)")
            putExtra(Intent.EXTRA_TEXT, csvBuilder.toString())
        }
        context.startActivity(Intent.createChooser(sendIntent, "CSV Veri Setini Dışa Aktar"))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "7 Günlük Trend Analizi",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "Bioklimatik Konfor ve Veri Madenciliği Raporu",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CSV Export Button with Material 3 TableChart Icon
            Button(
                onClick = { exportCsv() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = "Veri Seti İndir",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Veri Seti İndir (CSV)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "📈 Haftalık Atmosferik Projeksiyon",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Önümüzdeki günlerde beklenen basınç ve sıcaklık salınımlarının analizi ve dışa aktarılabilir ham veri setleri.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                items(mockTrends) { trend ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = trend.emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = trend.dayName,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    )
                                    Text(
                                        text = trend.condition,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${trend.temp}°C",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Skor: %${trend.score}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
