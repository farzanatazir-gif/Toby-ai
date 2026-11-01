package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ChamberScreen
import com.example.ui.screens.DemandsScreen
import com.example.ui.screens.DodgeArenaScreen
import com.example.ui.screens.ReleaseSimScreen
import com.example.ui.theme.DarkWorldCyan
import com.example.ui.theme.DeterminationGold
import com.example.ui.theme.DogBoneWhite
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SoulRed
import com.example.ui.theme.TextGray
import com.example.ui.theme.UndertaleBlack
import com.example.ui.theme.UndertaleBorder
import com.example.ui.theme.UndertaleCardBg
import com.example.viewmodel.GameViewModel

enum class AppNavTab(val label: String, val icon: ImageVector) {
    CHAMBER("Chamber", Icons.Default.Pets),
    DEMANDS("Demands", Icons.Default.Tune),
    DODGE("Dodge Arena", Icons.Default.Favorite),
    RELEASE("Release Sim", Icons.Default.Warning)
}

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsState()
                var currentTab by remember { mutableStateOf(AppNavTab.CHAMBER) }
                var showInfoDialog by remember { mutableStateOf(false) }

                // Handle Android back button
                BackHandler(enabled = currentTab != AppNavTab.CHAMBER) {
                    currentTab = AppNavTab.CHAMBER
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(UndertaleBlack),
                    containerColor = UndertaleBlack,
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = SoulRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "HOLD CHAPTER 3",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = DogBoneWhite
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = UndertaleBlack,
                                titleContentColor = DogBoneWhite
                            ),
                            actions = {
                                IconButton(
                                    onClick = { viewModel.toggleSound() },
                                    modifier = Modifier.testTag("toggle_sound_btn")
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isSoundOn) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                        contentDescription = "Toggle Audio",
                                        tint = if (uiState.isSoundOn) DeterminationGold else TextGray
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.toggleHaptics() },
                                    modifier = Modifier.testTag("toggle_haptics_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Vibration,
                                        contentDescription = "Toggle Haptics",
                                        tint = if (uiState.isHapticsOn) DarkWorldCyan else TextGray
                                    )
                                }
                                IconButton(
                                    onClick = { showInfoDialog = true },
                                    modifier = Modifier.testTag("info_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "About App",
                                        tint = DogBoneWhite
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = UndertaleCardBg,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .border(1.dp, UndertaleBorder.copy(alpha = 0.4f))
                                .testTag("bottom_nav_bar")
                        ) {
                            AppNavTab.entries.forEach { tab ->
                                val selected = currentTab == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.label,
                                            tint = if (selected) DeterminationGold else TextGray
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 10.sp,
                                            color = if (selected) DeterminationGold else TextGray
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent,
                                        selectedIconColor = DeterminationGold,
                                        unselectedIconColor = TextGray
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            AppNavTab.CHAMBER -> ChamberScreen(
                                state = uiState,
                                onTapDog = { offset -> viewModel.tapDog(offset) },
                                onExecuteTactic = { tactic -> viewModel.executeTactic(tactic) },
                                onDismissParticle = { id -> viewModel.dismissParticle(id) },
                                onNavigateToDodge = { currentTab = AppNavTab.DODGE }
                            )

                            AppNavTab.DEMANDS -> DemandsScreen(
                                state = uiState
                            )

                            AppNavTab.DODGE -> DodgeArenaScreen(
                                state = uiState,
                                onStartArena = { viewModel.startArenaGame() },
                                onStopArena = { viewModel.stopArenaGame(defeated = false) },
                                onMoveSoul = { pos -> viewModel.updateSoulPosition(pos) }
                            )

                            AppNavTab.RELEASE -> ReleaseSimScreen(
                                state = uiState,
                                onTriggerEarlyRelease = { viewModel.triggerGlitchReleaseSimulation() },
                                onRewindTime = { viewModel.rewindGlitchSimulation() }
                            )
                        }
                    }

                    if (showInfoDialog) {
                        AlertDialog(
                            onDismissRequest = { showInfoDialog = false },
                            confirmButton = {
                                TextButton(onClick = { showInfoDialog = false }) {
                                    Text(
                                        text = "UNDERSTOOD",
                                        color = DeterminationGold,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            title = {
                                Text(
                                    text = "ABOUT: HOLD CHAPTER 3",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = DeterminationGold
                                )
                            },
                            text = {
                                Column {
                                    Text(
                                        text = "A humorous, satirical parody clicker and bullet-dodging simulator honoring Toby Fox, Undertale, and Deltarune.",
                                        fontFamily = FontFamily.Monospace,
                                        color = DogBoneWhite,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Features:\n• Interactive Annoying Dog interrogation\n• 9 funny community balance demands\n• Retro 8-bit sound synthesis\n• Real-time Undertale bullet-hell dodge mini-game\n• Parody 'Early Release Glitch' simulator",
                                        fontFamily = FontFamily.Monospace,
                                        color = TextGray,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            },
                            containerColor = UndertaleCardBg,
                            shape = RoundedCornerShape(6.dp)
                        )
                    }
                }
            }
        }
    }
}
