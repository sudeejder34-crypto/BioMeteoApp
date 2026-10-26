package com.kampplus.hava.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimulationScreen() {
    var temp by remember { mutableStateOf(24f) }
    var humidity by remember { mutableStateOf(50f) }

    // Dynamic calculations based on sliders
    val humanScore = (100.0 - Math.abs(temp - 22.0) * 3.0).coerceIn(20.0, 98.0).toInt()
    val agriScore = (100 - if (humidity > 85) 40 else 15).coerceIn(30, 95)
    val thi = 0.8 * temp + (humidity / 100.0) * (temp - 14.4) + 46.4
    val animalScore = (100.0 - Math.max(0.0, (thi - 72.0) * 4.0)).coerceIn(40.0, 98.0).toInt()

    val gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF311042))

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
                text = "Simülasyon Laboratuvarı",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "\"What-If\" Bioklimatik Senaryo Testi",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    // Sliders Card
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
                                text = "🎛️ Parametre Kontrol Paneli",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Sıcaklık: ${temp.toInt()}°C", color = Color.White, fontWeight = FontWeight.Medium)
                            Slider(
                                value = temp,
                                onValueChange = { temp = it },
                                valueRange = -10f..45f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF4F46E5), activeTrackColor = Color(0xFF4F46E5))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "Bağıl Nem: %${humidity.toInt()}", color = Color.White, fontWeight = FontWeight.Medium)
                            Slider(
                                value = humidity,
                                onValueChange = { humidity = it },
                                valueRange = 10f..100f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF4F46E5), activeTrackColor = Color(0xFF4F46E5))
                            )
                        }
                    }
                }

                item {
                    // Results Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "📊 Simülasyon Risk Skorları",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            SimScoreRow("İnsan & Bilişsel Performans", humanScore)
                            Spacer(modifier = Modifier.height(12.dp))
                            SimScoreRow("Tarım & Fungal Risk", agriScore)
                            Spacer(modifier = Modifier.height(12.dp))
                            SimScoreRow("Hayvancılık & THI Stresi", animalScore)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimScoreRow(title: String, score: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFF0F172A))
            Text(text = "%$score", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF4F46E5))
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                score >= 75 -> Color(0xFF10B981)
                score >= 50 -> Color(0xFF4F46E5)
                else -> Color(0xFFEF4444)
            },
            trackColor = Color.LightGray.copy(alpha = 0.4f)
        )
    }
}
