package com.kireaji.minimallauncherapp.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kireaji.minimallauncherapp.ui.viewmodel.AppListViewModel
import com.kireaji.minimallauncherapp.R
import com.kireaji.minimallauncherapp.data.model.AppInfo
import kotlinx.coroutines.launch

// インデックス文字を抽出（重複排除、ソート順維持）
private fun extractIndexCharacters(appInfoList: List<AppInfo>): List<Char> {
    return appInfoList
        .mapNotNull { it.label.firstOrNull()?.uppercaseChar() }
        .distinct()
}

// 文字→リスト位置のマッピング作成
private fun createCharToPositionMap(appInfoList: List<AppInfo>): Map<Char, Int> {
    val map = mutableMapOf<Char, Int>()
    appInfoList.forEachIndexed { index, appInfo ->
        val firstChar = appInfo.label.firstOrNull()?.uppercaseChar()
        if (firstChar != null && !map.containsKey(firstChar)) {
            map[firstChar] = index
        }
    }
    return map
}

// Y座標からインデックスを計算
private fun calculateIndexFromOffset(yOffset: Float, totalHeight: Float, itemCount: Int): Int {
    if (itemCount == 0 || totalHeight <= 0) return 0
    val fraction = (yOffset / totalHeight).coerceIn(0f, 0.999f)
    return (fraction * itemCount).toInt()
}

@Composable
fun AppListScreen(
    viewModel: AppListViewModel
) {
    val appInfoListState = viewModel.appInfoListStateFlow.collectAsState()
    AppListScreen(
        appInfoList = appInfoListState.value,
        onAppClick = {
            viewModel.launchApp(it)
        }
    )
}

/**
 * 状態を引数で受け取る版。VRT（AppListScreenVrtTest）はこちらを撮影する。
 * テスト側で AppTheme / Surface を再宣言すると画面の見た目の定義が二重化するため、
 * スキャフォールドは必ずここに一箇所だけ置くこと。
 */
@Composable
fun AppListScreen(
    appInfoList: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit
) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppList(
                appInfoList = appInfoList,
                onAppClick = onAppClick
            )
        }
    }
}

@Composable
fun AppList(
    appInfoList: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit
) {
    AppListWithIndexBar(
        appInfoList = appInfoList,
        onAppClick = onAppClick
    )
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

@Composable
private fun IndexPopup(
    character: Char,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = character.toString(),
            color = colorResource(id = R.color.base_text),
            fontSize = 48.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun IndexBar(
    indexCharacters: List<Char>,
    onCharacterSelected: (Char, Boolean) -> Unit,  // 第2引数: isDragging
    onTouchStart: () -> Unit,
    onTouchEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var barHeight by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(24.dp)
            .padding(vertical = 20.dp)
            .onSizeChanged { barHeight = it.height }
            .pointerInput(indexCharacters) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onTouchStart()
                        val index = calculateIndexFromOffset(
                            offset.y,
                            barHeight.toFloat(),
                            indexCharacters.size
                        )
                        if (index in indexCharacters.indices) {
                            onCharacterSelected(indexCharacters[index], true)
                        }
                    },
                    onDragEnd = { onTouchEnd() },
                    onDragCancel = { onTouchEnd() },
                    onDrag = { change, _ ->
                        change.consume()
                        val index = calculateIndexFromOffset(
                            change.position.y,
                            barHeight.toFloat(),
                            indexCharacters.size
                        )
                        if (index in indexCharacters.indices) {
                            onCharacterSelected(indexCharacters[index], true)
                        }
                    }
                )
            }
            .pointerInput(indexCharacters) {
                detectTapGestures { offset ->
                    onTouchStart()
                    val index = calculateIndexFromOffset(
                        offset.y,
                        barHeight.toFloat(),
                        indexCharacters.size
                    )
                    if (index in indexCharacters.indices) {
                        onCharacterSelected(indexCharacters[index], false)
                    }
                    onTouchEnd()
                }
            },
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        indexCharacters.forEach { char ->
            Text(
                text = char.toString(),
                color = colorResource(id = R.color.base_text).copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun AppListWithIndexBar(
    appInfoList: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val indexCharacters = remember(appInfoList) { extractIndexCharacters(appInfoList) }
    val charToPositionMap = remember(appInfoList) { createCharToPositionMap(appInfoList) }

    var selectedCharacter by remember { mutableStateOf<Char?>(null) }
    var isPopupVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // メインリスト
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp, end = 24.dp)
        ) {
            items(appInfoList) { appInfo ->
                AppItem(appInfo = appInfo, onAppClick = onAppClick)
            }
        }

        // インデックスバー（右端）
        if (indexCharacters.isNotEmpty()) {
            IndexBar(
                indexCharacters = indexCharacters,
                onCharacterSelected = { char, isDragging ->
                    selectedCharacter = char
                    charToPositionMap[char]?.let { position ->
                        coroutineScope.launch {
                            // ビューポート中央にアイテムを配置するためのオフセット計算
                            val viewportHeight = listState.layoutInfo.viewportSize.height
                            val centerOffset = (-viewportHeight / 2.2f).toInt()

                            if (isDragging) {
                                listState.scrollToItem(position, scrollOffset = centerOffset)
                            } else {
                                listState.animateScrollToItem(position, scrollOffset = centerOffset)
                            }
                        }
                    }
                },
                onTouchStart = { isPopupVisible = true },
                onTouchEnd = { isPopupVisible = false },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        // ポップアップ（中央）
        if (isPopupVisible && selectedCharacter != null) {
            IndexPopup(
                character = selectedCharacter!!,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// プレビュー用のサンプルデータは PreviewData.kt（previewAppList）に集約している。
// VRT と同じデータを使うことで、プレビューが通れば期待画像も同じ内容になる。

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
                appInfoList = previewAppList(),
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
                appInfoList = previewAppList(),
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
