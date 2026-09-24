package com.example.dungeonhomeserver.core.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dungeonhomeserver.R

val NexusGold = Color(0xFFFFC56B)
val NexusOrange = Color(0xFFFF9B5A)
val NexusAqua = Color(0xFF62E2CC)
val NexusAmberSurface = Color(0xEC4A291C)
val NexusAmberSurfaceSoft = Color(0xD95B3320)

fun readableShadow() = Shadow(
    color = Color.Black.copy(alpha = .85f),
    offset = Offset(0f, 2f),
    blurRadius = 6f
)

@Composable
fun NexusBackground(
    imageRes: Int = R.drawable.portale_nexus,
    blurred: Boolean = false,
    darkened: Boolean = false
) = Box(Modifier.fillMaxSize()) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().then(if (blurred) Modifier.blur(18.dp) else Modifier)
    )
    if (darkened) {
        Box(Modifier.fillMaxSize().background(Color(0x99080B14)))
    } else {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0x55060A12)))))
    }
}
