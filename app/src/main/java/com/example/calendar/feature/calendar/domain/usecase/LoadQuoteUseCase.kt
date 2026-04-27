package com.example.calendar.feature.calendar.domain.usecase

import com.example.calendar.feature.calendar.data.remote.HitokotoApi
import timber.log.Timber

class LoadQuoteUseCase(
    private val hitokotoApi: HitokotoApi
) {
    data class QuoteResult(
        val text: String,
        val author: String?
    )

    suspend operator fun invoke(): Result<QuoteResult> {
        return try {
            val response = hitokotoApi.getHitokoto()
            Result.success(
                QuoteResult(
                    text = response.hitokoto,
                    author = response.from_who ?: response.from
                )
            )
        } catch (e: Exception) {
            Timber.w(e, "LoadQuoteUseCase failed")
            Result.failure(e)
        }
    }
}
