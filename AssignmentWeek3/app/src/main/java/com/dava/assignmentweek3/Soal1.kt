package com.dava.assignmentweek3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToLong
import kotlin.random.Random

enum class ReactionState {
    IDLE,
    WAITING,
    READY,
    RESULT,
    FINAL
}

enum class ReactionCategory(
    val message: String,
    val color: Color,
    val imageRes: Int
) {
    FAST("DANG YOU ARE SO FAST BRO!", Color(0xFF00E676), R.drawable.rispek_picture),
    GOOD("YOUR REFLEX IS GOOD", Color(0xFF2196F3), R.drawable.good_picture),
    MEH("MEH LIKE OTHER PERSON", Color(0xFFFF9800), R.drawable.soso_picture),
    SNAIL("YOU LIKE A SNAIL BRO", Color(0xFFFF5722), R.drawable.bad_picture);

    companion object {
        fun fromAverage(avg: Long?): ReactionCategory = when {
            avg == null -> SNAIL
            avg < 180 -> FAST
            avg < 280 -> GOOD
            avg < 450 -> MEH
            else -> SNAIL
        }
    }
}

@Composable
fun Soal1() {
    var state by remember { mutableStateOf(ReactionState.IDLE) }
    var trial by remember { mutableStateOf(0) }

    val results = remember { mutableStateListOf<Long?>(null, null, null) }
    var lastFailed by remember { mutableStateOf(false) }
    var readyStart by remember { mutableStateOf(0L) }

    val validTimes = results.filterNotNull()
    val average: Long? =
        if (validTimes.isEmpty()) null else validTimes.average().roundToLong()

    when (state) {
        ReactionState.IDLE -> IdleScreen(
            results = results,
            showCard = trial > 0,
            onStart = { state = ReactionState.WAITING }
        )

        ReactionState.WAITING -> WaitingScreen(
            onEarlyTap = {
                results[trial] = null
                lastFailed = true
                state = ReactionState.RESULT
            },
            onTimeUp = { startTime ->
                readyStart = startTime
                state = ReactionState.READY
            }
        )

        ReactionState.READY -> ReadyScreen(
            onTap = {
                val reaction = System.currentTimeMillis() - readyStart
                results[trial] = reaction
                lastFailed = false
                state = ReactionState.RESULT
            }
        )

        ReactionState.RESULT -> ResultScreen(
            trial = trial,
            failed = lastFailed,
            time = results[trial],
            results = results,
            onContinue = {
                if (trial >= 2) {
                    state = ReactionState.FINAL
                } else {
                    trial++
                    state = ReactionState.IDLE
                }
            }
        )

        ReactionState.FINAL -> FinalScreen(
            results = results,
            average = average,
            onRestart = {
                for (i in results.indices) results[i] = null
                trial = 0
                lastFailed = false
                state = ReactionState.IDLE
            }
        )
    }
}

@Composable
private fun ScreenBase(
    color: Color,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .clickable { onClick() }
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun TrialResultsCard(
    results: List<Long?>,
    showAverage: Boolean = false,
    average: Long? = null
) {
    Card(
        modifier = Modifier.padding(top = 24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Trial Results",
                color = Color(0xFF1976D2),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                results.forEachIndexed { index, ms ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${index + 1}",
                            color = if (ms != null) Color(0xFF4CAF50) else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (ms != null) "${ms}ms" else "-",
                            color = Color.Black,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            if (showAverage) {
                Text(
                    text = "Average Score",
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = if (average != null) "${average}ms" else "-",
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun IdleScreen(
    results: List<Long?>,
    showCard: Boolean,
    onStart: () -> Unit
) {
    ScreenBase(color = Color(0xFF6EC6D9), onClick = onStart) {
        Text("Reaction", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Image(
            painter = painterResource(R.drawable.flash),
            contentDescription = "Flash Icon",
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .padding(vertical = 24.dp)
                .height(64.dp)
        )
        Text("Test", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(
            "Click to Start",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        if (showCard) TrialResultsCard(results)
    }
}

@Composable
private fun WaitingScreen(
    onEarlyTap: () -> Unit,
    onTimeUp: (startTime: Long) -> Unit
) {
    LaunchedEffect(Unit) {
        delay(Random.nextLong(500L, 4501L))
        onTimeUp(System.currentTimeMillis())
    }

    ScreenBase(color = Color(0xFFD9D9D9), onClick = onEarlyTap) {
        Text("Get Ready", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Image(
            painter = painterResource(R.drawable.warning),
            contentDescription = "Warning Icon",
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .padding(vertical = 24.dp)
                .height(64.dp)
        )
        Text("Wait for green light...", color = Color.White, fontSize = 13.sp)
        Text(
            "DON'T CLICK YET!",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun ReadyScreen(onTap: () -> Unit) {
    ScreenBase(color = Color(0xFF4CAF50), onClick = onTap) {
        Text("GO!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Image(
            painter = painterResource(R.drawable.running),
            contentDescription = "Running Icon",
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .padding(vertical = 24.dp)
                .height(72.dp)
        )
        Text("CLICK NOW!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(
            "TAP AS FAST AS YOU CAN!",
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ResultScreen(
    trial: Int,
    failed: Boolean,
    time: Long?,
    results: List<Long?>,
    onContinue: () -> Unit
) {
    if (failed) {
        ScreenBase(color = Color(0xFFF94144), onClick = onContinue) {
            Text("FAIL!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Image(
                painter = painterResource(R.drawable.negative_feedback),
                contentDescription = "Fail Icon",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .height(72.dp)
            )
            Text(
                "You clicked too early, TRY TO READ THE RULE BRO",
                color = Color.White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Text(
                if (trial >= 2) "SEE FINAL RESULT" else "TRY AGAIN",
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            TrialResultsCard(results)
        }
    } else {
        ScreenBase(color = Color(0xFF4CAF50), onClick = onContinue) {
            Text(
                "Trial ${trial + 1} Complete!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Image(
                painter = painterResource(R.drawable.correct),
                contentDescription = "Correct Icon",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .height(72.dp)
            )
            Text("Time: ${time}ms", color = Color.White, fontSize = 14.sp)
            Text(
                if (trial >= 2) "See Final Result" else "Continue to Trial ${trial + 2}",
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            TrialResultsCard(results)
        }
    }
}

@Composable
private fun FinalScreen(
    results: List<Long?>,
    average: Long?,
    onRestart: () -> Unit
) {
    val category = ReactionCategory.fromAverage(average)

    ScreenBase(color = category.color, onClick = onRestart) {
        Text(
            text = category.message,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(category.imageRes),
            contentDescription = "Result Picture",
            modifier = Modifier
                .padding(vertical = 20.dp)
                .height(130.dp)
        )
        Text(
            text = "Average: ${average?.toString() ?: "-"}ms",
            color = Color.White,
            fontSize = 14.sp
        )
        Text(
            "Click to Start New Test",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        TrialResultsCard(results, showAverage = true, average = average)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Soal1Preview() {
    Soal1()
}

