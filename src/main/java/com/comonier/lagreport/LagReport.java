package com.comonier.lagreport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LagReport extends JavaPlugin implements TabCompleter {

    private FileConfiguration messages;

    @Override
    public void onEnable() {
        // Inicializa arquivos de configuração e idiomas
        saveDefaultConfig();
        saveResource("messages_pt.yml", false);
        saveResource("messages_en.yml", false);
        loadMessages();

        // Cancela tarefas órfãs de reloads anteriores
        try {
            Bukkit.getScheduler().cancelTasks(this);
            AntiLagController.modoEmergencia = false;
        } catch (Exception ignored) {}

        // Registra Comandos e TabCompleter
        PluginCommand cmd = this.getCommand("lagreport");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }

        // Registra Eventos de Anti-Lag
        Bukkit.getPluginManager().registerEvents(new LagEvents(), this);

        // Inicia as Tarefas (Reports a cada 1 hora | Monitoramento a cada 5 segundos)
        new AnalyzerTask(this).runTaskTimer(this, 1200L, 72000L);
        new AntiLagController(this).runTaskTimer(this, 100L, 100L);

        // LOG DINÂMICO: Puxa a versão direto do plugin.yml/pom.xml
        getLogger().info("LagReport v" + getDescription().getVersion() + " - Plugin Enabled (Language: " + getConfig().getString("language") + ")");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        AntiLagController.modoEmergencia = false;
        getLogger().info("LagReport - Plugin Disabled.");
    }

    // Carrega o arquivo de mensagem baseado na config.yml
    public void loadMessages() {
        String lang = getConfig().getString("language", "en");
        File langFile = new File(getDataFolder(), "messages_" + lang + ".yml");
        
        if (!langFile.exists()) {
            langFile = new File(getDataFolder(), "messages_en.yml");
        }
        
        messages = YamlConfiguration.loadConfiguration(langFile);
    }

    // Método central para pegar mensagens traduzidas com suporte a cores &
    public String getMsg(String path) {
        if (messages == null) return "§cMessages not loaded!";
        String msg = messages.getString(path);
        if (msg == null) return "§cMissing key: " + path;
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6§lLagReport §8- §fCommands:");
            sender.sendMessage("§e/lagreport reload §7- Reload settings and languages.");
            sender.sendMessage("§e/lagreport test §7- Trigger audit report to Discord.");
            sender.sendMessage("§e/lagreport simulate §7- Start a 60s emergency simulation.");
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("reload")) {
            if (sender.hasPermission("lagreport.admin")) {
                reloadConfig();
                loadMessages();
                sender.sendMessage("§a[LagReport] Config and Messages reloaded!");
                
                String url = getConfig().getString("webhook-url");
                double tps = Bukkit.getTPS()[0];
                DiscordWebhook.enviar(url, "🔄 **System Reload:** Plugin ready. Current TPS: " + String.format("%.2f", tps));
                return true;
            }
            sender.sendMessage("§cNo permission.");
            return true;
        }

        if (sub.equals("test")) {
            if (sender.hasPermission("lagreport.admin")) {
                sender.sendMessage("§e[LagReport] Generating manual audit report...");
                new AnalyzerTask(this).run();
                sender.sendMessage("§a[LagReport] Audit sent to Discord!");
                return true;
            }
            sender.sendMessage("§cNo permission.");
            return true;
        }

        if (sub.equals("simulate")) {
            if (sender.hasPermission("lagreport.admin")) {
                sender.sendMessage("§e[LagReport] Starting emergency simulation sequence...");
                new SimulationTask(this).runTaskTimer(this, 0L, 20L);
                return true;
            }
            sender.sendMessage("§cNo permission.");
            return true;
        }

        sender.sendMessage("§cUnknown command. Use /lagreport for help.");
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
            if ("simulate".startsWith(input)) list.add("simulate");
            return list;
        }
        return Collections.emptyList();
    }
}
