package com.comonier.lagreport;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AntiLagController extends BukkitRunnable {
    private final LagReport plugin;
    public static boolean modoEmergencia = false;

    public AntiLagController(LagReport plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        double[] tpsArray = Bukkit.getTPS();
        double tpsAtual = tpsArray[0];
        
        if (tpsAtual >= 20.0) tpsAtual = 20.0;
        String tpsFormatado = String.format("%.2f", tpsAtual);

        if (15.0 >= tpsAtual) {
            if (modoEmergencia == false) {
                modoEmergencia = true;
                
                Bukkit.broadcastMessage("§c§l[!] EMERGENCY MODE ACTIVATED: LOW TPS (" + tpsFormatado + ")");
                
                String url = plugin.getConfig().getString("webhook-url");
                String msg = "-=-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
                    "⚠️ **EMERGENCY ACTIVATED**\n" +
                    "**TPS**: `" + tpsFormatado + "`\n" +
                    "-------------------------------\n" +
                    "Status: `PROTECTION ENABLED`\n" +
                    "Details: `Redstone, Spawns and Physics frozen.`\n" +
                    "-=-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
                    "@everyone";
                
                DiscordWebhook.enviar(url, msg);
            }
        }

        if (tpsAtual >= 19.5) {
            if (modoEmergencia == true) {
                modoEmergencia = false;
                
                Bukkit.broadcastMessage("§a§l[!] EMERGENCY MODE DEACTIVATED: SERVER STABILIZED.");
                
                String url = plugin.getConfig().getString("webhook-url");
                String msg = "-=-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
                    "✅ **SERVER STABILIZED**\n" +
                    "**TPS**: `" + tpsFormatado + "`\n" +
                    "-------------------------------\n" +
                    "Status: `NORMAL OPERATION`\n" +
                    "Details: `All systems restored.`\n" +
                    "-=-=-=-=-=-=-=-=-=-=-=-=-=-";
                
                DiscordWebhook.enviar(url, msg);
            }
        }
    }
}
