package com.mmt.guitarlab.ui.metronome

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.TimeSignature
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import com.mmt.guitarlab.domain.model.MetronomeSound
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.components.StudioPill
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricRuby
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.StudioCardBg
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MetronomeScreen(viewModel: MetronomeViewModel = hiltViewModel()) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val beat by viewModel.beat.collectAsStateWithLifecycle()
    val running by viewModel.running.collectAsStateWithLifecycle()

    val pulse by animateFloatAsState(
        targetValue = if (beat?.accent == true) 1.28f else if (beat != null) 1.12f else 1f,
        animationSpec = tween(80),
        label = "beatPulse",
    )

    val tempoName = when (config.bpm) {
        in 40..59 -> "Largo"
        in 60..65 -> "Larghetto"
        in 66..75 -> "Adagio"
        in 76..107 -> "Andante"
        in 108..119 -> "Moderato"
        in 120..155 -> "Allegro"
        in 156..199 -> "Vivace"
        else -> "Presto"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Hero Pulse Dial Card (Soundbrenner Style)
        StudioCard(
            modifier = Modifier.fillMaxWidth(),
            accentBorder = if (running && beat?.accent == true) ElectricAmber else null,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Beat Dots (1, 2, 3, 4...)
                BeatDots(
                    beats = config.timeSignature.beatsPerBar,
                    accents = config.timeSignature.accentBeats,
                    current = beat?.beatInBar,
                    pulse = pulse,
                )

                Spacer(Modifier.height(18.dp))

                // Big Glowing Pulse Wheel & BPM Display
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(if (running) pulse else 1f),
                    contentAlignment = Alignment.Center,
                ) {
                    TempoCircularWheel(
                        bpm = config.bpm,
                        running = running,
                        accent = beat?.accent == true,
                        modifier = Modifier.fillMaxSize(),
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "${config.bpm}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Black,
                            ),
                            color = if (running && beat?.accent == true) ElectricAmber else StudioTextPrimary,
                        )
                        Text(
                            text = "BPM · $tempoName",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (running) ElectricTeal else StudioTextSecondary,
                            letterSpacing = 1.2.sp,
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Fine Adjustment Buttons (-5, -1, +1, +5)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TempoStepButton(label = "-5", onClick = { viewModel.setBpm(config.bpm - 5) })
                    TempoStepButton(label = "-1", onClick = { viewModel.setBpm(config.bpm - 1) })
                    TempoStepButton(label = "+1", onClick = { viewModel.setBpm(config.bpm + 1) })
                    TempoStepButton(label = "+5", onClick = { viewModel.setBpm(config.bpm + 5) })
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Main Play / Stop Hero Action Bar & Tap Tempo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Tap Tempo 3D Button
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF262F42), Color(0xFF181E2B)),
                        ),
                    )
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(18.dp))
                    .clickable { viewModel.onTapTempo() }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Studio3DIconBadge(
                    icon = Icons.Default.TouchApp,
                    contentDescription = null,
                    size = 36.dp,
                    accent = Studio3DAccent.TEAL,
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "TAP TEMPO",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary,
                    )
                    Text(
                        text = "Tap to rhythm",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTextSecondary,
                    )
                }
            }

            // Play / Pause Master 3D Button
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = CircleShape,
                        ambientColor = if (running) ElectricRuby else ElectricAmber,
                        spotColor = if (running) ElectricRuby else ElectricAmber,
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            if (running) {
                                listOf(Color(0xFFFF5277), Color(0xFFFF2A55), Color(0xFFB80028))
                            } else {
                                listOf(Color(0xFFFFD166), ElectricAmber, Color(0xFFC47D00))
                            },
                        ),
                    )
                    .border(
                        1.5.dp,
                        Color.White.copy(alpha = 0.4f),
                        CircleShape,
                    )
                    .clickable { viewModel.toggle() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (running) "Stop" else "Start",
                    tint = if (running) Color.White else Color(0xFF1F1400),
                    modifier = Modifier.size(34.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Time Signature Selector
        StudioCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                    text = "TIME SIGNATURE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextMuted,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TimeSignature.entries.forEach { ts ->
                        val isSelected = config.timeSignature == ts
                        StudioPill(
                            text = ts.label,
                            selected = isSelected,
                            onClick = { viewModel.setTimeSignature(ts) },
                            accentColor = ElectricAmber,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Sound Selection & Silent Vibration Mode Card
        StudioCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                // Vibration Mode Toggle Row (Vibrate instead of sound)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Studio3DIconBadge(
                            icon = Icons.Default.Vibration,
                            contentDescription = "Вибрация",
                            size = 32.dp,
                            accent = if (config.vibrateOnly) Studio3DAccent.TEAL else Studio3DAccent.SLATE,
                        )
                        Column {
                            Text(
                                text = "РЕЖИМ ВИБРАЦИИ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary,
                            )
                            Text(
                                text = if (config.vibrateOnly) "Тактильные удары вместо звука" else "Звуковые клики включены",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (config.vibrateOnly) ElectricTeal else StudioTextSecondary,
                            )
                        }
                    }

                    Switch(
                        checked = config.vibrateOnly,
                        onCheckedChange = { viewModel.setVibrateOnly(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElectricTeal,
                            checkedTrackColor = Color(0xFF134E4A),
                            uncheckedThumbColor = StudioTextMuted,
                            uncheckedTrackColor = StudioCardBorder,
                        ),
                    )
                }

                if (!config.vibrateOnly) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "ВЫБОР ЗВУКА МЕТРОНОМА",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextMuted,
                        letterSpacing = 1.sp,
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        MetronomeSound.entries.forEach { sound ->
                            val isSelected = config.sound == sound
                            StudioPill(
                                text = sound.label,
                                selected = isSelected,
                                onClick = { viewModel.setSound(sound) },
                                accentColor = ElectricAmber,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Click Volume Card
        StudioCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "METRONOME VOLUME",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextMuted,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        text = "${(config.volume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElectricAmber,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeDown,
                        contentDescription = null,
                        tint = StudioTextSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                    Slider(
                        value = config.volume,
                        onValueChange = viewModel::setVolume,
                        valueRange = 0.1f..1f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricAmber,
                            activeTrackColor = ElectricAmber,
                            inactiveTrackColor = StudioCardBorder,
                        ),
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = ElectricAmber,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TempoStepButton(
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF232A3B), Color(0xFF141924)),
                ),
            )
            .border(1.dp, StudioCardBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = StudioTextPrimary,
        )
    }
}

@Composable
private fun BeatDots(beats: Int, accents: Set<Int>, current: Int?, pulse: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(beats) { index ->
            val beatNum = index + 1
            val active = current == beatNum
            val isAccent = beatNum in accents

            val dotColor = when {
                active && isAccent -> ElectricAmber
                active -> ElectricTeal
                isAccent -> ElectricAmber.copy(alpha = 0.35f)
                else -> Color(0xFF263042)
            }

            val dotSize = if (isAccent) 22.dp else 16.dp
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(if (active) pulse else 1f)
                    .clip(CircleShape)
                    .background(dotColor)
                    .border(
                        width = 1.dp,
                        color = if (active) Color.White.copy(alpha = 0.6f) else Color.Transparent,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isAccent) {
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Black,
                        color = if (active) Color(0xFF261800) else ElectricAmber,
                    )
                }
            }
        }
    }
}

@Composable
private fun TempoCircularWheel(
    bpm: Int,
    running: Boolean,
    accent: Boolean,
    modifier: Modifier = Modifier,
) {
    val trackColor = Color(0xFF1B2130)
    val activeGlow = when {
        running && accent -> ElectricAmber
        running -> ElectricTeal
        else -> Color(0xFF333E56)
    }

    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f - 12.dp.toPx()

        // Background Track
        drawCircle(
            color = trackColor,
            radius = radius,
            center = center,
            style = stroke,
        )

        // Progress Arc (Normalized between 40 and 240 BPM)
        val norm = ((bpm - 40f) / (240f - 40f)).coerceIn(0f, 1f)
        val sweepAngle = norm * 360f

        drawArc(
            color = activeGlow,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = stroke,
        )

        // Draw radial ticks around the wheel
        for (i in 0 until 40) {
            val angle = Math.toRadians((i * (360.0 / 40.0)))
            val inner = radius - 14.dp.toPx()
            val outer = radius - 8.dp.toPx()
            drawLine(
                color = Color(0xFF3B4863).copy(alpha = 0.6f),
                start = Offset(
                    center.x + inner * cos(angle).toFloat(),
                    center.y + inner * sin(angle).toFloat(),
                ),
                end = Offset(
                    center.x + outer * cos(angle).toFloat(),
                    center.y + outer * sin(angle).toFloat(),
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}
