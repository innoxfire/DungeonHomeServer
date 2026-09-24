package com.example.dungeonhomeserver.feature.portal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dungeonhomeserver.core.designsystem.NexusAmberSurface
import com.example.dungeonhomeserver.core.designsystem.NexusAmberSurfaceSoft
import com.example.dungeonhomeserver.core.designsystem.NexusGold
import com.example.dungeonhomeserver.core.designsystem.NexusBackground
import com.example.dungeonhomeserver.core.designsystem.readableShadow

@Composable
fun PortalLanding(onChooseProject: () -> Unit) = androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {
    NexusBackground()
    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp, vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = NexusAmberSurfaceSoft,
            contentColor = NexusGold,
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, NexusGold.copy(alpha = .55f))
        ) {
            Text("DUNGEON HOME", Modifier.padding(horizontal = 15.dp, vertical = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.5.sp)
        }
        Spacer(Modifier.weight(1f))
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = NexusAmberSurface),
            border = BorderStroke(1.dp, NexusGold.copy(alpha = .48f))
        ) {
            Column(Modifier.padding(horizontal = 24.dp, vertical = 22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Il Nexus attende", color = Color(0xFFFFF5E8), style = MaterialTheme.typography.headlineMedium.copy(shadow = readableShadow()), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Scegli il regno da attraversare", color = Color(0xFFFFE1BD), modifier = Modifier.padding(top = 7.dp), style = MaterialTheme.typography.bodyLarge.copy(shadow = readableShadow()), textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                Button(onClick = onChooseProject, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9A4E1E), contentColor = Color(0xFFFFF5E8)), border = BorderStroke(1.dp, NexusGold), contentPadding = PaddingValues(horizontal = 28.dp, vertical = 15.dp)) {
                    Text("SCEGLI UN PROGETTO  ✦", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Surface(color = NexusAmberSurfaceSoft, contentColor = Color(0xFFFFE1BD), shape = RoundedCornerShape(50), border = BorderStroke(1.dp, NexusGold.copy(alpha = .38f))) {
            Text("Portale del Nexus", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp)
        }
    }
}
