# LagReport 🛡️ [v2.3]

**LagReport** is a high-performance audit and active protection plugin for **Paper 1.21.1**. It provides transparent data comparing client-side load vs. actual hardware usage, while offering an automated emergency "Halt" system to prevent server crashes.

## 🚀 Features

**Audit Consumption Report:** Every 1 hour, the plugin performs a "Hardware Proof" analysis (Chunks, Entities, Redstone) comparing player impact against the server's real hardware status.<br>
**Slimefun Performance Audit:** 🧩 Automatic hourly report of **Slimefun 4** timings sent to a dedicated Discord channel. Identifies laggy machines, chunks, and sub-plugins without needing the Slimefun API.<br>
**Emergency Mode (Anti-Lag):** If TPS drops to **15.0** or lower, the plugin triggers a global **HALT**, disabling:<br>
- All entity spawning, explosions, and redstone clocks.<br>
- Block physics (Sand, Gravel, Anvils) and Liquid flow (Water/Lava).<br>
**Leash-Aware AI Safety:** 🛡️ Never interferes with leashed entities. Preserves "asleep" or "tethered" states, ensuring compatibility with plugins like LeashedMobsTeleport.<br>
**Automatic Recovery:** Systems are restored only when the TPS stabilizes at **19.5** or higher.<br>
**Emergency Simulation:** A built-in 60-second sequence to test "Halt" mechanics and Discord alerts safely.<br>
**Dual Discord Integration:** Separate Webhooks for Hardware Audits/Emergencies and Slimefun Timings.<br>
**Multi-language Support:** Native support for English (EN) and Portuguese (PT).

## 🛠️ Commands and Permissions


| Command | Description | Permission |
| :--- | :--- | :--- |
| `/lagreport reload` | Reloads config and language files. | `lagreport.admin` |
| `/lagreport test` | Manually triggers the Hardware Audit to Discord. | `lagreport.admin` |
| `/lagreport simulate` | Starts a 60s emergency simulation sequence. | `lagreport.admin` |
| `/sftimings` | Manually triggers the Slimefun Report to Discord. | `lagreport.admin` |
| `/sftimings test` | Sends a connectivity test to the Slimefun Webhook. | `lagreport.admin` |

## ⚙️ Configuration

Edit `plugins/LagReport/config.yml` to set your preferences:

```yaml
# LagReport Config
webhook-url: "YOUR_HARDWARE_WEBHOOK_HERE"
slimefun-webhook-url: "YOUR_SLIMEFUN_WEBHOOK_HERE"
# Choose your language: "en" or "pt"
language: "pt"
```
⚠️ Important: Update Note
When updating from v2.2 to v2.3, you MUST delete the plugins/LagReport/ folder (or at least the config.yml and language files) to allow the plugin to generate the new configuration keys and translation strings required for the Slimefun module.

📊 Audit Logic
The plugin distinguishes between Individual Load (what players are rendering) and Hardware Proof (what the server is actually processing). This transparency helps identify if lag is caused by player clusters, redstone machines, or Slimefun tickers.

Developed with ❤️ by Comonier
