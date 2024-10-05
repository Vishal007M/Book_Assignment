package com.example.bookasignment.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.antsglobe.restcommerse.network.ApiService
import com.example.bookasignment.model.request.TranslateDataResponse
import com.example.bookasignment.model.request.Translations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TranslateDataViewModel(private val apiService: ApiService) : ViewModel()  {

    private val _getTranslateData: MutableLiveData<TranslateDataResponse?> = MutableLiveData()
    val getTranslateData: MutableLiveData<TranslateDataResponse?> get() = _getTranslateData

    private val _apiTranslateResponse: MutableLiveData<List<Translations?>> = MutableLiveData()
    val getTranslateResponse: MutableLiveData<List<Translations?>> get() = _apiTranslateResponse


    fun getTranslateVM(key: String, source: String, target: String,q: String) {
        try {
            apiService.translateApi(key, source, target, q).enqueue(object :
                Callback<TranslateDataResponse> {
                override fun onResponse(
                    call: Call<TranslateDataResponse>,
                    response: Response<TranslateDataResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        _getTranslateData?.value = body

                        body?.data?.translations?.let { content ->
                            _apiTranslateResponse?.value = content
                        }
                    }
                }

                override fun onFailure(call: Call<TranslateDataResponse>, t: Throwable) {
                    _apiTranslateResponse?.value = null
                    Log.e("LoginViewModel", "Login error: ${t.message}")
                }
            })
        } catch (e: Exception) {
            println("in catch ${e.message}")
        }
    }

}