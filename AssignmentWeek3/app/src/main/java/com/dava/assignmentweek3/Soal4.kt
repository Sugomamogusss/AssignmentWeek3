package com.dava.assignmentweek3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class Rps(val emoji: String, val label: String) {
    ROCK("✊", "Rock"),
    PAPER("✋", "Paper"),
    SCISSOR("✌️", "Scissor");

    fun beats(other: Rps): Boolean = when (this) {
        ROCK -> other == SCISSOR
        PAPER -> other == ROCK
        SCISSOR -> other == PAPER
    }
}

enum class RpsState { INITIAL, PICK, REVEAL, FINISHED }

enum class Result(val text: String) {
    WIN("You Win!"),
    LOSE("You Lose"),
    DRAW("Draw")
}

private const val BEST_OF = 5
private const val WIN_TARGET = BEST_OF / 2 + 1

@Composable
fun Soal4() {
    var state by rememberSaveable { mutableStateOf(RpsState.INITIAL) }
    var yourScore by rememberSaveable { mutableStateOf(0) }
    var cpuScore by rememberSaveable { mutableStateOf(0) }
    var bestScore by rememberSaveable { mutableStateOf<Int?>(null) }

    var playerPick by rememberSaveable { mutableStateOf<Rps?>(null) }
    var cpuPick by rememberSaveable { mutableStateOf<Rps?>(null) }
    var result by rememberSaveable { mutableStateOf<Result?>(null) }

    var buttonOrder by remember { mutableStateOf(Rps.entries.shuffled()) }

    fun resetMatch() {
        yourScore = 0
        cpuScore = 0
        playerPick = null
        cpuPick = null
        result = null
        buttonOrder = Rps.entries.shuffled()
    }

    LaunchedEffect(state) {
        if (state == RpsState.REVEAL) {
            delay(700)
            if (yourScore >= WIN_TARGET || cpuScore >= WIN_TARGET) {
                val diff = yourScore - cpuScore
                if (bestScore == null || diff > bestScore!!) bestScore = diff
                state = RpsState.FINISHED
            } else {
                playerPick = null
                cpuPick = null
                result = null
                buttonOrder = Rps.entries.shuffled()
                state = RpsState.PICK
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "🧑 $yourScore — $cpuScore 🤖",
                fontSize = 16.sp,
                modifier = Modifier.semantics {
                    contentDescription = "Score: you $yourScore, computer $cpuScore"
                }
            )
            Text("Best of $BEST_OF", fontSize = 12.sp)
        }

        when (state) {
            RpsState.INITIAL -> {
                Text("Rock • Paper • Scissors", fontSize = 22.sp)
                Button(
                    onClick = {
                        resetMatch()
                        state = RpsState.PICK
                    },
                    modifier = Modifier.padding(top = 24.dp)
                ) { Text("Start") }
            }

            RpsState.PICK -> {
                Text("Pick your move!", fontSize = 14.sp)
                Text("❔ VS ❔", fontSize = 32.sp, modifier = Modifier.padding(vertical = 16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    buttonOrder.forEach { move ->
                        Button(
                            onClick = {
                                val cpu = Rps.entries[Random.nextInt(Rps.entries.size)]
                                playerPick = move
                                cpuPick = cpu
                                val r = when {
                                    move == cpu -> Result.DRAW
                                    move.beats(cpu) -> Result.WIN
                                    else -> Result.LOSE
                                }
                                result = r
                                if (r == Result.WIN) yourScore++
                                if (r == Result.LOSE) cpuScore++
                                state = RpsState.REVEAL
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Choose ${move.label}"
                            }
                        ) { Text("${move.emoji} ${move.label}", fontSize = 12.sp) }
                    }
                }
            }

            RpsState.REVEAL -> {
                Text(
                    "🧑 ${playerPick?.emoji ?: "❔"}  VS  ${cpuPick?.emoji ?: "❔"} 🤖",
                    fontSize = 32.sp,
                    modifier = Modifier.semantics {
                        contentDescription =
                            "You chose ${playerPick?.label}, computer chose ${cpuPick?.label}"
                    }
                )
                Text(
                    result?.text ?: "",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .semantics { contentDescription = "Result: ${result?.text}" }
                )
            }

            RpsState.FINISHED -> {
                Text(
                    if (yourScore > cpuScore) "You Win the Match!" else "You Lose the Match",
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Final: 🧑 $yourScore - $cpuScore 🤖",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    "Best Score: ${bestScore ?: 0}",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        resetMatch()
                        state = RpsState.PICK
                    }) { Text("Restart") }
                    Button(onClick = {
                        resetMatch()
                        state = RpsState.INITIAL
                    }) { Text("Exit") }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Soal4Preview() {
    Soal4()
}

