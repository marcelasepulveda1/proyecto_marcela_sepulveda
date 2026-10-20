package com.example.panalsuite.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/** Hexagon ("pointy top") used for avatars, icons and the progress steps. */
val HexagonShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val radius = min(size.width, size.height) / 2f
        return Outline.Generic(hexagonPath(Offset(size.width / 2f, size.height / 2f), radius))
    }
}

/** Decorative honeycomb pattern drawn with a Canvas, used as a background texture. */
@Composable
fun HoneycombPattern(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
    cellRadius: Dp = 22.dp
) {
    Canvas(modifier = modifier) {
        val radius = cellRadius.toPx()
        val cellWidth = sqrt(3f) * radius
        val rowHeight = radius * 1.5f
        val stroke = Stroke(width = 1.5.dp.toPx())

        var row = 0
        var y = 0f
        while (y < size.height + radius * 2) {
            var x = if (row % 2 == 0) 0f else cellWidth / 2f
            while (x < size.width + cellWidth) {
                drawPath(hexagonPath(Offset(x, y), radius * 0.92f), color = color, style = stroke)
                x += cellWidth
            }
            y += rowHeight
            row++
        }
    }
}

private fun hexagonPath(center: Offset, radius: Float): Path = Path().apply {
    for (corner in 0 until 6) {
        val angle = Math.toRadians(60.0 * corner - 30.0)
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()
        if (corner == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}
