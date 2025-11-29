package com.kireaji.minimallauncherapp.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kireaji.minimallauncherapp.ui.viewmodel.AppListViewModel
import com.kireaji.minimallauncherapp.R
import com.kireaji.minimallauncherapp.data.model.AppInfo

@Composable
fun AppListScreen(
    viewModel: AppListViewModel
) {
    val appInfoListState = viewModel.appInfoListStateFlow.collectAsState()
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppList(
                appInfoList = appInfoListState.value,
                onAppClick = {
                    viewModel.launchApp(it)
                }
            )
        }
    }
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

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AppListPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppList(
                appInfoList = AppInfo.samples(),
                onAppClick = {}
            )
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AppListPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppList(
                appInfoList = AppInfo.samples(),
                onAppClick = {}
            )
        }
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AppItemPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
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
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AppItemPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
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
    }
}
