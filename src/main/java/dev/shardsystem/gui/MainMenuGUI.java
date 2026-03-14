package dev.shardsystem.gui;

import dev.shardsystem.ShardSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MainMenuGUI {

    // Title shown in inventory header – small caps shard shop in dark gray
    public static final String TITLE = "\u00a78 \ua731\u029c\u1d00\u0280\u1d05 \ua731\u029c\u1d0f\u1d18";

    private final ShardSystem plugin;

    public MainMenuGUI(ShardSystem plugin) { this.plugin = plugin; }

    public Inventory build(Player player) {
        // Use legacy serializer so &8 color code renders correctly in the title
        Inventory inv = Bukkit.createInventory(null, 54,
            LegacyComponentSerializer.legacyAmpersand().deserialize(
                "&8 \ua731\u029c\u1d00\u0280\u1d05 \ua731\u029c\u1d0f\u1d18"));

        GuiUtil.fillAll(inv);

        int shards = plugin.getShardManager().getShards(player);

        // Balance display – slot 4 (top center)
        inv.setItem(4, balanceItem(shards));

        // Row 3 (slots 20-24): categories 1–5
        inv.setItem(20, GuiUtil.catItem(Material.AMETHYST_SHARD,      "\u2746 Shards",       "Buy Shards",          NamedTextColor.LIGHT_PURPLE));
        inv.setItem(21, GuiUtil.catItem(Material.BLAZE_ROD,           "\u2694 Sell Wands",    "Sell wands & more",   NamedTextColor.GOLD));
        inv.setItem(22, GuiUtil.catItem(Material.NETHERITE_CHESTPLATE,"\u265b Ranks",         "Buy ranks",           NamedTextColor.RED));
        inv.setItem(23, GuiUtil.catItem(Material.SUNFLOWER,           "\u26a1 Boosters",      "XP & Loot boosters",  NamedTextColor.YELLOW));
        inv.setItem(24, GuiUtil.catItem(Material.BARREL,              "\ud83d\udce6 Vault",   "Expand your vault",   NamedTextColor.GOLD));

        // Row 4 (slots 29-33): categories 6–10
        inv.setItem(29, GuiUtil.catItem(Material.NAME_TAG,            "\ud83c\udff7 Chat Tags",   "Custom chat tags",   NamedTextColor.AQUA));
        inv.setItem(30, GuiUtil.catItem(Material.CYAN_DYE,            "\ud83c\udfa8 Chat Colors", "Custom chat colors", NamedTextColor.DARK_AQUA));
        inv.setItem(31, GuiUtil.catItem(Material.PAPER,               "\ud83d\udcb0 Money",       "Buy in-game money",  NamedTextColor.GREEN));
        inv.setItem(32, GuiUtil.catItem(Material.ORANGE_DYE,          "\ud83c\udf08 Gradients",   "Gradient name tags", NamedTextColor.GOLD));
        inv.setItem(33, GuiUtil.catItem(Material.TRIPWIRE_HOOK,       "\ud83d\udddd Crate Keys",  "Buy crate keys",     NamedTextColor.LIGHT_PURPLE));

        // Row 5 center (slot 49): Glows
        inv.setItem(49, GuiUtil.catItem(Material.LEATHER_CHESTPLATE,  "\u2728 Glows",         "Player glow effects", NamedTextColor.WHITE));

        return inv;
    }

    private ItemStack balanceItem(int shards) {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta  = item.getItemMeta();
        meta.displayName(Component.text("Your Balance", NamedTextColor.LIGHT_PURPLE)
            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
            Component.empty(),
            Component.text("  \u2746 Shards: ", NamedTextColor.WHITE)
                .append(Component.text(String.format("%,d", shards), NamedTextColor.LIGHT_PURPLE)
                    .decorate(TextDecoration.BOLD))
                .decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("Visit ", NamedTextColor.GRAY)
                .append(Component.text("store.runemc.org", NamedTextColor.AQUA)
                    .decorate(TextDecoration.UNDERLINED))
                .append(Component.text(" to buy more!", NamedTextColor.GRAY))
                .decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }
}
