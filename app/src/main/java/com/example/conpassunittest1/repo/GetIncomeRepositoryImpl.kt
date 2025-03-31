package com.example.conpassunittest1.repo

import com.example.conpassunittest1.data.Income

class GetIncomeRepositoryImpl(private val apiService: ApiService) {
    suspend fun fetchIncome(name: String): Income {
        return apiService.getIncome(name)
    }
}