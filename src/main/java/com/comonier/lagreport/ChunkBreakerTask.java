package com.comonier.lagreport;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

public class ChunkBreakerTask extends BukkitRunnable {
    private final LagReport plugin;

    public ChunkBreakerTask(LagReport plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        int maxEnt = plugin.getConfig().getInt("breaker-settings.max-entities-per-chunk", 500);
        
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                Entity[] entities = chunk.getEntities();
                
                if (entities.length > maxEnt) {
                    performEmergencyFix(chunk);
                    
                    String log = String.format("🚨 **CHUNK CRÍTICO:** [%d, %d] em %s com %d entidades. Redstone ejetada e drops limpos.", 
                        chunk.getX(), chunk.getZ(), world.getName(), entities.length);
                    DiscordWebhook.enviar(plugin.getConfig().getString("webhook-url"), log);
                    
                    Bukkit.broadcastMessage(String.format(plugin.getMsg("emergency.chunk_cleaned"), 
                        chunk.getX(), chunk.getZ()));
                }
            }
        }
    }

    private void performEmergencyFix(Chunk center) {
        // 1. Limpa drops no chunk e raio configurado
        int radius = plugin.getConfig().getInt("breaker-settings.clean-radius", 1);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Chunk neighbor = center.getWorld().getChunkAt(center.getX() + x, center.getZ() + z);
                for (Entity en : neighbor.getEntities()) {
                    if (en instanceof Item) en.remove();
                }
            }
        }

        // 2. Localiza e ejeta o fio de redstone (ajustado para camadas negativas)
        Material target = Material.REDSTONE_WIRE;
        boolean broken = false;
        World w = center.getWorld();
        
        // Varredura inteligente: busca de cima para baixo no chunk
        for (int x = 0; x < 16 && !broken; x++) {
            for (int z = 0; z < 16 && !broken; z++) {
                for (int y = w.getMaxHeight() - 1; y >= w.getMinHeight(); y--) {
                    Block b = center.getBlock(x, y, z);
                    if (b.getType() == target) {
                        b.breakNaturally(); // O item vira drop e o circuito abre
                        broken = true;
                        break;
                    }
                }
            }
        }
    }
}
