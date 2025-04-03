package com.example.conpassunittest1.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conpassunittest1.data.Income
import com.example.conpassunittest1.repo.GetIncomeRepositoryImpl
import com.example.conpassunittest1.usecase.GetIncomeUsecaseImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DemoViewModel(
    private val incomeRepository: GetIncomeRepositoryImpl
) : ViewModel() {

    val income: MutableState<Income> = mutableStateOf(Income(name = "", income = 0.0))

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

    fun getIncome(name: String) {
        val getIncomeUseCase = GetIncomeUsecaseImpl(incomeRepository)

        viewModelScope.launch {
            val result = getIncomeUseCase.execute(name)
            result.onSuccess { resultIncome ->
                println("Name: ${resultIncome.name}, Income: ${resultIncome.income}")
                income.value = resultIncome
            }.onFailure { ex ->
                println("Error fetching income: ${ex.message}")
            }
        }
    }

    suspend fun calculateIncomeTaxByRepo(name: String): Double {
        withContext(Dispatchers.Main) {
            getIncome(name)
        }

        return calculateIncomeTax(income.value.income)
    }
}