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
    // Nova flag para impedir que o controlador automático interfira no modo simulação
    public static boolean emSimulacao = false; 

    public AntiLagController(LagReport plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        // Se uma simulação manual estiver rodando, o controlador automático fica em standby
        if (emSimulacao) return;

        double[] tpsArray = Bukkit.getTPS();
        double tpsAtual = Math.min(20.0, tpsArray[0]);
        String tpsFormatado = String.format("%.2f", tpsAtual);

        // ATIVAÇÃO REAL (Gatilho por TPS baixo)
        if (15.0 >= tpsAtual && !modoEmergencia) {
            modoEmergencia = true;
            freezeAllMobs(); 
            
            String gameMsg = String.format(plugin.getMsg("emergency.activated_game"), tpsFormatado);
            String discMsg = String.format(plugin.getMsg("emergency.activated_discord"), tpsFormatado);
            broadcastAndDiscord(gameMsg, discMsg);
        }

        // DESATIVAÇÃO REAL (Gatilho por normalização do TPS)
        if (tpsAtual >= 19.5 && modoEmergencia) {
            modoEmergencia = false;
            restoreAllMobs(); 
            
            // CORREÇÃO: Agora usa String.format para processar o (%s) do TPS
            String gameMsg = String.format(plugin.getMsg("emergency.stabilized_game"), tpsFormatado);
            String discMsg = String.format(plugin.getMsg("emergency.stabilized_discord"), tpsFormatado);
            broadcastAndDiscord(gameMsg, discMsg);
        }
    }

    private void freezeAllMobs() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity en : w.getEntities()) {
                if (en instanceof LivingEntity mob && !(en instanceof Player) && !mob.isLeashed()) {
                    mob.setAI(false);
                }
            }
        }
    }

    private void restoreAllMobs() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity en : w.getEntities()) {
                if (en instanceof LivingEntity mob && !(en instanceof Player) && !mob.isLeashed()) {
                    mob.setAI(true);
                }
            }
        }
    }

    private void broadcastAndDiscord(String gameMsg, String discordMsg) {
        Bukkit.broadcastMessage(gameMsg);
        String url = plugin.getConfig().getString("webhook-url");
        if (url != null && !url.contains("SUA_URL_AQUI")) {
            DiscordWebhook.enviar(url, discordMsg);
        }
    }
}
