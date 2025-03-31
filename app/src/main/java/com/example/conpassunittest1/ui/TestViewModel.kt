package com.example.conpassunittest1.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import java.time.LocalDateTime

class TestViewModel {
    private fun computeSomething(x: Int, y: Int, operation: (Int, Int) -> Int): Int {
        return operation(x, y)
    }

    data class ComputationResult(val result: Int)

    fun complexSum(testValue1: Int, testValue2: Int, testValue3: Int): Int {
        fun performAddition(x: Int, y: Int): Int {
            return computeSomething(x, y) { a, b -> a + b }
        }

        val intermediateResult1 = ComputationResult(performAddition(testValue1, testValue2))
        val intermediateResult2 = ComputationResult(performAddition(intermediateResult1.result, testValue3))

        return intermediateResult2.result
    }


    val flag: MutableState<Int> = mutableIntStateOf(0)

    suspend fun changeFlagAfterDelay(delayMillis: Long) {
        delay(delayMillis)
        flag.value = -1
    }


    // Method that returns a bonus based on the current month for demonstration
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateMonthlyBonus(income: Double): Double {
        // 12月のみボーナスがある
        val currentMonth = LocalDateTime.now().monthValue
        return if (currentMonth == 12) income * 0.10 else 0.0
    }


    fun calculateIncomeTax(income: Double): Double { //所得税
        val nationalIncomeTax = when { // 所得別に違う税金率を掛ける
            income <= 1950000 -> income * 0.05
            income <= 3300000 -> 97500 + (income - 1950000) * 0.10
            income <= 6950000 -> 232500 + (income - 3300000) * 0.20
            income <= 9000000 -> 962500 + (income - 6950000) * 0.23
            income <= 18000000 -> 1434000 + (income - 9000000) * 0.33
            income <= 40000000 -> 4404000 + (income - 18000000) * 0.40
            else -> 13204000 + (income - 40000000) * 0.45
        }

        val inhabitantTax = calculateLocalInhabitantTax(income = income) // 住民税を算出する

        return (nationalIncomeTax + inhabitantTax)
    }

    fun calculateLocalInhabitantTax(income: Double): Double { //住民税
        return income * 0.10 //住民税は一律的に適用
    }

    // A sample method for calculating tax
    fun calculateTax(income: Double): Double {
        return income * 0.2 // 20% tax for demonstration
    }

}