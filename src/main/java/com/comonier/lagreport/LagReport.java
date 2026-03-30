package com.comonier.lagreport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;

public class LagReport extends JavaPlugin {
    private FileConfiguration messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages_pt.yml", false);
        saveResource("messages_en.yml", false);
        loadMessages();

        try {
            Bukkit.getScheduler().cancelTasks(this);
            AntiLagController.modoEmergencia = false;
        } catch (Exception ignored) {}

        PluginCommand mainCmd = this.getCommand("lagreport");
        if (mainCmd != null) {
            LagReportCommand executor = new LagReportCommand(this);
            mainCmd.setExecutor(executor);
            mainCmd.setTabCompleter(executor);
        }

        PluginCommand sfCmd = this.getCommand("sftimings");
        if (sfCmd != null) sfCmd.setExecutor(new SlimefunCommand(this));

        // ATUALIZADO: Passando 'this' para o LagEvents
        Bukkit.getPluginManager().registerEvents(new LagEvents(this), this);
        
        new AnalyzerTask(this).runTaskTimer(this, 1200L, 72000L);
        
        new BukkitRunnable() {
            @Override
            public void run() { new Slimefun(LagReport.this).reportTimings(); }
        }.runTaskTimer(this, 6000L, 72000L);

        new AntiLagController(this).runTaskTimer(this, 100L, 100L);

        getLogger().info("LagReport v" + getDescription().getVersion() + " - Ready.");
    }

    public void loadMessages() {
        String lang = getConfig().getString("language", "en");
        File langFile = new File(getDataFolder(), "messages_" + lang + ".yml");
        if (!langFile.exists()) langFile = new File(getDataFolder(), "messages_en.yml");
        messages = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMsg(String path) {
        if (messages == null) return "§cMessages not loaded!";
        String msg = messages.getString(path);
        return msg != null ? ChatColor.translateAlternateColorCodes('&', msg) : "§cKey: " + path;
    }
}
