package com.kampplus.hava.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.ui.components.SegmentTab
import com.kampplus.hava.ui.state.LoadState

@Composable
fun DetailScreen(
    state: LoadState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onNavigateToBioCoach: () -> Unit
) {
    var selectedDomain by rememberSaveable { mutableStateOf(0) }

    val weatherCode = (state as? LoadState.Success)?.currentWeather?.weatherCode ?: 0
    val gradientColors = when (weatherCode) {
        0 -> listOf(Color(0xFF1CB5E0), Color(0xFF000851))
        in 1..3, in 45..48 -> listOf(Color(0xFF4CA1AF), Color(0xFF2C3E50))
        in 51..67, in 80..82 -> listOf(Color(0xFF374151), Color(0xFF1F2937), Color(0xFF111827))
        in 71..77, in 85..86 -> listOf(Color(0xFF83a4d4), Color(0xFFb6fbff))
        in 95..99 -> listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF311042))
        else -> listOf(Color(0xFF2980B9), Color(0xFF2C3E50))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "weatherAnimation")
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

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

            // Top Bar with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.clickable(onClick = onBack)
                ) {
                    Text(
                        text = "← Geri",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Text(
                    text = "Meteorolojik & Sektörel Analiz",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (state) {
                is LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                is LoadState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Hata: ${state.message}", color = Color.White)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = onRetry) { Text("Tekrar Dene") }
                        }
                    }
                }
                is LoadState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Veri bulunamadı", color = Color.White.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = onRetry) { Text("Tekrar Dene") }
                        }
                    }
                }
                is LoadState.Success -> {
                    val weather = state.currentWeather
                    val activeScore = when (selectedDomain) {
                        0 -> weather.humanScore
                        1 -> weather.agriculturalScore
                        else -> weather.animalScore
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        item {
                            // Frosted Glass Weather Hero Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.18f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = weather.cityName,
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                            Text(
                                                text = weather.country,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (weather.canGoOutside) Color(0xFF10B981) else Color(0xFFEF4444)
                                        ) {
                                            Text(
                                                text = if (weather.canGoOutside) "🟢 Dış Saha Uygun" else "🔴 Kapalı Alan Şart",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${weather.temperature.toInt()}°C",
                                                style = MaterialTheme.typography.displayLarge.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.White
                                                )
                                            )
                                            Text(
                                                text = weather.description,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                                color = Color.White.copy(alpha = 0.95f)
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .scale(scaleAnim)
                                                .size(72.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = when (weatherCode) {
                                                    0 -> "☀️"
                                                    in 1..3 -> "⛅"
                                                    in 51..67, in 80..82 -> "🌧️"
                                                    in 71..77 -> "❄️"
                                                    in 95..99 -> "⛈️"
                                                    else -> "🌤️"
                                                },
                                                fontSize = 54.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        WeatherMetricItem(label = "Nem", value = "%${weather.humidity}", icon = "💧")
                                        WeatherMetricItem(label = "Basınç", value = "${weather.pressure.toInt()} hPa", icon = "📊")
                                        WeatherMetricItem(label = "Rüzgar", value = "${weather.windSpeed} km/s", icon = "💨")
                                    }
                                }
                            }
                        }

                        item {
                            // Biometeorological Decision Engine Card with Segmented Tabs & Visual Progress Indicator
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🧬 Bioklimatik Karar Destek",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = when {
                                                activeScore >= 75 -> Color(0xFF10B981)
                                                activeScore >= 50 -> Color(0xFFF59E0B)
                                                else -> Color(0xFFEF4444)
                                            }
                                        ) {
                                            Text(
                                                text = "Skor: %$activeScore",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Visual Progress Indicator (Grafiksel Skor Barı)
                                    LinearProgressIndicator(
                                        progress = { activeScore / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = when {
                                            activeScore >= 75 -> Color(0xFF10B981)
                                            activeScore >= 50 -> Color(0xFF4F46E5)
                                            else -> Color(0xFFEF4444)
                                        },
                                        trackColor = Color.LightGray.copy(alpha = 0.4f)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Modern Segmented Tabs
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        SegmentTab(
                                            title = "İnsan & Biliş",
                                            isSelected = selectedDomain == 0,
                                            onClick = { selectedDomain = 0 },
                                            modifier = Modifier.weight(1f),
                                            accentColor = Color(0xFF4F46E5)
                                        )
                                        SegmentTab(
                                            title = "Tarım",
                                            isSelected = selectedDomain == 1,
                                            onClick = { selectedDomain = 1 },
                                            modifier = Modifier.weight(1f),
                                            accentColor = Color(0xFF4F46E5)
                                        )
                                        SegmentTab(
                                            title = "Hayvancılık",
                                            isSelected = selectedDomain == 2,
                                            onClick = { selectedDomain = 2 },
                                            modifier = Modifier.weight(1f),
                                            accentColor = Color(0xFF4F46E5)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Airy container for insight text
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFFF8FAFC),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(modifier = Modifier.padding(18.dp)) {
                                            val insightText = when (selectedDomain) {
                                                0 -> weather.insights.medicalInsight
                                                1 -> weather.insights.agriculturalInsight
                                                else -> weather.insights.animalInsight
                                            }
                                            Text(
                                                text = insightText,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Medium,
                                                    lineHeight = 24.sp,
                                                    color = Color(0xFF0F172A)
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Navigation button to Bio-Coach screen
                                    Button(
                                        onClick = onNavigateToBioCoach,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                                    ) {
                                        Text(
                                            text = "💡 Bio-Coach & Günlük Tüyolar",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherMetricItem(label: String, value: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
    }
}
