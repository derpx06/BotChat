package com.example.botchat.ui.components.chat

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class ReverseRoundedShape : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerRadius = CornerRadius(12f * density.density)
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(0f, 0f, size.width, size.height + cornerRadius.y),
                    topLeft = CornerRadius.Zero,
                    topRight = CornerRadius.Zero,
                    bottomLeft = cornerRadius,
                    bottomRight = cornerRadius
                )
            )
        }
        return Outline.Generic(path)
    }
}