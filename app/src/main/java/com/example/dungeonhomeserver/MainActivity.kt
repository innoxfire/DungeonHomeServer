package com.example.dungeonhomeserver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dungeonhomeserver.ui.theme.DungeonHomeServerTheme

/** Struttura che in seguito sarà ricevuta da GET /api/v1/projects. */
data class DungeonProject(
    val id: String,
    val name: String,
    val shortDescription: String,
    val icon: String,
    val accent: Color,
    val sections: List<ProjectSection>
)

data class ProjectSection(
    val id: String,
    val title: String,
    val description: String,
    val endpoint: String
)

private val demoProjects = listOf(
    DungeonProject("bestario", "Bestiario", "Creature, incontri e schede viventi.", "♞", Color(0xFF7DD3B0), listOf(
        ProjectSection("creature", "Creature", "Archivio e filtri delle creature.", "/api/v1/bestiary/creatures"),
        ProjectSection("encounters", "Incontri", "Generatore degli incontri di gioco.", "/api/v1/bestiary/encounters")
    )),
    DungeonProject("quest", "Quest Board", "Missioni, obiettivi e ricompense.", "⚔", Color(0xFFF7B267), listOf(
        ProjectSection("active", "Missioni attive", "Quest disponibili per il party.", "/api/v1/quests/active"),
        ProjectSection("rewards", "Ricompense", "Tesori, esperienza e premi.", "/api/v1/quests/rewards")
    )),
    DungeonProject("world", "World Forge", "Luoghi, mappe e cronache del mondo.", "✦", Color(0xFF9DA9FF), listOf(
        ProjectSection("locations", "Luoghi", "Regni, città e punti d'interesse.", "/api/v1/world/locations"),
        ProjectSection("timeline", "Cronache", "Eventi e timeline della campagna.", "/api/v1/world/timeline")
    )),
    DungeonProject("codex", "Arcane Codex", "Incantesimi, oggetti e regole custom.", "✧", Color(0xFFD8A7FF), listOf(
        ProjectSection("spells", "Incantesimi", "Grimorio consultabile e ricercabile.", "/api/v1/codex/spells"),
        ProjectSection("items", "Oggetti", "Inventario degli oggetti magici.", "/api/v1/codex/items")
    ))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { DungeonHomeServerTheme { DungeonServerApp() } }
    }
}

@Composable
fun DungeonServerApp() {
    var selectedProject by remember { mutableStateOf<DungeonProject?>(null) }
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF101321)) {
        AnimatedContent(targetState = selectedProject, label = "project-navigation") { project ->
            if (project == null) ProjectHome(demoProjects) { selectedProject = it }
            else ProjectDashboard(project) { selectedProject = null }
        }
    }
}

@Composable
private fun ProjectHome(projects: List<DungeonProject>, onProjectSelected: (DungeonProject) -> Unit) {
    Box(Modifier.fillMaxSize()) {
        FantasyBackground()
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 28.dp)) {
            Text("DUNGEON HOME", color = Color(0xFFF8E7B4), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Text("Scegli un regno", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Ogni creatura custodisce un progetto indipendente.", color = Color(0xFFC7CCDF), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(28.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) { items(projects, key = { it.id }) { ProjectTile(it) { onProjectSelected(it) } } }
        }
    }
}

@Composable
private fun ProjectTile(project: DungeonProject, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) .95f else 1f, label = "tile-scale")
    val elevation by animateDpAsState(if (pressed) 2.dp else 10.dp, label = "tile-elevation")
    val transition = rememberInfiniteTransition(label = "icon-pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.09f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon-pulse-value"
    )
    Card(
        modifier = Modifier.fillMaxWidth().height(192.dp).scale(scale)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xE6212638)),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                Modifier.size(58.dp).scale(pulse).shadow(16.dp, CircleShape, ambientColor = project.accent, spotColor = project.accent).clip(CircleShape).background(project.accent.copy(alpha = .20f)),
                contentAlignment = Alignment.Center
            ) { Text(project.icon, fontSize = 31.sp, color = project.accent) }
            Spacer(Modifier.weight(1f))
            Text(project.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(project.shortDescription, color = Color(0xFFB8C0D7), style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ProjectDashboard(project: DungeonProject, onBack: () -> Unit) {
    var selectedSection by remember(project.id) { mutableStateOf(project.sections.first()) }
    Box(Modifier.fillMaxSize()) {
        FantasyBackground()
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text("‹  Tutti i progetti", color = Color(0xFFF8E7B4), modifier = Modifier.clickable(onClick = onBack), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(24.dp))
                Text(project.icon, color = project.accent, fontSize = 46.sp)
                Text(project.name, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(project.shortDescription, color = Color(0xFFC7CCDF), modifier = Modifier.padding(top = 6.dp))
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xE6212638)), shape = RoundedCornerShape(22.dp)) {
                    Column(Modifier.padding(18.dp)) {
                        Text("CONNESSIONE BACKEND", color = project.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Endpoint selezionato", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(selectedSection.endpoint, color = Color(0xFFC7CCDF), modifier = Modifier.padding(top = 3.dp), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item { Text("SEZIONI", color = Color(0xFFF8E7B4), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp) }
            items(project.sections.size) { index ->
                val section = project.sections[index]
                SectionCard(section, project.accent, section.id == selectedSection.id) { selectedSection = section }
            }
        }
    }
}

@Composable
private fun SectionCard(section: ProjectSection, accent: Color, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) accent.copy(alpha = .20f) else Color(0xE6212638)),
        border = if (isSelected) BorderStroke(1.dp, accent.copy(alpha = .75f)) else null
    ) {
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
}

@Composable
private fun FantasyBackground() {
    Canvas(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF111426), Color(0xFF20223B), Color(0xFF101321))))) {
        drawCircle(Color(0xFF6358A7).copy(alpha = .22f), size.minDimension * .58f, Offset(size.width * .95f, size.height * .08f))
        drawCircle(Color(0xFF367F79).copy(alpha = .14f), size.minDimension * .70f, Offset(size.width * .02f, size.height * .92f))
        drawCircle(Color.White.copy(alpha = .24f), 2.dp.toPx(), Offset(size.width * .18f, size.height * .17f))
        drawCircle(Color.White.copy(alpha = .18f), 1.dp.toPx(), Offset(size.width * .82f, size.height * .33f))
        drawCircle(Color(0xFFF8E7B4).copy(alpha = .3f), 2.dp.toPx(), Offset(size.width * .68f, size.height * .76f))
    }
}
