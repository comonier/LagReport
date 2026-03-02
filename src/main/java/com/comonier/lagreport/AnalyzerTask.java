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

        double[] tpsArray = Bukkit.getTPS();
        double tpsAtual = tpsArray[0];
        if (tpsAtual >= 20.0) tpsAtual = 20.0;
        String tpsFormatado = String.format("%.2f", tpsAtual);

        int totalChunks = 0;
        int totalEntities = 0;
        for (World world : Bukkit.getWorlds()) {
            totalChunks = totalChunks + world.getLoadedChunks().length;
            totalEntities = totalEntities + world.getEntities().size();
        }

        Player topCulprit = null;
        double highestScore = -1.0;
        int pChunksCount = 0;
        int pEntitiesCount = 0;
        Map<String, Integer> pRedstoneMap = null;

        for (Player p : Bukkit.getOnlinePlayers()) {
            int viewDist = p.getClientViewDistance();
            int chunksAtivos = (viewDist * 2 + 1) * (viewDist * 2 + 1); 
            int entitiesPerto = p.getNearbyEntities(80, 80, 80).size();
            Map<String, Integer> redstonePerto = RedstoneScanner.scan(p, 32);

            double score = (entitiesPerto * 2.0) + (redstonePerto.size() * 10.0);

            if (score >= highestScore) {
                highestScore = score;
                topCulprit = p;
                pChunksCount = chunksAtivos;
                pEntitiesCount = entitiesPerto;
                pRedstoneMap = redstonePerto;
            }
        }

        if (topCulprit != null) {
            String redstoneTxt = pRedstoneMap.isEmpty() ? "0" : pRedstoneMap.toString();

            String msgMine = "\n§8§l------\n§6§lTPS: §e" + tpsFormatado + "\n§8§l------\n§6§lLagReport\n" +
                "§eLog de Consumo Geral:\n§fOnline: §a" + Bukkit.getOnlinePlayers().size() + "\n" +
                "§f- Total Chunks: §7" + totalChunks + "\n§f- Total Entities: §7" + totalEntities + "\n\n" +
                "§c§lTop Consumer:\n§fPlayer: §b" + topCulprit.getName() + "\n" +
                "§f- Player Chunks: §7" + pChunksCount + "\n" +
                "§f- Nearby Entities: §7" + pEntitiesCount + "\n" +
                "§f- Active Redstone: §7" + redstoneTxt + "\n§8§m---------------------------------------";

            for (Player all : Bukkit.getOnlinePlayers()) {
                all.sendMessage(msgMine);
                all.playSound(all.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
            }

            String url = plugin.getConfig().getString("webhook-url");
            String msgDiscord = "-=-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
                "**LagReport**: **TPS**: `" + tpsFormatado + "`\n" +
                "-------------------------------\n" +
                "Online Players: `" + Bukkit.getOnlinePlayers().size() + "`\n" +
                "Total Server Chunks: `" + totalChunks + "`\n" +
                "Total Server Entities: `" + totalEntities + "`\n" +
                "-------------------------------\n" +
                "**Top Consumer**: `" + topCulprit.getName() + "`\n" +
                "Player Chunks: `" + pChunksCount + "`\n" +
                "Nearby Entities: `" + pEntitiesCount + "`\n" +
                "Active Redstone: `" + redstoneTxt + "`\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-";

            DiscordWebhook.enviar(url, msgDiscord);
        }
    }
}
