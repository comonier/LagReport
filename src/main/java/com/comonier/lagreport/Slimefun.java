package com.comonier.lagreport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;

public class Slimefun {
    private final LagReport plugin;

    public Slimefun(LagReport plugin) { this.plugin = plugin; }

    public void reportTimings() {
        List<String> results = new ArrayList<>();
        Logger logger = (Logger) LogManager.getRootLogger();

        Appender appender = new AbstractAppender("LagReportCapturer", null, null, false, Property.EMPTY_ARRAY) {
            @Override
            public void append(org.apache.logging.log4j.core.LogEvent event) {
                String msg = event.getMessage().getFormattedMessage();
                if (isProfilerLine(msg)) results.add(msg);
            }
        };

        appender.start();
        logger.addAppender(appender);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sf timings");

        new BukkitRunnable() {
            @Override
            public void run() {
                logger.removeAppender(appender);
                appender.stop();
                
                if (results.isEmpty()) return;

                StringBuilder sb = new StringBuilder("📊 **Slimefun Timings**\n```yaml\n");
                for (String line : results) {
                    // Limpeza radical: remove cores, caracteres de controle e espaços duplicados
                    String clean = line.replaceAll("\u001B\\[[;\\d]*m", "")
                                       .replaceAll("§[0-9a-fk-orx]", "")
                                       .trim();
                    
                    if (!clean.isEmpty() && !clean.contains("Please wait")) {
                        // Corta a linha se for absurdamente longa para evitar quebra de JSON
                        if (clean.length() > 150) clean = clean.substring(0, 147) + "...";
                        sb.append(clean).append("\n");
                    }
                }
                sb.append("```");

                // Proteção contra o limite de 2000 caracteres do Discord
                String finalMsg = sb.toString();
                if (finalMsg.length() > 1950) {
                    finalMsg = finalMsg.substring(0, 1900) + "\n... (truncated)```";
                }
                
                DiscordWebhook.enviar(plugin.getConfig().getString("slimefun-webhook-url"), finalMsg);
            }
        }.runTaskLater(plugin, 60L);
    }

    private boolean isProfilerLine(String l) {
        if (l == null) return false;
        return l.contains("Slimefun") || l.contains("Profiler") || l.contains("blocks") || 
               l.contains("chunks") || l.contains("plugins") || l.contains("ms)") || 
               l.contains("Average:") || l.contains("world (") || l.startsWith("  ");
    }
}
