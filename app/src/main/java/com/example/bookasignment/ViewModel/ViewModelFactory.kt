package com.example.bookasignment.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.antsglobe.restcommerse.network.ApiService

class ViewModelFactory(private val apiService: ApiService) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TranslateDataViewModel::class.java) -> TranslateDataViewModel(apiService) as T

            else -> throw java.lang.IllegalArgumentException("ViewModel not found")
        }
    }
}


//class VMF(private val aServ: Provider.Service) : ViewModelProvider.NewInstanceFactory() {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return when {
//            modelClass.isAssignableFrom(vModel::class.java) -> vModel(aServ) as T
//            else -> throw java.lang.IllegalArgumentException("Uncaused error")
//        }
//    }
//}
