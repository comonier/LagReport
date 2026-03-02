package com.comonier.lagreport;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AntiLagController extends BukkitRunnable {
    private final LagReport plugin;
    public static boolean modoEmergencia = false;

    public AntiLagController(LagReport plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        // Pega o TPS do ultimo 1 minuto
        double tpsAtual = Bukkit.getTPS()[0];

        // Se 15.0 for MAIOR ou IGUAL ao TPS, ativa
        if (15.0 >= tpsAtual) {
            if (modoEmergencia == false) {
                modoEmergencia = true;
                String msg = "§c§l[!] MODO DE EMERGENCIA ATIVADO: TPS BAIXO (" + String.format("%.1f", tpsAtual) + ")";
                Bukkit.broadcastMessage(msg);
                
                String webhookUrl = plugin.getConfig().getString("webhook-url");
                DiscordWebhook.enviar(webhookUrl, "⚠️ **EMERGENCIA ATIVADA:** TPS caiu para " + String.format("%.1f", tpsAtual) + " @everyone");
            }
        }

        // Se o TPS for MAIOR ou IGUAL a 19.5, desativa
        if (tpsAtual >= 19.5) {
            if (modoEmergencia == true) {
                modoEmergencia = false;
                String msg = "§a§l[!] MODO DE EMERGENCIA DESATIVADO: TPS ESTABILIZADO.";
                Bukkit.broadcastMessage(msg);
                
                String webhookUrl = plugin.getConfig().getString("webhook-url");
                DiscordWebhook.enviar(webhookUrl, "✅ **ESTABILIZADO:** O servidor voltou para " + String.format("%.1f", tpsAtual));
            }
        }
    }
}
