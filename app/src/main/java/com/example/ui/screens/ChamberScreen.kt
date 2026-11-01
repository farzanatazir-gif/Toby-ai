package com.example.ui.screens

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DamageParticle
import com.example.model.TortureTactic
import com.example.ui.components.AnnoyingDogView
import com.example.ui.components.FloatingParticle
import com.example.ui.components.RetroActionButton
import com.example.ui.components.TypewriterDialogBox
import com.example.ui.components.UndertaleBox
import com.example.ui.components.UndertaleHpBar
import com.example.ui.theme.DarkWorldAccent
import com.example.ui.theme.DarkWorldCyan
import com.example.ui.theme.DeltarunePurple
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
fun ChamberScreen(
    state: GameUiState,
    onTapDog: (Offset) -> Unit,
    onExecuteTactic: (TortureTactic) -> Unit,
    onDismissParticle: (Long) -> Unit,
    onNavigateToDodge: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Hero Banner: Chapter 3 Locked in chains
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(2.dp, UndertaleBorder, RoundedCornerShape(6.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ch3_banner),
                        contentDescription = "Deltarune Chapter 3 Locked Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Semi-transparent dark overlay for banner text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(UndertaleBlack.copy(alpha = 0.45f))
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(UndertaleBlack.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                            .border(1.dp, DeterminationGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = SoulRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "HOLD ACTIVE: CHAPTER 3 FROZEN",
                            color = DeterminationGold,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Stats HUD Box
            item {
                UndertaleBox(
                    borderColor = UndertaleBorder,
                    backgroundColor = UndertaleBlack
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Pets,
                                    contentDescription = null,
                                    tint = DeterminationGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${state.dogResidue} RESIDUE",
                                    color = DeterminationGold,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                text = "+${state.passiveResiduePerSec}/sec from angry fans",
                                color = TextGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val enforced = state.balanceDemands.count { it.isEnforced }
                            val total = state.balanceDemands.size
                            Text(
                                text = "PATCHES: $enforced / $total",
                                color = if (enforced == total) MercyGreen else DarkWorldCyan,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "STAGE ${state.stageLevel} PERSUASION",
                                color = TextGray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    UndertaleHpBar(
                        currentValue = state.tobyStubbornness,
                        maxValue = state.maxStubbornness,
                        label = "TOBY'S STUBBORNNESS",
                        fillColor = DeterminationGold
                    )
                }
            }

            // Annoying Dog Interrogation Stage
            item {
                Box {
                    AnnoyingDogView(
                        expression = state.tobyExpression,
                        isRetaliating = state.isRetaliating,
                        onDogTapped = onTapDog
                    )

                    // Floating damage numbers layer
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("floating_particles_box")
                    ) {
                        state.particles.forEach { particle ->
                            FloatingParticle(
                                particle = particle,
                                onDismiss = { onDismissParticle(particle.id) }
                            )
                        }
                    }
                }
            }

            // Real-time Typewriter Dialogue
            item {
                TypewriterDialogBox(
                    text = state.dialogueText,
                    modifier = Modifier.testTag("dialogue_box")
                )
            }

            // Quick Tap Action & Provoke Retaliation
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RetroActionButton(
                        text = "Tickle Toby",
                        subtitle = "+Dog Residue",
                        onClick = { onTapDog(Offset.Zero) },
                        modifier = Modifier.weight(1f),
                        accentColor = DeterminationGold,
                        testTagId = "tickle_toby_btn"
                    )
                    RetroActionButton(
                        text = "Provoke Battle",
                        subtitle = "Enter Dodge Arena",
                        onClick = onNavigateToDodge,
                        modifier = Modifier.weight(1f),
                        accentColor = SoulRed,
                        testTagId = "provoke_battle_btn"
                    )
                }
            }

            // Persuasion Tactics Header
            item {
                Text(
                    text = "PERSUASION METHODS & TACTICS",
                    color = DarkWorldCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // List of funny tactics
            items(state.tactics, key = { it.id }) { tactic ->
                TacticCard(
                    tactic = tactic,
                    canAfford = state.dogResidue >= tactic.residueCost,
                    onExecute = { onExecuteTactic(tactic) }
                )
            }
        }
    }
}

@Composable
private fun TacticCard(
    tactic: TortureTactic,
    canAfford: Boolean,
    onExecute: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (canAfford) UndertaleBorder else DarkWorldAccent, RoundedCornerShape(4.dp))
            .testTag("tactic_card_${tactic.id}"),
        colors = CardDefaults.cardColors(containerColor = UndertaleCardBg),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tactic.name,
                    color = DogBoneWhite,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "+${tactic.stressDamage} Persuasion",
                    color = MercyGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            Text(
                text = tactic.description,
                color = TextGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.5.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (tactic.residueCost > 0) "Cost: ${tactic.residueCost} Residue" else "Cost: FREE (Tap)",
                    color = if (canAfford) DeterminationGold else SoulRed,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )

                RetroActionButton(
                    text = "DEPLOY",
                    enabled = canAfford,
                    onClick = onExecute,
                    accentColor = DeterminationGold,
                    modifier = Modifier.width(110.dp),
                    testTagId = "deploy_${tactic.id}"
                )
            }
        }
    }
}
