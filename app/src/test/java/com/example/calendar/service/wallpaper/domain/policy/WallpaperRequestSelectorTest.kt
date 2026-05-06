package com.example.calendar.service.wallpaper.domain.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WallpaperRequestSelectorTest {
    @Test
    fun `small phones include mobile friendly requests`() {
        val requests = WallpaperRequestSelector.requestsForWidth(360)

        assertTrue(requests.any { it.category == "mobile_wallpaper" && it.type == null })
        assertTrue(requests.any { it.category == "acg" && it.type == "mb" })
    }

    @Test
    fun `large screens include wide wallpaper requests`() {
        val requests = WallpaperRequestSelector.requestsForWidth(720)

        assertTrue(requests.any { it.category == "pc_wallpaper" && it.type == null })
        assertTrue(requests.any { it.category == "acg" && it.type == "pc" })
    }

    @Test
    fun `selection cycles deterministically through available requests`() {
        val requests = WallpaperRequestSelector.requestsForWidth(360)

        assertEquals(requests[0], WallpaperRequestSelector.select(360, 0))
        assertEquals(requests[1], WallpaperRequestSelector.select(360, 1))
        assertEquals(requests[0], WallpaperRequestSelector.select(360, requests.size))
    }
}
