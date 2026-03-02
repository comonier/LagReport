package com.comonier.lagreport;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class LagReport extends JavaPlugin implements TabCompleter {

    @Override
    public void onEnable() {
        try {
            Bukkit.getScheduler().cancelTasks(this);
            AntiLagController.modoEmergencia = false;
        } catch (Exception e) {}

        saveDefaultConfig();
        
        PluginCommand cmd = this.getCommand("lagreport");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }

        Bukkit.getPluginManager().registerEvents(new LagEvents(), this);

        new AnalyzerTask(this).runTaskTimer(this, 1200L, 72000L);
        new AntiLagController(this).runTaskTimer(this, 100L, 100L);

        getLogger().info("LagReport v1.0 - Enabled.");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        AntiLagController.modoEmergencia = false;
        getLogger().info("LagReport v1.0 - Disabled.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!this.isEnabled()) return false;

        if (args.length == 0) {
            sender.sendMessage("§6§lLagReport §8- §fCommands:");
            sender.sendMessage("§e/lagreport reload §7- Reload settings.");
            sender.sendMessage("§e/lagreport test §7- Trigger report now.");
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage("§cUnknown command. Type \"/lagreport\" for help.");
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("reload")) {
            if (sender.hasPermission("lagreport.admin")) {
                reloadConfig();
                sender.sendMessage("§a[LagReport] Config reloaded!");
                
                String url = getConfig().getString("webhook-url");
                double[] tpsArr = Bukkit.getTPS();
                DiscordWebhook.enviar(url, "🔄 **System Reload:** Webhook active. TPS: " + String.format("%.2f", tpsArr[0]));
                return true;
            }
            sender.sendMessage("§cNo permission.");
            return true;
        }

        if (sub.equals("test")) {
            if (sender.hasPermission("lagreport.admin")) {
                sender.sendMessage("§e[LagReport] Running manual analysis...");
                new AnalyzerTask(this).run();
                sender.sendMessage("§a[LagReport] Report sent to Discord!");
                return true;
            }
            sender.sendMessage("§cNo permission.");
            return true;
        }

        sender.sendMessage("§cUnknown command. Type \"/lagreport\" for help.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("lagreport.admin")) return Collections.emptyList();

        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            String input = args[0].toLowerCase();
            if ("reload".startsWith(input)) list.add("reload");
            if ("test".startsWith(input)) list.add("test");
            return list;
        }
        return Collections.emptyList();
    }
}
