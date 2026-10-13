package com.aiv2ray.client.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.serversStore by preferencesDataStore(name = "v2ray_servers")

data class ServerProfile(
    val name: String,
    val uri: String,
    val json: String
)

class ServerRepository(private val ctx: Context) {

    private val keyServers = stringPreferencesKey("servers_json_v1")
    private val keySelected = stringPreferencesKey("selected_server_v1")

    val servers: Flow<List<ServerProfile>> = ctx.serversStore.data.map { p ->
        decodeServers(p[keyServers].orEmpty())
    }

    val selected: Flow<ServerProfile?> = ctx.serversStore.data.map { p ->
        val all = decodeServers(p[keyServers].orEmpty())
        p[keySelected]?.let { id -> all.firstOrNull { it.name == id } } ?: all.firstOrNull()
    }

    suspend fun add(profile: ServerProfile) {
        ctx.serversStore.edit { p ->
            val all = decodeServers(p[keyServers].orEmpty()).toMutableList()
            all.removeAll { it.name == profile.name }
            all += profile
            p[keyServers] = encodeServers(all)
            p[keySelected] = profile.name
        }
    }

    suspend fun remove(name: String) {
        ctx.serversStore.edit { p ->
            val all = decodeServers(p[keyServers].orEmpty()).filter { it.name != name }
            p[keyServers] = encodeServers(all)
            if (p[keySelected] == name) p.remove(keySelected)
        }
    }

    suspend fun select(name: String) {
        ctx.serversStore.edit { p -> p[keySelected] = name }
    }

    private fun encodeServers(list: List<ServerProfile>): String {
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject().apply {
                    put("name", it.name); put("uri", it.uri); put("json", it.json)
                }
            )
        }
        return arr.toString()
    }

    private fun decodeServers(raw: String): List<ServerProfile> {
        if (raw.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                ServerProfile(o.optString("name"), o.optString("uri"), o.optString("json"))
            }
        } catch (_: Throwable) {
            emptyList()
        }
    }

    /** Build a full Xray config JSON from a share link. */
    fun buildConfig(uri: String): String = V2RayConfigParser.parse(uri)

    fun displayName(uri: String): String =
        uri.substringAfter('#', "").ifBlank { uri.substringBefore("://") + " node" }
}
