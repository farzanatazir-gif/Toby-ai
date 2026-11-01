package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DamageParticle
import com.example.ui.theme.DarkWorldCyan
import com.example.ui.theme.DeterminationGold
import com.example.ui.theme.DogBoneWhite
import com.example.ui.theme.MercyGreen
import com.example.ui.theme.SoulRed
import com.example.ui.theme.TextGray
import com.example.ui.theme.UndertaleBlack
import com.example.ui.theme.UndertaleBorder
import com.example.ui.theme.UndertaleCardBg
import kotlinx.coroutines.delay

@Composable
fun UndertaleBox(
    modifier: Modifier = Modifier,
    borderColor: Color = UndertaleBorder,
    backgroundColor: Color = UndertaleBlack,
    headerTitle: String? = null,
    headerColor: Color = DeterminationGold,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(2.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(12.dp)
    ) {
        if (headerTitle != null) {
            Text(
                text = headerTitle,
                color = headerColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        content()
    }
}

@Composable
fun TypewriterDialogBox(
    text: String,
    onCharTyped: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var displayedChars by remember(text) { mutableStateOf(0) }

    LaunchedEffect(text) {
        displayedChars = 0
        while (displayedChars < text.length) {
            delay(18)
            displayedChars++
            if (displayedChars % 3 == 0) {
                onCharTyped()
            }
        }
    }

    UndertaleBox(
        modifier = modifier,
        borderColor = UndertaleBorder,
        backgroundColor = UndertaleBlack
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "* ",
                color = DogBoneWhite,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = text.take(displayedChars),
                color = DogBoneWhite,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 13.5.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun UndertaleHpBar(
    currentValue: Long,
    maxValue: Long,
    label: String = "TOBY STUBBORNNESS",
    fillColor: Color = DeterminationGold,
    modifier: Modifier = Modifier
) {
    val progress = (currentValue.toFloat() / maxValue.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = DogBoneWhite,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = "$currentValue / $maxValue HP",
                color = fillColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(SoulRed)
                .border(1.5.dp, DogBoneWhite, RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress)
                    .height(14.dp)
                    .background(fillColor)
            )
        }
    }
}

@Composable
fun RetroActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentColor: Color = DeterminationGold,
    subtitle: String? = null,
    testTagId: String = "action_btn"
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (enabled) UndertaleCardBg else Color(0xFF14141A))
            .border(
                width = 2.dp,
                color = if (enabled) (if (isPressed) DeterminationGold else accentColor) else TextGray.copy(alpha = 0.4f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(enabled = enabled) {
                isPressed = true
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag(testTagId)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = if (enabled) (if (isPressed) SoulRed else accentColor) else TextGray,
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 6.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = text.uppercase(),
                    color = if (enabled) DogBoneWhite else TextGray,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = if (enabled) accentColor else TextGray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingParticle(
    particle: DamageParticle,
    onDismiss: () -> Unit
) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(particle.id) {
        offsetY.animateTo(
            targetValue = -60f,
            animationSpec = tween(600)
        )
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(250)
        )
        onDismiss()
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(particle.xOffset.toInt(), (particle.yOffset + offsetY.value).toInt()) }
    ) {
        Text(
            text = particle.text,
            color = particle.color.copy(alpha = alpha.value),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp
        )
    }
}
