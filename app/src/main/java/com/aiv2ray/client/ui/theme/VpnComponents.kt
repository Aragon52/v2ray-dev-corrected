package com.aiv2ray.client.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * A brushed-steel recessed tray. Top inner highlight + bottom inner shadow
 * create a chamfered pocket feel. Use for cards, chips, gauge surrounds.
 */
@Composable
fun SteelTray(
    modifier: Modifier = Modifier,
    radius: androidx.compose.ui.unit.Dp = 14.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(radius),
                ambientColor = Steel.Shadow,
                spotColor = Steel.Shadow
            )
            .background(
                brush = Brush.verticalGradient(
                    0f to Steel.Mid,
                    1f to Steel.Base
                ),
                shape = RoundedCornerShape(radius)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    0f to Steel.High,
                    1f to Steel.Shadow
                ),
                shape = RoundedCornerShape(radius)
            )
            .padding(12.dp)
    ) {
        content()
    }
}

/**
 * Brushed-steel key-cap button. Has a pressed-in default look; add a copper
 * under-glow when active.
 */
@Composable
fun KeyCapButton(
    modifier: Modifier = Modifier,
    active: Boolean = false,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val glow = if (active)
        Modifier.shadow(elevation = 4.dp, ambientColor = Copper.Glow.copy(alpha = 0.35f))
    else Modifier

    Box(
        modifier = modifier
            .then(glow)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(999.dp),
                ambientColor = Steel.Shadow,
                spotColor = Steel.Shadow
            )
            .background(
                brush = Brush.verticalGradient(
                    0f to Steel.Mid,
                    1f to Color(0xFF1F2226)
                ),
                shape = RoundedCornerShape(999.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    0f to Steel.High,
                    1f to Steel.Shadow
                ),
                shape = RoundedCornerShape(999.dp)
            )
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
