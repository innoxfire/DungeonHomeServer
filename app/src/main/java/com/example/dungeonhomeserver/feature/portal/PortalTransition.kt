package com.example.dungeonhomeserver.feature.portal

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dungeonhomeserver.core.designsystem.NexusAqua
import com.example.dungeonhomeserver.core.designsystem.NexusBackground
import com.example.dungeonhomeserver.core.designsystem.NexusGold
import com.example.dungeonhomeserver.core.designsystem.NexusOrange
import com.example.dungeonhomeserver.core.model.DungeonProject
import kotlinx.coroutines.delay

@Composable
fun PortalTransition(project: DungeonProject, onFinished: () -> Unit) {
    var entering by remember { mutableStateOf(false) }
    var radiating by remember { mutableStateOf(false) }
    val iconScale by animateFloatAsState(if (entering) .18f else 1f, tween(680, easing = FastOutSlowInEasing), label = "project-icon-entry")
    val iconAlpha by animateFloatAsState(if (radiating) .15f else 1f, tween(420), label = "project-icon-fade")
    val burstRadius by animateFloatAsState(if (radiating) 2_100f else 0f, tween(820, easing = FastOutSlowInEasing), label = "nexus-burst-radius")
    val aquaFillAlpha by animateFloatAsState(if (radiating) .92f else 0f, tween(650, delayMillis = 170, easing = FastOutSlowInEasing), label = "nexus-aqua-fill")
    LaunchedEffect(project.id) {
        entering = true
        delay(620)
        radiating = true
        delay(900)
        onFinished()
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        NexusBackground()
        Box(Modifier.fillMaxSize().background(NexusAqua.copy(alpha = aquaFillAlpha)))
        if (radiating) {
            Box(Modifier.fillMaxSize().background(Brush.radialGradient(colors = listOf(NexusGold.copy(alpha = .98f), NexusOrange.copy(alpha = .82f), NexusAqua.copy(alpha = .55f), Color.Transparent), radius = burstRadius.coerceAtLeast(1f))))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            project.iconImageRes?.let { imageRes ->
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = "Icona di ${project.name}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(160.dp).scale(iconScale).alpha(iconAlpha)
                )
            } ?: Box(Modifier.size(92.dp).scale(iconScale).alpha(iconAlpha).clip(CircleShape).background(project.accent.copy(alpha = .92f)), contentAlignment = Alignment.Center) {
                Text(project.icon, color = Color(0xFF101321), fontSize = 54.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(22.dp))
        }
    }
}
