package dev.shardsystem.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiUtil {

    // ── Item builders ─────────────────────────────────────────────────────────

    public static ItemStack glass() {
        return namedPane(Material.GRAY_STAINED_GLASS_PANE, Component.empty());
    }

    public static ItemStack namedPane(Material mat, Component name) {
        ItemStack p = new ItemStack(mat);
        ItemMeta m = p.getItemMeta();
        m.displayName(name.decoration(TextDecoration.ITALIC, false));
        p.setItemMeta(m);
        return p;
    }

    /** Category item for main menu */
    public static ItemStack catItem(Material mat, String name, String desc, NamedTextColor color) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta  = item.getItemMeta();
        meta.displayName(Component.text(name, color)
            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
            Component.empty(),
            Component.text(desc, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
            Component.empty(),
            Component.text("► Click to open", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }

    /** Shop item with price lore (purchasable) */
    public static ItemStack shopItem(Material mat, String name, String desc,
                                     String priceLabel, int price, String extra) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta  = item.getItemMeta();
        meta.displayName(Component.text(name, NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        if (desc != null && !desc.isEmpty()) {
            lore.add(Component.text("Information", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));
            for (String line : wrap(desc, 32))
                lore.add(Component.text(line, NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
        }
        lore.add(Component.text(priceLabel, NamedTextColor.LIGHT_PURPLE)
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("  \u2726 Shards: ", NamedTextColor.WHITE)
            .append(Component.text(String.format("%,d", price), NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD))
            .decoration(TextDecoration.ITALIC, false));
        if (extra != null && !extra.isEmpty()) {
            lore.add(Component.empty());
            lore.add(Component.text(extra, NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        }
        lore.add(Component.empty());
        lore.add(Component.text("\u25ba ", NamedTextColor.YELLOW)
            .append(Component.text("CLICK", NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED))
            .append(Component.text(" to Purchase", NamedTextColor.WHITE))
            .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /** Website redirect item */
    public static ItemStack websiteItem(Material mat, String name, String desc, String url) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta  = item.getItemMeta();
        meta.displayName(Component.text(name, NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        if (desc != null && !desc.isEmpty()) {
            lore.add(Component.text("Information", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));
            for (String line : wrap(desc, 32))
                lore.add(Component.text(line, NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.empty());
        }
        lore.add(Component.text("Purchase on our Store", NamedTextColor.LIGHT_PURPLE)
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("  \u266a ", NamedTextColor.YELLOW)
            .append(Component.text(url, NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED))
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("\u25ba ", NamedTextColor.YELLOW)
            .append(Component.text("CLICK", NamedTextColor.YELLOW)
                .decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED))
            .append(Component.text(" to visit store in chat", NamedTextColor.WHITE))
            .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static void fillAll(Inventory inv) {
        ItemStack g = glass();
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, g);
    }

    public static String plain(Component c) {
        return PlainTextComponentSerializer.plainText().serialize(c);
    }

    private static List<String> wrap(String text, int max) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder cur = new StringBuilder();
        for (String w : words) {
            if (cur.length() + w.length() + 1 > max && cur.length() > 0) {
                lines.add(cur.toString()); cur = new StringBuilder();
            }
            if (cur.length() > 0) cur.append(" ");
            cur.append(w);
        }
        if (cur.length() > 0) lines.add(cur.toString());
        return lines;
    }
}
