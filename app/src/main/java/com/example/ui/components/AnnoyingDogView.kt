package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TobyExpression
import com.example.ui.theme.DarkWorldCyan
import com.example.ui.theme.DeterminationGold
import com.example.ui.theme.DogBoneWhite
import com.example.ui.theme.SoulRed
import com.example.ui.theme.UndertaleBlack
import com.example.ui.theme.UndertaleBorder
import com.example.ui.theme.UndertaleCardBg
import kotlinx.coroutines.launch

@Composable
fun AnnoyingDogView(
    expression: TobyExpression,
    isRetaliating: Boolean,
    onDogTapped: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }

    // Infinite breathing / idle bobbing
    val infiniteTransition = rememberInfiniteTransition(label = "dog_idle")
    val idleBob by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_bob"
    )

    val maracaRotate by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(180, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "maraca_shake"
    )

    val sweatSlide by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweat_slide"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(UndertaleCardBg)
            .border(2.dp, UndertaleBorder, RoundedCornerShape(8.dp))
            .testTag("annoying_dog_stage")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                coroutineScope.launch {
                    shakeOffset.snapTo(10f)
                    shakeOffset.animateTo(
                        0f,
                        animationSpec = tween(150)
                    )
                }
                onDogTapped(Offset(0f, 0f))
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // Desk / Table
            val deskY = canvasH * 0.72f
            drawRoundRect(
                color = Color(0xFF2A2B3D),
                topLeft = Offset(canvasW * 0.1f, deskY),
                size = Size(canvasW * 0.8f, canvasH * 0.24f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            // Desk outline
            drawRoundRect(
                color = UndertaleBorder,
                topLeft = Offset(canvasW * 0.1f, deskY),
                size = Size(canvasW * 0.8f, canvasH * 0.24f),
                cornerRadius = CornerRadius(6f, 6f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
            )

            // Laptop on desk
            val laptopW = 110f
            val laptopH = 70f
            val laptopX = canvasW * 0.68f
            val laptopY = deskY - 55f
            // Screen
            drawRect(
                color = Color(0xFF0F172A),
                topLeft = Offset(laptopX, laptopY),
                size = Size(laptopW, laptopH)
            )
            drawRect(
                color = UndertaleBorder,
                topLeft = Offset(laptopX, laptopY),
                size = Size(laptopW, laptopH),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
            )
            // Screen content: Red Lock & "CH 3"
            drawCircle(
                color = SoulRed,
                radius = 10f,
                center = Offset(laptopX + laptopW / 2, laptopY + laptopH / 2 - 6f)
            )
            drawRect(
                color = DeterminationGold,
                topLeft = Offset(laptopX + 15f, laptopY + laptopH - 22f),
                size = Size(laptopW - 30f, 10f)
            )

            // Annoying Dog Center Position
            val dogCenterX = (canvasW * 0.38f) + shakeOffset.value
            val dogCenterY = (canvasH * 0.52f) + (if (expression == TobyExpression.SLEEPING) 12f else idleBob)

            // Draw Dog Pixel Body
            drawAnnoyingDog(
                centerX = dogCenterX,
                centerY = dogCenterY,
                expression = expression,
                isRetaliating = isRetaliating,
                maracaAngle = maracaRotate,
                sweatOffset = sweatSlide
            )
        }

        // Overlay status badges on the chamber view
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .background(UndertaleBlack, RoundedCornerShape(4.dp))
                .border(1.dp, UndertaleBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isRetaliating) "STATUS: BARKING RAGE!" else "TARGET: TOBY FOX (ANNOYING DOG)",
                color = if (isRetaliating) SoulRed else DeterminationGold,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(UndertaleBlack, RoundedCornerShape(4.dp))
                .border(1.dp, UndertaleBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "TAP TO TICKLE / PERSUADE",
                color = DarkWorldCyan,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        }
    }
}

private fun DrawScope.drawAnnoyingDog(
    centerX: Float,
    centerY: Float,
    expression: TobyExpression,
    isRetaliating: Boolean,
    maracaAngle: Float,
    sweatOffset: Float
) {
    val pixelSize = 5.2f

    // Dog body blocks (Authentic Undertale Annoying Dog silhouette)
    // Dog Body Main White Block
    drawRoundRect(
        color = DogBoneWhite,
        topLeft = Offset(centerX - 45f, centerY - 25f),
        size = Size(80f, 55f),
        cornerRadius = CornerRadius(14f, 14f)
    )

    // Dog Head
    drawRoundRect(
        color = DogBoneWhite,
        topLeft = Offset(centerX + 15f, centerY - 55f),
        size = Size(52f, 48f),
        cornerRadius = CornerRadius(16f, 16f)
    )

    // Dog Snout / Muzzle
    drawRoundRect(
        color = DogBoneWhite,
        topLeft = Offset(centerX + 48f, centerY - 38f),
        size = Size(25f, 25f),
        cornerRadius = CornerRadius(8f, 8f)
    )

    // Dog Ears
    // Left Ear
    drawRoundRect(
        color = DogBoneWhite,
        topLeft = Offset(centerX + 18f, centerY - 72f),
        size = Size(18f, 24f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    // Right Ear
    drawRoundRect(
        color = DogBoneWhite,
        topLeft = Offset(centerX + 42f, centerY - 70f),
        size = Size(16f, 22f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Dog Legs (front & back paws)
    drawRoundRect(
        color = DogBoneWhite,
        topLeft = Offset(centerX - 35f, centerY + 25f),
        size = Size(18f, 28f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = DogBoneWhite,
        topLeft = Offset(centerX + 22f, centerY + 25f),
        size = Size(18f, 28f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Dog Tail (wiggles if not sleeping)
    val tailTilt = if (expression == TobyExpression.SLEEPING) 0f else maracaAngle
    rotate(tailTilt, pivot = Offset(centerX - 45f, centerY - 15f)) {
        drawRoundRect(
            color = DogBoneWhite,
            topLeft = Offset(centerX - 62f, centerY - 30f),
            size = Size(20f, 16f),
            cornerRadius = CornerRadius(6f, 6f)
        )
    }

    // Facial features based on TobyExpression
    val noseX = centerX + 66f
    val noseY = centerY - 32f

    // Black Nose
    drawOval(
        color = UndertaleBlack,
        topLeft = Offset(noseX, noseY),
        size = Size(11f, 8f)
    )

    // Eyes
    when (expression) {
        TobyExpression.SLEEPING -> {
            // Closed eyes (- -)
            drawLine(
                color = UndertaleBlack,
                start = Offset(centerX + 32f, centerY - 44f),
                end = Offset(centerX + 42f, centerY - 44f),
                strokeWidth = 3f
            )
            drawLine(
                color = UndertaleBlack,
                start = Offset(centerX + 50f, centerY - 44f),
                end = Offset(centerX + 60f, centerY - 44f),
                strokeWidth = 3f
            )
            // Floating "Z"
            drawLine(
                color = DarkWorldCyan,
                start = Offset(centerX + 65f, centerY - 75f),
                end = Offset(centerX + 77f, centerY - 75f),
                strokeWidth = 3f
            )
            drawLine(
                color = DarkWorldCyan,
                start = Offset(centerX + 77f, centerY - 75f),
                end = Offset(centerX + 65f, centerY - 62f),
                strokeWidth = 3f
            )
            drawLine(
                color = DarkWorldCyan,
                start = Offset(centerX + 65f, centerY - 62f),
                end = Offset(centerX + 77f, centerY - 62f),
                strokeWidth = 3f
            )
        }

        TobyExpression.SWEATING -> {
            // Worried wide dot eyes
            drawCircle(
                color = UndertaleBlack,
                radius = 4.5f,
                center = Offset(centerX + 36f, centerY - 44f)
            )
            drawCircle(
                color = UndertaleBlack,
                radius = 4.5f,
                center = Offset(centerX + 54f, centerY - 44f)
            )
            // Mouth wobbly
            drawLine(
                color = UndertaleBlack,
                start = Offset(centerX + 48f, centerY - 25f),
                end = Offset(centerX + 62f, centerY - 25f),
                strokeWidth = 2.5f
            )
            // Blue Sweatdrop sliding down
            drawOval(
                color = DarkWorldCyan,
                topLeft = Offset(centerX + 20f, centerY - 65f + sweatOffset),
                size = Size(8f, 14f)
            )
        }

        TobyExpression.BARKING -> {
            // Big open mouth barking
            drawCircle(
                color = UndertaleBlack,
                radius = 4f,
                center = Offset(centerX + 36f, centerY - 46f)
            )
            drawCircle(
                color = UndertaleBlack,
                radius = 4f,
                center = Offset(centerX + 54f, centerY - 46f)
            )
            // Open mouth
            drawRoundRect(
                color = UndertaleBlack,
                topLeft = Offset(centerX + 50f, centerY - 28f),
                size = Size(18f, 15f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Pink tongue
            drawRoundRect(
                color = SoulRed,
                topLeft = Offset(centerX + 53f, centerY - 22f),
                size = Size(12f, 8f),
                cornerRadius = CornerRadius(3f, 3f)
            )
            // Bark lines
            drawLine(
                color = DeterminationGold,
                start = Offset(centerX + 75f, centerY - 42f),
                end = Offset(centerX + 90f, centerY - 48f),
                strokeWidth = 3f
            )
            drawLine(
                color = DeterminationGold,
                start = Offset(centerX + 76f, centerY - 25f),
                end = Offset(centerX + 93f, centerY - 25f),
                strokeWidth = 3f
            )
        }

        TobyExpression.MARACAS -> {
            // Joyous closed happy curve eyes (^^)
            drawArc(
                color = UndertaleBlack,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX + 32f, centerY - 47f),
                size = Size(10f, 8f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(3f)
            )
            drawArc(
                color = UndertaleBlack,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX + 50f, centerY - 47f),
                size = Size(10f, 8f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(3f)
            )
            // Smile
            drawArc(
                color = UndertaleBlack,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX + 50f, centerY - 28f),
                size = Size(14f, 8f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(2.5f)
            )

            // Red & Green Maracas in Paws!
            rotate(maracaAngle, pivot = Offset(centerX + 38f, centerY - 5f)) {
                // Stick
                drawLine(
                    color = Color(0xFFD7A15C),
                    start = Offset(centerX + 38f, centerY - 5f),
                    end = Offset(centerX + 38f, centerY - 32f),
                    strokeWidth = 4f
                )
                // Head
                drawCircle(
                    color = SoulRed,
                    radius = 12f,
                    center = Offset(centerX + 38f, centerY - 35f)
                )
            }
            rotate(-maracaAngle, pivot = Offset(centerX + 62f, centerY - 5f)) {
                drawLine(
                    color = Color(0xFFD7A15C),
                    start = Offset(centerX + 62f, centerY - 5f),
                    end = Offset(centerX + 62f, centerY - 32f),
                    strokeWidth = 4f
                )
                drawCircle(
                    color = Color(0xFF22C55E),
                    radius = 12f,
                    center = Offset(centerX + 62f, centerY - 35f)
                )
            }
        }

        TobyExpression.SURRENDERED -> {
            // White flag waving
            drawCircle(
                color = UndertaleBlack,
                radius = 5f,
                center = Offset(centerX + 35f, centerY - 43f)
            )
            drawCircle(
                color = UndertaleBlack,
                radius = 5f,
                center = Offset(centerX + 53f, centerY - 43f)
            )
            // Droopy sad mouth
            drawArc(
                color = UndertaleBlack,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX + 48f, centerY - 23f),
                size = Size(14f, 8f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(2.5f)
            )
            // White flag in mouth/paw
            drawLine(
                color = Color.LightGray,
                start = Offset(centerX + 55f, centerY - 20f),
                end = Offset(centerX + 55f, centerY - 70f),
                strokeWidth = 3f
            )
            drawRect(
                color = DogBoneWhite,
                topLeft = Offset(centerX + 55f, centerY - 70f),
                size = Size(26f, 18f)
            )
        }

        else -> {
            // Classic neutral eyes and smile
            drawCircle(
                color = UndertaleBlack,
                radius = 4f,
                center = Offset(centerX + 36f, centerY - 44f)
            )
            drawCircle(
                color = UndertaleBlack,
                radius = 4f,
                center = Offset(centerX + 54f, centerY - 44f)
            )
            // Cute neutral mouth line
            drawLine(
                color = UndertaleBlack,
                start = Offset(centerX + 50f, centerY - 26f),
                end = Offset(centerX + 62f, centerY - 26f),
                strokeWidth = 2.5f
            )
        }
    }

    // Rage aura when retaliating
    if (isRetaliating) {
        drawCircle(
            color = SoulRed.copy(alpha = 0.25f),
            radius = 80f,
            center = Offset(centerX, centerY)
        )
    }
}
