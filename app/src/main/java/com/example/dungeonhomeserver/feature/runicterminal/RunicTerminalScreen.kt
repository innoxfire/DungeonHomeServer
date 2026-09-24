package com.example.dungeonhomeserver.feature.runicterminal

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dungeonhomeserver.core.designsystem.NexusBackground
import com.example.dungeonhomeserver.core.model.DungeonProject
import com.example.dungeonhomeserver.feature.projects.ProjectDashboard

/**
 * Dashboard della connessione e terminale dimostrativo locale.
 * Non apre ancora una VPN ne' una sessione SSH.
 */
@Composable
fun RunicTerminalScreen(project: DungeonProject, onBack: () -> Unit) {
    var terminalOpen by rememberSaveable { mutableStateOf(false) }

    if (terminalOpen) {
        BackHandler { terminalOpen = false }
        RunicTerminalConsole(project = project, onBack = { terminalOpen = false })
    } else {
        ProjectDashboard(
            project = project,
            summaryLabel = "RUNIC CONNECTION",
            selectedLabel = "Connessione selezionata",
            sectionLabel = "CONNESSIONE",
            onSectionSelected = { terminalOpen = true },
            onBack = onBack
        )
    }
}

private val TerminalBackground = Color(0xFF151A17)
private val TerminalText = Color(0xFFF5EBDD)
private val TerminalPrompt = Color(0xFF64DEC9)
private val TerminalIdentity = Color(0xFFFFC46B)
private val TerminalDirectory = Color(0xFFF19A57)

private data class TerminalTab(
    val id: Int,
    val title: String,
    val command: String = "",
    val transcript: List<String> = initialTerminalTranscript()
)

private fun initialTerminalTranscript() = listOf(
    "Runic Terminal  •  sessione simulata",
    "Nessuna connessione SSH è stata aperta.",
    "Scrivi help per vedere i comandi dimostrativi."
)

@Composable
private fun RunicTerminalConsole(project: DungeonProject, onBack: () -> Unit) {
    var maximumContrast by rememberSaveable { mutableStateOf(false) }
    var specialKeysVisible by rememberSaveable { mutableStateOf(false) }
    var tabs by remember { mutableStateOf(listOf(TerminalTab(id = 1, title = "Terminale 1"))) }
    var activeTabId by remember { mutableStateOf(1) }
    var nextTabId by remember { mutableStateOf(2) }
    val activeTab = tabs.first { it.id == activeTabId }
    val terminalScrollState = rememberLazyListState()

    fun updateActiveTab(transform: (TerminalTab) -> TerminalTab) {
        tabs = tabs.map { tab -> if (tab.id == activeTabId) transform(tab) else tab }
    }

    fun submitCommand() {
        val submitted = activeTab.command.trim()
        if (submitted.isEmpty()) return
        updateActiveTab { tab ->
            if (submitted.equals("clear", ignoreCase = true)) {
                tab.copy(command = "", transcript = emptyList())
            } else {
                tab.copy(command = "", transcript = tab.transcript + "❯ $submitted" + simulatedOutputFor(submitted))
            }
        }
    }

    LaunchedEffect(activeTabId, activeTab.transcript.size) {
        if (activeTab.transcript.isNotEmpty()) {
            terminalScrollState.animateScrollToItem(activeTab.transcript.lastIndex)
        }
    }

    fun openNewTerminal() {
        val newTab = TerminalTab(id = nextTabId, title = "Terminale $nextTabId")
        tabs = tabs + newTab
        activeTabId = newTab.id
        nextTabId++
    }

    fun addSpecialKey(key: String) {
        updateActiveTab { tab ->
            tab.copy(transcript = tab.transcript + "[Tasto speciale simulato: $key]")
        }
    }

    val clipboard = LocalClipboardManager.current

    Box(Modifier.fillMaxSize().background(TerminalBackground)) {
        if (!maximumContrast) {
            NexusBackground(imageRes = project.backgroundImageRes)
            Box(Modifier.fillMaxSize().background(TerminalBackground.copy(alpha = .26f)))
        }
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TerminalHeader(
                project = project,
                maximumContrast = maximumContrast,
                onContrastChanged = { maximumContrast = it },
                onSpecialKeysToggle = { specialKeysVisible = !specialKeysVisible },
                onBack = onBack
            )
            TerminalPane(
                modifier = Modifier.fillMaxWidth().weight(1f),
                project = project,
                maximumContrast = maximumContrast,
                tabs = tabs,
                activeTabId = activeTabId,
                transcript = activeTab.transcript,
                command = activeTab.command,
                scrollState = terminalScrollState,
                onTabSelected = { activeTabId = it },
                onNewTerminal = ::openNewTerminal,
                onCommandChanged = { updatedCommand -> updateActiveTab { it.copy(command = updatedCommand) } },
                onSubmit = ::submitCommand,
                onCopyOutput = { clipboard.setText(AnnotatedString(activeTab.transcript.joinToString("\n"))) }
            )
            Spacer(Modifier.weight(.35f))
        }
        AnimatedVisibility(
            visible = specialKeysVisible,
            modifier = Modifier.align(Alignment.CenterEnd).statusBarsPadding().navigationBarsPadding(),
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            TerminalKeyPanel(
                onSpecialKey = ::addSpecialKey,
                onDismiss = { specialKeysVisible = false }
            )
        }
    }
}

@Composable
private fun TerminalHeader(
    project: DungeonProject,
    maximumContrast: Boolean,
    onContrastChanged: (Boolean) -> Unit,
    onSpecialKeysToggle: () -> Unit,
    onBack: () -> Unit
) = Surface(
    color = Color(0xEF202923),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalIdentity.copy(alpha = .52f))
) {
    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            project.iconImageRes?.let { imageRes ->
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = "Icona Runic Terminal",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(38.dp)
                )
            } ?: Text("✦", color = TerminalPrompt, fontSize = 28.sp)
            Spacer(Modifier.size(8.dp))
            Column(Modifier.weight(1f)) {
                Text("Home Server", color = TerminalText, fontWeight = FontWeight.Bold)
                Text("utente@home-server  ~/docker", color = TerminalDirectory, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = onBack,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalText.copy(alpha = .5f))
            ) { Text("INDIETRO", fontSize = 11.sp) }
            Spacer(Modifier.size(6.dp))
            OutlinedButton(
                onClick = onSpecialKeysToggle,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalPrompt.copy(alpha = .7f))
            ) { Text("TASTI", fontSize = 11.sp) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            StatusBadge("Tailscale: demo", TerminalIdentity)
            StatusBadge("SSH: simulata", TerminalPrompt)
            OutlinedButton(
                onClick = { onContrastChanged(!maximumContrast) },
                contentPadding = PaddingValues(horizontal = 9.dp, vertical = 3.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TerminalText)
            ) {
                Text(if (maximumContrast) "SFONDO ON" else "CONTRASTO MAX", fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, color: Color) = Surface(
    color = color.copy(alpha = .14f),
    shape = RoundedCornerShape(50),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = .55f))
) {
    Text(label, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun TerminalInput(command: String, onCommandChanged: (String) -> Unit, onSubmit: () -> Unit) = Row(
    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Text("❯", color = TerminalPrompt, fontFamily = FontFamily.Monospace, fontSize = 20.sp)
    Spacer(Modifier.size(8.dp))
    BasicTextField(
        value = command,
        onValueChange = onCommandChanged,
        modifier = Modifier.weight(1f).border(1.dp, TerminalPrompt.copy(alpha = .5f), RoundedCornerShape(7.dp)).padding(horizontal = 10.dp, vertical = 9.dp),
        textStyle = TextStyle(color = Color(0xFFFFF1D4), fontFamily = FontFamily.Monospace, fontSize = 15.sp),
        cursorBrush = SolidColor(TerminalPrompt),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSubmit() })
    )
    Spacer(Modifier.size(8.dp))
    Button(
        onClick = onSubmit,
        colors = ButtonDefaults.buttonColors(containerColor = TerminalPrompt, contentColor = TerminalBackground),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) { Text("INVIA", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
}

@Composable
private fun TerminalPane(
    modifier: Modifier,
    project: DungeonProject,
    maximumContrast: Boolean,
    tabs: List<TerminalTab>,
    activeTabId: Int,
    transcript: List<String>,
    command: String,
    scrollState: androidx.compose.foundation.lazy.LazyListState,
    onTabSelected: (Int) -> Unit,
    onNewTerminal: () -> Unit,
    onCommandChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onCopyOutput: () -> Unit
) = Surface(
    modifier = modifier,
    color = Color.Transparent,
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalPrompt.copy(alpha = .62f))
) {
    Box(Modifier.fillMaxSize()) {
        if (!maximumContrast) {
            Image(
                painter = painterResource(project.backgroundImageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().blur(18.dp).alpha(.42f)
            )
        }
        Box(Modifier.fillMaxSize().background(TerminalBackground.copy(alpha = .72f)))
        Column(Modifier.fillMaxSize().padding(14.dp)) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                items(tabs, key = { it.id }) { tab ->
                    OutlinedButton(
                        onClick = { onTabSelected(tab.id) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (tab.id == activeTabId) TerminalPrompt else TerminalText
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (tab.id == activeTabId) TerminalPrompt else TerminalText.copy(alpha = .5f)
                        )
                    ) { Text(tab.title, fontSize = 10.sp) }
                }
                item {
                    OutlinedButton(
                        onClick = onNewTerminal,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TerminalIdentity)
                    ) { Text("+ NUOVO", fontSize = 10.sp) }
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = onCopyOutput,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TerminalText)
                ) { Text("COPIA OUTPUT", fontSize = 10.sp) }
            }
            SelectionContainer {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(transcript) { line ->
                        Text(
                            text = line,
                            color = terminalLineColor(line),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            TerminalInput(
                command = command,
                onCommandChanged = onCommandChanged,
                onSubmit = onSubmit
            )
        }
    }
}

@Composable
private fun TerminalKeyPanel(onSpecialKey: (String) -> Unit, onDismiss: () -> Unit) = Surface(
    modifier = Modifier.fillMaxHeight().width(116.dp),
    color = TerminalBackground.copy(alpha = .96f),
    shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalPrompt.copy(alpha = .7f))
) {
    Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("TASTI", color = TerminalIdentity, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        OutlinedButton(
            onClick = onDismiss,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TerminalText)
        ) { Text("CHIUDI", fontSize = 10.sp) }
        listOf("Ctrl", "Esc", "Tab", "~", "↑", "↓", "←", "→", "Tastiera").forEach { key ->
            OutlinedButton(
                onClick = { onSpecialKey(key) },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 5.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalPrompt.copy(alpha = .55f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TerminalText)
            ) { Text(key, fontFamily = FontFamily.Monospace, fontSize = 12.sp) }
        }
    }
}

private fun simulatedOutputFor(command: String): List<String> = when (command.lowercase()) {
    "help" -> listOf("Comandi demo: help, status, clear.")
    "status" -> listOf("[DEMO] Tailscale: non verificato • SSH: non connessa")
    else -> listOf("[DEMO] '$command' non è stato inviato a nessuna macchina.")
}

private fun terminalLineColor(line: String): Color = when {
    line.startsWith("❯") -> TerminalPrompt
    line.startsWith("[DEMO]") -> TerminalIdentity
    line.contains("Nessuna connessione") -> Color(0xFFE88A57)
    else -> TerminalText
}
