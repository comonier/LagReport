package com.comonier.lagreport;

import org.bukkit.Bukkit;
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

        double tps = Math.min(20.0, Bukkit.getTPS()[0]);
        
        int serverTotalChunks = 0;
        int serverTotalEntities = 0;
        for (World world : Bukkit.getWorlds()) {
            serverTotalChunks += world.getLoadedChunks().length;
            serverTotalEntities += world.getEntities().size();
        }

        StringBuilder playerTable = new StringBuilder();
        playerTable.append(plugin.getMsg("report.header")).append("\n");

        int sumPlayersChunks = 0;
        int sumPlayersEntities = 0;
        int sumPlayersRedstone = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {
            int pChunks = (p.getViewDistance() * 2 + 1) * (p.getViewDistance() * 2 + 1);
            int pEntities = p.getNearbyEntities(80, 80, 80).size();
            Map<String, Integer> pRedstoneMap = RedstoneScanner.scan(p, 32);
            int pRedstone = pRedstoneMap.values().stream().mapToInt(Integer::intValue).sum();

            sumPlayersChunks += pChunks;
            sumPlayersEntities += pEntities; 
            sumPlayersRedstone += pRedstone;

            playerTable.append(String.format("%s | %d | %d | %d\n", 
                p.getName(), pChunks, pEntities, pRedstone));
        }

        String report = "```\n" +
            plugin.getMsg("report.title") + "\n" +
            "-------------------------------------------\n" +
            playerTable.toString() +
            "-------------------------------------------\n" +
            plugin.getMsg("report.sum_players") + "\n" +
            String.format("CHUNKS: %d | ENTITIES: %d | REDSTONE: %d\n", 
                sumPlayersChunks, sumPlayersEntities, sumPlayersRedstone) +
            "-------------------------------------------\n" +
            plugin.getMsg("report.server_status") + "\n" +
            String.format(plugin.getMsg("report.total_chunks"), serverTotalChunks) + " | " +
            String.format(plugin.getMsg("report.total_entities"), serverTotalEntities) + "\n" +
            "-------------------------------------------\n" +
            String.format(plugin.getMsg("report.tps_label"), tps) + "\n" +
            "```\n" +
            plugin.getMsg("report.footer_note");

        String url = plugin.getConfig().getString("webhook-url");
        DiscordWebhook.enviar(url, report);
    }
}
