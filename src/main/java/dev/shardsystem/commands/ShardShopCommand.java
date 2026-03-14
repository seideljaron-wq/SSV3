package dev.shardsystem.commands;

import dev.shardsystem.ShardSystem;
import dev.shardsystem.gui.MainMenuGUI;
import dev.shardsystem.managers.ShopManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShardShopCommand implements CommandExecutor, TabCompleter {
    private final ShardSystem plugin;
    public ShardShopCommand(ShardSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Player only."); return true; }

        if (args.length == 0) { player.openInventory(new MainMenuGUI(plugin).build(player)); return true; }

        switch (args[0].toLowerCase()) {
            case "sell"   -> handleSell(player, args);
            case "delete" -> handleDelete(player);
            default -> player.sendMessage(Component.text("Usage: /shardshop [sell <price>|delete]", NamedTextColor.YELLOW));
        }
        return true;
    }

    private void handleSell(Player player, String[] args) {
        if (!plugin.isShopAdmin(player)) { player.sendMessage(Component.text("\u2717 No permission.", NamedTextColor.RED)); return; }
        if (args.length < 2) { player.sendMessage(Component.text("Usage: /shardshop sell <price>", NamedTextColor.YELLOW)); return; }
        int price;
        try { price = Integer.parseInt(args[1]); if (price <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { player.sendMessage(Component.text("\u2717 Invalid price.", NamedTextColor.RED)); return; }

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir()) { player.sendMessage(Component.text("\u2717 Hold an item!", NamedTextColor.RED)); return; }

        ItemStack shopItem = hand.clone(); shopItem.setAmount(1);
        ItemMeta meta = shopItem.getItemMeta();
        if (meta != null) {
            List<Component> lore = new ArrayList<>();
            if (meta.lore() != null) meta.lore().forEach(l -> {
                if (!PlainTextComponentSerializer.plainText().serialize(l).startsWith("Price:")) lore.add(l);
            });
            lore.add(Component.text("Price: ", NamedTextColor.GRAY)
                .append(Component.text(price + " Shards", NamedTextColor.LIGHT_PURPLE)));
            meta.lore(lore); shopItem.setItemMeta(meta);
        }

        int slot = plugin.getShopManager().addItem(shopItem, price);
        if (slot == -1) { player.sendMessage(Component.text("\u2717 Shop is full.", NamedTextColor.RED)); return; }
        String name = ShopManager.displayName(shopItem);
        player.sendMessage(Component.text("\u2714 Added ", NamedTextColor.GREEN).append(Component.text(name, NamedTextColor.AQUA)).append(Component.text(" for " + price + " Shards.", NamedTextColor.GREEN)));
        plugin.getDiscordLogger().logShopItemAdded(player.getName(), name, price);
    }

    private void handleDelete(Player player) {
        if (!plugin.isShopAdmin(player)) { player.sendMessage(Component.text("\u2717 No permission.", NamedTextColor.RED)); return; }
        if (plugin.getShopManager().getItems().isEmpty()) { player.sendMessage(Component.text("\u26a0 No items to delete.", NamedTextColor.YELLOW)); return; }

        Inventory inv = Bukkit.createInventory(null, 27,
            Component.text("\u2746 Delete Item", NamedTextColor.RED));
        ItemStack g = new ItemStack(org.bukkit.Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta gm = g.getItemMeta(); gm.displayName(Component.empty()); g.setItemMeta(gm);
        for (int i = 0; i < 27; i++) inv.setItem(i, g);
        plugin.getShopManager().getItems().forEach((slot, si) ->
            inv.setItem(slot, ShopManager.withPriceLore(si.item().clone(), si.price())));
        player.openInventory(inv);
        player.sendMessage(Component.text("Click an item to remove it.", NamedTextColor.YELLOW));
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return List.of("sell","delete");
        if (args.length == 2 && args[0].equalsIgnoreCase("sell")) return List.of("10","50","100","500");
        return List.of();
    }
}
