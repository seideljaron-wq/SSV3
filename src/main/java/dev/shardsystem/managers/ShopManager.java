package dev.shardsystem.managers;

import dev.shardsystem.ShardSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ShopManager {

    private final ShardSystem plugin;
    private final Map<Integer, ShopItem> items = new LinkedHashMap<>();

    // Content slot fill order – Phase1(row2), Phase2(row3), Phase3(row1)
    static final int[] CONTENT_SLOTS = {11,12,13,14,15, 20,21,22,23,24, 2,3,4,5,6};
    static final Set<Integer> BORDER_SLOTS = Set.of(0,1,7,8, 9,10,16,17, 18,19,25,26);

    public ShopManager(ShardSystem plugin) { this.plugin = plugin; load(); }

    public record ShopItem(ItemStack item, int price) {}

    public Map<Integer, ShopItem> getItems()  { return items; }
    public boolean isBorder(int slot)         { return BORDER_SLOTS.contains(slot); }

    public int addItem(ItemStack item, int price) {
        for (int s : CONTENT_SLOTS) {
            if (!items.containsKey(s)) { items.put(s, new ShopItem(item, price)); save(); return s; }
        }
        return -1;
    }

    public boolean removeAtSlot(int slot) {
        if (items.remove(slot) != null) { save(); return true; }
        return false;
    }

    private void load() {
        items.clear();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("shop.items");
        if (sec == null) return;
        for (String k : sec.getKeys(false)) {
            try {
                int slot = Integer.parseInt(k);
                ItemStack item = sec.getItemStack(k + ".item");
                int price = sec.getInt(k + ".price");
                if (item != null) items.put(slot, new ShopItem(item, price));
            } catch (NumberFormatException ignored) {}
        }
    }

    public void save() {
        plugin.getConfig().set("shop.items", null);
        for (var e : items.entrySet()) {
            plugin.getConfig().set("shop.items." + e.getKey() + ".item",  e.getValue().item());
            plugin.getConfig().set("shop.items." + e.getKey() + ".price", e.getValue().price());
        }
        plugin.saveConfig();
    }

    // ── Static helpers ────────────────────────────────────────────────────────

    public static ItemStack withPriceLore(ItemStack item, int price) {
        ItemStack c = item.clone();
        ItemMeta m = c.getItemMeta(); if (m == null) return c;
        List<Component> lore = new ArrayList<>();
        if (m.lore() != null) m.lore().forEach(l -> {
            if (!plain(l).startsWith("Price:")) lore.add(l);
        });
        lore.add(Component.text("Price: ", NamedTextColor.GRAY)
            .append(Component.text(price + " Shards", NamedTextColor.LIGHT_PURPLE)));
        m.lore(lore); c.setItemMeta(m); return c;
    }

    public static ItemStack stripPriceLore(ItemStack item) {
        ItemStack c = item.clone();
        ItemMeta m = c.getItemMeta(); if (m == null || m.lore() == null) return c;
        List<Component> lore = new ArrayList<>();
        m.lore().forEach(l -> { if (!plain(l).startsWith("Price:")) lore.add(l); });
        m.lore(lore); c.setItemMeta(m); return c;
    }

    public static String displayName(ItemStack item) {
        if (item == null) return "Unknown";
        ItemMeta m = item.getItemMeta();
        if (m != null && m.hasDisplayName() && m.displayName() != null)
            return plain(m.displayName());
        String raw = item.getType().name().replace('_',' ');
        StringBuilder sb = new StringBuilder();
        for (String w : raw.split(" "))
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0)))
                .append(w.substring(1).toLowerCase()).append(" ");
        return sb.toString().trim();
    }

    private static String plain(Component c) {
        return PlainTextComponentSerializer.plainText().serialize(c);
    }
}
