# Privacy Policy — Ai V2Ray Client

**Last updated:** 2026-10-12

Ai V2Ray Client ("the app") is a network utility. This policy explains what
information the app handles.

## Summary

The app does not collect, store, transmit, or sell any personal data of any
kind. It does not contain analytics, advertising, or crash-reporting SDKs.
The app is fully offline-capable except for the network traffic the user
chooses to route through the tunnel.

## Data the app DOES handle (on-device only)

| Data | Purpose | Stored where |
|---|---|---|
| VPN tunnel packets | Routing user-selected traffic through Xray core | In-memory only |
| Server URIs (vmess://, vless://, trojan://, ss://) the user pastes or imports | Building the user's local routing config | Local DataStore (per-app sandbox) |
| Tunnel up/down state (last connected timestamp, current server) | Convenience for user | Local DataStore |
| Coarse connectivity check (network state permission) | Determine whether a tunnel endpoint is reachable | Not stored |

All of the above remains on the user's device. The app does not transmit any
of this data off-device.

## Data the app does NOT collect

- Analytics, telemetry, usage events
- Crash reports sent off-device
- Advertising identifiers (GAID, OAID, IDFA equivalents)
- Browsing history
- Installed-app inventory
- Contacts, calendar, location, microphone, camera
- Device ID, serial number, advertising ID
- Phone or account identifiers

## Network traffic

All tunnel traffic is encrypted between the user's device and the server the
user has configured. The app developer has no visibility into and does not
log any of this traffic.

## Permissions used

- `INTERNET` — for the VPN tunnel
- `ACCESS_NETWORK_STATE` — to check reachability
- `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_SPECIAL_USE` — typed FGS, VPN subtype
- `POST_NOTIFICATIONS` — persistent VPN notification (Android 13+)
- `BIND_VPN_SERVICE` — required runtime permission for tun-based VPN

No permissions are requested for purposes the user cannot see. No `READ_LOGS`,
no `QUERRY_ALL_PACKAGES`, no `PACKAGE_USAGE_STATS`, no ad-related permissions.

## Children

The app is not directed at children under the age of digital consent in their
jurisdiction.

## Changes to this policy

Material changes will be reflected in an updated "Last updated" date and a
new release in the same channel.

## Contact

support@example.invalid (placeholder — replace with your real support address)
