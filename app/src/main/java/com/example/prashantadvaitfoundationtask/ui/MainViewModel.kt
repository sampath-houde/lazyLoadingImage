package com.example.prashantadvaitfoundationtask.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prashantadvaitfoundationtask.data.model.MainResponse
import com.example.prashantadvaitfoundationtask.data.repo.MainRepo
import com.example.prashantadvaitfoundationtask.utils.Status
import com.example.prashantadvaitfoundationtask.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepo
) : ViewModel() {

    val mainState = MutableStateFlow(
        ViewState(
            Status.LOADING,
            emptyList<MainResponse>(), ""
        )
    )

    init {
        fetchMainData()
    }

    fun fetchMainData() {
        mainState.value = ViewState.loading()
        viewModelScope.launch {

            repository.fetchMainData()
                .catch {
                    mainState.value =
                        ViewState.error(it.message.toString())
                }
                .collect {
                    mainState.value = ViewState.success(it.data)
                }
        }
    }
}