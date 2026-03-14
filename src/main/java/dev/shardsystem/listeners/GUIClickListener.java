package dev.shardsystem.listeners;

import dev.shardsystem.ShardSystem;
import dev.shardsystem.gui.CategoryGUI;
import dev.shardsystem.gui.ConfirmGUI;
import dev.shardsystem.gui.GuiUtil;
import dev.shardsystem.gui.MainMenuGUI;
import dev.shardsystem.managers.ShopManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIClickListener implements Listener {

    private final ShardSystem plugin;
    private final CategoryGUI catGUI;

    // All known GUI title prefixes/equals
    private static final String[] TITLES = {
        CategoryGUI.CAT_SHARDS, CategoryGUI.CAT_SELLWAND, CategoryGUI.CAT_RANKS,
        CategoryGUI.CAT_BOOSTERS, CategoryGUI.CAT_VAULT, CategoryGUI.CAT_CHATTAGS,
        CategoryGUI.CAT_CHATCOLOR, CategoryGUI.CAT_MONEY, CategoryGUI.CAT_GRADIENTS,
        CategoryGUI.CAT_CRATES, CategoryGUI.CAT_GLOWS,
        "\ua731\u029c\u1d00\u0280\u1d05 \ua731\u029c\u1d0f\u1d18" // main menu (without color code)
    };

    public GUIClickListener(ShardSystem plugin) {
        this.plugin = plugin;
        this.catGUI = new CategoryGUI(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = PlainTextComponentSerializer.plainText()
            .serialize(event.getView().title());

        if (!isOurGUI(title)) return;

        // Cancel ALL actions inside our GUIs – no item movement possible
        event.setCancelled(true);
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) return;

        // Ignore clicks in player's own inventory (bottom half)
        if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        int slot = event.getRawSlot();

        // ── Main Menu ──────────────────────────────────────────────────────────
        if (title.contains("\ua731\u029c\u1d00\u0280\u1d05")) {
            handleMainMenu(player, slot);
            return;
        }

        // ── Back button ────────────────────────────────────────────────────────
        if (slot == CategoryGUI.BACK_SLOT && clicked.getType() == Material.ARROW) {
            reopen(player, new MainMenuGUI(plugin).build(player));
            return;
        }

        // ── Confirm GUI ────────────────────────────────────────────────────────
        if (title.startsWith("Confirm: ")) {
            handleConfirm(player, slot, clicked, event.getInventory());
            return;
        }

        // ── Website redirect categories ────────────────────────────────────────
        if (title.equals(CategoryGUI.CAT_SHARDS) ||
            title.equals(CategoryGUI.CAT_SELLWAND) ||
            title.equals(CategoryGUI.CAT_RANKS)) {
            sendWebsite(player);
            return;
        }

        // ── Purchasable – open confirm ─────────────────────────────────────────
        int price = extractPrice(clicked);
        if (price > 0) {
            player.setMetadata("shop_cat",
                new org.bukkit.metadata.FixedMetadataValue(plugin, title));
            reopen(player, new ConfirmGUI().build(clicked, price));
        }
    }

    // ── Main Menu navigation ──────────────────────────────────────────────────

    private void handleMainMenu(Player player, int slot) {
        switch (slot) {
            case 20 -> reopen(player, catGUI.buildShards());
            case 21 -> reopen(player, catGUI.buildSellWands());
            case 22 -> reopen(player, catGUI.buildRanks());
            case 23 -> reopen(player, catGUI.buildBoosters());
            case 24 -> reopen(player, catGUI.buildVault());
            case 29 -> reopen(player, catGUI.buildChatTags());
            case 30 -> reopen(player, catGUI.buildChatColors());
            case 31 -> reopen(player, catGUI.buildMoney());
            case 32 -> reopen(player, catGUI.buildGradients());
            case 33 -> reopen(player, catGUI.buildCrates());
            case 49 -> reopen(player, catGUI.buildGlows());
        }
    }

    // ── Confirm ───────────────────────────────────────────────────────────────

    private void handleConfirm(Player player, int slot, ItemStack clicked, Inventory inv) {
        if (clicked.getType() == Material.GREEN_STAINED_GLASS_PANE) {
            ItemStack center   = inv.getItem(13);
            ItemStack priceDisp = inv.getItem(4);
            if (center == null || priceDisp == null) {
                reopen(player, new MainMenuGUI(plugin).build(player)); return;
            }

            int price = extractPriceFromName(getPlainName(priceDisp));
            String category = player.hasMetadata("shop_cat")
                ? player.getMetadata("shop_cat").get(0).asString() : "Shop";
            player.removeMetadata("shop_cat", plugin);

            if (price <= 0) {
                player.sendMessage(Component.text("\u2717 Could not read price. Contact an admin.", NamedTextColor.RED));
                reopen(player, new MainMenuGUI(plugin).build(player)); return;
            }

            if (!plugin.getShardManager().hasShards(player, price)) {
                player.sendMessage(
                    Component.text("\u2717 Not enough Shards! ", NamedTextColor.RED)
                        .append(Component.text("Need: ", NamedTextColor.GRAY))
                        .append(Component.text(String.format("%,d", price) + " Shards", NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text(" | You have: ", NamedTextColor.GRAY))
                        .append(Component.text(String.format("%,d", plugin.getShardManager().getShards(player)) + " Shards", NamedTextColor.LIGHT_PURPLE)));
                reopen(player, new MainMenuGUI(plugin).build(player)); return;
            }

            plugin.getShardManager().removeShards(player, price);
            int remaining = plugin.getShardManager().getShards(player);
            String itemName = getPlainName(center);

            // Execute purchase action
            plugin.getPurchaseHandler().handle(player, category, itemName);

            player.sendMessage(
                Component.text("  Remaining balance: ", NamedTextColor.GRAY)
                    .append(Component.text(String.format("%,d", remaining) + " Shards",
                        NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD)));

            plugin.getDiscordLogger().logPurchase(
                player.getName(), category, itemName, price, remaining);

            reopen(player, new MainMenuGUI(plugin).build(player));

        } else if (clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
            player.removeMetadata("shop_cat", plugin);
            reopen(player, new MainMenuGUI(plugin).build(player));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void sendWebsite(Player player) {
        String url = plugin.getConfig().getString("store-url", "store.runemc.org");
        player.sendMessage(Component.text(""));
        player.sendMessage(
            Component.text("  \u2746 RuneMC Store: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(url, NamedTextColor.AQUA)
                    .decorate(TextDecoration.UNDERLINED)));
        player.sendMessage(Component.text(""));
        player.closeInventory();
    }

    private int extractPrice(ItemStack item) {
        if (item == null) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.lore() == null) return 0;
        for (var line : meta.lore()) {
            String plain = PlainTextComponentSerializer.plainText().serialize(line);
            if (plain.contains("Shards:")) {
                try { return Integer.parseInt(plain.replaceAll("[^0-9]", "")); }
                catch (NumberFormatException ignored) {}
            }
        }
        return 0;
    }

    private int extractPriceFromName(String name) {
        try { return Integer.parseInt(name.replaceAll("[^0-9]", "")); }
        catch (NumberFormatException e) { return 0; }
    }

    private String getPlainName(ItemStack item) {
        if (item == null) return "Unknown";
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.displayName() != null)
            return PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        return item.getType().name();
    }

    private boolean isOurGUI(String title) {
        if (title.startsWith("Confirm: ")) return true;
        for (String t : TITLES) if (title.equals(t)) return true;
        // Main menu: contains small caps S
        if (title.contains("\ua731\u029c\u1d00\u0280\u1d05")) return true;
        return false;
    }

    private void reopen(Player player, Inventory inv) {
        plugin.getServer().getScheduler().runTask(plugin,
            () -> player.openInventory(inv));
    }
}
