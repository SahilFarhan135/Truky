package com.example.truky.ui.listactivity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networkdomain.model.TruckDto
import com.example.networkdomain.network.Resources
import com.example.networkdomain.usecase.GetAllTrucksUseCase
import com.example.truky.base.ViewState
import com.example.truky.util.toLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getAllTrucksUseCase: GetAllTrucksUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>(ViewState.Idle)
    val viewState = _viewState.toLiveData()


    private val _setTruckList = MutableLiveData<TruckDto>()
    val setTruckList = _setTruckList.toLiveData()

    private val _setError = MutableLiveData<String>()
    val setError = _setError.toLiveData()


    fun getTruckList() {
        _viewState.postValue(ViewState.Loading)
        getAllTrucksUseCase().onEach { result ->
            when (result) {
                is Resources.Success -> {
                    _setTruckList.postValue(result.data)
                    _viewState.postValue(ViewState.Success())
                }
                is Resources.Error -> {
                    _setError.postValue(result.message)
                    _viewState.postValue(ViewState.Error())
                }
                is Resources.Loading -> {
                    _viewState.postValue(ViewState.Loading)
                }
            }
        }.launchIn(viewModelScope + exceptionHandler)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        handleFailure(throwable = exception)
    }

    private fun handleFailure(throwable: Throwable?) {
        _viewState.postValue(ViewState.Error(throwable))
        Log.e("NETWORK_ERROR", throwable.toString())
    }


}

