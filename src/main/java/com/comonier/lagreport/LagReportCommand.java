package com.comonier.lagreport;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LagReportCommand implements CommandExecutor, TabCompleter {
    private final LagReport plugin;

    public LagReportCommand(LagReport plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                             @NotNull String label, @NotNull String[] args) {
        String prefix = plugin.getMsg("prefix");

        if (args.length == 0) {
            sender.sendMessage("§6§lLagReport §8- §fCommands:");
            sender.sendMessage("§e/lagreport reload §7- Recarrega configs.");
            sender.sendMessage("§e/lagreport test §7- Auditoria Hardware.");
            sender.sendMessage("§e/lagreport simulate §7- Simulação 60s.");
            return true;
        }

        if (!sender.hasPermission("lagreport.admin")) {
            sender.sendMessage(prefix + plugin.getMsg("commands.no_permission"));
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("reload")) {
            plugin.reloadConfig();
            plugin.loadMessages();
            sender.sendMessage(prefix + plugin.getMsg("commands.reload_success"));
            
            String url = plugin.getConfig().getString("webhook-url");
            double tps = Bukkit.getTPS()[0];
            DiscordWebhook.enviar(url, "🔄 **System Reload:** Plugin ready. Current TPS: " + String.format("%.2f", tps));
            return true;
        }

        if (sub.equals("test")) {
            sender.sendMessage(prefix + plugin.getMsg("commands.audit_generating"));
            new AnalyzerTask(plugin).run();
            sender.sendMessage(prefix + plugin.getMsg("commands.audit_sent"));
            return true;
        }

        if (sub.equals("simulate")) {
            sender.sendMessage(prefix + plugin.getMsg("commands.simulation_start"));
            new SimulationTask(plugin).runTaskTimer(plugin, 0L, 20L);
            return true;
        }

        sender.sendMessage(prefix + plugin.getMsg("commands.unknown"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                     @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("lagreport.admin")) return Collections.emptyList();
        
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            String input = args[0].toLowerCase();
            if ("reload".startsWith(input)) list.add("reload");
            if ("test".startsWith(input)) list.add("test");
            if ("simulate".startsWith(input)) list.add("simulate");
            return list;
        }
        return Collections.emptyList();
    }
}
