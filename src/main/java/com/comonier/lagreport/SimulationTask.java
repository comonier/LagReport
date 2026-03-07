package com.comonier.lagreport;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SimulationTask extends BukkitRunnable {
    private final LagReport plugin;
    private int seconds = 60;

    public SimulationTask(LagReport plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        String url = plugin.getConfig().getString("webhook-url");

        // Alertas Periódicos (60, 30, 20, 10s)
        if (seconds == 60 || seconds == 30 || seconds == 20 || seconds == 10) {
            String gameMsg = String.format(plugin.getMsg("simulation.warning"), seconds);
            String discMsg = String.format(plugin.getMsg("simulation.alert_discord"), seconds);
            broadcastAndDiscord(url, gameMsg, discMsg);
        }

        // Contagem Regressiva Final (5s a 1s)
        if (seconds <= 5 && seconds > 0) {
            String countMsg = String.format(plugin.getMsg("simulation.final_countdown"), seconds);
            broadcastAndDiscord(null, countMsg, null);
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f));
        }

        // ATIVAÇÃO (0s)
        if (seconds == 0) {
            AntiLagController.modoEmergencia = true;
            freezeAllmobs(); // Segurança LMT: Ignora quem está no laço
            broadcastAndDiscord(url, plugin.getMsg("simulation.activated_game"), plugin.getMsg("simulation.activated_discord"));
        }

        // FEEDBACK DURANTE O HALT (15s de teste)
        if (seconds < 0 && seconds > -15) {
            int timeLeft = 15 + seconds;
            Bukkit.broadcastMessage(String.format(plugin.getMsg("simulation.tick_countdown"), timeLeft));
        }

        // DESATIVAÇÃO E NORMALIZAÇÃO (-15s)
        if (seconds == -15) {
            AntiLagController.modoEmergencia = false;
            restoreAllmobs(); // Segurança LMT: Só devolve IA se NÃO estiver no laço
            broadcastAndDiscord(url, plugin.getMsg("simulation.finished_game"), plugin.getMsg("simulation.finished_discord"));
            this.cancel();
        }

        seconds--;
    }

    private void freezeAllmobs() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity en : w.getEntities()) {
                if (en instanceof LivingEntity mob && !mob.isLeashed() && !(en instanceof Player)) {
                    mob.setAI(false);
                }
            }
        }
    }

    private void restoreAllmobs() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity en : w.getEntities()) {
                if (en instanceof LivingEntity mob && !mob.isLeashed() && !(en instanceof Player)) {
                    mob.setAI(true);
                }
            }
        }
    }

    private void broadcastAndDiscord(String url, String gameMsg, String discordMsg) {
        if (gameMsg != null) Bukkit.broadcastMessage(gameMsg);
        if (url != null && discordMsg != null && !url.contains("SUA_URL_AQUI")) {
            DiscordWebhook.enviar(url, discordMsg);
        }
    }
}
