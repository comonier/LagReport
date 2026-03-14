package com.comonier.lagreport;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SlimefunCommand implements CommandExecutor {
    private final LagReport plugin;

    public SlimefunCommand(LagReport plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                             @NotNull String label, @NotNull String[] args) {
        
        String prefix = plugin.getMsg("prefix");

        if (!sender.hasPermission("lagreport.admin")) {
            sender.sendMessage(prefix + plugin.getMsg("commands.no_permission"));
            return true;
        }

        String url = plugin.getConfig().getString("slimefun-webhook-url");
        if (url == null || url.isEmpty() || url.contains("URL_SLIMEFUN")) {
            sender.sendMessage(prefix + "§cErro: 'slimefun-webhook-url' não configurada!");
            return true;
        }

        // NOVO: Teste direto de conexão
        if (args.length > 0 && args[0].equalsIgnoreCase("test")) {
            sender.sendMessage(prefix + "§aEnviando teste de conexão para Webhook Slimefun...");
            DiscordWebhook.enviar(url, "📡 **Teste de Conexão:** A Webhook do Slimefun está configurada corretamente!");
            return true;
        }

        sender.sendMessage(prefix + plugin.getMsg("commands.sf_requesting"));
        new Slimefun(plugin).reportTimings();
        
        return true;
    }
}
