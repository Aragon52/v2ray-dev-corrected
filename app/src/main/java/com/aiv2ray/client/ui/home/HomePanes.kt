package com.aiv2ray.client.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiv2ray.client.data.ServerProfile
import com.aiv2ray.client.ui.theme.Copper
import com.aiv2ray.client.ui.theme.Iron
import com.aiv2ray.client.ui.theme.KeyCapButton
import com.aiv2ray.client.ui.theme.Steel
import com.aiv2ray.client.ui.theme.SteelTray
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ConnectionPane(
    servers: List<ServerProfile>,
    selected: ServerProfile?,
    connecting: Boolean,
    message: String?,
    onAddLink: (String) -> Unit,
    onSelect: (ServerProfile) -> Unit,
    onRemove: (String) -> Unit,
    onTapConnect: () -> Unit
) {
    var linkInput by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AddNodePanel(
            input = linkInput,
            onInput = { linkInput = it },
            onAdd = {
                onAddLink(linkInput)
                linkInput = ""
            }
        )

        if (!message.isNullOrBlank()) {
            Text(
                text = message,
                color = Copper.Highlight,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        NodeList(servers = servers, selected = selected, onSelect = onSelect, onRemove = onRemove)

        Spacer(Modifier.height(4.dp))
        BottomDials(connecting = connecting, onTapConnect = onTapConnect)
    }
}

@Composable
private fun AddNodePanel(
    input: String,
    onInput: (String) -> Unit,
    onAdd: () -> Unit
) {
    SteelTray(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Copper.Glow.copy(alpha = 0.18f), CircleShape)
                        .border(1.dp, Copper.Front, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.VpnKey, contentDescription = null,
                        tint = Copper.Front, modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ADD NODE",
                    color = Copper.Highlight,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Text(
                text = "Paste a share link to add a server.",
                color = Iron.EngraveLight, fontSize = 13.sp
            )
            Text(
                text = "vmess://  ·  vless://  ·  trojan://  ·  ss://",
                color = Iron.EngraveLight.copy(alpha = 0.7f), fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )

            EngravedField(
                value = input,
                onValueChange = onInput,
                placeholder = "paste share link…"
            )

            KeyCapButton(
                active = input.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                onClick = onAdd
            ) {
                Text(
                    text = "ADD SERVER",
                    color = if (input.isNotBlank()) Copper.Front else Iron.EngraveLight,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun EngravedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(10.dp), ambientColor = Steel.Shadow)
            .background(Brush.verticalGradient(listOf(Steel.Mid, Steel.Base)), RoundedCornerShape(10.dp))
            .border(1.dp, Steel.High, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = Iron.EngraveLight.copy(alpha = 0.55f),
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = Iron.EngraveLight,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            ),
            cursorBrush = SolidColor(Copper.Front),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NodeList(
    servers: List<ServerProfile>,
    selected: ServerProfile?,
    onSelect: (ServerProfile) -> Unit,
    onRemove: (String) -> Unit
) {
    SteelTray(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Public, contentDescription = null,
                    tint = Copper.Front, modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "NODES (${servers.size})",
                    color = Copper.Highlight, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp
                )
            }

            if (servers.isEmpty()) {
                Text(
                    text = "No nodes yet — add one above.",
                    color = Iron.EngraveLight.copy(alpha = 0.7f), fontSize = 13.sp
                )
            } else {
                servers.forEach { server ->
                    NodeRow(
                        server = server,
                        active = server.name == selected?.name,
                        onSelect = { onSelect(server) },
                        onRemove = { onRemove(server.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NodeRow(
    server: ServerProfile,
    active: Boolean,
    onSelect: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(Steel.Mid, Steel.Base)),
                RoundedCornerShape(8.dp)
            )
            .border(
                if (active) 1.dp else 1.dp,
                if (active) Copper.Front else Steel.High,
                RoundedCornerShape(8.dp)
            )
            .clickable { onSelect() }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = server.name,
            color = if (active) Copper.Highlight else Iron.EngraveLight,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.Close, contentDescription = "Remove",
            tint = Iron.EngraveLight.copy(alpha = 0.7f),
            modifier = Modifier
                .size(16.dp)
                .clickable { onRemove() }
        )
    }
}

@Composable
private fun BottomDials(connecting: Boolean, onTapConnect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Dial(
            label = "STATUS", value = if (connecting) "ON" else "OFF", unit = "",
            accent = if (connecting) Copper.Highlight else Iron.StatusLed,
            modifier = Modifier.weight(1f)
        )
        PowerButton(
            active = connecting, onTap = onTapConnect,
            modifier = Modifier.weight(1.3f)
        )
        Dial(
            label = "ENGINE", value = "XRAY", unit = "",
            accent = Copper.Front,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun Dial(
    label: String,
    value: String,
    unit: String,
    accent: Color,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(14.dp), ambientColor = Steel.Shadow)
            .background(Brush.verticalGradient(listOf(Steel.Mid, Steel.Base)), RoundedCornerShape(14.dp))
            .border(1.dp, Steel.High, RoundedCornerShape(14.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cx = w / 2f
                val cy = h / 2f
                val r = (minOf(w, h) / 2f) - 6f
                drawArc(
                    color = Steel.High,
                    startAngle = 135f, sweepAngle = 270f, useCenter = false,
                    topLeft = Offset(cx - r, cy - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = 3f)
                )
                drawArc(
                    color = accent,
                    startAngle = 135f, sweepAngle = 60f, useCenter = false,
                    topLeft = Offset(cx - r, cy - r),
                    size = Size(r * 2, r * 2),
                    style = Stroke(width = 4f)
                )
                drawLine(
                    color = accent,
                    start = Offset(cx, cy),
                    end = Offset(
                        cx + r * cos(Math.toRadians(195.0)).toFloat(),
                        cy + r * sin(Math.toRadians(195.0)).toFloat()
                    ),
                    strokeWidth = 3f
                )
            }
        }
        Text(
            text = label, color = Iron.EngraveLight, fontSize = 9.sp,
            letterSpacing = 1.5.sp, fontWeight = FontWeight.SemiBold
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                value, color = Copper.Highlight,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp, fontWeight = FontWeight.Bold
            )
            if (unit.isNotBlank()) {
                Spacer(Modifier.width(4.dp))
                Text(unit, color = Copper.Highlight, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun PowerButton(active: Boolean, onTap: () -> Unit, modifier: Modifier) {
    val glow = if (active) Copper.Glow.copy(alpha = 0.35f) else Color.Transparent
    Box(
        modifier = modifier
            .height(120.dp)
            .shadow(12.dp, CircleShape, ambientColor = glow, spotColor = glow)
            .background(Brush.radialGradient(listOf(Steel.Mid, Steel.Shadow)), CircleShape)
            .border(2.dp, if (active) Copper.Highlight else Copper.Front, CircleShape)
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Bolt, contentDescription = "Connect",
            tint = if (active) Copper.Highlight else Copper.Front,
            modifier = Modifier.size(38.dp)
        )
    }
}
