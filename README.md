# LagReport 🛡️ [v2.1]

**LagReport** is a high-performance audit and active protection plugin for **Paper 1.21.1**. It provides transparent data comparing client-side load vs. actual hardware usage, while offering an automated emergency "Halt" system to prevent server crashes.

## 🚀 Features

**Audit Consumption Report:** Every 1 hour, the plugin performs a "Hardware Proof" analysis. It lists individual player impact (Chunks, Entities, Redstone) and compares the sum against the server's actual hardware status.<br>
**Emergency Mode (Anti-Lag):** If TPS drops to 15.0 or lower, the plugin triggers a global HALT, disabling:<br>
All entity spawning, explosions, and redstone clocks.<br>
Block physics (Sand, Gravel, Anvils) and Liquid flow (Water/Lava).<br>
**Leash-Aware AI Safety:** 🛡️ Unlike other anti-lag plugins, LagReport never interferes with leashed entities. This ensures full compatibility with plugins like LeashedMobsTeleport, preserving the "asleep" or "tethered" state of mobs during and after emergencies.<br>
**Automatic Recovery:** Systems are restored only when the TPS stabilizes at 19.5 or higher.<br>
**Emergency Simulation:** A built-in 60-second sequence to test the "Halt" mechanics and Discord alerts safely.<br>
**Discord Integration:** Detailed audit logs and @everyone emergency alerts via Webhook.<br>
**Multi-language Support:** Native support for English (EN) and Portuguese (PT).

## 🛠️ Commands and Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/lagreport reload` | Reloads config and language files. | `lagreport.admin` |
| `/lagreport test` | Manually triggers the Audit Report to Discord. | `lagreport.admin` |
| `/lagreport simulate` | Starts a 60s emergency simulation sequence. | `lagreport.admin` |

## ⚙️ Configuration

Edit `plugins/LagReport/config.yml` to set your preferences:

```yaml
# LagReport Config
webhook-url: "YOUR_WEBHOOK_URL_HERE"
# Choose your language: "en" or "pt"
language: "en"
```

## 📊 Audit Logic
- The plugin distinguishes between Individual Load (what players are rendering) and Hardware Proof (what the server is actually processing). This transparency helps identify if lag is caused by player clusters or external server factors.

Developed with ❤️ by Comonier
