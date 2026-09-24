package com.example.dungeonhomeserver.core.model

import androidx.compose.ui.graphics.Color
import com.example.dungeonhomeserver.R

data class DungeonProject(
    val id: String,
    val name: String,
    val shortDescription: String,
    val icon: String,
    val accent: Color,
    val sections: List<ProjectSection>,
    val iconImageRes: Int? = null,
    val backgroundImageRes: Int = R.drawable.portale_nexus
)

data class ProjectSection(
    val id: String,
    val title: String,
    val description: String,
    val endpoint: String
)

/** Dati finti: in futuro saranno forniti da un repository. */
object DemoProjects {
    val items = listOf(
        DungeonProject(
            id = "runic_terminal",
            name = "Runic Terminal",
            shortDescription = "Collegamento runico con la macchina.",
            icon = "✦",
            accent = Color(0xFFFFC56B),
            sections = listOf(ProjectSection("runic_connection", "Runic Connection", "Canale di collegamento del terminale.", "/api/v1/runic/connection")),
            iconImageRes = R.drawable.runic_terminal_icon,
            backgroundImageRes = R.drawable.golem_runico_rovine_montane
        ),
        DungeonProject("quest", "Quest Board", "Missioni, obiettivi e ricompense.", "⚔", Color(0xFFF7B267), listOf(ProjectSection("active", "Missioni attive", "Quest disponibili per il party.", "/api/v1/quests/active"), ProjectSection("rewards", "Ricompense", "Tesori, esperienza e premi.", "/api/v1/quests/rewards"))),
        DungeonProject("world", "World Forge", "Luoghi, mappe e cronache del mondo.", "✦", Color(0xFF9DA9FF), listOf(ProjectSection("locations", "Luoghi", "Regni, città e punti d'interesse.", "/api/v1/world/locations"), ProjectSection("timeline", "Cronache", "Eventi e timeline della campagna.", "/api/v1/world/timeline"))),
        DungeonProject("codex", "Arcane Codex", "Incantesimi, oggetti e regole custom.", "✧", Color(0xFFD8A7FF), listOf(ProjectSection("spells", "Incantesimi", "Grimorio consultabile e ricercabile.", "/api/v1/codex/spells"), ProjectSection("items", "Oggetti", "Inventario degli oggetti magici.", "/api/v1/codex/items")))
    )
}
