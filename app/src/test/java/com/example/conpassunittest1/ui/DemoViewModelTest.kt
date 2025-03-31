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
    private val mockApiService: ApiService = mockk()
    private val mockIncomeRepository: GetIncomeRepositoryImpl = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = DemoViewModel(mockApiService, mockIncomeRepository)
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
        val expectedInhabitantTax = testIncome * 0.10

        //Act
        val realTax = viewModel.calculateLocalInhabitantTax(income = testIncome)

        //Assert
        assertEquals(expectedInhabitantTax, realTax, "calculated inhabitant tax is wrong")
    }


    @Test
    fun test_CalculateIncomeTax_should_return_tax_with_its_first_bracket_rate() {
        // Arrange
        val testIncome = 2000000.0
        val expectedTax = (testIncome * 0.10) + ((testIncome - 1950000) * 0.10 + (1950000 * 0.05))

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

        coEvery { mockIncomeRepository.fetchIncome("TestName") } returns expectedIncome

        // Act
        viewModel.getIncome("TestName")
        advanceUntilIdle() // Ensure all coroutines have completed

        val tax = viewModel.calculateIncomeTaxByRepo("TestName")

        // Assert
        val expectedTax = viewModel.calculateIncomeTax(expectedIncome.income)
        assertEquals(expectedTax, tax)
    }
}