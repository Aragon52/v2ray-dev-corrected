package com.aiv2ray.client.ui.home

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiv2ray.client.service.V2RayVpnService
import com.aiv2ray.client.ui.info.InfoPane
import com.aiv2ray.client.ui.settings.SettingsPane
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
            .padding(16.dp)
    ) {
        Header(connecting = connecting)
        Spacer(Modifier.height(12.dp))
        NavBar(current = tab, onPick = { tab = it })
        Spacer(Modifier.height(16.dp))

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

private fun startTunnel(ctx: android.content.Context, vm: HomeViewModel, json: String) {
    val i = Intent(ctx, V2RayVpnService::class.java)
        .putExtra(V2RayVpnService.EXTRA_CONFIG_JSON, json)
    ctx.startForegroundService(i)
    vm.setMessage("Tunnel starting…")
}

@Composable
private fun Header(connecting: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(if (connecting) Copper.Front else Iron.StatusLed, CircleShape)
                .shadow(2.dp, CircleShape, ambientColor = Iron.StatusLed)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Ai V2Ray Client",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = Copper.Front
        )
        Spacer(Modifier.weight(1f))
        StatusPill(connecting)
    }
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
            .shadow(8.dp, RoundedCornerShape(999.dp), ambientColor = Steel.Shadow)
            .background(Brush.verticalGradient(listOf(Steel.Mid, Steel.Base)), RoundedCornerShape(999.dp))
            .border(1.dp, Steel.High, RoundedCornerShape(999.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(Tab.Connection, current, "CONNECTION", Icons.Default.Bolt) { onPick(Tab.Connection) }
        NavItem(Tab.Settings, current, "SETTINGS", Icons.Default.Settings) { onPick(Tab.Settings) }
        NavItem(Tab.Information, current, "INFO", Icons.Default.Info) { onPick(Tab.Information) }
    }
}

@Composable
private fun RowScope.NavItem(
    tab: Tab,
    current: Tab,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val active = current == tab
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (active) Brush.verticalGradient(listOf(Steel.Shadow, Steel.Base))
                else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon, contentDescription = null,
                tint = if (active) Copper.Front else Iron.EngraveLight,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                color = if (active) Copper.Front else Iron.EngraveLight,
                fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}
