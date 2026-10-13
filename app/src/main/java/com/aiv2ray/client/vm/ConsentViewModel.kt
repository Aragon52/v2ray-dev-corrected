package com.aiv2ray.client.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Application.consentStore by preferencesDataStore(name = "user_consent")

class ConsentViewModel(app: Application) : AndroidViewModel(app) {
    private val key = booleanPreferencesKey("vpn_consent_v1")
    val accepted: Flow<Boolean> = app.consentStore.data.map { it[key] ?: false }

    fun accept() {
        viewModelScope.launch {
            app.consentStore.edit { it[key] = true }
        }
    }
}
