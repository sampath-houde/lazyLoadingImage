package com.example.prashantadvaitfoundationtask.data.repo

import com.example.prashantadvaitfoundationtask.api.ApiServices
import com.example.prashantadvaitfoundationtask.data.model.MainResponse
import com.example.prashantadvaitfoundationtask.utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepo @Inject constructor(private val apiServices: ApiServices) {

    suspend fun fetchMainData(): Flow<ViewState<List<MainResponse>>> {
        return flow {

            val mainDataList = apiServices.getMainData()

            emit(ViewState.success(mainDataList))
        }.flowOn(Dispatchers.IO)
    }

}