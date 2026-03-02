package com.comonier.lagreport;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Observer;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class RedstoneScanner {
    public static Map<String, Integer> scan(Player p, int radius) {
        Map<String, Integer> active = new HashMap<>();
        Block center = p.getLocation().getBlock();
        int negR = radius * -1;
        for (int x = radius; x >= negR; x = x - 3) {
            for (int y = radius; y >= negR; y = y - 3) {
                for (int z = radius; z >= negR; z = z - 3) {
                    Block b = center.getRelative(x, y, z);
                    Material t = b.getType();
                    if (t == Material.REDSTONE_WIRE || t == Material.REPEATER || t == Material.COMPARATOR || t == Material.OBSERVER) {
                        if (b.getBlockData() instanceof Powerable pwr && pwr.isPowered()) active.merge(t.name(), 1, Integer::sum);
                        else if (b.getBlockData() instanceof Observer obs && obs.isPowered()) active.merge(t.name(), 1, Integer::sum);
                    }
                }
            }
        }
        return active;
    }
}
