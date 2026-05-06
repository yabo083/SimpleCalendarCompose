package com.example.calendar.feature.calendar.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calendar.core.designsystem.BackgroundImage
import com.example.calendar.core.designsystem.PreferenceButtons
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun CalendarScreen() {
    val viewModel: CalendarViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val wallpaperPath by viewModel.wallpaperPath.collectAsStateWithLifecycle()
    val currentCategory by viewModel.currentCategory.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val density = LocalDensity.current
    var backgroundWidthDp by remember { mutableIntStateOf(0) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        Timber.d("CalendarScreen: permission result granted=$granted")
    }

    LaunchedEffect(backgroundWidthDp) {
        if (backgroundWidthDp > 0) {
            viewModel.refreshBackground(backgroundWidthDp)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                val measuredWidth = with(density) {
                    size.width.toDp().value.roundToInt().coerceAtLeast(1)
                }
                if (backgroundWidthDp != measuredWidth) {
                    backgroundWidthDp = measuredWidth
                }
            }
    ) {
        BackgroundImage(imageUrl = wallpaperPath) {
            CalendarContent(
                uiState = uiState,
                onDateSelected = viewModel::onDateSelected,
                onRefreshQuote = viewModel::loadQuote,
                onMonthChanged = viewModel::onMonthChanged
            )
        }

        PreferenceButtons(
            onLike = { viewModel.onLike(currentCategory) },
            onDislike = { viewModel.onDislike(currentCategory) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 128.dp)
        )
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }


}

@Composable
fun CalendarContent(
    uiState: CalendarUiState,
    onDateSelected: (LocalDate) -> Unit,
    onRefreshQuote: () -> Unit,
    onMonthChanged: (LocalDate) -> Unit,
) {
    val initialPage = Int.MAX_VALUE / 2
    val now = remember { LocalDate.now().withDayOfMonth(1) }

    val pagerState = rememberPagerState(initialPage) { Int.MAX_VALUE }

    val scope = rememberCoroutineScope()
    val headerShadow = Shadow(
        color = Color.Black.copy(alpha = 0.55f),
        offset = Offset(0f, 2f),
        blurRadius = 5f
    )

    val scrollToMonth: (Int) -> Unit = { offset ->
        scope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage + offset)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val monthOffset = page - initialPage
            onMonthChanged(now.plusMonths(monthOffset.toLong()))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { scrollToMonth(-1) }) {
                Text("<")
            }
            Text(
                text = "${uiState.currentMonth.year}年${uiState.currentMonth.monthValue}月",
                style = MaterialTheme.typography.titleLarge.copy(shadow = headerShadow),
                color = Color.White
            )
            IconButton(onClick = { scrollToMonth(1) }) {
                Text(">")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f),
                contentColor = Color.White
            )
        ) {
            HorizontalPager(state = pagerState) { page ->
                val monthOffset = page - initialPage
                val pageMonth = now.plusMonths(monthOffset.toLong())
                val days = uiState.monthDaysMap[pageMonth] ?: emptyList()
                CalendarGrid(
                    days = days,
                    onDateClick = onDateSelected,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

//        Spacer(modifier = Modifier.height(16.dp))
//        Text(
//            text = "已选日期: ${uiState.selectedDate}",
//            style = MaterialTheme.typography.bodyLarge
//        )

        Spacer(modifier = Modifier.weight(1f))

        QuoteCard(
            quoteText = uiState.quoteText,
            quoteAuthor = uiState.quoteAuthor,
            isLoading = uiState.isQuoteLoading,
            onRefresh = onRefreshQuote
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun QuoteCard(
    quoteText: String,
    quoteAuthor: String?,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.22f),
            contentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "换一句",
                    tint = Color.White.copy(alpha = 0.72f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White.copy(alpha = 0.9f)
                    )
                } else {
                    Text(
                        text = "「$quoteText」",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.78f),
                                offset = Offset(0f, 1.5f),
                                blurRadius = 6f
                            )
                        ),
                        color = Color.White.copy(alpha = 0.96f),
                        textAlign = TextAlign.Center
                    )
                    if (quoteAuthor != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "—— $quoteAuthor",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.65f),
                                    offset = Offset(0f, 1f),
                                    blurRadius = 5f
                                )
                            ),
                            color = Color.White.copy(alpha = 0.80f),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}
