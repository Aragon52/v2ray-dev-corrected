package com.aiv2ray.client.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aiv2ray.client.data.ServerProfile
import com.aiv2ray.client.data.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ServerRepository(app)

    private val _connect = MutableStateFlow(false)
    val connecting: StateFlow<Boolean> = _connect.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    val servers: StateFlow<List<ServerProfile>> =
        repo.servers.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val selected: StateFlow<ServerProfile?> =
        repo.selected.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun toggleConnecting(v: Boolean) {
        _connect.value = v
    }

    fun setMessage(m: String?) {
        _message.value = m
    }

    fun addServer(uri: String, name: String? = null) {
        val trimmed = uri.trim()
        if (trimmed.isBlank()) {
            _message.value = "Paste a share link first"
            return
        }
        viewModelScope.launch {
            try {
                val json = repo.buildConfig(trimmed)
                val display = name?.takeIf { it.isNotBlank() } ?: repo.displayName(trimmed)
                repo.add(ServerProfile(display, trimmed, json))
                _message.value = "Added: $display"
            } catch (t: Throwable) {
                _message.value = "Invalid link: ${t.message ?: t.javaClass.simpleName}"
            }
        }
    }

    fun select(profile: ServerProfile) {
        viewModelScope.launch { repo.select(profile.name) }
    }

    fun remove(name: String) {
        viewModelScope.launch { repo.remove(name) }
    }
}
