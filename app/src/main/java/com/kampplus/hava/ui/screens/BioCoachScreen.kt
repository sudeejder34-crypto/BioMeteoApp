package com.kampplus.hava.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.ui.state.LoadState

data class ExpandableTip(
    val tip: String,
    val scientificReason: String,
    val isExpanded: Boolean = false,
    val isCompleted: Boolean = false
)

@Composable
fun BioCoachScreen(
    state: LoadState,
    onBack: () -> Unit
) {
    val gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF311042))

    val tipsState = remember {
        mutableStateOf(
            listOf(
                ExpandableTip(
                    tip = "Barometrik basınç değişimine karşı mola sürelerini optimize edin.",
                    scientificReason = "Atmosferik basınç düşüşleri iç kulak reseptörlerini ve vasküler sistemi etkileyerek odaklanma süresini kısaltabilir. 10 dakikalık nefes molaları sinir sistemini dengeler."
                ),
                ExpandableTip(
                    tip = "Yüksek nem oranında ev ve bitki sulama rutinini esnetin.",
                    scientificReason = "Yüksek bağıl nem topraktaki buharlaşma oranını düşürür; kök çürümesini önlemek için sulama aralığı uzatılmalıdır."
                ),
                ExpandableTip(
                    tip = "Termal konfor aralığında açık hava odaklanma egzersizleri yapın.",
                    scientificReason = "18-24°C sıcaklık bandı prefrontal korteks aktivitesini maksimize ederek problem çözme yeteneğini %20 oranında artırır."
                ),
                ExpandableTip(
                    tip = "Günlük su tüketiminizi nem indeksine göre 2.5 litreye sabitleyin.",
                    scientificReason = "Havadaki düşük/yüksek nem buharlaşma hızını artırarak dehidrasyona sebep olur; hidrasyon sinaptik iletimi doğrudan destekler."
                )
            )
        )
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

            // Top Bar
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
                    text = "Bio-Coach & Günlük Tüyolar",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (state) {
                is LoadState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                                        .padding(24.dp)
                                ) {
                                    Text(
                                        text = "💡 İnteraktif Uzman Koçluğu",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Kartlara dokunarak bilimsel arka planlarını inceleyebilir ve 'Bunu Uyguladım' seçeneği ile günlük bioklimatik rutininizi takip edebilirsiniz.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }

                        items(tipsState.value) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .clickable {
                                        tipsState.value = tipsState.value.map {
                                            if (it == item) it.copy(isExpanded = !it.isExpanded) else it
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (item.isCompleted) Color(0xFFE8F8F5) else Color.White.copy(alpha = 0.95f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(text = if (item.isCompleted) "✅" else "📌", fontSize = 22.sp)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = item.tip,
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF0F172A)
                                                )
                                            )
                                        }
                                    }

                                    if (item.isExpanded) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "🔬 Bilimsel Arka Plan:",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5))
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = item.scientificReason,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Color(0xFF334155),
                                                lineHeight = 20.sp
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Bunu Uyguladım",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (item.isCompleted) Color(0xFF10B981) else Color(0xFF64748B)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Checkbox(
                                            checked = item.isCompleted,
                                            onCheckedChange = { checked ->
                                                tipsState.value = tipsState.value.map {
                                                    if (it == item) it.copy(isCompleted = checked) else it
                                                }
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
    }
}
