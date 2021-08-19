package com.hampson.sharework_kotlin.ui.job_create

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.core.math.MathUtils

class TimePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TimePicker(context, attrs) {
    private val defaultInterval = 5
    val minInterval = 1
    val maxInterval = 30

    var timeInterval = defaultInterval
        set(value) {
            if (field !in minInterval..maxInterval) {
                Log.w("RangeTimePicker", "timeInterval must be between $minInterval..$maxInterval")
            }

            field = MathUtils.clamp(minInterval, maxInterval, value)

            invalidate()
        }

    init {

    }

}