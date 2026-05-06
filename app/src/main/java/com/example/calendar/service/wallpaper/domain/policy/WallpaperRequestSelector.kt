package com.example.calendar.service.wallpaper.domain.policy

import com.example.calendar.service.wallpaper.domain.model.WallpaperRequest

object WallpaperRequestSelector {
    fun requestsForWidth(deviceWidthDp: Int): List<WallpaperRequest> {
        return when {
            deviceWidthDp < 480 -> listOf(
                WallpaperRequest("anime"),
                WallpaperRequest("acg", "mb"),
                WallpaperRequest("mobile_wallpaper"),
                WallpaperRequest("landscape"),
                WallpaperRequest("general_anime"),
                WallpaperRequest("furry", "s4k")
            )

            deviceWidthDp < 720 -> listOf(
                WallpaperRequest("anime"),
                WallpaperRequest("acg"),
                WallpaperRequest("landscape"),
                WallpaperRequest("pc_wallpaper"),
                WallpaperRequest("general_anime"),
                WallpaperRequest("mobile_wallpaper")
            )

            else -> listOf(
                WallpaperRequest("anime"),
                WallpaperRequest("acg", "pc"),
                WallpaperRequest("pc_wallpaper"),
                WallpaperRequest("landscape"),
                WallpaperRequest("general_anime"),
                WallpaperRequest("furry", "4k")
            )
        }
    }

    fun select(deviceWidthDp: Int, index: Int): WallpaperRequest {
        val requests = requestsForWidth(deviceWidthDp)
        return requests[index.floorMod(requests.size)]
    }

    private fun Int.floorMod(divisor: Int): Int = ((this % divisor) + divisor) % divisor
}
