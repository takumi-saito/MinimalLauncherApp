package com.kireaji.minimallauncherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppListViewModel @Inject constructor(
    private val appUseCase: AppUseCase
) : ViewModel() {

    private val _appInfoListStateFlow = MutableStateFlow(emptyList<AppInfo>())
    val appInfoListStateFlow: StateFlow<List<AppInfo>> = _appInfoListStateFlow

    fun notifyGetAppInfoList() {
        viewModelScope.launch {
            val list = appUseCase.getAppInfoList()
            _appInfoListStateFlow.value = list
        }
    }

    fun launchApp(appInfo: AppInfo) {
        appUseCase.launch(appInfo)
    }
}