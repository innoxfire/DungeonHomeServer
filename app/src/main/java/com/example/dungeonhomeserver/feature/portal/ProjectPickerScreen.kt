package com.example.dungeonhomeserver.feature.portal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dungeonhomeserver.core.designsystem.NexusAmberSurface
import com.example.dungeonhomeserver.core.designsystem.NexusBackground
import com.example.dungeonhomeserver.core.designsystem.NexusGold
import com.example.dungeonhomeserver.core.model.DungeonProject

@Composable
fun ProjectPickerScreen(projects: List<DungeonProject>, onDismiss: () -> Unit, onProjectSelected: (DungeonProject) -> Unit) = Box(Modifier.fillMaxSize()) {
    NexusBackground(blurred = true)
    Box(Modifier.fillMaxSize().background(Color(0x8A3B2017)))
    Card(Modifier.fillMaxSize().statusBarsPadding().padding(18.dp), shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = NexusAmberSurface), border = BorderStroke(1.dp, NexusGold.copy(alpha = .48f))) {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("‹", Modifier.clickable(onClick = onDismiss).padding(end = 12.dp), NexusGold, fontSize = 32.sp)
                Column {
                    Text("Scegli un regno", color = Color(0xFFFFF5E8), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Il portale assumerà il suo colore.", color = Color(0xFFFFD8AF), style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(22.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 12.dp)) {
                items(projects, key = { it.id }) { project -> ProjectTile(project) { onProjectSelected(project) } }
            }
        }
    }
}

@Composable
private fun ProjectTile(project: DungeonProject, onClick: () -> Unit) = Card(Modifier.fillMaxWidth().height(176.dp).clickable(onClick = onClick), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xE657301F)), border = BorderStroke(1.dp, project.accent.copy(alpha = .62f))) {
    Box(Modifier.fillMaxSize()) {
        if (project.iconImageRes != null) {
            Image(painter = painterResource(project.backgroundImageRes), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0x3825160F), Color(0xF03B1E14)))))
        }
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Box(Modifier.size(54.dp).clip(CircleShape).background(project.accent.copy(alpha = .28f)), contentAlignment = Alignment.Center) {
                project.iconImageRes?.let { imageRes -> Image(painter = painterResource(imageRes), contentDescription = "Icona di ${project.name}", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
                    ?: Text(project.icon, fontSize = 30.sp, color = project.accent)
            }
            Spacer(Modifier.weight(1f))
            Text(project.name, color = Color(0xFFFFF5E8), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(project.shortDescription, color = Color(0xFFFFD8AF), style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
