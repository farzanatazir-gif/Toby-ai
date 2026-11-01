package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BalanceDemand
import com.example.model.TargetGame
import com.example.ui.components.UndertaleBox
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
fun DemandsScreen(
    state: GameUiState,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<TargetGame?>(null) }

    val filteredDemands = remember(state.balanceDemands, selectedFilter) {
        if (selectedFilter == null) state.balanceDemands
        else state.balanceDemands.filter { it.game == selectedFilter }
    }

    val totalEnforced = state.balanceDemands.count { it.isEnforced }
    val totalCount = state.balanceDemands.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Balance Manifesto Header
        item {
            UndertaleBox(
                borderColor = UndertaleBorder,
                backgroundColor = UndertaleBlack,
                headerTitle = "OFFICIAL COMMUNITY BALANCE DEMANDS",
                headerColor = DarkWorldCyan
            ) {
                Text(
                    text = "Deltarune Chapter 3 is legally held in custody until Toby Fox patches and signs off on every gameplay grievance in Undertale and Deltarune Chapters 1 & 2.",
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
                        text = "RATIFIED: $totalEnforced / $totalCount",
                        color = if (totalEnforced == totalCount) MercyGreen else DeterminationGold,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "TOTAL PERSUASION: ${state.totalDamageDealt}",
                        color = TextGray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }

                // Progress Bar
                val progress = (totalEnforced.toFloat() / totalCount.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(UndertaleCardBg)
                        .border(1.dp, UndertaleBorder, RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress)
                            .height(10.dp)
                            .background(if (totalEnforced == totalCount) MercyGreen else DarkWorldCyan)
                    )
                }
            }
        }

        // Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipItem(
                    label = "ALL (${state.balanceDemands.size})",
                    isSelected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    modifier = Modifier.weight(1f)
                )
                FilterChipItem(
                    label = "UNDERTALE",
                    isSelected = selectedFilter == TargetGame.UNDERTALE,
                    onClick = { selectedFilter = TargetGame.UNDERTALE },
                    modifier = Modifier.weight(1f)
                )
                FilterChipItem(
                    label = "DELTARUNE",
                    isSelected = selectedFilter == TargetGame.DELTARUNE,
                    onClick = { selectedFilter = TargetGame.DELTARUNE },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Demands List
        items(filteredDemands, key = { it.id }) { demand ->
            DemandCard(
                demand = demand,
                totalDamage = state.totalDamageDealt
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) DarkWorldCyan else UndertaleCardBg)
            .border(1.5.dp, if (isSelected) DogBoneWhite else UndertaleBorder, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) UndertaleBlack else DogBoneWhite,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun DemandCard(
    demand: BalanceDemand,
    totalDamage: Long
) {
    val isEnforced = demand.isEnforced
    val gameTagColor = if (demand.game == TargetGame.UNDERTALE) DeterminationGold else DeltarunePurple

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = if (isEnforced) MercyGreen else UndertaleBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .testTag("demand_card_${demand.id}"),
        colors = CardDefaults.cardColors(containerColor = UndertaleCardBg),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Game Tag & Status Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(gameTagColor.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                            .border(1.dp, gameTagColor, RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = demand.game.name,
                            color = gameTagColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = demand.patchVersion,
                        color = TextGray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isEnforced) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = if (isEnforced) MercyGreen else SoulRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isEnforced) "SIGNED & PATCHED" else "PENDING PRESSURE",
                        color = if (isEnforced) MercyGreen else SoulRed,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Demand Title
            Text(
                text = demand.title,
                color = DogBoneWhite,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
            )

            // Problem
            Text(
                text = "ISSUE: ${demand.problem}",
                color = TextGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Fix
            Text(
                text = "PATCH: ${demand.patchFix}",
                color = DarkWorldCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Toby Fox Developer Signed Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(UndertaleBlack, RoundedCornerShape(4.dp))
                    .border(1.dp, if (isEnforced) MercyGreen else DarkWorldCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = if (isEnforced) demand.tobyNote else "* (Toby Fox is still resisting. Needs ${demand.stressThreshold} Total Persuasion)",
                    color = if (isEnforced) DogBoneWhite else TextGray,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }
    }
}
