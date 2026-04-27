package com.example.calendar.feature.calendar.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import java.time.LocalDate

@Composable
fun CalendarScreen() {
    val viewModel: CalendarViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        Timber.d("CalendarScreen: permission result granted=$granted")
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    CalendarContent(
        uiState = uiState,
        onDateSelected = viewModel::onDateSelected,
        onRefreshQuote = viewModel::loadQuote,
        onMonthChanged = viewModel::onMonthChanged
    )
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

    val scrollToMonth: (Int) -> Unit = { offset ->
        scope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage + offset)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
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
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { scrollToMonth(1) }) {
                Text(">")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(state = pagerState) { page ->
            val monthOffset = page - initialPage
            val pageMonth = now.plusMonths(monthOffset.toLong())

            val days = uiState.monthDaysMap[pageMonth] ?: emptyList()
            CalendarGrid(
                days = days,
                onDateClick = onDateSelected
            )
        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(
//            text = "已选日期: ${uiState.selectedDate}",
//            style = MaterialTheme.typography.bodyLarge
//        )

        Spacer(modifier = Modifier.height(24.dp))

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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "「$quoteText」",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                if (quoteAuthor != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "—— $quoteAuthor",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onRefresh) {
                Text("换一句")
            }
        }
    }
}
