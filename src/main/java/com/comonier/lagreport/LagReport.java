package com.comonier.lagreport;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class LagReport extends JavaPlugin implements TabCompleter {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        // REGISTRO DO EXECUTOR E DO TAB COMPLETER
        if (this.getCommand("lagreport") != null) {
            this.getCommand("lagreport").setExecutor(this);
            this.getCommand("lagreport").setTabCompleter(this);
        }

        Bukkit.getPluginManager().registerEvents(new LagEvents(), this);

        // Tarefa do Reporte Horario (1 hora)
        new AnalyzerTask(this).runTaskTimer(this, 1200L, 72000L);

        // Monitor de Emergencia (5 segundos)
        new AntiLagController(this).runTaskTimer(this, 100L, 100L);

        getLogger().info("LagReport Online - Comando e TabComplete registrados.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Verifica se existe ao menos 1 argumento
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("lagreport.admin")) {
                    
                    reloadConfig();
                    sender.sendMessage("§a[LagReport] Configuracao recarregada!");

                    String url = getConfig().getString("webhook-url");
                    double tps = Bukkit.getTPS()[0]; // Pega o primeiro indice (TPS atual)
                    String msgTeste = "🔄 **Teste de Conexao:** Webhook vinculada com sucesso!\\nTPS Atual: " + String.format("%.2f", tps);
                    
                    DiscordWebhook.enviar(url, msgTeste);
                    sender.sendMessage("§e[LagReport] Tentando enviar teste para o Discord...");
                    
                    return true;
                }
                sender.sendMessage("§cVoce nao tem permissao.");
                return true;
            }
        }
        
        sender.sendMessage("§eUse: /lagreport reload");
        return true;
    }

    // LOGICA DO TAB COMPLETE (Sugestoes no chat)
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> sugestoes = new ArrayList<>();
        
        // Se estiver no primeiro argumento e tiver permissao
        if (args.length == 1) {
            if (sender.hasPermission("lagreport.admin")) {
                sugestoes.add("reload");
            }
        }
        
        return sugestoes;
    }
}
