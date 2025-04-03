package com.example.conpassunittest1.ui

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestViewModelTest {
    private lateinit var viewModel: TestViewModel

    @BeforeEach
    fun setup() {
        viewModel = TestViewModel()
    }

    @Test
    fun test_complexSum_should_returns_correct_sum() {
        // Arrange
        val testValue1 = 1
        val testValue2 = 2
        val testValue3 = 3
        val expectedSum = 6  // (1 + 2) + 3

        // Act
        val actualSum = viewModel.complexSum(testValue1, testValue2, testValue3)

        // Assert
        assertEquals(expectedSum, actualSum, "The computed sum is not as expected.")
    }

    @Test
    fun test_flag_change_after_delay() = runTest {
        // Act
        viewModel.changeFlagAfterDelay(300000)// 5分間実行を待つ

        // Assert
        assertEquals(viewModel.flag.value, -1,"Flag was not set to -1 after delay.")// メソッドの実行結果が'-1'かないかを確認する
    }

    @Test
    fun calculateIncomeTax_should_calculate_correctly() {
        // Arrange
        val income = 1000000.0// 税金を計算する所得を設定
        val expectedLowIncomeTax = 150000.0// 正しい税金計算の結果

        // Act
        val actualTax = viewModel.calculateIncomeTax(income)//メソッドを実行し、所得税を算出

        //Assert
        assertEquals(expectedLowIncomeTax, actualTax, 1e-2)//メソッドからの算出された税金が正しいかを確認
    }

    @Test
    fun test_bonus_calculation_that_depends_on_current_month() {
        val income = 5000000.0

        // 現在の時期によりボーナスを計算する
        val calculatedBonus = viewModel.calculateMonthlyBonus(income)

        //12月には10%のボーナスを貰えるので10%のボーナスを設定する
        val expectedBonus = income * 0.10

        // 実際にボーナスがあるかを確認する
        assertEquals(expectedBonus, calculatedBonus, "Bonus should be calculated only in December")
    }

    @Test
    fun test_tax_calculation_that_requires_manual_inspection() {
        val income = 1000000.0
        val calculatedTax = viewModel.calculateTax(income)

        // 評価をするのではなく、結果を表示するようにする
        println("Calculated tax for income $income is: $calculatedTax")

        // 実行者や、他の確認で実行結果の検証が必要になるからだめ。
    }
}