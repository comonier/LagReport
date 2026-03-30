package com.comonier.lagreport;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ArmorStand;

public class LagEvents implements Listener {
    private final LagReport plugin;

    public LagEvents(LagReport plugin) { this.plugin = plugin; }

    private boolean isHalt() { return AntiLagController.modoEmergencia; }
    private boolean check(String path) { return plugin.getConfig().getBoolean("halt-on-settings." + path, true); }

    @EventHandler 
    public void onRedstone(BlockRedstoneEvent e) { 
        if (isHalt() && check("disable-redstone")) e.setNewCurrent(0); 
    }

    @EventHandler 
    public void onSpawn(EntitySpawnEvent e) { 
        if (isHalt() && check("disable-mob-spawn")) e.setCancelled(true); 
    }

    @EventHandler 
    public void onExplode(EntityExplodeEvent e) { 
        if (isHalt() && check("disable-explosions")) e.setCancelled(true); 
    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (isHalt() && check("disable-mob-ai")) {
            if (e.getEntity() instanceof LivingEntity en && en.isLeashed()) return;
            e.setCancelled(true);
        }
    }

    @EventHandler 
    public void onPhysics(BlockPhysicsEvent e) {
        if (isHalt() && check("disable-gravity-blocks")) {
            Material m = e.getBlock().getType();
            if (m == Material.SAND || m == Material.GRAVEL || m == Material.ANVIL) e.setCancelled(true);
        }
    }

    @EventHandler 
    public void onFlow(BlockFromToEvent e) { 
        if (isHalt() && check("disable-liquid-flow")) e.setCancelled(true); 
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent e) {
        if (isHalt() && check("disable-pistons")) e.setCancelled(true);
    }

    @EventHandler
    public void onHopper(InventoryMoveItemEvent e) {
        if (isHalt() && check("disable-hoppers")) e.setCancelled(true);
    }

    @EventHandler
    public void onGrow(BlockGrowEvent e) {
        if (isHalt() && check("disable-crop-growth")) e.setCancelled(true);
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {
        if (isHalt() && check("disable-portals")) e.setCancelled(true);
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent e) {
        if (isHalt() && e.getEntityType() == EntityType.ENDERMAN && check("disable-enderman-teleport")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (!isHalt()) return;
        if (e.getRightClicked().getType() == EntityType.VILLAGER && check("disable-villager-trading")) e.setCancelled(true);
        if (e.getRightClicked() instanceof ArmorStand && check("disable-armor-stand-interact")) e.setCancelled(true);
    }

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent e) {
        if (isHalt() && check("disable-leaf-decay")) e.setCancelled(true);
    }

    @EventHandler
    public void onFireSpread(BlockBurnEvent e) {
        if (isHalt() && check("disable-fire-spread")) e.setCancelled(true);
    }

    @EventHandler
    public void onFireSpread2(BlockSpreadEvent e) {
        if (!isHalt()) return;
        if (e.getSource().getType() == Material.FIRE && check("disable-fire-spread")) e.setCancelled(true);
        if (e.getSource().getType() == Material.GRASS_BLOCK && check("disable-grass-spread")) e.setCancelled(true);
    }

    @EventHandler
    public void onIceMelt(BlockFadeEvent e) {
        if (isHalt() && check("disable-ice-melt")) e.setCancelled(true);
    }

    @EventHandler
    public void onIceForm(BlockFormEvent e) {
        if (isHalt() && check("disable-ice-form")) e.setCancelled(true);
    }
}
