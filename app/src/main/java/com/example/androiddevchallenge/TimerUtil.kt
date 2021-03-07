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

import android.os.CountDownTimer
import java.lang.Integer.parseInt

object TimerUtil {
    fun getCountDownTimer(
        milliseconds: Float,
        onTick1: (Long) -> Unit,
        onFinish: () -> Unit
    ): CountDownTimer {
        return object : CountDownTimer(milliseconds.toLong(), 1) {
            override fun onTick(millisUntilFinished: Long) {
                onTick1(millisUntilFinished)
            }
            override fun onFinish() {
                onFinish()
            }
        }
    }

    /**
     * take in 5 min as 500
     * 10500
     */
    fun convertInputToMilliseconds(value: String): Float {
        var temp = 0

        try {
            temp = parseInt(value)
        } catch (e: NumberFormatException) {
            return 0f
        }

        var seconds = temp % 100 // get seconds
        seconds += ((temp / 100) % 100) * 60
        seconds += ((temp / 10000) % 100) * 3600

        // if don't return higher than 99 hours, 99 mins, 99 seconds
        if (seconds * 1000f > 359999000) {
            return 359999000f
        } else {
            return seconds * 1000f
        }
    }
}
