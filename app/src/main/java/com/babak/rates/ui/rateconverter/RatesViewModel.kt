package com.babak.rates.ui.rateconverter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babak.rates.data.Result
import com.babak.rates.data.source.RatesRepository
import com.babak.rates.ui.model.Error
import com.babak.rates.ui.model.Rate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class RatesViewModel @Inject constructor(private val ratesRepository: RatesRepository) :
    ViewModel() {

    private val _items = MutableLiveData<List<Rate>>().apply { value = emptyList() }
    val items: LiveData<List<Rate>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _error = MutableLiveData<Error>()
    val error: LiveData<Error> = _error


    private val _isChangeCurrency = MutableLiveData<Boolean>().apply { value = false }
    val isChangeCurrency: LiveData<Boolean> = _isChangeCurrency

    private var currencyChanged = false

    private var job: Job? = null
    var baseValue = 1.0
    private var baseCurrency = "GBP"

    init {
        _dataLoading.value = true
        startFetchingRates(baseCurrency, baseValue)
    }

    fun startFetchingRates(baseCurrency: String, baseValue: Double) {
        job = viewModelScope.launch {
            while (true) {
                ratesRepository.getAllRates(baseCurrency).let { result ->

                    when (result) {
                        is Result.Success -> {
                            val tempList = mutableListOf<Rate>()
                            tempList.add(
                                Rate(
                                    baseCurrency,
                                    baseValue
                                )
                            )
                            result.data?.rates?.map {
                                tempList.add(
                                    Rate(
                                        it.key,
                                        it.value * baseValue
                                    )
                                )
                            }
                            _items.postValue(tempList)
                        }
                        is Result.NetworkError -> {
                            _error.postValue(Error("Network Error: Please Check Your Internet Connection"))
                        }
                        is Result.GenericError -> {
                            _error.postValue(
                                Error(
                                    result.error ?: "Unknown Error"
                                )
                            )
                        }
                    }

                    _dataLoading.postValue(false)
                    if (currencyChanged) {
                        currencyChanged = false
                        _isChangeCurrency.postValue(true)
                    }

                }

                delay(1000)
            }
        }
    }

    fun changeBaseCurrency(currency: String) {
        job?.cancel()
        currencyChanged = true
        baseCurrency = currency
        startFetchingRates(baseCurrency, baseValue)
    }

    fun valueChanged(newValue: String) {
        job?.cancel()
        baseValue = newValue.toDouble()
        startFetchingRates(baseCurrency, baseValue)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}