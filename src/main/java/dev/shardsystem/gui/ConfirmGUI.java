package dev.shardsystem.gui;

import dev.shardsystem.managers.ShopManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmGUI {

    public Inventory build(ItemStack shopItem, int price) {
        String name = GuiUtil.plain(
            shopItem.getItemMeta() != null && shopItem.getItemMeta().hasDisplayName()
                && shopItem.getItemMeta().displayName() != null
                ? shopItem.getItemMeta().displayName()
                : Component.text(shopItem.getType().name()));

        Inventory inv = Bukkit.createInventory(null, 27, "Confirm: " + name);
        GuiUtil.fillAll(inv);

        ItemStack cancel  = GuiUtil.namedPane(Material.RED_STAINED_GLASS_PANE,
            Component.text("\u2717 Cancel", NamedTextColor.RED));
        ItemStack confirm = GuiUtil.namedPane(Material.GREEN_STAINED_GLASS_PANE,
            Component.text("\u2714 Confirm Purchase", NamedTextColor.GREEN));

        for (int s : new int[]{0,1,2,9,10,11,18,19,20}) inv.setItem(s, cancel);
        for (int s : new int[]{6,7,8,15,16,17,24,25,26}) inv.setItem(s, confirm);

        // Center: clean item (no price lore)
        inv.setItem(13, ShopManager.stripPriceLore(shopItem.clone()));

        // Price display at slot 4
        ItemStack priceItem = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = priceItem.getItemMeta();
        meta.displayName(Component.text("Cost: " + String.format("%,d", price) + " Shards",
            NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
            Component.empty(),
            Component.text("Click green to confirm.", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
        ));
        priceItem.setItemMeta(meta);
        inv.setItem(4, priceItem);

        return inv;
    }
}
