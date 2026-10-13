package com.aiv2ray.client.data

import android.util.Base64
import java.net.URI
import java.net.URLDecoder
import org.json.JSONArray
import org.json.JSONObject

/**
 * Parses vmess:// / vless:// / trojan:// / ss:// share links into a **complete**
 * Xray-core JSON configuration, including the `"protocol": "tun"` inbound that
 * the AndroidLibXrayLite core consumes (the core reads the tun fd from the
 * `xray.tun.fd` env var, which CoreController.startLoop() sets).
 *
 * No telemetry, no network access — pure local parsing.
 */
object V2RayConfigParser {

    fun parse(uri: String): String {
        val u = uri.trim()
        return when {
            u.startsWith("vmess://") -> vmessOutbound(u.removePrefix("vmess://"))
            u.startsWith("vless://") -> standardOutbound(u.removePrefix("vless://"), "vless")
            u.startsWith("trojan://") -> standardOutbound(u.removePrefix("trojan://"), "trojan")
            u.startsWith("ss://") -> shadowsocksOutbound(u.removePrefix("ss://"))
            u.startsWith("{") -> u
            else -> throw IllegalArgumentException("Unsupported URI scheme: ${u.take(16)}")
        }
    }

    // ---------------------------------------------------------------- vmess

    private fun vmessOutbound(b64: String): String {
        val o = JSONObject(decodeBase64(b64))
        val out = JSONObject().apply {
            put("tag", "proxy")
            put("protocol", "vmess")
            put(
                "settings",
                JSONObject().apply {
                    put(
                        "vnext",
                        JSONArray().put(
                            JSONObject().apply {
                                put("address", o.optString("add"))
                                put("port", optPort(o, "port", 443))
                                put(
                                    "users",
                                    JSONArray().put(
                                        JSONObject().apply {
                                            put("id", o.optString("id"))
                                            put("alterId", o.optInt("aid", 0))
                                            put("security", o.optString("scy", "auto"))
                                        }
                                    )
                                )
                            }
                        )
                    )
                }
            )
            put("streamSettings", streamSettings(
                network = o.optString("net", "tcp"),
                security = o.optString("tls", ""),
                host = o.optString("host", ""),
                path = o.optString("path", ""),
                sni = o.optString("sni", ""),
                headerType = o.optString("type", "")
            ))
            put("mux", JSONObject().apply { put("enabled", false) })
        }
        return wrapWithTunInbound(out)
    }

    // --------------------------------------------------------- vless/trojan

    private fun standardOutbound(raw: String, protocol: String): String {
        val withoutFrag = raw.substringBefore('#')
        val s = if (withoutFrag.contains("://")) withoutFrag else "$protocol://$withoutFrag"
        val uri = URI(s)

        val userInfo = uri.userInfo.orEmpty()
        val id = userInfo.substringBefore(':')
        val host = uri.host.orEmpty()
        val port = if (uri.port > 0) uri.port else defaultPort(protocol)
        val q = parseQuery(uri.rawQuery)

        val out = JSONObject().apply {
            put("tag", "proxy")
            put("protocol", protocol)
            put(
                "settings",
                JSONObject().apply {
                    put(
                        "vnext",
                        JSONArray().put(
                            JSONObject().apply {
                                put("address", host)
                                put("port", port)
                                put(
                                    "users",
                                    JSONArray().put(
                                        JSONObject().apply {
                                            put("id", id)
                                            put("encryption", q["encryption"] ?: "none")
                                            if (protocol == "trojan") put("security", "auto")
                                        }
                                    )
                                )
                            }
                        )
                    )
                }
            )
            put("streamSettings", streamSettings(
                network = q["type"] ?: "tcp",
                security = q["security"] ?: if (protocol == "trojan") "tls" else "none",
                host = q["host"] ?: "",
                path = q["path"] ?: "",
                sni = q["sni"] ?: q["peer"] ?: "",
                headerType = q["headerType"] ?: ""
            ))
            put("mux", JSONObject().apply { put("enabled", false) })
        }
        return wrapWithTunInbound(out)
    }

    // --------------------------------------------------------- shadowsocks

    private fun shadowsocksOutbound(raw: String): String {
        val noFrag = raw.substringBefore('#')
        // Two forms:  ss://base64(method:password)@host:port   and
        //             ss://base64(method:password@host:port)
        val at = noFrag.indexOf('@')
        val method: String
        val password: String
        val host: String
        val port: Int
        if (at >= 0) {
            val cred = decodeBase64(noFrag.substring(0, at))
            method = cred.substringBefore(':')
            password = cred.substringAfter(':', "")
            val hostPart = noFrag.substring(at + 1)
            host = hostPart.substringBefore(':')
            port = hostPart.substringAfter(':', "8388").substringBefore('?').toIntOrNull() ?: 8388
        } else {
            val decoded = decodeBase64(noFrag.substringBefore('?'))
            method = decoded.substringBefore(':')
            password = decoded.substringAfter(':', "").substringBefore('@')
            val hostPort = decoded.substringAfterLast('@', decoded)
            host = hostPort.substringBefore(':')
            port = hostPort.substringAfter(':', "8388").toIntOrNull() ?: 8388
        }

        val out = JSONObject().apply {
            put("tag", "proxy")
            put("protocol", "shadowsocks")
            put(
                "settings",
                JSONObject().apply {
                    put("servers", JSONArray().put(
                        JSONObject().apply {
                            put("address", host)
                            put("port", port)
                            put("method", method)
                            put("password", password)
                            put("uot", false)
                        }
                    ))
                }
            )
            put("streamSettings", JSONObject().apply { put("network", "tcp") })
            put("mux", JSONObject().apply { put("enabled", false) })
        }
        return wrapWithTunInbound(out)
    }

    // ------------------------------------------------------------ helpers

    private fun streamSettings(
        network: String,
        security: String,
        host: String,
        path: String,
        sni: String,
        headerType: String
    ): JSONObject = JSONObject().apply {
        put("network", network.ifBlank { "tcp" })
        put("security", security.ifBlank { "none" })
        if (sni.isNotBlank()) put("sni", sni)
        if (host.isNotBlank() || path.isNotBlank()) {
            when (network) {
                "ws" -> put(
                    "wsSettings",
                    JSONObject().apply {
                        if (path.isNotBlank()) put("path", path)
                        if (host.isNotBlank()) put("headers", JSONObject().apply { put("Host", host) })
                    }
                )
                "h2" -> put(
                    "httpSettings",
                    JSONObject().apply {
                        if (path.isNotBlank()) put("path", path)
                        if (host.isNotBlank()) put("host", JSONArray().put(host))
                    }
                )
                "tcp" -> if (headerType == "http") {
                    put(
                        "tcpSettings",
                        JSONObject().apply {
                            put(
                                "header",
                                JSONObject().apply {
                                    put("type", "http")
                                    put(
                                        "request",
                                        JSONObject().apply {
                                            put("path", JSONArray().put(path.ifBlank { "/" }))
                                            put(
                                                "headers",
                                                JSONObject().apply {
                                                    if (host.isNotBlank()) put("Host", JSONArray().put(host))
                                                }
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
        if (security == "tls" || security == "reality") {
            put(
                "tlsSettings",
                JSONObject().apply {
                    if (sni.isNotBlank()) put("serverName", sni)
                    if (host.isNotBlank()) put("serverName", if (sni.isBlank()) host else sni)
                }
            )
        }
    }

    /** Wrap a single outbound in a runnable config with a tun inbound + direct/block. */
    private fun wrapWithTunInbound(proxy: JSONObject): String {
        val root = JSONObject().apply {
            put("log", JSONObject().apply { put("loglevel", "warning") })
            put(
                "inbounds",
                JSONArray().put(
                    JSONObject().apply {
                        put("tag", "tun")
                        put("protocol", "tun")
                        put(
                            "settings",
                            JSONObject().apply {
                                put("name", "xray0")
                                put("MTU", 1500)
                                put("userLevel", 8)
                            }
                        )
                        put(
                            "sniffing",
                            JSONObject().apply {
                                put("enabled", true)
                                put("destOverride", JSONArray().put("http").put("tls").put("quic"))
                            }
                        )
                    }
                )
            )
            put(
                "outbounds",
                JSONArray().apply {
                    put(proxy)
                    put(
                        JSONObject().apply {
                            put("tag", "direct")
                            put("protocol", "freedom")
                            put("streamSettings", JSONObject().apply {
                                put("sockopt", JSONObject().apply { put("domainStrategy", "UseIP") })
                            })
                        }
                    )
                    put(
                        JSONObject().apply {
                            put("tag", "block")
                            put("protocol", "blackhole")
                            put("settings", JSONObject().apply {
                                put("response", JSONObject().apply { put("type", "http") })
                            })
                        }
                    )
                }
            )
        }
        return root.toString()
    }

    private fun parseQuery(raw: String?): Map<String, String> {
        if (raw.isNullOrBlank()) return emptyMap()
        return raw.split('&')
            .filter { it.isNotBlank() }
            .mapNotNull { kv ->
                val i = kv.indexOf('=')
                if (i <= 0) null
                else URLDecoder.decode(kv.substring(0, i), "UTF-8") to
                    URLDecoder.decode(kv.substring(i + 1), "UTF-8")
            }
            .toMap()
    }

    private fun optPort(o: JSONObject, key: String, fallback: Int): Int {
        val v = o.opt(key)
        return when (v) {
            is Number -> v.toInt()
            is String -> v.toIntOrNull() ?: fallback
            else -> fallback
        }
    }

    private fun defaultPort(protocol: String): Int = when (protocol) {
        "vless" -> 443
        "trojan" -> 443
        else -> 443
    }

    private fun decodeBase64(s: String): String {
        val normalized = s.trim().replace('-', '+').replace('_', '/')
        val pad = "=".repeat((4 - normalized.length % 4) % 4)
        return String(Base64.decode(normalized + pad, Base64.DEFAULT))
    }
}
