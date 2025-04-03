package com.example.conpassunittest1.ui

import com.example.conpassunittest1.data.Income
import com.example.conpassunittest1.repo.ApiService
import com.example.conpassunittest1.repo.GetIncomeRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DemoViewModelTest {
    private lateinit var viewModel: DemoViewModel
    private val mockIncomeRepository: GetIncomeRepositoryImpl = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = DemoViewModel(mockIncomeRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun test_CalculateLocalInhabitantTax_should_return_correct_tax() {
        // Arrange
        val testIncome = 2000000.0
        val expectedInhabitantTax = 200000.0
        //testIncome * 0.10

        //Act
        val realTax = viewModel.calculateLocalInhabitantTax(income = testIncome)

        //Assert
        assertEquals(expectedInhabitantTax, realTax, "calculated inhabitant tax is wrong")
    }


    @Test
    fun test_CalculateIncomeTax_should_return_tax_with_its_first_bracket_rate() {
        // Arrange
        val testIncome = 2000000.0
        val expectedTax = 302500.0
        //(testIncome * 0.10) + ((testIncome - 1950000) * 0.10 + (1950000 * 0.05))

        //Act
        val realTax = viewModel.calculateIncomeTax(income = testIncome)

        //Assert
        assertEquals(expectedTax, realTax, "calculated income tax is wrong")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_calculateIncomeTaxByRepo_should_return_tax_for_given_name() = runTest {
        // Arrange
        val expectedIncome = Income("TestName", 5000000.0)

        coEvery { mockIncomeRepository.fetchIncome("TestName") } returns expectedIncome//実際の挙動とmockを使う理由を説明し、そのケースを説明する。テスト壊れる原因が複数なのはよくない

        // Act
        val tax = viewModel.calculateIncomeTaxByRepo("TestName")
        advanceUntilIdle() // コールティンが終わるまで実行

        // Assert
        val expectedTax = viewModel.calculateIncomeTax(expectedIncome.income)
        assertEquals(expectedTax, tax)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_getIncome_retrieve_income_from_API() = runTest {
        // Arrange
        val expectedIncome = Income("TestName", 2000000.0) //期待値の設定

        coEvery { mockIncomeRepository.fetchIncome("TestName") } returns expectedIncome//実際の挙動とmockを使う理由を説明し、そのケースを説明する。テスト壊れる原因が複数なのはよくない

        // Act
        viewModel.getIncome("TestName")
        advanceUntilIdle() // コールティンが終わるまで実行

        globalIncome = viewModel.income.value.income // 取得した値をグローバル変数に入れる
        // Assert

        assertEquals(expectedIncome.income, globalIncome) // 取得した値と、期待している値が同じかを確認する
    }

    @Test
    fun test_CalculateIncomeTax_should_return_tax_with_its_givenIncome() {
        // Arrange
        val expectedTax = 302500.0 // 期待値の設定
        //(testIncome * 0.10) + ((testIncome - 1950000) * 0.10 + (1950000 * 0.05))

        //Act
        val realTax = viewModel.calculateIncomeTax(income = globalIncome) // グローバル変数を入れてメソッドを実行する

        //Assert
        assertEquals(expectedTax, realTax, "calculated income tax is wrong") // 期待値とメソッドの実行結果が同じかを確認する
    }
}