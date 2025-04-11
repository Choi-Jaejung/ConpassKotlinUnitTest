package com.example.conpassunittest1.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conpassunittest1.repo.GetIncomeRepositoryImpl
import com.example.conpassunittest1.usecase.GetIncomeUsecaseImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DemoViewModel(
    private val incomeRepository: GetIncomeRepositoryImpl // viewModelのクラス中でAPI連携を呼び出すために必要
) : ViewModel() {

    var income: MutableState<Double> = mutableDoubleStateOf(0.0)

    /**
     * 所得税を算出するメソッド
     * incomeを引数として貰い、incomeの額により違う税金率を適用する
     * @param income 所得
     * @return 所得税 TODO:ちゃんとコメントを書くなら、このように書きます。
     * */
    fun calculateIncomeTax(income: Double): Double { //所得税
        val nationalIncomeTax = when { // 所得別に違う税金率を掛ける
            income <= 1950_000 -> income * 0.05
            income <= 3300000 -> 97500 + (income - 1950000) * 0.10
            income <= 6950000 -> 232500 + (income - 3300000) * 0.20
            income <= 9000000 -> 962500 + (income - 6950000) * 0.23
            income <= 18000000 -> 1434000 + (income - 9000000) * 0.33
            income <= 40000000 -> 4404000 + (income - 18000000) * 0.40
            else -> 13204000 + (income - 40000000) * 0.45 //4000000を超える所得の場合はこっちに入る
        }

        val inhabitantTax = calculateLocalInhabitantTax(income = income) // 住民税を算出する

        return (nationalIncomeTax + inhabitantTax)// 住民税と所得税を合算して返す
    }

    /**
     * 住民税を計算するメソッド
     * 固定値で引数*0.10の計算値を返す
     * */
    fun calculateLocalInhabitantTax(income: Double): Double { //住民税
        return income * 0.10 //住民税は一律的に適用
    }

    /**
     * APIから値を貰うメソッド
     * nameをキーとして該当するIncome(Double)の値を貰う。
     * */
    fun getIncome(name: String) {
        val getIncomeUseCase = GetIncomeUsecaseImpl(incomeRepository)

        viewModelScope.launch {// Coroutineを使って外部と連携をする処理が行われる <= TODO:これはコードでやっていることをそのまま言っているだけ。
            val result = getIncomeUseCase.execute(name)
            result.onSuccess { resultIncome ->

                income.value = resultIncome
            }.onFailure { ex ->
                println("Error fetching income: ${ex.message}")
            }
        }//モックすることで、単体テストの実装の際にはロジックの実行を気にしなくてもいい <= TODO:ここに書くことではないのでは？
    }

    /**
     * APIを呼び出すメソッドからIncomeを貰い、そのIncomeを引数として税金を算出するメソッドを呼ぶ
     * */
    suspend fun calculateIncomeTaxByRepo(name: String): Double {
        withContext(Dispatchers.Main) {// APIとの連携するメソッドを呼び出す <= TODO:APIからデータもらう。でよくないか？
            getIncome(name)
        }

        return calculateIncomeTax(income.value)// APIからもらった値を使い税金を算出する
    }
}