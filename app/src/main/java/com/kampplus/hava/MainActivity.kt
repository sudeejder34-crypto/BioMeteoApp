package com.kampplus.hava

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kampplus.hava.ui.navigation.WeatherNavGraph
import com.kampplus.hava.ui.theme.HavaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        enableEdgeToEdge()

        setContent {
            HavaTheme {
                WeatherNavGraph()
            }
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}
