package me.jiahuan.gradle_plugin_sample

import android.os.SystemClock
import android.util.Log

object CostCalculator {
    private val sStartTimeMap = HashMap<String, Long>()
    private const val TAG = "CostCalculator"

    fun startCal(signature: String) {
        sStartTimeMap[signature] = SystemClock.elapsedRealtime()
    }

    fun endCal(signature: String) {
        val start = sStartTimeMap[signature]
        if (start != null) {
            Log.d(TAG, "$signature cost time = ${SystemClock.elapsedRealtime() - start}ms")
            sStartTimeMap.remove(signature)
        }
    }
}