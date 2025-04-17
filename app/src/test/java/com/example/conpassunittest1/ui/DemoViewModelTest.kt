package com.example.conpassunittest1.ui

import com.example.conpassunittest1.repo.GetIncomeRepositoryImpl
import io.mockk.coEvery
import io.mockk.every
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
    /*
    * テストケースで共通的に使われる値は、テストクラスで宣言することで、各々のテストメソッドの内部で重複して宣言する必要をなくす
    *
    * 宣言の際に初期化をしない値は、lateinitを付ける
    *
    * viewModelは毎テーストケース実行前に初期化することで、各々のテストが独立されていることを担保出来る。
    * */
    private lateinit var viewModel: DemoViewModel
    private val mockIncomeRepository: GetIncomeRepositoryImpl = mockk()
    private val testDispatcher = StandardTestDispatcher()

    /*
    * @BeforeEach
    * 各々の単体テストケースが実行される前に実行される。
    *
    * 例）テスト対象メソッドを格納しているクラスを初期化する
    * */
    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DemoViewModel(mockIncomeRepository)
    }

    /*
    *@AfterEach
    *各々の単体テストのケースが実行された後実行される
    *
    * 例）モック化した値をモックし直す、モックを解除する
    * */
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

        //Assert　第一引数：expected, 第二引数：actual
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

        //Assert　第一引数：expected, 第二引数：actual
        assertEquals(expectedTax, realTax, "calculated income tax is wrong")
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_calculateIncomeTaxByRepo_should_return_tax_for_given_name() = runTest {
        // Arrange
        val expectedIncome = 5000000.0
        viewModel.income = mockk()
        val expectedTax = viewModel.calculateIncomeTax(expectedIncome)

        coEvery { mockIncomeRepository.fetchIncome("testName") } returns expectedIncome
        every { viewModel.income.value } returns expectedIncome
        //テスト壊れる原因が複数なのはよくない。そのため、テストで確認する要因以外は仮の値を使う。

        // Act
        val tax = viewModel.calculateIncomeTaxByRepo("TestName")
        advanceUntilIdle()
        // Coroutineの処理が終わるまで待機

        //Assert　第一引数：expected, 第二引数：actual
        assertEquals(expectedTax, tax)
    }
}