package com.example.conpassunittest1.repo

class GetIncomeRepositoryImpl(private val apiService: ApiService) {
    suspend fun fetchIncome(name: String): Double {
        return apiService.getIncome(name)
    }
}