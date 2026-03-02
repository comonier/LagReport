package com.comonier.lagreport;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Map;

public class AnalyzerTask extends BukkitRunnable {
    private final LagReport plugin;

    public AnalyzerTask(LagReport plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;

        int totalChunks = 0;
        int totalEntities = 0;
        for (World world : Bukkit.getWorlds()) {
            totalChunks = totalChunks + world.getLoadedChunks().length;
            totalEntities = totalEntities + world.getEntities().size();
        }

        Player topCulprit = null;
        double highestScore = -1.0;
        int topChunks = 0;
        int topEntities = 0;
        Map<String, Integer> topRedstone = null;

        for (Player p : Bukkit.getOnlinePlayers()) {
            int pChunks = p.getWorld().getLoadedChunks().length;
            int pEntities = p.getNearbyEntities(80, 80, 80).size();
            Map<String, Integer> pRedstone = RedstoneScanner.scan(p, 32);

            double currentScore = (pEntities * 2.0) + (pRedstone.size() * 10.0);

            if (currentScore >= highestScore) {
                highestScore = currentScore;
                topCulprit = p;
                topChunks = pChunks;
                topEntities = pEntities;
                topRedstone = pRedstone;
            }
        }

        if (topCulprit != null) {
            String msg = "\n" +
                "§8§m---------------------------------------\n" +
                "§6§lLagReport\n" +
                "§eLog de Consumo Geral:\n" +
                "§fJogadores Online: §a" + Bukkit.getOnlinePlayers().size() + "\n" +
                "§f- Chunks carregados: §7" + totalChunks + "\n" +
                "§f- Entidades carregadas: §7" + totalEntities + "\n" +
                "\n" +
                "§c§lJogador com maior consumo:\n" +
                "§fJogador: §b" + topCulprit.getName() + "\n" +
                "§f- Chunks carregados: §7" + topChunks + "\n" +
                "§f- Entidades carregadas: §7" + topEntities + "\n" +
                "§f- Redstone ativa: §7" + topRedstone.toString() + "\n" +
                "§8§m---------------------------------------";

            for (Player all : Bukkit.getOnlinePlayers()) {
                all.sendMessage(msg);
                all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
            }

            // Enviar para o Discord
            String webhookUrl = plugin.getConfig().getString("webhook-url");
            String discordMsg = msg.replace("§", "").replace("\n", "\\n");
            DiscordWebhook.enviar(webhookUrl, "**[RELATORIO HORARIO]**\\n" + discordMsg);
        }
    }
}
