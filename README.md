# LagReport 🛡️ [v2.4]

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

## ⚙️ Granular Event Control (`halt-on-settings`)

You can toggle each of these systems independently in the `config.yml` during **HALT ON**:<br><br>
- **disable-redstone:** Stops all clocks and wire updates.<br>
- **disable-mob-spawn:** Prevents all new entities from spawning.<br>
- **disable-explosions:** Cancels TNT, Creepers, and Crystal explosions.<br>
- **disable-mob-ai:** Freezes pathfinding and targeting (ignoring leashed mobs).<br>
- **disable-gravity-blocks:** Stops Sand, Gravel, and Anvils from falling.<br>
- **disable-liquid-flow:** Freezes Water and Lava spreading.<br>
- **disable-pistons:** Prevents piston extension and retraction.<br>
- **disable-hoppers:** Stops item transfer and hopper checks.<br>
- **disable-crop-growth:** Halts growth of plants, saplings, and cacti.<br>
- **disable-portals:** Disables travel through Nether and End portals.<br>
- **disable-leaf-decay:** Prevents leaves from disappearing naturally.<br>
- **disable-fire-spread:** Stops fire from burning blocks or spreading.<br>
- **disable-ice-melt:** Stops ice and snow from melting.<br>
- **disable-ice-form:** Prevents new ice or snow layers from forming.<br>
- **disable-grass-spread:** Stops Grass and Mycelium from spreading.<br>
- **disable-enderman-teleport:** Blocks Endermen from teleporting.<br>
- **disable-villager-trading:** Closes interactions with Villager NPCs.<br>
- **disable-armor-stand-interact:** Prevents manipulation of Armor Stands.

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
webhook-url: "URL_HARDWARE"
slimefun-webhook-url: "URL_SLIMEFUN"
language: "pt"
settings:
  tps-min-threshold: 15.0
  tps-max-threshold: 19.5
  clear-drops-on-activation: false
halt-on-settings:
  disable-redstone: true
  disable-mob-spawn: true
  disable-explosions: true
  disable-mob-ai: true
  disable-gravity-blocks: true
  disable-liquid-flow: true
  disable-pistons: true
  disable-hoppers: true
  disable-crop-growth: true
  disable-portals: true
  disable-enderman-teleport: true
  disable-villager-trading: true
  disable-armor-stand-interact: true
  disable-leaf-decay: true
  disable-fire-spread: true
  disable-ice-melt: true
  disable-ice-form: true
  disable-grass-spread: true
```
## ⚠️ Important: Update Note

When updating from v2.2 to v2.3, you MUST delete the plugins/LagReport/ folder (or at least the config.yml and language files) to allow the plugin to generate the new configuration keys and translation strings required for the Slimefun module.

## 📊 Audit Logic
The plugin distinguishes between Individual Load (what players are rendering) and Hardware Proof (what the server is actually processing). This transparency helps identify if lag is caused by player clusters, redstone machines, or Slimefun tickers.

Developed with ❤️ by Comonier
