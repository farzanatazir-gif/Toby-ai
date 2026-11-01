package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Bullet
import com.example.model.BulletType
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
fun DodgeArenaScreen(
    state: GameUiState,
    onStartArena: () -> Unit,
    onStopArena: () -> Unit,
    onMoveSoul: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Battle Arena Info Header
        UndertaleBox(
            borderColor = UndertaleBorder,
            backgroundColor = UndertaleBlack,
            headerTitle = "TOBY'S RETALIATION ARENA",
            headerColor = SoulRed
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Soul HP
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = SoulRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SOUL: ${state.soulHp} / ${state.maxSoulHp} HP",
                        color = if (state.soulHp < 20) SoulRed else DeterminationGold,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Timer & High Score
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = DarkWorldCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.arenaTimerSec}s (BEST: ${state.arenaHighScoreSec}s)",
                        color = DarkWorldCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Touch and drag your RED SOUL to dodge Toby's barking dogs, flying maracas, and bug reports. Surviving grants immense Dog Residue and weakens Toby!",
                color = TextGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }

        // The Undertale Battle Box Arena
        Box(
            modifier = Modifier
                .size(340.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(UndertaleBlack)
                .border(3.dp, if (state.arenaActive) DogBoneWhite else TextGray, RoundedCornerShape(6.dp))
                .pointerInput(state.arenaActive) {
                    if (state.arenaActive) {
                        detectDragGestures(
                            onDragStart = { offset -> onMoveSoul(offset) },
                            onDrag = { change, _ ->
                                change.consume()
                                onMoveSoul(change.position)
                            }
                        )
                    }
                }
                .testTag("dodge_battle_arena")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // If not active, show standby message
                if (!state.arenaActive) {
                    // Draw centered idle soul
                    drawSoulHeart(Offset(size.width / 2, size.height / 2), size = 26f)
                } else {
                    // Draw Bullets
                    state.bullets.forEach { bullet ->
                        drawBullet(bullet)
                    }

                    // Draw Player's Red Soul Heart
                    drawSoulHeart(state.soulPos, size = 22f)
                }
            }

            if (!state.arenaActive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 80.dp)
                        .background(UndertaleCardBg, RoundedCornerShape(4.dp))
                        .border(1.dp, DeterminationGold, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "PRESS 'ENTER BATTLE' TO DODGE",
                        color = DeterminationGold,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!state.arenaActive) {
                RetroActionButton(
                    text = "ENTER BATTLE",
                    subtitle = "Dodge & Earn Residue",
                    onClick = onStartArena,
                    modifier = Modifier.weight(1f),
                    accentColor = DeterminationGold,
                    testTagId = "start_arena_btn"
                )
            } else {
                RetroActionButton(
                    text = "FLEE / CASH OUT",
                    subtitle = "Keep ${state.arenaTimerSec * 25} Residue",
                    onClick = onStopArena,
                    modifier = Modifier.weight(1f),
                    accentColor = MercyGreen,
                    testTagId = "flee_arena_btn"
                )
            }
        }
    }
}

// Draw Undertale Red Soul Heart
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSoulHeart(
    center: Offset,
    size: Float
) {
    val path = Path().apply {
        val half = size / 2f
        moveTo(center.x, center.y + half * 0.7f)
        cubicTo(
            center.x - half * 1.2f, center.y,
            center.x - half * 1.2f, center.y - half * 0.9f,
            center.x - half * 0.4f, center.y - half * 0.9f
        )
        cubicTo(
            center.x, center.y - half * 0.6f,
            center.x, center.y - half * 0.6f,
            center.x, center.y - half * 0.4f
        )
        cubicTo(
            center.x, center.y - half * 0.6f,
            center.x, center.y - half * 0.6f,
            center.x + half * 0.4f, center.y - half * 0.9f
        )
        cubicTo(
            center.x + half * 1.2f, center.y - half * 0.9f,
            center.x + half * 1.2f, center.y,
            center.x, center.y + half * 0.7f
        )
        close()
    }
    drawPath(path, color = SoulRed)
}

// Draw different funny bullet types
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBullet(bullet: Bullet) {
    when (bullet.type) {
        BulletType.DOG_HEAD -> {
            // Little bouncing white dog head
            drawRoundRect(
                color = DogBoneWhite,
                topLeft = Offset(bullet.x - 12f, bullet.y - 10f),
                size = Size(24f, 20f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Black eyes
            drawCircle(
                color = UndertaleBlack,
                radius = 2.5f,
                center = Offset(bullet.x - 4f, bullet.y - 2f)
            )
            drawCircle(
                color = UndertaleBlack,
                radius = 2.5f,
                center = Offset(bullet.x + 4f, bullet.y - 2f)
            )
            // Black nose
            drawCircle(
                color = UndertaleBlack,
                radius = 2f,
                center = Offset(bullet.x, bullet.y + 3f)
            )
        }

        BulletType.MARACA -> {
            // Spinning maraca
            rotate(bullet.rotation, pivot = Offset(bullet.x, bullet.y)) {
                drawLine(
                    color = Color(0xFFD7A15C),
                    start = Offset(bullet.x, bullet.y + 12f),
                    end = Offset(bullet.x, bullet.y - 6f),
                    strokeWidth = 3f
                )
                drawCircle(
                    color = DeterminationGold,
                    radius = 9f,
                    center = Offset(bullet.x, bullet.y - 6f)
                )
            }
        }

        BulletType.BONE -> {
            // Classic Undertale Bone
            drawRoundRect(
                color = DogBoneWhite,
                topLeft = Offset(bullet.x - 4f, bullet.y - 12f),
                size = Size(8f, 24f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            drawCircle(color = DogBoneWhite, radius = 5f, center = Offset(bullet.x - 4f, bullet.y - 12f))
            drawCircle(color = DogBoneWhite, radius = 5f, center = Offset(bullet.x + 4f, bullet.y - 12f))
            drawCircle(color = DogBoneWhite, radius = 5f, center = Offset(bullet.x - 4f, bullet.y + 12f))
            drawCircle(color = DogBoneWhite, radius = 5f, center = Offset(bullet.x + 4f, bullet.y + 12f))
        }

        BulletType.BARK_WORD -> {
            // Cyan magic projectile
            drawCircle(
                color = DarkWorldCyan,
                radius = 12f,
                center = Offset(bullet.x, bullet.y)
            )
            drawCircle(
                color = DogBoneWhite,
                radius = 6f,
                center = Offset(bullet.x, bullet.y)
            )
        }
    }
}
