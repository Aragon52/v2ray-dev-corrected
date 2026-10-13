package com.aiv2ray.client.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScrewHead(modifier: Modifier = Modifier, size: Dp = 10.dp) {
    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val centerX = this.size.width / 2f
        val centerY = this.size.height / 2f
        val slotLength = radius * 0.65f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF6B727A), Color(0xFF1E2125)),
                center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f),
                radius = radius
            ),
            radius = radius
        )
        drawCircle(
            color = Color(0xFF0D0F11),
            radius = radius,
            style = Stroke(width = 1f)
        )
        drawLine(
            color = Color(0xFF0C0E10),
            start = Offset(centerX - slotLength, centerY),
            end = Offset(centerX + slotLength, centerY),
            strokeWidth = 1.5f
        )
        drawLine(
            color = Color(0xFF0C0E10),
            start = Offset(centerX, centerY - slotLength),
            end = Offset(centerX, centerY + slotLength),
            strokeWidth = 1.5f
        )
    }
}

@Composable
fun ScrewedSteelPlate(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    hasCornerScrews: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Steel.Shadow,
                spotColor = Color.Black
            )
            .background(
                brush = Brush.verticalGradient(
                    0f to Color(0xFF5E656E),
                    0.04f to Color(0xFF434951),
                    0.5f to Color(0xFF33383F),
                    0.96f to Color(0xFF23262A),
                    1f to Color(0xFF191B1E)
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF8B949F), Color(0xFF3B4047), Color(0xFF141618))
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(12.dp)
    ) {
        content()
        if (hasCornerScrews) {
            ScrewHead(Modifier.align(Alignment.TopStart).offset(2.dp, 2.dp))
            ScrewHead(Modifier.align(Alignment.TopEnd).offset((-2).dp, 2.dp))
            ScrewHead(Modifier.align(Alignment.BottomStart).offset(2.dp, (-2).dp))
            ScrewHead(Modifier.align(Alignment.BottomEnd).offset((-2).dp, (-2).dp))
        }
    }
}

@Composable
fun CopperHeaderTitle(text: String = "Ai V2Ray Client") {
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp,
            color = Color(0xFFFF8A3D).copy(alpha = 0.55f),
            modifier = Modifier.blur(8.dp)
        )
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp,
            color = Color(0xFF281105),
            modifier = Modifier.offset(y = 3.dp)
        )
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp,
            color = Color(0xFF8B471D),
            modifier = Modifier.offset(y = 1.dp)
        )
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp,
            color = Color(0xFFE28B52)
        )
    }
}

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
