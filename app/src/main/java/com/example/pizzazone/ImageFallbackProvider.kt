package com.example.pizzazone

import java.util.Locale

/**
 * Provides local asset image URIs for known product title patterns.
 * Used as a fallback when ItemModel.picUrl is empty.
 */
object ImageFallbackProvider {
    fun getAssetImageForTitle(title: String?): String? {
        if (title.isNullOrBlank()) return null
        val t = title.lowercase(Locale.getDefault())

        return when {
            t.contains("3kw") && t.contains("solar") -> "file:///android_asset/3KW Solar Power System.webp"
            t.contains("3kw") -> "file:///android_asset/3kw-solar-power-system09116288583.webp"

            t.contains("5kw") && t.contains("all in one") -> "file:///android_asset/5KW All In One Solar Power System.webp"
            t.contains("5kw") -> "file:///android_asset/5KW Hot Sales All In One Solar System.webp"

            t.contains("20kw") -> "file:///android_asset/20kw-solar-power-system31528781028.webp"

            t.contains("60 poly") || (t.contains("poly") && t.contains("panel")) ->
                "file:///android_asset/60 Poly Solar Panel 250W-290W.webp"

            t.contains("75kw") || t.contains("off-grid") -> "file:///android_asset/75KW Off-grid Solar System.webp"

            t.contains("360w") || t.contains("380w") || t.contains("solar panal") || (t.contains("solar") && t.contains("panel")) ->
                "file:///android_asset/360w-380w solar panal.webp"

            t.contains("lithium") -> "file:///android_asset/lithium battery.jpg"
            t.contains("battery") -> "file:///android_asset/popular off grid battery.webp"

            else -> null
        }
    }
}
