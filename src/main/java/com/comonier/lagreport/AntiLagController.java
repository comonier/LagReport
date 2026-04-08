package com.comonier.lagreport;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;

public class AntiLagController extends BukkitRunnable {
    private final LagReport plugin;
    public static boolean modoEmergencia = false;
    public static boolean emSimulacao = false;
    private boolean emContagem = false;

    public AntiLagController(LagReport plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        if (emSimulacao) return;

        double tpsAtual = Math.min(20.0, Bukkit.getTPS()[0]);
        String tpsF = String.format("%.2f", tpsAtual);
        
        // Gatilho 1: Intervenção em Chunks (Entity Overflow)
        double breakerTps = plugin.getConfig().getDouble("breaker-settings.activation-tps", 16.0);
        if (tpsAtual <= breakerTps && !modoEmergencia && !emContagem) {
            new ChunkBreakerTask(plugin).runTask(plugin);
        }

        // Gatilho 2: Modo de Emergência Global (Halt On)
        double minTps = plugin.getConfig().getDouble("settings.tps-min-threshold", 15.0);
        double maxTps = plugin.getConfig().getDouble("settings.tps-max-threshold", 19.5);

        if (minTps >= tpsAtual && !modoEmergencia && !emContagem) {
            iniciarContagemRegressiva(tpsF);
        }

        if (tpsAtual >= maxTps && modoEmergencia) {
            desativarHalt(tpsF);
        }
    }

    private void iniciarContagemRegressiva(String tps) {
        emContagem = true;
        new BukkitRunnable() {
            int timeLeft = 60;
            @Override
            public void run() {
                if (timeLeft == 60) {
                    Bukkit.broadcastMessage(String.format(plugin.getMsg("emergency.pre_alert_60s"), tps));
                    enviarInfoEventos();
                } else if (timeLeft == 30) {
                    Bukkit.broadcastMessage(plugin.getMsg("emergency.pre_alert_30s"));
                } else if (timeLeft <= 10 && timeLeft > 0) {
                    Bukkit.broadcastMessage(String.format(plugin.getMsg("emergency.pre_alert_10s"), timeLeft));
                    Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f));
                } else if (timeLeft == 0) {
                    ativarHalt(tps);
                    emContagem = false;
                    this.cancel();
                    return;
                }
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void enviarInfoEventos() {
        List<String> ativos = new ArrayList<>();
        org.bukkit.configuration.ConfigurationSection sec = plugin.getConfig().getConfigurationSection("halt-on-settings");
        if (sec != null) {
            for (String k : sec.getKeys(false)) {
                if (sec.getBoolean(k)) ativos.add(k.replace("disable-", "").replace("-", " "));
            }
        }
        Bukkit.broadcastMessage(String.format(plugin.getMsg("emergency.event_info"), String.join(", ", ativos)));
        Bukkit.broadcastMessage(plugin.getMsg("emergency.ticket_invite"));
        DiscordWebhook.enviar(plugin.getConfig().getString("webhook-url"), "⚠️ **CONTTAGEM HALT ON** | Sistemas: " + ativos);
    }

    private void ativarHalt(String tps) {
        modoEmergencia = true;
        manageAI(false);
        if (plugin.getConfig().getBoolean("settings.clear-drops-on-activation", false)) clearDrops();
        broadcastAndDiscord(String.format(plugin.getMsg("emergency.activated_game"), tps), 
                           String.format(plugin.getMsg("emergency.activated_discord"), tps));
    }

    private void desativarHalt(String tps) {
        modoEmergencia = false;
        manageAI(true);
        broadcastAndDiscord(String.format(plugin.getMsg("emergency.stabilized_game"), tps), 
                           String.format(plugin.getMsg("emergency.stabilized_discord"), tps));
    }

    private void manageAI(boolean state) {
        for (World w : Bukkit.getWorlds()) {
            for (Entity en : w.getEntities()) {
                if (en instanceof LivingEntity mob && !(en instanceof Player) && !mob.isLeashed()) mob.setAI(state);
            }
        }
    }

    private void clearDrops() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity en : w.getEntities()) { if (en instanceof Item) en.remove(); }
        }
    }

    private void broadcastAndDiscord(String g, String d) {
        Bukkit.broadcastMessage(g);
        String url = plugin.getConfig().getString("webhook-url");
        if (url != null && !url.contains("URL_")) DiscordWebhook.enviar(url, d);
    }
}
