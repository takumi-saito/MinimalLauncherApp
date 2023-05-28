package com.kireaji.minimallauncherapp

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppListScreen(
    viewModel: AppListViewModel
) {
    val appInfoListState = viewModel.appInfoListStateFlow.collectAsState()
    AppList(
        appInfoList = appInfoListState.value,
        onAppClick = {
            viewModel.launchApp(it)
        }
    )
}

@Composable
fun AppList(
    appInfoList: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        items(appInfoList) {
            AppItem(
                appInfo = it,
                onAppClick = onAppClick
            )
        }
    }
}

@Composable
fun AppItem(
    appInfo: AppInfo,
    onAppClick: (AppInfo) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp, horizontal = 30.dp)
        .clickable {
            onAppClick(appInfo)
        }
    ) {
        Text(
            text = appInfo.label,
            color = colorResource(id = R.color.base_text),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Light
        )
    }
}

@Preview
@Composable
fun AppListPreview() {
    AppList(
        appInfoList = AppInfo.samples(),
        onAppClick = {}
    )
}

@Preview
@Composable
fun AppItemPreview() {
    AppItem(
        appInfo = AppInfo(
            packageName = "packageName",
            icon = null,
            label = "label",
            sourceDir = "sourceDir",
            componentName = null
        ),
        onAppClick = {}
    )
}