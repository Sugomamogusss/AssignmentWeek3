package com.dava.assignmentweek3

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun Soal2() {
    var coins by remember { mutableStateOf(0) }
    var valuePerTap by remember { mutableStateOf(1) }
    var upgradeCost by remember { mutableStateOf(10) }

    val nextValue = (valuePerTap * 1.5).roundToInt()
    val canUpgrade = coins >= upgradeCost

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.background_cat),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.35f))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Your Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$coins", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 36.sp)
                Text("$valuePerTap coins per tap", color = Color.White, fontSize = 12.sp)
            }

            CatTapArea(onTap = { coins += valuePerTap })

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Give Me Your Coin",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF424242)
                )
                Text(
                    "Next upgrade: +$nextValue coins per tap",
                    fontSize = 12.sp,
                    color = Color(0xFF616161),
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                )
                Button(
                    onClick = {
                        if (coins >= upgradeCost) {
                            coins -= upgradeCost
                            valuePerTap = nextValue
                            upgradeCost *= 2
                        }
                    },
                    enabled = canUpgrade,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFBDBDBD),
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (canUpgrade) "Pay for $upgradeCost coins"
                        else "Find ${upgradeCost - coins} more coins",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CatTapArea(onTap: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Tap the Cat!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Image(
            painter = painterResource(if (isPressed) R.drawable.bop_cat_open else R.drawable.bop_cat_stay),
            contentDescription = "Cat",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onTap
                )
        )
        Text(
            if (isPressed) "Meow!" else "Purr~",
            color = Color.White,
            fontSize = 13.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Soal2Preview() {
    Soal2()
}

