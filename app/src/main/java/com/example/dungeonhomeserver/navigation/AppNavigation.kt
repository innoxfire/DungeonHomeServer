package com.example.dungeonhomeserver.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.dungeonhomeserver.core.model.DemoProjects
import com.example.dungeonhomeserver.core.model.DungeonProject
import com.example.dungeonhomeserver.feature.portal.PortalLanding
import com.example.dungeonhomeserver.feature.portal.PortalTransition
import com.example.dungeonhomeserver.feature.portal.ProjectPickerScreen
import com.example.dungeonhomeserver.feature.projects.ProjectDashboard
import com.example.dungeonhomeserver.feature.runicterminal.RunicTerminalScreen

private enum class AppDestination { PORTAL, PICKER, ENTERING_PROJECT, DASHBOARD }

/** Tiene lo stato di navigazione provvisorio finche' non introdurremo Navigation Compose. */
@Composable
fun DungeonHomeServerApp() {
    var destination by remember { mutableStateOf(AppDestination.PORTAL) }
    var selectedProject by remember { mutableStateOf<DungeonProject?>(null) }

    BackHandler(enabled = destination != AppDestination.PORTAL) {
        destination = AppDestination.PORTAL
    }

    Surface(Modifier.fillMaxSize(), color = Color.Black) {
        when (destination) {
            AppDestination.PORTAL -> PortalLanding { destination = AppDestination.PICKER }
            AppDestination.PICKER -> ProjectPickerScreen(
                projects = DemoProjects.items,
                onDismiss = { destination = AppDestination.PORTAL },
                onProjectSelected = {
                    selectedProject = it
                    destination = AppDestination.ENTERING_PROJECT
                }
            )
            AppDestination.ENTERING_PROJECT -> selectedProject?.let { project ->
                PortalTransition(project) { destination = AppDestination.DASHBOARD }
            }
            AppDestination.DASHBOARD -> selectedProject?.let { project ->
                if (project.id == "runic_terminal") {
                    RunicTerminalScreen(project) { destination = AppDestination.PORTAL }
                } else {
                    ProjectDashboard(project) { destination = AppDestination.PORTAL }
                }
            }
        }
    }
}
