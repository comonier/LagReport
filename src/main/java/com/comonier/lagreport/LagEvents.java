package com.comonier.lagreport;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

public class LagEvents implements Listener {

    @EventHandler 
    public void onRedstone(BlockRedstoneEvent e) { 
        if (AntiLagController.modoEmergencia) e.setNewCurrent(0); 
    }

    @EventHandler 
    public void onSpawn(EntitySpawnEvent e) { 
        if (AntiLagController.modoEmergencia) e.setCancelled(true); 
    }

    @EventHandler 
    public void onExplode(EntityExplodeEvent e) { 
        if (AntiLagController.modoEmergencia) e.setCancelled(true); 
    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        // Bloqueia IA de busca de alvo na emergência, EXCETO se estiver no laço
        if (AntiLagController.modoEmergencia) {
            if (e.getEntity() instanceof LivingEntity entity && entity.isLeashed()) return;
            e.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPhysics(BlockPhysicsEvent e) {
        if (AntiLagController.modoEmergencia) {
            Material m = e.getBlock().getType();
            if (m == Material.SAND || m == Material.GRAVEL || m == Material.ANVIL) e.setCancelled(true);
        }
    }

    @EventHandler 
    public void onFlow(BlockFromToEvent e) { 
        if (AntiLagController.modoEmergencia) e.setCancelled(true); 
    }
}
