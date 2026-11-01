package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.model.BalanceDemand
import com.example.model.Bullet
import com.example.model.BulletType
import com.example.model.Chapter3GlitchBug
import com.example.model.DamageParticle
import com.example.model.TargetGame
import com.example.model.TobyExpression
import com.example.model.TortureTactic
import com.example.ui.theme.DarkWorldCyan
import com.example.ui.theme.DeterminationGold
import com.example.ui.theme.MercyGreen
import com.example.ui.theme.SoulRed
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameUiState(
    val tobyStubbornness: Long = 1000000L,
    val maxStubbornness: Long = 1000000L,
    val totalDamageDealt: Long = 0L,
    val dogResidue: Long = 30L,
    val passiveResiduePerSec: Long = 1L,
    val tobyExpression: TobyExpression = TobyExpression.NEUTRAL,
    val dialogueText: String = "Toby Fox sits stubbornly at his keyboard. 'Chapter 3 comes out when it's done... unless you have notes?'",
    val isRetaliating: Boolean = false,
    val particles: List<DamageParticle> = emptyList(),
    val tactics: List<TortureTactic> = emptyList(),
    val balanceDemands: List<BalanceDemand> = emptyList(),
    val totalClicks: Long = 0L,
    val stageLevel: Int = 1,
    // Mini-game state
    val arenaActive: Boolean = false,
    val soulHp: Int = 50,
    val maxSoulHp: Int = 50,
    val soulPos: Offset = Offset(200f, 200f),
    val bullets: List<Bullet> = emptyList(),
    val arenaTimerSec: Int = 0,
    val arenaHighScoreSec: Int = 0,
    // Glitch / Early Release simulator
    val glitchSimulationActive: Boolean = false,
    val glitchBugs: List<Chapter3GlitchBug> = emptyList(),
    // Settings
    val isSoundOn: Boolean = true,
    val isHapticsOn: Boolean = true
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val soundManager = SoundManager(application)
    private val prefs = application.getSharedPreferences("deltapatch_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var particleCounter = 0L
    private var bulletCounter = 0L
    private var arenaGameJob: Job? = null
    private var passiveIncomeJob: Job? = null

    init {
        loadInitialData()
        startPassiveLoop()
    }

    private fun loadInitialData() {
        val defaultTactics = listOf(
            TortureTactic(
                id = "tickle_temmie",
                name = "Tickle with Temmie Flakes",
                description = "Lightly brush Toby's paws with premium dog treats. Produces gentle yips and dog residue.",
                stressDamage = 15L,
                residueCost = 0L,
                levelRequired = 1,
                iconKey = "flake",
                quoteReaction = "* Toby yips softly. Dog residue sprinkles from his fur!"
            ),
            TortureTactic(
                id = "kazoo_megalovania",
                name = "Megalovania Kazoo Loop",
                description = "Play a slightly off-key 10-hour Megalovania kazoo cover through his studio headphones.",
                stressDamage = 120L,
                residueCost = 50L,
                levelRequired = 1,
                iconKey = "kazoo",
                quoteReaction = "* 'Not the kazoo cover again!' Toby covers his floppy ears."
            ),
            TortureTactic(
                id = "wiimote_sans",
                name = "Inverted Broken Wiimote Sans",
                description = "Force Toby to beat the Sans genocide fight using a third-party Wiimote with stick drift.",
                stressDamage = 650L,
                residueCost = 200L,
                levelRequired = 2,
                iconKey = "gamepad",
                quoteReaction = "* Toby suffers bad time flashbacks. 'Fine, I will look at the platform collision!'"
            ),
            TortureTactic(
                id = "jevil_carousel",
                name = "Endless Jevil Scythe Carousel",
                description = "Trap Toby in a simulation of Jevil's chaos carousel at 200% speed with no healing items.",
                stressDamage = 2800L,
                residueCost = 800L,
                levelRequired = 3,
                iconKey = "scythe",
                quoteReaction = "* 'CHAOS CHAOS! Okay I admit the scythe hitboxes were a little chunky!'"
            ),
            TortureTactic(
                id = "threaten_soundfonts",
                name = "Threaten Deltarune Soundfonts",
                description = "Hold his vintage Roland SC-88 and Casio synths hostage in a temperature-controlled vault.",
                stressDamage = 12000L,
                residueCost = 3000L,
                levelRequired = 4,
                iconKey = "synth",
                quoteReaction = "* 'NOOO! NOT MY PIANO SAMPLES! ANYTHING BUT THE MARACAS!'"
            ),
            TortureTactic(
                id = "papyrus_spaghetti",
                name = "Serve Papyrus's Frozen Spaghetti",
                description = "Place a plate of Papyrus's spaghetti, frozen solid to the dining table, on his work desk.",
                stressDamage = 45000L,
                residueCost = 10000L,
                levelRequired = 5,
                iconKey = "spaghetti",
                quoteReaction = "* The cold spaghetti emits a menacing aura. Toby's teeth chatter loudly."
            ),
            TortureTactic(
                id = "gamemaker_98",
                name = "Lock in GameMaker 7 on Win98",
                description = "Downgrade his workstation to Windows 98 with 128MB RAM and GameMaker 7. 45-minute compile times.",
                stressDamage = 180000L,
                residueCost = 40000L,
                levelRequired = 6,
                iconKey = "computer",
                quoteReaction = "* Toby stares in horror at the hourglass cursor. 'I will fix the graze boxes immediately!'"
            ),
            TortureTactic(
                id = "spamton_drift",
                name = "Spamton NEO with 1 Hand",
                description = "Make him fight Spamton NEO with one hand tied while Big Shot blasts at 120dB.",
                stressDamage = 600000L,
                residueCost = 150000L,
                levelRequired = 7,
                iconKey = "bigshot",
                quoteReaction = "* '[[BIG SHOT]] NERFED! I AM CALLING THE JANITOR TO REBALANCE PIPIS!'"
            ),
            TortureTactic(
                id = "steal_maracas",
                name = "Confiscate Toby's Maracas",
                description = "The ultimate psychological weapon: take away his pair of fiesta maracas.",
                stressDamage = 2500000L,
                residueCost = 500000L,
                levelRequired = 8,
                iconKey = "maraca",
                quoteReaction = "* The Annoying Dog loses all rhythm and collapses into a puddle of surrender!"
            )
        )

        val defaultDemands = listOf(
            BalanceDemand(
                id = "ut_sans_rng",
                game = TargetGame.UNDERTALE,
                title = "Sans Final Attack Platform RNG",
                problem = "The bone elevator platforms in the final attack phase have randomized spawn heights that can clip the SOUL into unavoidable bones.",
                patchFix = "Fix platform heights to deterministic intervals and add 4 invincibility frames when jumping onto moving bones.",
                stressThreshold = 500L,
                patchVersion = "UT v1.08b",
                tobyNote = "* Toby groans: 'Fine, Sans now stops giggling when the platform clips you.'"
            ),
            BalanceDemand(
                id = "ut_undyne_colorblind",
                game = TargetGame.UNDERTALE,
                title = "Undyne Undying Yellow Arrow Clarity",
                problem = "Colorblind players cannot easily distinguish incoming reverse yellow arrows from normal blue/green arrows during high speed phases.",
                patchFix = "Added distinct double-chevron visual indicators and subtle pitch-shifted audio cues for reverse arrows.",
                stressThreshold = 2000L,
                patchVersion = "UT v1.09",
                tobyNote = "* 'I added little wings to the arrows. Undyne said it looks stylish.'"
            ),
            BalanceDemand(
                id = "ut_asgore_shortcut",
                game = TargetGame.UNDERTALE,
                title = "Asgore Walk of Shame Shortcut",
                problem = "Dying to Asgore forces players to walk 5 long corridor screens from the castle save point repeatedly.",
                patchFix = "Added a golden flower warp bench right in front of the throne room door.",
                stressThreshold = 6000L,
                patchVersion = "UT v1.10",
                tobyNote = "* 'Now you can sit on a bench and drink tea instead of walking forever.'"
            ),
            BalanceDemand(
                id = "ut_muffet_donut",
                game = TargetGame.UNDERTALE,
                title = "Muffet Donut Skipping Balance",
                problem = "If a player buys a Spider Donut in the ruins for 7G, Muffet instant-mercy skips the fight, making her iconic theme barely audible.",
                patchFix = "Spider Donut now unlocks an optional 'Encore Tea Dance' mode so players can still enjoy Spider Dance with full boss mechanics.",
                stressThreshold = 18000L,
                patchVersion = "UT v1.11",
                tobyNote = "* 'Muffet agreed to play the accordion remix if you bring pastries.'"
            ),
            BalanceDemand(
                id = "dr_jevil_hitboxes",
                game = TargetGame.DELTARUNE,
                title = "Jevil Carousel Hitbox Normalization",
                problem = "Devilsknife scythe sprites in Chapter 1 have rectangular collision boxes 30% wider than the actual glowing blade visual.",
                patchFix = "Reshaped scythe colliders to tightly match the crescent blade pixels and reduced rotation acceleration by 10%.",
                stressThreshold = 50000L,
                patchVersion = "DR Ch1 v1.2",
                tobyNote = "* 'The scythes are now aerodynamically balanced. Jevil is mildly annoyed.'"
            ),
            BalanceDemand(
                id = "dr_ralsei_def",
                game = TargetGame.DELTARUNE,
                title = "Ralsei Base Defense Buff in Ch 1",
                problem = "Ralsei starts with 0 DEF in Chapter 1 and gets frequently one-shot by basic Clover or K.Round attacks before he can cast Heal Prayer.",
                patchFix = "Increased Ralsei's base DEF from 0 to 4 and gave the Dainty Scarf +1 extra magic.",
                stressThreshold = 140000L,
                patchVersion = "DR Ch1 v1.3",
                tobyNote = "* 'I knitted Ralsei a slightly thicker green scarf. He gave me a hug.'"
            ),
            BalanceDemand(
                id = "dr_graze_spark",
                game = TargetGame.DELTARUNE,
                title = "TP Graze Spark Radius Clarity",
                problem = "Grazing bullets generates tension points, but players cannot gauge the exact safety threshold without risking collision.",
                patchFix = "Added a pulsing cyan halo around the SOUL when grazing and boosted Susie's Rude Buster efficiency.",
                stressThreshold = 400000L,
                patchVersion = "DR Ch2 v1.1",
                tobyNote = "* 'The graze ring now sparkles like anime magic. Susie approves.'"
            ),
            BalanceDemand(
                id = "dr_spamton_pipis",
                game = TargetGame.DELTARUNE,
                title = "Spamton NEO Pipis Lingering Splash",
                problem = "Pipis projectiles in the snowgrave route leave lingering blue blast hitboxes for 6 frames after visual detonation.",
                patchFix = "Pipis explosions now dissolve instantaneously into harmless celebratory confetti when shot down.",
                stressThreshold = 1000000L,
                patchVersion = "DR Ch2 v1.2",
                tobyNote = "* 'Pipis splash frames deleted. Spamton sent me an invoice for [[KROMER]].'"
            ),
            BalanceDemand(
                id = "dr_queen_acid",
                game = TargetGame.DELTARUNE,
                title = "Queen Acid River Platforming Flow",
                problem = "The swan boat rhythm section has awkward turn lag on Android and mobile inputs.",
                patchFix = "Smoothed steering responsiveness and added generous buffer inputs on directional taps.",
                stressThreshold = 2500000L,
                patchVersion = "DR Ch2 v1.3",
                tobyNote = "* Queen typed: 'LMAO Skill Issue Officially Patched In Source Code'."
            )
        )

        _uiState.update {
            it.copy(
                tactics = defaultTactics,
                balanceDemands = defaultDemands
            )
        }
    }

    private fun startPassiveLoop() {
        passiveIncomeJob?.cancel()
        passiveIncomeJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    val gain = state.passiveResiduePerSec
                    val newResidue = state.dogResidue + gain
                    state.copy(dogResidue = newResidue)
                }
            }
        }
    }

    fun tapDog(tapOffset: Offset) {
        val clickBonus = 5L + (_uiState.value.stageLevel * 2L)
        soundManager.playDogBark()

        // Random dog expression on tap
        val randomExp = when (Random.nextInt(5)) {
            0 -> TobyExpression.BARKING
            1 -> TobyExpression.SWEATING
            2 -> TobyExpression.MARACAS
            3 -> TobyExpression.CONFUSED
            else -> TobyExpression.NEUTRAL
        }

        applyDamage(damage = clickBonus, isManualTap = true)

        _uiState.update { state ->
            val randomReaction = when (Random.nextInt(4)) {
                0 -> "* Toby barks furiously: 'BARK BARK! Stop tickling me!'"
                1 -> "* You tapped the dog. Dog residue clings to your fingers."
                2 -> "* Toby shakes a maraca in defiance!"
                else -> "* The dog absorbs your criticism and turns into a sphere."
            }

            state.copy(
                dogResidue = state.dogResidue + (2L + state.stageLevel),
                totalClicks = state.totalClicks + 1L,
                tobyExpression = randomExp,
                dialogueText = randomReaction
            )
        }
    }

    fun executeTactic(tactic: TortureTactic) {
        val state = _uiState.value
        if (state.dogResidue < tactic.residueCost) return

        soundManager.playCrit()
        applyDamage(damage = tactic.stressDamage, isManualTap = false)

        val newPassive = state.passiveResiduePerSec + (tactic.stressDamage / 50L).coerceAtLeast(1L)

        _uiState.update {
            it.copy(
                dogResidue = it.dogResidue - tactic.residueCost,
                passiveResiduePerSec = newPassive,
                tobyExpression = TobyExpression.SWEATING,
                dialogueText = tactic.quoteReaction
            )
        }
    }

    private fun applyDamage(damage: Long, isManualTap: Boolean) {
        _uiState.update { state ->
            val newStubbornness = (state.tobyStubbornness - damage).coerceAtLeast(0L)
            val newTotalDamage = state.totalDamageDealt + damage

            // Auto-advance stage level if stubbornness reaches 0
            val shouldAdvance = newStubbornness <= 0
            val nextLevel = if (shouldAdvance) state.stageLevel + 1 else state.stageLevel
            val nextMax = if (shouldAdvance) state.maxStubbornness * 2L else state.maxStubbornness
            val resetStubbornness = if (shouldAdvance) nextMax else newStubbornness

            // Auto-check balance demands that can be enforced
            val updatedDemands = state.balanceDemands.map { demand ->
                if (!demand.isEnforced && newTotalDamage >= demand.stressThreshold) {
                    demand.copy(isEnforced = true)
                } else demand
            }

            // Spawn floating damage particle
            val particleId = ++particleCounter
            val newParticle = DamageParticle(
                id = particleId,
                text = if (isManualTap) "-$damage HP" else "PERSUASION -$damage!",
                xOffset = (Random.nextFloat() * 120f) - 60f,
                yOffset = -30f,
                color = if (isManualTap) MercyGreen else SoulRed
            )

            state.copy(
                tobyStubbornness = resetStubbornness,
                maxStubbornness = nextMax,
                totalDamageDealt = newTotalDamage,
                stageLevel = nextLevel,
                balanceDemands = updatedDemands,
                particles = (state.particles + newParticle).takeLast(6)
            )
        }
    }

    fun dismissParticle(id: Long) {
        _uiState.update { state ->
            state.copy(particles = state.particles.filterNot { it.id == id })
        }
    }

    // --- Dodge Arena Mini-game ---

    fun startArenaGame() {
        arenaGameJob?.cancel()
        soundManager.playFanfare()

        _uiState.update {
            it.copy(
                arenaActive = true,
                soulHp = it.maxSoulHp,
                soulPos = Offset(180f, 180f),
                bullets = emptyList(),
                arenaTimerSec = 0,
                isRetaliating = true,
                tobyExpression = TobyExpression.BARKING,
                dialogueText = "* Toby Fox enters RETALIATION MODE! Dodge his annoying dogs, maracas, and bug reports!"
            )
        }

        arenaGameJob = viewModelScope.launch {
            var ticks = 0
            while (_uiState.value.arenaActive) {
                delay(30)
                ticks++

                // Every 1 second
                if (ticks % 33 == 0) {
                    val newTimer = _uiState.value.arenaTimerSec + 1
                    val newHighScore = maxOf(_uiState.value.arenaHighScoreSec, newTimer)
                    // Determination bonus
                    _uiState.update {
                        it.copy(
                            arenaTimerSec = newTimer,
                            arenaHighScoreSec = newHighScore,
                            dogResidue = it.dogResidue + (newTimer * 5L)
                        )
                    }
                }

                // Spawn bullets
                if (ticks % 15 == 0) {
                    spawnRandomBullet()
                }

                // Update bullet positions and check collisions
                updateBulletsAndCheckCollision()
            }
        }
    }

    private fun spawnRandomBullet() {
        val arenaSize = 360f
        val edge = Random.nextInt(4)
        val (startX, startY) = when (edge) {
            0 -> Pair(Random.nextFloat() * arenaSize, 0f) // top
            1 -> Pair(Random.nextFloat() * arenaSize, arenaSize) // bottom
            2 -> Pair(0f, Random.nextFloat() * arenaSize) // left
            else -> Pair(arenaSize, Random.nextFloat() * arenaSize) // right
        }

        val targetX = arenaSize / 2f + (Random.nextFloat() * 100f - 50f)
        val targetY = arenaSize / 2f + (Random.nextFloat() * 100f - 50f)
        val dx = targetX - startX
        val dy = targetY - startY
        val dist = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
        val speed = 3.5f + Random.nextFloat() * 3.5f

        val bulletType = when (Random.nextInt(4)) {
            0 -> BulletType.DOG_HEAD
            1 -> BulletType.MARACA
            2 -> BulletType.BONE
            else -> BulletType.BARK_WORD
        }

        val newBullet = Bullet(
            id = ++bulletCounter,
            x = startX,
            y = startY,
            vx = (dx / dist) * speed,
            vy = (dy / dist) * speed,
            radius = when (bulletType) {
                BulletType.DOG_HEAD -> 14f
                BulletType.MARACA -> 12f
                BulletType.BONE -> 10f
                BulletType.BARK_WORD -> 16f
            },
            type = bulletType,
            rotation = Random.nextFloat() * 360f
        )

        _uiState.update { state ->
            it_bullets_add(state, newBullet)
        }
    }

    private fun it_bullets_add(state: GameUiState, bullet: Bullet): GameUiState {
        return state.copy(bullets = (state.bullets + bullet).takeLast(25))
    }

    private fun updateBulletsAndCheckCollision() {
        val soulPos = _uiState.value.soulPos
        val soulRadius = 10f
        var hitOccurred = false

        val updatedBullets = _uiState.value.bullets.mapNotNull { b ->
            val nx = b.x + b.vx
            val ny = b.y + b.vy

            // Check collision with Soul
            val distSq = (nx - soulPos.x) * (nx - soulPos.x) + (ny - soulPos.y) * (ny - soulPos.y)
            val combinedRadius = soulRadius + b.radius
            if (distSq < combinedRadius * combinedRadius) {
                hitOccurred = true
                null // consume bullet on hit
            } else if (nx < -40 || nx > 400 || ny < -40 || ny > 400) {
                null // out of bounds
            } else {
                b.copy(x = nx, y = ny)
            }
        }

        if (hitOccurred) {
            soundManager.playSoulHurt()
            val newHp = (_uiState.value.soulHp - 10).coerceAtLeast(0)
            if (newHp <= 0) {
                stopArenaGame(defeated = true)
                return
            }
            _uiState.update { it.copy(bullets = updatedBullets, soulHp = newHp) }
        } else {
            _uiState.update { it.copy(bullets = updatedBullets) }
        }
    }

    fun updateSoulPosition(newPos: Offset) {
        val clampedX = newPos.x.coerceIn(20f, 340f)
        val clampedY = newPos.y.coerceIn(20f, 340f)
        _uiState.update { it.copy(soulPos = Offset(clampedX, clampedY)) }
    }

    fun stopArenaGame(defeated: Boolean = false) {
        arenaGameJob?.cancel()
        val earnedResidue = _uiState.value.arenaTimerSec * 25L

        if (defeated) {
            _uiState.update {
                it.copy(
                    arenaActive = false,
                    isRetaliating = false,
                    tobyExpression = TobyExpression.MARACAS,
                    dialogueText = "* YOU WERE OVERWHELMED BY BARKING! Toby wiggles his maracas triumphantly."
                )
            }
        } else {
            soundManager.playFanfare()
            applyDamage(earnedResidue * 2L, isManualTap = false)
            _uiState.update {
                it.copy(
                    arenaActive = false,
                    isRetaliating = false,
                    dogResidue = it.dogResidue + earnedResidue,
                    tobyExpression = TobyExpression.SURRENDERED,
                    dialogueText = "* You survived Toby's retaliation! Earned $earnedResidue Dog Residue and broke his stubbornness!"
                )
            }
        }
    }

    // --- Premature Release Simulator ---

    fun triggerGlitchReleaseSimulation() {
        soundManager.playSoulHurt()
        val funnyBugs = listOf(
            Chapter3GlitchBug(
                title = "Ralsei Inverted Sprite",
                description = "Ralsei spawns upside down and shoots lasers from his hat at your party members.",
                severity = "CRITICAL"
            ),
            Chapter3GlitchBug(
                title = "Jevil Second Phase in Classroom",
                description = "Entering Kris's classroom initiates an unskippable 45-minute Jevil carousel boss battle.",
                severity = "CHAOS"
            ),
            Chapter3GlitchBug(
                title = "Susie Infinite Chalk Glitch",
                description = "Susie eats all classroom chalk and reaches 9999 ATK, instantly destroying the dark fountain in Chapter 1.",
                severity = "GAME-BREAKING"
            ),
            Chapter3GlitchBug(
                title = "Sans Playing Trombone in Every Room",
                description = "Sans appears in the corner of every Dark World room playing a single trombone note whenever you open your inventory.",
                severity = "HILARIOUS"
            ),
            Chapter3GlitchBug(
                title = "Spamton Took Over the Title Screen",
                description = "The title screen only plays the Big Shot trumpet loop on 300% volume and sells Pipis for real Kromer.",
                severity = "FATAL"
            )
        )

        _uiState.update {
            it.copy(
                glitchSimulationActive = true,
                glitchBugs = funnyBugs,
                tobyExpression = TobyExpression.CONFUSED,
                dialogueText = "* ERROR: UNBALANCED CHAPTER 3 LAUNCHED! The codebase is falling apart! Quick, rewind time!"
            )
        }
    }

    fun rewindGlitchSimulation() {
        soundManager.playFanfare()
        _uiState.update {
            it.copy(
                glitchSimulationActive = false,
                tobyExpression = TobyExpression.SWEATING,
                dialogueText = "* Time rewound! Toby is shaken: 'Okay, okay, I will finish balancing the previous games first! Don't release that again!'"
            )
        }
    }

    fun toggleSound() {
        val next = !_uiState.value.isSoundOn
        soundManager.isSoundEnabled = next
        _uiState.update { it.copy(isSoundOn = next) }
    }

    fun toggleHaptics() {
        val next = !_uiState.value.isHapticsOn
        soundManager.isHapticsEnabled = next
        _uiState.update { it.copy(isHapticsOn = next) }
    }
}
