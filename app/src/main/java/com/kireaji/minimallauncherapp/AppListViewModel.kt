package com.kireaji.minimallauncherapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppListViewModel : ViewModel() {

    private val _appInfoListStateFlow = MutableStateFlow(emptyList<AppInfo>())
    val appInfoListStateFlow: StateFlow<List<AppInfo>> = _appInfoListStateFlow

    fun notifyGetAppInfoList(context: Context) {
        viewModelScope.launch {
            val list = AppUseCase.getAppInfoList(context)
            _appInfoListStateFlow.value = list
        }
    }
}