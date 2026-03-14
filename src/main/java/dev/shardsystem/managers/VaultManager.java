package dev.shardsystem.managers;

import dev.shardsystem.ShardSystem;
import org.bukkit.entity.Player;

public class VaultManager {
    private final ShardSystem plugin;
    public VaultManager(ShardSystem plugin) { this.plugin = plugin; }
    public boolean giveMoney(Player p, double amount) {
        if (plugin.getEconomy() == null) return false;
        plugin.getEconomy().depositPlayer(p, amount);
        return true;
    }
    public boolean hasVault() { return plugin.getEconomy() != null; }
}
