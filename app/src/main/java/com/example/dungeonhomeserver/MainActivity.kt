package com.example.dungeonhomeserver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dungeonhomeserver.navigation.DungeonHomeServerApp
import com.example.dungeonhomeserver.ui.theme.DungeonHomeServerTheme

/**
 * Punto di ingresso Android: ospita una sola UI Compose.
 * Le schermate e la navigazione vivono nei rispettivi package.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DungeonHomeServerTheme {
                DungeonHomeServerApp()
            }
        }
    }
}
