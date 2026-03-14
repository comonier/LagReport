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

        if (seconds == 60) AntiLagController.emSimulacao = true;

        if (seconds == 60 || seconds == 30 || seconds == 20 || seconds == 10) {
            String gameMsg = String.format(plugin.getMsg("simulation.warning"), seconds);
            String discMsg = String.format(plugin.getMsg("simulation.alert_discord"), seconds);
            broadcastAndDiscord(url, gameMsg, discMsg);
        }

        if (seconds <= 5 && seconds > 0) {
            String countMsg = String.format(plugin.getMsg("simulation.final_countdown"), seconds);
            Bukkit.broadcastMessage(countMsg);
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f));
        }

        if (seconds == 0) {
            AntiLagController.modoEmergencia = true;
            manageAI(false);
            broadcastAndDiscord(url, plugin.getMsg("simulation.activated_game"), plugin.getMsg("simulation.activated_discord"));
        }

        if (seconds < 0 && seconds > -15) {
            Bukkit.broadcastMessage(String.format(plugin.getMsg("simulation.tick_countdown"), (15 + seconds)));
        }

        if (seconds == -15) {
            AntiLagController.modoEmergencia = false;
            AntiLagController.emSimulacao = false; 
            manageAI(true);
            broadcastAndDiscord(url, plugin.getMsg("simulation.finished_game"), plugin.getMsg("simulation.finished_discord"));
            this.cancel();
        }
        seconds--;
    }

    private void manageAI(boolean state) {
        for (World w : Bukkit.getWorlds()) {
            for (Entity en : w.getEntities()) {
                if (en instanceof LivingEntity mob && !mob.isLeashed() && !(en instanceof Player)) {
                    mob.setAI(state);
                }
            }
        }
    }

    private void broadcastAndDiscord(String url, String game, String discord) {
        if (game != null) Bukkit.broadcastMessage(game);
        if (url != null && discord != null && !url.contains("SUA_URL")) {
            DiscordWebhook.enviar(url, discord);
        }
    }
}
