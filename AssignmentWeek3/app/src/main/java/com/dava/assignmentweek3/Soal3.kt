package com.dava.assignmentweek3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class GameState { INITIAL, COUNTDOWN, RUNNING, GAME_OVER }

enum class Mode(val label: String, val clue: String) {
    COLOR("COLOR", "Pilih WARNA TINTA"),
    TEXT("TEXT", "Pilih NAMA KATA")
}

enum class Choice { INK, WORD }

enum class ColorName(val label: String, val color: Color) {
    RED("RED", Color(0xFFE53935)),
    GREEN("GREEN", Color(0xFF43A047)),
    BLUE("BLUE", Color(0xFF1E88E5)),
    ORANGE("ORANGE", Color(0xFFFB8C00))
}

data class Question(
    val word: ColorName,
    val ink: ColorName,
    val mode: Mode,
    val inkOnLeft: Boolean
)

private const val QUESTION_TIME_MS = 5000L
private const val TICK_MS = 50L
private const val MAX_ERRORS = 3

private fun newQuestion(): Question {
    val word = ColorName.entries.random()
    val ink = ColorName.entries.filter { it != word }.random()
    return Question(
        word = word,
        ink = ink,
        mode = Mode.entries.random(),
        inkOnLeft = Random.nextBoolean()
    )
}

@Composable
fun Soal3() {
    var state by remember { mutableStateOf(GameState.INITIAL) }
    var score by remember { mutableStateOf(0) }
    var errors by remember { mutableStateOf(0) }
    var bestScore by remember { mutableStateOf(0) }
    var question by remember { mutableStateOf(newQuestion()) }
    var questionId by remember { mutableStateOf(0) }
    var remainingMs by remember { mutableStateOf(QUESTION_TIME_MS) }
    var countdownText by remember { mutableStateOf("3") }

    fun nextQuestion() {
        question = newQuestion()
        remainingMs = QUESTION_TIME_MS
        questionId++
    }

    fun startGame() {
        score = 0
        errors = 0
        countdownText = "3"
        state = GameState.COUNTDOWN
    }

    fun registerError() {
        errors++
        if (errors >= MAX_ERRORS) {
            if (score > bestScore) bestScore = score
            state = GameState.GAME_OVER
        } else {
            nextQuestion()
        }
    }

    fun onAnswer(choice: Choice) {
        val correct = if (question.mode == Mode.COLOR) Choice.INK else Choice.WORD
        if (choice == correct) {
            score++
            nextQuestion()
        } else {
            registerError()
        }
    }

    LaunchedEffect(state) {
        if (state == GameState.COUNTDOWN) {
            for (n in 3 downTo 1) {
                countdownText = n.toString()
                delay(1000L)
            }
            countdownText = "Start!"
            delay(700L)
            nextQuestion()
            state = GameState.RUNNING
        }
    }

    LaunchedEffect(questionId, state) {
        if (state == GameState.RUNNING) {
            while (remainingMs > 0L) {
                delay(TICK_MS)
                remainingMs -= TICK_MS
            }
            registerError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (state) {
            GameState.INITIAL -> {
                Text(
                    text = "Welcome\nto\nColor Word Matching",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { startGame() },
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Text("Start Game")
                }
            }

            GameState.COUNTDOWN -> {
                Text(text = countdownText, fontSize = 48.sp)
            }

            GameState.RUNNING -> {
                val seconds = (remainingMs + 999L) / 1000L
                val options = if (question.inkOnLeft) {
                    listOf(
                        Choice.INK to question.ink.label,
                        Choice.WORD to question.word.label
                    )
                } else {
                    listOf(
                        Choice.WORD to question.word.label,
                        Choice.INK to question.ink.label
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Mode: ${question.mode.label}", fontSize = 14.sp)
                    Text(text = "✅ $score  ❌ $errors/$MAX_ERRORS", fontSize = 14.sp)
                }

                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE3F2FD))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = question.mode.clue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "$seconds s",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 24.dp)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFEEEEEE))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((remainingMs / QUESTION_TIME_MS.toFloat()).coerceIn(0f, 1f))
                            .height(6.dp)
                            .background(Color(0xFF42A5F5))
                    )
                }

                Text(
                    text = question.word.label,
                    color = question.ink.color,
                    fontSize = 56.sp,
                    modifier = Modifier.padding(vertical = 40.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    options.forEach { (choice, label) ->
                        Button(onClick = { onAnswer(choice) }) {
                            Text(label)
                        }
                    }
                }
            }

            GameState.GAME_OVER -> {
                Text(text = "Game Over!", fontSize = 28.sp)
                Text(
                    text = "Your Score",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 24.dp)
                )
                Text(text = "$score", fontSize = 32.sp)
                Text(
                    text = "Best Score",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(text = "$bestScore", fontSize = 16.sp)
                Button(
                    onClick = { startGame() },
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text("Restart Game")
                }
                Button(onClick = { state = GameState.INITIAL }) {
                    Text("Exit")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Soal3Preview() {
    Soal3()
}

