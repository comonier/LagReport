package com.comonier.lagreport;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class RedstoneScanner {
    public static Map<String, Integer> scan(Player p, int radius) {
        Map<String, Integer> active = new HashMap<>();
        Block center = p.getLocation().getBlock();
        int negativeRadius = radius * -1;

        // Loop invertido para usar apenas operadores de MAIOR ou IGUAL
        for (int x = radius; x >= negativeRadius; x = x - 3) {
            for (int y = radius; y >= negativeRadius; y = y - 3) {
                for (int z = radius; z >= negativeRadius; z = z - 3) {
                    Block b = center.getRelative(x, y, z);
                    Material type = b.getType();
                    
                    if (type == Material.REDSTONE_WIRE || type == Material.REPEATER || 
                        type == Material.COMPARATOR || type == Material.OBSERVER) {
                        
                        if (b.getBlockData() instanceof Powerable pwr) {
                            if (pwr.isPowered()) {
                                active.merge(type.name(), 1, Integer::sum);
                            }
                        }
                    }
                }
            }
        }
        return active;
    }
}
