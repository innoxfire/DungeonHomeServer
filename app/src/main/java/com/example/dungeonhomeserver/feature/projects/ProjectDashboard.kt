package com.example.dungeonhomeserver.feature.projects

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dungeonhomeserver.core.designsystem.NexusBackground
import com.example.dungeonhomeserver.core.model.DungeonProject
import com.example.dungeonhomeserver.core.model.ProjectSection

@Composable
fun ProjectDashboard(
    project: DungeonProject,
    summaryLabel: String = "CONNESSIONE BACKEND",
    selectedLabel: String = "Endpoint selezionato",
    sectionLabel: String = "SEZIONI",
    onSectionSelected: (ProjectSection) -> Unit = {},
    onBack: () -> Unit
) {
    var selectedSection by remember(project.id) { mutableStateOf(project.sections.first()) }
    Box(Modifier.fillMaxSize()) {
        NexusBackground(imageRes = project.backgroundImageRes, darkened = true)
        LazyColumn(Modifier.fillMaxSize().statusBarsPadding(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text("‹  Tutti i progetti", color = Color(0xFFFFE5A3), modifier = Modifier.clickable(onClick = onBack).padding(vertical = 6.dp), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(20.dp))
                project.iconImageRes?.let { imageRes ->
                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = "Icona di ${project.name}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(64.dp)
                    )
                } ?: Text(project.icon, color = project.accent, fontSize = 46.sp)
                Text(project.name, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(project.shortDescription, color = Color(0xFFE1E4EF), modifier = Modifier.padding(top = 6.dp))
            }
            item { ConnectionSummary(project, selectedSection, summaryLabel, selectedLabel) }
            item { Text(sectionLabel, color = Color(0xFFFFE5A3), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp) }
            items(project.sections, key = { it.id }) { section ->
                SectionCard(section, project.accent, section.id == selectedSection.id) {
                    selectedSection = section
                    onSectionSelected(section)
                }
            }
        }
    }
}

@Composable
private fun ConnectionSummary(project: DungeonProject, selectedSection: ProjectSection, summaryLabel: String, selectedLabel: String) = Card(colors = CardDefaults.cardColors(containerColor = Color(0xE9212638)), shape = RoundedCornerShape(22.dp)) {
    Column(Modifier.padding(18.dp)) {
        Text(summaryLabel, color = project.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(Modifier.height(8.dp))
        Text(selectedLabel, color = Color.White, fontWeight = FontWeight.SemiBold)
        Text(selectedSection.endpoint, color = Color(0xFFC7CCDF), modifier = Modifier.padding(top = 3.dp), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SectionCard(section: ProjectSection, accent: Color, isSelected: Boolean, onClick: () -> Unit) = Card(Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (isSelected) accent.copy(alpha = .25f) else Color(0xE9212638)), border = if (isSelected) BorderStroke(1.dp, accent.copy(alpha = .75f)) else null) {
    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(accent))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(section.title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(section.description, color = Color(0xFFC7CCDF), style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 3.dp))
        }
        Text("›", color = accent, fontSize = 28.sp)
    }
}
