package com.aiv2ray.client.ui.home

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiv2ray.client.service.V2RayVpnService
import com.aiv2ray.client.ui.info.InfoPane
import com.aiv2ray.client.ui.settings.SettingsPane
import com.aiv2ray.client.ui.theme.CopperHeaderTitle
import com.aiv2ray.client.ui.theme.Copper
import com.aiv2ray.client.ui.theme.Iron
import com.aiv2ray.client.ui.theme.Steel
import com.aiv2ray.client.vm.HomeViewModel

private enum class Tab { Connection, Settings, Information }

@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()) {
    val ctx = LocalContext.current
    var tab by rememberSaveable { mutableStateOf(Tab.Connection) }
    val servers by vm.servers.collectAsState()
    val selected by vm.selected.collectAsState()
    val connecting by vm.connecting.collectAsState()
    val message by vm.message.collectAsState()

    val vpnPrepLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            vm.toggleConnecting(true)
            startTunnel(ctx, vm, selected?.json ?: "{}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Steel.Base, Steel.Shadow)))
            .safeDrawingPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CopperHeaderTitle()
        Spacer(Modifier.height(6.dp))
        StatusPill(connecting)
        Spacer(Modifier.height(14.dp))
        NavBar(current = tab, onPick = { tab = it })
        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (tab) {
                Tab.Connection -> ConnectionPane(
                    servers = servers,
                    selected = selected,
                    connecting = connecting,
                    message = message,
                    onAddLink = { vm.addServer(it) },
                    onSelect = { vm.select(it) },
                    onRemove = { vm.remove(it) },
                    onTapConnect = {
                        when {
                            connecting -> {
                                ctx.startService(
                                    Intent(ctx, V2RayVpnService::class.java)
                                        .setAction(V2RayVpnService.ACTION_STOP)
                                )
                                vm.toggleConnecting(false)
                            }
                            selected == null -> vm.setMessage("Add a server link first")
                            else -> {
                                val intent = VpnService.prepare(ctx)
                                if (intent == null) {
                                    vm.toggleConnecting(true)
                                    startTunnel(ctx, vm, selected?.json ?: "{}")
                                } else {
                                    vpnPrepLauncher.launch(intent)
                                }
                            }
                        }
                    }
                )
                Tab.Settings -> SettingsPane()
                Tab.Information -> InfoPane()
            }
        }
    }
}

private fun startTunnel(ctx: android.content.Context, vm: HomeViewModel, json: String) {
    val i = Intent(ctx, V2RayVpnService::class.java)
        .putExtra(V2RayVpnService.EXTRA_CONFIG_JSON, json)
    ctx.startForegroundService(i)
    vm.setMessage("Tunnel starting…")
}

@Composable
private fun StatusPill(connecting: Boolean) {
    Box(
        modifier = Modifier
            .border(1.dp, Iron.OffLineBorder, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (connecting) " ONLINE " else " OFFLINE ",
            color = if (connecting) Copper.Highlight else Iron.StatusLed,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
private fun NavBar(current: Tab, onPick: (Tab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(6.dp, RoundedCornerShape(10.dp), ambientColor = Steel.Shadow)
            .background(
                Brush.verticalGradient(listOf(Color(0xFF2B2E33), Color(0xFF1E2024))),
                RoundedCornerShape(10.dp)
            )
            .border(1.dp, Color(0xFF4A5057), RoundedCornerShape(10.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            Tab.Connection to "Connection",
            Tab.Settings to "Settings",
            Tab.Information to "Information"
        ).forEach { (tab, label) ->
            val active = current == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (active) {
                            Brush.verticalGradient(
                                listOf(Color(0xFF5A626C), Color(0xFF373C43), Color(0xFF2A2E33))
                            )
                        } else {
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                        }
                    )
                    .then(
                        if (active) {
                            Modifier.border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(Color(0xFF86909C), Color(0xFF22252A))
                                ),
                                RoundedCornerShape(8.dp)
                            )
                        } else {
                            Modifier
                        }
                    )
                    .selectable(selected = active, role = Role.Tab) { onPick(tab) }
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        color = if (active) Color(0xFFF1F3F5) else Color(0xFF8D939C),
                        fontSize = 11.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                        maxLines = 1
                    )
                    if (active) {
                        Spacer(Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .shadow(4.dp, CircleShape, ambientColor = Copper.Glow)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            Color(0xFFFFD499),
                                            Color(0xFFD47333),
                                            Color(0xFF6B300B)
                                        )
                                    ),
                                    CircleShape
                                )
                                .border(0.8.dp, Color(0xFFFFECC4), CircleShape)
                        )
                    }
                }
            }
        }
    }
}
