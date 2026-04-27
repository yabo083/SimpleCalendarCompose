package com.example.calendar.feature.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendar.feature.calendar.domain.usecase.GetMonthDaysUseCase
import com.example.calendar.feature.calendar.domain.usecase.LoadQuoteUseCase
import com.example.calendar.feature.settings.domain.repository.SettingsRepository
import com.example.calendar.feature.weather.domain.repository.WeatherRepository
import com.example.calendar.feature.weather.domain.usecase.RefreshWeatherUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import kotlin.time.Duration.Companion.minutes

class CalendarViewModel(
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository,
    private val getMonthDays: GetMonthDaysUseCase,
    private val refreshWeather: RefreshWeatherUseCase,
    private val loadQuoteUseCase: LoadQuoteUseCase
) : ViewModel() {
    private val _currentMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    private data class QuoteState(
        val text: String = "",
        val author: String? = null,
        val isLoading: Boolean = false
    )

    private val _quoteState = MutableStateFlow(QuoteState())

    val uiState: StateFlow<CalendarUiState> = combine(
        _currentMonth,
        _selectedDate,
        weatherRepository.getWeatherFlow(),
        _quoteState
    ) { currentMonth, selectedDate, weatherList, quote ->
        val monthsToLoad = listOf(
            currentMonth.minusMonths(1),
            currentMonth,
            currentMonth.plusMonths(1)
        )
        val daysMap = monthsToLoad.associateWith { month ->
            getMonthDays(month, selectedDate, weatherList)
        }
        CalendarUiState(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            monthDaysMap = daysMap,
            quoteText = quote.text,
            quoteAuthor = quote.author,
            isQuoteLoading = quote.isLoading
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())
        .also { Timber.d("CalendarViewModel: uiState Flow created") }

    init {
        Timber.d("CalendarViewModel: created")
        viewModelScope.launch { refreshWeather() }
        startPeriodicRefresh()
        loadQuote()
    }

    fun loadQuote() {
        viewModelScope.launch {
            _quoteState.update { it.copy(isLoading = true) }
            loadQuoteUseCase().onSuccess { result ->
                _quoteState.update {
                    QuoteState(
                        text = result.text,
                        author = result.author,
                        isLoading = false
                    )
                }
            }.onFailure {
                Timber.w(it, "loadQuote failed")
                _quoteState.update { current ->
                    if (current.text.isEmpty()) {
                        QuoteState(
                            text = "生活不止眼前的苟且，还有诗和远方。",
                            author = "高晓松",
                            isLoading = false
                        )
                    } else {
                        current.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun startPeriodicRefresh() {
        viewModelScope.launch {
            while (isActive) {
                val interval = settingsRepository.getTodayRefreshMinutes()
                delay(interval.minutes)
                refreshWeather()
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.update { date }
    }

    fun onMonthChanged(newMonth: LocalDate) {
        _currentMonth.update { newMonth }
    }
}
