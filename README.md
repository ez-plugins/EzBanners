# EzBanners

EzBanners collects live server data and securely syncs it to your banner API for dynamic image generation.

## Features
- Compatible with Minecraft 1.7 – 1.21+ (Bukkit/Spigot/Paper)
- Async HTTP sync with retry/backoff
- Optional PlaceholderAPI support
- Secure token + HMAC signatures

## Setup
1. Drop `EzBanners.jar` into your server `plugins/` folder.
2. Start the server to generate `plugins/EzBanners/config.yml`.
3. Edit `config.yml` and set:
   - `api.endpoint`
   - `api.token` (or use `/ezbanners link <token>`)
4. (Optional) Configure `placeholderapi.mappings` if you use PlaceholderAPI.
5. Restart or reload the plugin.

## Command
- `/ezbanners link <token>` — links the server to your API token.

## Permissions
- `ezbanners.link` — allows the link command (default: op).

## Payload
Each sync sends:
- Token
- Server UUID
- Timestamp
- HMAC signature
- Data fields (controlled by `enabled.data.fields`)

## Notes
- TPS and PlaceholderAPI values are only sent when available.
- HTTP requests are async and will never block the main thread.
