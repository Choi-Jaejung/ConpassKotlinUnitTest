package com.example.conpassunittest1.usecase

import com.example.conpassunittest1.data.Income
import com.example.conpassunittest1.repo.GetIncomeRepositoryImpl

class GetIncomeUsecaseImpl(private val incomeRepository: GetIncomeRepositoryImpl) {

    suspend fun execute(name: String): Result<Double> {
        return try {
            val income = incomeRepository.fetchIncome(name)
            Result.success(income)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}