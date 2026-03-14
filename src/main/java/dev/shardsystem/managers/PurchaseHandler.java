package dev.shardsystem.managers;

import dev.shardsystem.ShardSystem;
import dev.shardsystem.gui.CategoryGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PurchaseHandler {

    private final ShardSystem plugin;

    public PurchaseHandler(ShardSystem plugin) { this.plugin = plugin; }

    public void handle(Player player, String category, String itemName) {
        switch (category) {
            case CategoryGUI.CAT_BOOSTERS   -> handleBooster(player, itemName);
            case CategoryGUI.CAT_VAULT      -> handleVault(player, itemName);
            case CategoryGUI.CAT_CHATTAGS   -> handleChatTag(player, itemName);
            case CategoryGUI.CAT_CHATCOLOR  -> handleChatColor(player, itemName);
            case CategoryGUI.CAT_MONEY      -> handleMoney(player, itemName);
            case CategoryGUI.CAT_GRADIENTS  -> handleGradient(player, itemName);
            case CategoryGUI.CAT_CRATES     -> handleCrateKey(player, itemName);
            case CategoryGUI.CAT_GLOWS      -> handleGlow(player, itemName);
            default -> ok(player, "Purchase successful! " + itemName);
        }
    }

    private void handleBooster(Player player, String name) {
        int minutes = name.contains("30") ? 30 : 60;
        int ticks   = minutes * 60 * 20;
        boolean spawner = name.toLowerCase().contains("spawner");

        if (spawner) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, ticks, 1, false, true, true));
            runCmd(plugin.getConfig().getString("commands.spawner-booster",""),
                player, "%minutes%", String.valueOf(minutes));
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, ticks, 1, false, true, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, ticks, 1, false, true, true));
            runCmd(plugin.getConfig().getString("commands.loot-booster",""),
                player, "%minutes%", String.valueOf(minutes));
        }
        ok(player, name + " activated for " + minutes + " minutes!");
    }

    private void handleVault(Player player, String name) {
        boolean extra = name.toLowerCase().contains("extra");
        int rows = name.contains("+2") ? 2 : 1;
        String cmd = extra
            ? plugin.getConfig().getString("commands.vault-extra", "pv give %player% 1")
            : plugin.getConfig().getString("commands.vault-expand", "pv expand %player% %rows%")
                .replace("%rows%", String.valueOf(rows));
        runCmd(cmd, player, null, null);
        ok(player, "Vault " + (extra ? "unlocked!" : "expanded by " + rows + " row(s)!"));
    }

    private void handleChatTag(Player player, String name) {
        String tag = extractTag(name);
        String perm = plugin.getConfig().getString("permissions.chat-tag","chatcolor.tag.%tag%")
            .replace("%tag%", sanitize(tag));
        givePerm(player, perm);
        runCmd(plugin.getConfig().getString("commands.chat-tag",""), player, "%tag%", tag);
        ok(player, "Chat tag " + tag + " unlocked!");
    }

    private void handleChatColor(Player player, String name) {
        String color = name.toLowerCase().replace(" chat color","").replace(" ","_").trim();
        String perm = plugin.getConfig().getString("permissions.chat-color","chatcolor.color.%color%")
            .replace("%color%", color);
        givePerm(player, perm);
        runCmd(plugin.getConfig().getString("commands.chat-color",""), player, "%color%", color);
        ok(player, name + " unlocked!");
    }

    private void handleMoney(Player player, String name) {
        double money = parseNum(name);
        if (money > 0 && plugin.getVaultManager().hasVault()) {
            plugin.getVaultManager().giveMoney(player, money);
            ok(player, "$" + String.format("%,.0f", money) + " added to your balance!");
        } else {
            err(player, "Could not process money. Contact an admin.");
        }
    }

    private void handleGradient(Player player, String name) {
        String grad = name.toLowerCase().replace(" gradient","").replace(" ","_").trim();
        String perm = plugin.getConfig().getString("permissions.gradient","chatcolor.gradient.%gradient%")
            .replace("%gradient%", grad);
        givePerm(player, perm);
        runCmd(plugin.getConfig().getString("commands.gradient",""), player, "%gradient%", grad);
        ok(player, name + " unlocked!");
    }

    private void handleCrateKey(Player player, String name) {
        String type = name.toLowerCase()
            .replace(" crate key","").replace(" key","")
            .replace("runemc special","special").replace(" ","_").trim();
        String cmd = plugin.getConfig().getString("commands.crate-key","crates key give %player% %crate% 1")
            .replace("%crate%", type);
        runCmd(cmd, player, null, null);
        ok(player, name + " added to your crates!");
    }

    private void handleGlow(Player player, String name) {
        String color = name.toLowerCase().replace(" glow","").replace(" ","_").trim();
        String perm = plugin.getConfig().getString("permissions.glow","glow.color.%color%")
            .replace("%color%", color);
        givePerm(player, perm);
        runCmd(plugin.getConfig().getString("commands.glow",""), player, "%color%", color);
        ok(player, name + " activated!");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void givePerm(Player player, String permission) {
        try {
            net.luckperms.api.LuckPerms lp = net.luckperms.api.LuckPermsProvider.get();
            net.luckperms.api.model.user.User user = lp.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                user.data().add(net.luckperms.api.node.Node.builder(permission).build());
                lp.getUserManager().saveUser(user);
                return;
            }
        } catch (Exception ignored) {}
        // Fallback
        runConsole("lp user " + player.getName() + " permission set " + permission + " true");
    }

    private void runCmd(String template, Player player, String key, String val) {
        if (template == null || template.isBlank()) return;
        String cmd = template.replace("%player%", player.getName());
        if (key != null) cmd = cmd.replace(key, val);
        runConsole(cmd);
    }

    private void runConsole(String cmd) {
        if (cmd == null || cmd.isBlank()) return;
        Bukkit.getScheduler().runTask(plugin,
            () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
    }

    private void ok(Player p, String msg) {
        p.sendMessage(Component.text("✔ " + msg, NamedTextColor.GREEN));
    }
    private void err(Player p, String msg) {
        p.sendMessage(Component.text("✗ " + msg, NamedTextColor.RED));
    }

    private String extractTag(String name) {
        int s = name.indexOf('['), e = name.indexOf(']');
        return (s >= 0 && e > s) ? name.substring(s, e+1) : name;
    }
    private String sanitize(String s) {
        return s.toLowerCase().replace("[","").replace("]","").replace("#","num").replace(" ","_");
    }
    private double parseNum(String s) {
        try { return Double.parseDouble(s.replaceAll("[^0-9]","")); }
        catch (NumberFormatException e) { return 0; }
    }
}
