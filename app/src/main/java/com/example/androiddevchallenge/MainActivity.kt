/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.androiddevchallenge.TimerUtil.convertInputToMilliseconds
import com.example.androiddevchallenge.ui.theme.MyTheme

// This Basic Timer app could use a lot of refactoring
// wrote the basics, added more and more. Went at this without a design ahead of time, and just
// wanted to play around with it.

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        val countdownDefaultTime = remember { mutableStateOf(300000f) }
        val remainingTime = remember { mutableStateOf(300000f) }
        val isCountingDown = remember { mutableStateOf(false) }
        val displayKeypad = remember { mutableStateOf(false) }
        val timerFinished = remember { mutableStateOf(false) }

        var timerOnFinish: () -> Unit = {
            remainingTime.value = 0f
            timerFinished.value = true
        }

        var timer = remember {
            mutableStateOf(
                TimerUtil.getCountDownTimer(
                    milliseconds = remainingTime.value,
                    onTick1 = { millisUntilFinished ->
                        remainingTime.value = millisUntilFinished.toFloat()
                    },
                    onFinish = { timerOnFinish() },
                )
            )
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val (title, timerDisplay, buttons) = createRefs()

            // Title text
            Text(
                text = "Daniel Keyes - Compose Dev Challenge 2 - Timer",
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                    },
            )

            // Main Display
            Box(
                modifier = Modifier.constrainAs(timerDisplay) {
                    top.linkTo(title.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(buttons.top)
                },
            ) {
                if (displayKeypad.value) {
                    Keypad(
                        onFinished = {
                            remainingTime.value = convertInputToMilliseconds(it)
                            countdownDefaultTime.value = convertInputToMilliseconds(it)
                            timer.value = TimerUtil.getCountDownTimer(
                                milliseconds = remainingTime.value,
                                onTick1 = { millisUntilFinished ->
                                    remainingTime.value = millisUntilFinished.toFloat()
                                },
                                onFinish = {
                                    timerOnFinish()
                                },
                            )
                            displayKeypad.value = false
                        }
                    )
                } else {
                    if (timerFinished.value) {
                        Text(
                            text = "Finished!!!",
                            fontSize = 36.sp,
                        )
                    } else {
                        TimerDisplay(
                            remainingTime.value,
                            onClick = { displayKeypad.value = true },
                        )
                    }
                }
            }

            // BottomButtons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(buttons) {
                        bottom.linkTo(parent.bottom)
                    },
            ) {

                if (!displayKeypad.value) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (!timerFinished.value) {
                            Button(
                                onClick = {
                                    if (isCountingDown.value) {
                                        timer.value.cancel() // stop timer
                                        timer.value = TimerUtil.getCountDownTimer(
                                            // set new timer
                                            milliseconds = remainingTime.value,
                                            onTick1 = { millisUntilFinished ->
                                                remainingTime.value = millisUntilFinished.toFloat()
                                            },
                                            onFinish = {
                                                timerOnFinish()
                                            },
                                        )
                                    } else {
                                        timer.value.start()
                                    }
                                    isCountingDown.value = !isCountingDown.value
                                },
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = if (isCountingDown.value) "Pause" else "Start",
                                    textAlign = TextAlign.Center,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                    // Reset Button
                    Box(modifier = Modifier.weight(1f)) {
                        // dkeyes
                        Button(
                            onClick = {
                                timer.value.cancel()
                                remainingTime.value = countdownDefaultTime.value
                                timer.value = TimerUtil.getCountDownTimer(
                                    milliseconds = countdownDefaultTime.value,
                                    onTick1 = { millisUntilFinished ->
                                        remainingTime.value = millisUntilFinished.toFloat()
                                    },
                                    onFinish = {
                                        timerOnFinish()
                                    },
                                )
                                isCountingDown.value = false
                                timerFinished.value = false
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Reset",
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimerDisplay(
    milliseconds: Float,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    displayMilliseconds: Boolean = true,
) {
    val mSeconds = (milliseconds % 1000).toInt()
    val seconds: Int = ((milliseconds / 1000) % 60).toInt()
    val minutes: Int = ((milliseconds / 1000 / 60) % 60).toInt()
    val hours: Int = ((milliseconds / 1000 / 3600) % 60).toInt()

    Row(
        modifier = modifier
            .clickable { onClick?.let { it() } }
    ) {
        TimeUnit(amount = hours, length = 2, unit = "h")
        TimeUnit(amount = minutes, length = 2, unit = "m")
        TimeUnit(amount = seconds, length = 2, unit = "s")
        if (displayMilliseconds) {
            TimeUnit(amount = mSeconds, length = 3, unit = "ms")
        }
    }
}

@Composable
fun TimerDisplay(
    displayValue: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val seconds = displayValue.padStart(6, '0').substring(4, 6).toInt()
    val minutes: Int = displayValue.padStart(6, '0').substring(2, 4).toInt()
    val hours: Int = displayValue.padStart(6, '0').substring(0, 2).toInt()

    Row(
        modifier = modifier
            .clickable { onClick?.let { it() } }
    ) {
        TimeUnit(amount = hours, length = 2, unit = "h")
        TimeUnit(amount = minutes, length = 2, unit = "m")
        TimeUnit(amount = seconds, length = 2, unit = "s")
    }
}

@Composable
fun TimeUnit(amount: Int, length: Int, unit: String) {
    Row(modifier = Modifier.padding(4.dp)) {
        Text(
            text = "0".repeat(length - amount.toString().length) + amount.toString(),
            fontSize = 36.sp,
            modifier = Modifier.alignByBaseline()
        )
        Text(
            text = unit,
            fontSize = 24.sp,
            modifier = Modifier.alignByBaseline()
        )
    }
}

@Preview
@Composable
fun PreviewTimeUnit() {
    MyTheme(darkTheme = true) {
        TimerDisplay(milliseconds = 4702000f)
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}

@Preview
@Composable
fun PreviewKeypad() {
    Keypad(onFinished = {})
}

@Composable
fun Keypad(onFinished: (String) -> Unit) {
    val keypadValue = remember { mutableStateOf("") }

    // only allow up to 6 digits
    val updateTime: (String) -> Unit = { it ->
        if (keypadValue.value.length < 6) {
            keypadValue.value = keypadValue.value + it
        }
    }

    val finished: () -> Unit = {
        onFinished(keypadValue.value)
    }

    val deleteLastKeypadValue: () -> Unit = {
        keypadValue.value = keypadValue.value.dropLast(1)
    }

    Column() {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            TimerDisplay(
                displayValue = keypadValue.value,
                onClick = { },
            )
        }
        // programmatically create keypad 1-9 just because
        for (x in 1..3) {
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                for (y in 1..3) {
                    KeypadButton(
                        displayValue = "${((x - 1) * 3) + y}",
                        onClick = { updateTime(it) },
                    )
                }
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                KeypadButton(
                    drawable = R.drawable.backspace_24px,
                    onClick = { deleteLastKeypadValue() }
                )
            }
            KeypadButton(
                "0",
                onClick = { updateTime(it) },
            )
            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                KeypadButton(
                    drawable = R.drawable.done_24px,
                    onClick = { finished() },
                    enabled = keypadValue.value.isNotEmpty()
                )
            }
        }
    }
}

@Composable
fun KeypadButton(
    drawable: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .size(48.dp, 48.dp)
    ) {
        if (enabled) {
            Image(
                painter = painterResource(drawable),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
                contentDescription = "Backspace",
                modifier = Modifier
                    .clickable {
                        onClick()
                    }
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun KeypadButton(displayValue: String, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = { onClick(displayValue) },
        modifier = modifier.padding(12.dp)
    ) {
        Text(
            text = displayValue,
            fontSize = 36.sp,
            modifier = modifier

        )
    }
}
