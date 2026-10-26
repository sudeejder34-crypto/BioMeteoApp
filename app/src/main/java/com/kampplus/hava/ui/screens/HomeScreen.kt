package com.kampplus.hava.ui.screens

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.data.model.CityLocation
import com.kampplus.hava.ui.state.LoadState
import com.kampplus.hava.ui.viewmodel.MetropolisWeather

@Composable
fun HomeScreen(
    state: LoadState,
    metropolises: List<MetropolisWeather>,
    onSearch: (String) -> Unit,
    onSelectCity: (CityLocation) -> Unit,
    onQuickSelect: (CityLocation) -> Unit
) {
    val context = LocalContext.current

    val weatherCode = (state as? LoadState.Success)?.currentWeather?.weatherCode ?: 0
    // Guaranteed high-contrast rich deep gradients so white text is always 100% visible & striking
    val gradientColors = when (weatherCode) {
        0 -> listOf(Color(0xFF0284C7), Color(0xFF0F172A))
        in 1..3, in 45..48 -> listOf(Color(0xFF334155), Color(0xFF0F172A))
        in 51..67, in 80..82 -> listOf(Color(0xFF1E293B), Color(0xFF090D16))
        in 71..77, in 85..86 -> listOf(Color(0xFF475569), Color(0xFF1E293B))
        in 95..99 -> listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
        else -> listOf(Color(0xFF0284C7), Color(0xFF0F172A))
    }

    val searchQueryState = rememberSaveable { mutableStateOf("") }
    val currentQuery = when (state) {
        is LoadState.Success -> state.searchQuery
        else -> searchQueryState.value
    }

    // Pulse animation for IoT Sensor Active badge
    val infiniteTransition = rememberInfiniteTransition(label = "sensorPulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
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

            // Premium Header with Share Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bioclimatic Intelligence",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .alpha(alphaAnim)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF34D399))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Saha Sensörü Aktif",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF34D399)
                        )
                    }
                }

                // Share Button with Material 3 Share Icon
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.clickable {
                        val successState = state as? LoadState.Success
                        val reportText = if (successState != null) {
                            "🌍 Bioclimatic Intelligence Raporu\n" +
                                    "📍 Şehir: ${successState.currentWeather.cityName}, ${successState.currentWeather.country}\n" +
                                    "🌡️ Sıcaklık: ${successState.currentWeather.temperature}°C\n" +
                                    "💧 Nem: %${successState.currentWeather.humidity}\n" +
                                    "📊 Basınç: ${successState.currentWeather.pressure.toInt()} hPa\n" +
                                    "🧬 Sektörel Durum: ${if (successState.currentWeather.canGoOutside) "Dış Saha Uygun" else "Kapalı Alan Şart"}"
                        } else {
                            "Bioclimatic Intelligence Karar Destek Sistemi Aktif."
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Bioclimatic Intelligence Raporu")
                            putExtra(Intent.EXTRA_TEXT, reportText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Yönetici Raporunu Paylaş"))
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Paylaş",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Paylaş",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar (Vibrant & Crisp Text)
            OutlinedTextField(
                value = currentQuery,
                onValueChange = {
                    searchQueryState.value = it
                    onSearch(it)
                },
                placeholder = { Text("Dünya genelinde şehir ara (Örn: Berlin, Tokyo, Londra)...", color = Color.White.copy(alpha = 0.85f)) },
                leadingIcon = { Text("🔍", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.25f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Results or Full Dashboard
            when (state) {
                is LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                is LoadState.Success -> {
                    if (state.searchResults.isNotEmpty()) {
                        Text(
                            text = "Arama Sonuçları",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Start).padding(bottom = 6.dp)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            ) {
                                items(state.searchResults) { city ->
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = "${city.name}, ${city.country}",
                                                fontWeight = FontWeight.SemiBold,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color(0xFF0F172A)
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelectCity(city) }
                                    )
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                                }
                            }
                        }
                    } else {
                        // Full Live Dashboard (Günün Bioklimatik Radarı & Metropoller)
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            item {
                                // Günün Bioklimatik Radarı Summary Card
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onQuickSelect(CityLocation(state.currentWeather.cityName, state.currentWeather.country, 41.0082, 28.9784)) },
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.25f)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "📡 Günün Bioklimatik Radarı",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = Color(0xFF10B981)
                                            ) {
                                                Text(
                                                    text = state.currentWeather.cityName,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${state.currentWeather.temperature.toInt()}°C",
                                                style = MaterialTheme.typography.displaySmall.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.White
                                                )
                                            )
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = state.currentWeather.description,
                                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "Nem: %${state.currentWeather.humidity} | Rüzgar: ${state.currentWeather.windSpeed} km/s",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "Bilişsel Zindelik Skoru",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { state.currentWeather.humanScore / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = Color(0xFF34D399),
                                            trackColor = Color.White.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "🌍 Dünya Metropolleri Anlık Durum",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    ),
                                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                )
                            }

                            item {
                                // 2-Column Grid of Metropolises
                                if (metropolises.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        metropolises.chunked(2).forEach { rowItems ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                rowItems.forEach { item ->
                                                    Card(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clickable { onQuickSelect(item.location) },
                                                        shape = RoundedCornerShape(18.dp),
                                                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.22f))
                                                    ) {
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(14.dp)
                                                        ) {
                                                            Text(
                                                                text = item.cityName,
                                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color.White
                                                                )
                                                            )
                                                            Text(
                                                                text = item.country,
                                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                                                color = Color.White.copy(alpha = 0.9f)
                                                            )
                                                            Spacer(modifier = Modifier.height(8.dp))
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    text = "${item.temperature.toInt()}°C",
                                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                                        fontWeight = FontWeight.ExtraBold,
                                                                        color = Color.White
                                                                    )
                                                                )
                                                                Text(
                                                                    text = item.description,
                                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                                    color = Color.White
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                if (rowItems.size == 1) {
                                                    Spacer(modifier = Modifier.weight(1f))
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(32.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                is LoadState.Empty -> {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(text = "Şehir bulunamadı", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                is LoadState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(text = "Hata: ${state.message}", color = Color.White)
                    }
                }
            }
        }
    }
}
