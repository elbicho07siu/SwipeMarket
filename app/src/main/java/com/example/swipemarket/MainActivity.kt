package com.example.swipemarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.swipemarket.core.navigation.AppNavigation
import com.example.swipemarket.ui.theme.SwipeMarketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwipeMarketTheme {
                AppNavigation()
            }
        }
    }
}
