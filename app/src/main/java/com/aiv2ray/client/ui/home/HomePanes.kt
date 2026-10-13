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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.PowerSettingsNew
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiv2ray.client.data.ServerProfile
import com.aiv2ray.client.ui.theme.Copper
import com.aiv2ray.client.ui.theme.Iron
import com.aiv2ray.client.ui.theme.KeyCapButton
import com.aiv2ray.client.ui.theme.ScrewedSteelPlate
import com.aiv2ray.client.ui.theme.Steel
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
    ScrewedSteelPlate(modifier = Modifier.fillMaxWidth()) {
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
    ScrewedSteelPlate(modifier = Modifier.fillMaxWidth()) {
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
    ScrewedSteelPlate(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Dial(
                label = "CONNECTION",
                value = if (connecting) "ONLINE" else "OFFLINE",
                needleRatio = if (connecting) 0.72f else 0.05f,
                accent = if (connecting) Copper.Highlight else Iron.StatusLed,
                modifier = Modifier.weight(1f)
            )
            PowerButton(
                active = connecting,
                onTap = onTapConnect,
                modifier = Modifier.size(78.dp)
            )
            Dial(
                label = "CORE",
                value = "XRAY",
                needleRatio = 0.62f,
                accent = Copper.Front,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun Dial(
    label: String,
    value: String,
    needleRatio: Float,
    accent: Color,
    modifier: Modifier
) {
    val animatedRatio by animateFloatAsState(
        targetValue = needleRatio.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "dialNeedle"
    )

    Column(
        modifier = modifier
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .shadow(8.dp, CircleShape, ambientColor = Steel.Shadow)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF484E56), Color(0xFF1B1E22), Color(0xFF101214))
                    ),
                    CircleShape
                )
                .border(
                    2.dp,
                    Brush.verticalGradient(
                        listOf(Color(0xFF7E8793), Color(0xFF383D44), Color(0xFF101215))
                    ),
                    CircleShape
                )
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = (size.minDimension / 2f) - 2f
                for (tick in 0..28) {
                    val angle = Math.toRadians(135.0 + tick * 270.0 / 28)
                    val isMajor = tick % 4 == 0
                    val innerRadius = radius - if (isMajor) 7f else 4f
                    drawLine(
                        color = if (isMajor) Color(0xFFD6C29E) else Color(0xFF6E747C),
                        start = Offset(
                            center.x + innerRadius * cos(angle).toFloat(),
                            center.y + innerRadius * sin(angle).toFloat()
                        ),
                        end = Offset(
                            center.x + radius * cos(angle).toFloat(),
                            center.y + radius * sin(angle).toFloat()
                        ),
                        strokeWidth = if (isMajor) 1.5f else 1f
                    )
                }

                val needleAngle = Math.toRadians(135.0 + animatedRatio * 270.0)
                val needleTip = Offset(
                    center.x + (radius - 8f) * cos(needleAngle).toFloat(),
                    center.y + (radius - 8f) * sin(needleAngle).toFloat()
                )
                drawArc(
                    color = Color(0xFF151719),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius + 2f, center.y - radius + 2f),
                    size = Size((radius - 2f) * 2f, (radius - 2f) * 2f),
                    style = Stroke(width = 2f)
                )
                drawLine(accent, center, needleTip, strokeWidth = 2.5f)
                drawCircle(Color(0xFF0F1113), radius = 5f, center = center)
                drawCircle(Color(0xFFB56534), radius = 3.5f, center = center)
                drawCircle(Color(0xFFFFD199), radius = 1.5f, center = center)
            }
        }
        Text(
            text = value,
            color = Copper.Highlight,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = label,
            color = Iron.EngraveLight,
            fontSize = 8.sp,
            letterSpacing = 0.7.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun PowerButton(active: Boolean, onTap: () -> Unit, modifier: Modifier) {
    val halo = if (active) Copper.Glow.copy(alpha = 0.6f) else Color.Transparent
    Box(
        modifier = modifier
            .shadow(14.dp, CircleShape, ambientColor = halo, spotColor = halo)
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF5A616A), Color(0xFF282C31), Color(0xFF111316))
                ),
                CircleShape
            )
            .border(
                2.5.dp,
                if (active) Brush.radialGradient(listOf(Color(0xFFFFB673), Color(0xFFB85926)))
                else Brush.verticalGradient(listOf(Color(0xFF6F7782), Color(0xFF1E2125))),
                CircleShape
            )
            .padding(7.dp)
            .clickable(role = Role.Button, onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(listOf(Color(0xFF353A41), Color(0xFF121417))),
                    CircleShape
                )
                .border(
                    1.5.dp,
                    if (active) Color(0xFFFF9D47) else Color(0xFF4C535C),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.PowerSettingsNew,
                contentDescription = if (active) "Disconnect VPN" else "Connect VPN",
                tint = if (active) Color(0xFFFFB878) else Color(0xFF888F99),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
