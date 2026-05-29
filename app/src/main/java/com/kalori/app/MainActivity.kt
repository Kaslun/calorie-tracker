package com.kalori.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kalori.app.ui.navigation.KaloriNavHost
import com.kalori.app.ui.theme.KaloriTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single activity. Hosts the Compose tree; all screens are Composables under one NavHost.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KaloriTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KaloriNavHost()
                }
            }
        }
    }
}
