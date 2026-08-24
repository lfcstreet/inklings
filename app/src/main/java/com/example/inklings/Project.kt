package com.example.inklings

import android.graphics.Color
import androidx.core.graphics.ColorUtils

/**
 * Requirement 17A & 17B: Project Data Model.
 * The Project Name is derived from the directory name on the filesystem.
 * Other metadata is stored in .inklings-project.json.
 */
data class Project(
    val name: String,
    val baseFontColor: String,
    val isDefault: Boolean
) {
    /**
     * Requirement 17B Update: Derive a theme-safe color from the base color.
     * Ensures sufficient contrast against Light (white-ish) or Dark (black-ish) backgrounds
     * while preserving the original hue as much as possible.
     */
    fun getThemeAwareColor(isDark: Boolean): Int {
        val baseInt = try {
            Color.parseColor(baseFontColor)
        } catch (_: Exception) {
            return if (isDark) Color.WHITE else Color.BLACK
        }

        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(baseInt, hsl)

        val referenceColor = if (isDark) Color.BLACK else Color.WHITE
        
        // Target contrast ratio of 4.5:1 (WCAG AA for body text)
        val targetContrast = 4.5f
        
        var currentColor = ColorUtils.HSLToColor(hsl)
        var currentContrast = ColorUtils.calculateContrast(currentColor, referenceColor)

        if (currentContrast < targetContrast) {
            if (isDark) {
                // Too dark for a dark background -> increase lightness
                while (currentContrast < targetContrast && hsl[2] < 1.0f) {
                    hsl[2] = (hsl[2] + 0.05f).coerceAtMost(1.0f)
                    currentColor = ColorUtils.HSLToColor(hsl)
                    currentContrast = ColorUtils.calculateContrast(currentColor, referenceColor)
                }
            } else {
                // Too light for a light background -> decrease lightness
                while (currentContrast < targetContrast && hsl[2] > 0.0f) {
                    hsl[2] = (hsl[2] - 0.05f).coerceAtLeast(0.0f)
                    currentColor = ColorUtils.HSLToColor(hsl)
                    currentContrast = ColorUtils.calculateContrast(currentColor, referenceColor)
                }
            }
        }

        return currentColor
    }
}
