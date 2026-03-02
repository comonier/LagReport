# LagReport 🛡️

**LagReport** is a performance monitoring and active protection plugin designed for **Paper 1.21.1** servers. It identifies players causing high TPS (Ticks Per Second) impact and automatically triggers an emergency mode to prevent server crashes.

## 🚀 Features

- **Hourly Systematic Report:** Every 1 hour, the plugin analyzes the resource consumption of all players (Chunks, Entities, and Active Redstone) and globally announces who is generating the most load.
- **Emergency Mode (Anti-Lag):** If the TPS drops to **15.0** or lower, the plugin automatically disables:
  - All entity spawning.
  - Block physics (Sand, Gravel, Anvils).
  - Liquid flow (Water and Lava).
  - Explosions (TNT and Creepers).
  - Redstone systems (Clocks and Farms).
- **Automatic Recovery:** Server functions return to normal only when the TPS stabilizes at **19.5** or higher.
- **Discord Integration:** Real-time notifications via Webhook for reports and emergency alerts (including @everyone mentions during crises).
- **Test Command:** Instantly validate your configuration and connection.

## 🛠️ Commands and Permissions


| Command | Description | Permission |
| :--- | :--- | :--- |
| `/lagreport reload` | Reloads the config and sends a test message to Discord. | `lagreport.admin` |

## ⚙️ Configuration

After the first run, edit the `plugins/LagReport/config.yml` file:

```yaml
# LagReport Config
# Insert your Discord Channel Webhook URL below:
webhook-url: "YOUR_URL_HERE_INSIDE_QUOTES"
