package com.comonier.lagreport;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AntiLagController extends BukkitRunnable {
    private final LagReport plugin;
    public static boolean modoEmergencia = false;
    public static boolean emSimulacao = false; 

    public AntiLagController(LagReport plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        if (emSimulacao) return;

        double tpsAtual = Math.min(20.0, Bukkit.getTPS()[0]);
        String tpsFormatado = String.format("%.2f", tpsAtual);

        if (15.0 >= tpsAtual && !modoEmergencia) {
            modoEmergencia = true;
            manageAI(false);
            
            String gameMsg = String.format(plugin.getMsg("emergency.activated_game"), tpsFormatado);
            String discMsg = String.format(plugin.getMsg("emergency.activated_discord"), tpsFormatado);
            broadcastAndDiscord(gameMsg, discMsg);
        }

        if (tpsAtual >= 19.5 && modoEmergencia) {
            modoEmergencia = false;
            manageAI(true);
            
            String gameMsg = String.format(plugin.getMsg("emergency.stabilized_game"), tpsFormatado);
            String discMsg = String.format(plugin.getMsg("emergency.stabilized_discord"), tpsFormatado);
            broadcastAndDiscord(gameMsg, discMsg);
        }
    }

    private void manageAI(boolean state) {
        for (World w : Bukkit.getWorlds()) {
            for (Entity en : w.getEntities()) {
                if (en instanceof LivingEntity mob && !(en instanceof Player) && !mob.isLeashed()) {
                    mob.setAI(state);
                }
            }
        }
    }

    private void broadcastAndDiscord(String gameMsg, String discordMsg) {
        Bukkit.broadcastMessage(gameMsg);
        String url = plugin.getConfig().getString("webhook-url");
        if (url != null && !url.contains("SUA_URL")) {
            DiscordWebhook.enviar(url, discordMsg);
        }
    }
}
