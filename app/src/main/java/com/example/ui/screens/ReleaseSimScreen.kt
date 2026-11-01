package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RetroActionButton
import com.example.ui.components.UndertaleBox
import com.example.ui.theme.DarkWorldCyan
import com.example.ui.theme.DeterminationGold
import com.example.ui.theme.DogBoneWhite
import com.example.ui.theme.MercyGreen
import com.example.ui.theme.SoulRed
import com.example.ui.theme.TextGray
import com.example.ui.theme.UndertaleBlack
import com.example.ui.theme.UndertaleBorder
import com.example.ui.theme.UndertaleCardBg
import com.example.viewmodel.GameUiState

@Composable
fun ReleaseSimScreen(
    state: GameUiState,
    onTriggerEarlyRelease: () -> Unit,
    onRewindTime: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enforcedCount = state.balanceDemands.count { it.isEnforced }
    val totalCount = state.balanceDemands.size
    val isFullyBalanced = enforcedCount == totalCount

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Warning Banner
        item {
            UndertaleBox(
                borderColor = if (isFullyBalanced) MercyGreen else SoulRed,
                backgroundColor = UndertaleBlack,
                headerTitle = if (isFullyBalanced) "ALL BALANCE PATCHES RATIFIED" else "WARNING: UNTESTED CHAPTER 3 LAUNCH GATE",
                headerColor = if (isFullyBalanced) MercyGreen else SoulRed
            ) {
                Text(
                    text = if (isFullyBalanced) {
                        "Congratulations! Toby Fox has incorporated all 9 critical gameplay balance patches. Deltarune Chapter 3 is finally safe for public consumption!"
                    } else {
                        "Deltarune Chapter 3 is scheduled to launch, but $enforcedCount of $totalCount balance fixes are still un-patched! Releasing now risks catastrophic gameplay glitches and unbalanced boss fights."
                    },
                    color = DogBoneWhite,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BALANCE INTEGRITY: ${(enforcedCount * 100) / totalCount}%",
                        color = if (isFullyBalanced) MercyGreen else DeterminationGold,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "$enforcedCount / $totalCount Patches",
                        color = TextGray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Action Trigger
        item {
            if (!state.glitchSimulationActive) {
                if (!isFullyBalanced) {
                    RetroActionButton(
                        text = "TEST RELEASE CHAPTER 3 ANYWAY (SEE WHAT HAPPENS)",
                        subtitle = "Simulate un-balanced game release catastrophe",
                        onClick = onTriggerEarlyRelease,
                        accentColor = SoulRed,
                        modifier = Modifier.fillMaxWidth(),
                        testTagId = "early_release_btn"
                    )
                } else {
                    RetroActionButton(
                        text = "OFFICIALLY AUTHORIZE CHAPTER 3 RELEASE!",
                        subtitle = "Toby Fox is free & previous games are balanced",
                        onClick = onTriggerEarlyRelease,
                        accentColor = MercyGreen,
                        modifier = Modifier.fillMaxWidth(),
                        testTagId = "official_release_btn"
                    )
                }
            } else {
                RetroActionButton(
                    text = "REWIND TIME & CONTINUE PERSUASION",
                    subtitle = "Re-seal Chapter 3 until Toby fixes everything",
                    onClick = onRewindTime,
                    accentColor = DeterminationGold,
                    modifier = Modifier.fillMaxWidth(),
                    testTagId = "rewind_time_btn"
                )
            }
        }

        // If Glitch Simulation is active
        if (state.glitchSimulationActive) {
            item {
                UndertaleBox(
                    borderColor = SoulRed,
                    backgroundColor = Color(0xFF200508),
                    headerTitle = "!! CRITICAL SIMULATION LOG: UNBALANCED CHAPTER 3 !!",
                    headerColor = SoulRed
                ) {
                    // Funny ASCII Dog
                    Text(
                        text = """
                             /^ ^\
                            / 0 0 \   * (The dog laughs uncontrollably
                            V\ Y /V      at your un-balanced code.)
                             / - \
                            /|    |\
                        """.trimIndent(),
                        color = DogBoneWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Because you didn't force Toby to finish the balance ledger, players are reporting game-breaking disasters across Hometown and the Dark World:",
                        color = DogBoneWhite,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp
                    )
                }
            }

            items(state.glitchBugs) { bug ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SoulRed.copy(alpha = 0.6f), RoundedCornerShape(4.dp)),
                    colors = CardDefaults.cardColors(containerColor = UndertaleCardBg),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = null,
                            tint = SoulRed,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = bug.title,
                                    color = DogBoneWhite,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                                Text(
                                    text = bug.severity,
                                    color = SoulRed,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = bug.description,
                                color = TextGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
