package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.DarkWorldCyan
import com.example.ui.theme.DeterminationGold
import com.example.ui.theme.MercyGreen
import com.example.ui.theme.SoulRed

enum class TobyExpression {
    NEUTRAL,
    SWEATING,
    BARKING,
    SLEEPING,
    MARACAS,
    CONFUSED,
    SURRENDERED
}

enum class TargetGame {
    UNDERTALE,
    DELTARUNE
}

data class TortureTactic(
    val id: String,
    val name: String,
    val description: String,
    val stressDamage: Long,
    val residueCost: Long,
    val levelRequired: Int,
    val iconKey: String,
    val quoteReaction: String
)

data class BalanceDemand(
    val id: String,
    val game: TargetGame,
    val title: String,
    val problem: String,
    val patchFix: String,
    val stressThreshold: Long,
    val isEnforced: Boolean = false,
    val patchVersion: String,
    val tobyNote: String
)

data class DamageParticle(
    val id: Long,
    val text: String,
    val xOffset: Float,
    val yOffset: Float,
    val color: Color = SoulRed
)

enum class BulletType {
    BONE,
    DOG_HEAD,
    MARACA,
    BARK_WORD
}

data class Bullet(
    val id: Long,
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val radius: Float,
    val type: BulletType,
    val rotation: Float = 0f
)

data class Chapter3GlitchBug(
    val title: String,
    val description: String,
    val severity: String
)
