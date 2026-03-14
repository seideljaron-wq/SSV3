package dev.shardsystem.gui;

import dev.shardsystem.ShardSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CategoryGUI {

    public static final String CAT_SHARDS    = "\u2746 Shards";
    public static final String CAT_SELLWAND  = "\u2746 Sell Wands";
    public static final String CAT_RANKS     = "\u2746 Ranks";
    public static final String CAT_BOOSTERS  = "\u2746 Boosters";
    public static final String CAT_VAULT     = "\u2746 Vault Storage";
    public static final String CAT_CHATTAGS  = "\u2746 Chat Tags";
    public static final String CAT_CHATCOLOR = "\u2746 Chat Colors";
    public static final String CAT_MONEY     = "\u2746 Money";
    public static final String CAT_GRADIENTS = "\u2746 Gradients";
    public static final String CAT_CRATES    = "\u2746 Crate Keys";
    public static final String CAT_GLOWS     = "\u2746 Glows";

    public static final int BACK_SLOT = 27;

    private final ShardSystem plugin;

    public CategoryGUI(ShardSystem plugin) { this.plugin = plugin; }

    // ── 1. Shards ─────────────────────────────────────────────────────────────
    public Inventory buildShards() {
        Inventory inv = make(CAT_SHARDS);
        String url = plugin.getConfig().getString("store-url", "store.runemc.org");
        inv.setItem(11, GuiUtil.websiteItem(Material.AMETHYST_SHARD,   "250 Shards",   "Get 250 Shards added to your balance!", url));
        inv.setItem(13, GuiUtil.websiteItem(Material.AMETHYST_CLUSTER, "500 Shards",   "Get 500 Shards added to your balance!", url));
        inv.setItem(15, GuiUtil.websiteItem(Material.BUDDING_AMETHYST, "1,000 Shards", "Get 1,000 Shards added to your balance!", url));
        back(inv); return inv;
    }

    // ── 2. Sell Wands ─────────────────────────────────────────────────────────
    public Inventory buildSellWands() {
        Inventory inv = make(CAT_SELLWAND);
        String url = plugin.getConfig().getString("store-url", "store.runemc.org");
        inv.setItem(11, GuiUtil.websiteItem(Material.BLAZE_ROD, "Basic Sell Wand",  "Sell all items in a spawner chest with 1 hit!", url));
        inv.setItem(13, GuiUtil.websiteItem(Material.BLAZE_ROD, "Pro Sell Wand",    "Sell all items with a 10% money booster!", url));
        inv.setItem(15, GuiUtil.websiteItem(Material.BLAZE_ROD, "Elite Sell Wand",  "Sell all items with a 25% money booster!", url));
        back(inv); return inv;
    }

    // ── 3. Ranks ──────────────────────────────────────────────────────────────
    public Inventory buildRanks() {
        Inventory inv = make(CAT_RANKS);
        String url = plugin.getConfig().getString("store-url", "store.runemc.org");
        inv.setItem(10, GuiUtil.websiteItem(Material.BOOK,          "Titan Rank",    "Get the Titan rank! \u00a3 2.00", url));
        inv.setItem(11, GuiUtil.websiteItem(Material.BOOK,          "Overlord Rank", "Get the Overlord rank! \u00a3 3.00", url));
        inv.setItem(12, GuiUtil.websiteItem(Material.BOOK,          "Eternal Rank",  "Get the Eternal rank! \u00a3 4.00", url));
        inv.setItem(13, GuiUtil.websiteItem(Material.ENCHANTED_BOOK,"Ascended Rank", "Get the Ascended rank! \u00a3 6.00", url));
        inv.setItem(14, GuiUtil.websiteItem(Material.ENCHANTED_BOOK,"Immortal Rank", "Get the Immortal rank! \u00a3 8.00", url));
        back(inv); return inv;
    }

    // ── 4. Boosters ───────────────────────────────────────────────────────────
    public Inventory buildBoosters() {
        Inventory inv = make(CAT_BOOSTERS);
        inv.setItem(10, GuiUtil.shopItem(Material.EMERALD,          "Spawner Booster 1h",   "Doubles spawner rates for 1 hour!",              "Buy Booster", 800,  "Duration: 60 minutes"));
        inv.setItem(11, GuiUtil.shopItem(Material.EMERALD,          "Spawner Booster 30min","Doubles spawner rates for 30 minutes!",           "Buy Booster", 450,  "Duration: 30 minutes"));
        inv.setItem(14, GuiUtil.shopItem(Material.EXPERIENCE_BOTTLE,"Loot & XP Booster 1h", "Doubles loot drops & XP gain for 1 hour!",       "Buy Booster", 750,  "Duration: 60 minutes"));
        inv.setItem(15, GuiUtil.shopItem(Material.EXPERIENCE_BOTTLE,"Loot & XP Booster 30min","Doubles loot drops & XP gain for 30 minutes!", "Buy Booster", 400,  "Duration: 30 minutes"));
        back(inv); return inv;
    }

    // ── 5. Vault ──────────────────────────────────────────────────────────────
    public Inventory buildVault() {
        Inventory inv = make(CAT_VAULT);
        inv.setItem(11, GuiUtil.shopItem(Material.CHEST,       "Vault Extension [+1 Row]", "Extend all your vaults by 1 row.",  "Buy Storage", 1500, "Note: Does not grant a new vault."));
        inv.setItem(13, GuiUtil.shopItem(Material.CHEST,       "Vault Extension [+2 Rows]","Extend all your vaults by 2 rows.", "Buy Storage", 2500, "Best value upgrade!"));
        inv.setItem(15, GuiUtil.shopItem(Material.ENDER_CHEST, "Extra Vault",              "Unlock an additional vault slot!",  "Buy Vault",   5000, "You can have multiple vaults!"));
        back(inv); return inv;
    }

    // ── 6. Chat Tags ──────────────────────────────────────────────────────────
    public Inventory buildChatTags() {
        Inventory inv = make(CAT_CHATTAGS);
        record T(Material m, String name, int price) {}
        List<T> tags = List.of(
            new T(Material.PLAYER_HEAD,          "[#1]",      500),
            new T(Material.GOLD_INGOT,           "[Rich]",    400),
            new T(Material.DIAMOND,              "[Elite]",   600),
            new T(Material.NETHER_STAR,          "[Legend]",  800),
            new T(Material.BLAZE_POWDER,         "[Blaze]",   450),
            new T(Material.ENDER_EYE,            "[Phantom]", 500),
            new T(Material.BEACON,               "[God]",     1200),
            new T(Material.SLIME_BALL,           "[Slime]",   300),
            new T(Material.FLINT_AND_STEEL,      "[Inferno]", 550),
            new T(Material.TOTEM_OF_UNDYING,     "[Immortal]",1000),
            new T(Material.IRON_SWORD,           "[Warrior]", 350),
            new T(Material.GOLDEN_APPLE,         "[MVP]",     700),
            new T(Material.AMETHYST_SHARD,       "[RuneMC]",  900),
            new T(Material.WITHER_SKELETON_SKULL,"[Wither]",  650)
        );
        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25};
        for (int i = 0; i < tags.size() && i < slots.length; i++) {
            T t = tags.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(t.m(), t.name() + " Tag",
                "Show off as " + t.name() + " in chat!", "Buy Tag", t.price(), "Tag: " + t.name()));
        }
        back(inv); return inv;
    }

    // ── 7. Chat Colors ────────────────────────────────────────────────────────
    public Inventory buildChatColors() {
        Inventory inv = make(CAT_CHATCOLOR);
        record C(Material m, String name) {}
        List<C> colors = List.of(
            new C(Material.RED_DYE,        "Red"),
            new C(Material.ORANGE_DYE,     "Orange"),
            new C(Material.YELLOW_DYE,     "Yellow"),
            new C(Material.LIME_DYE,       "Lime"),
            new C(Material.GREEN_DYE,      "Green"),
            new C(Material.CYAN_DYE,       "Cyan"),
            new C(Material.LIGHT_BLUE_DYE, "Light Blue"),
            new C(Material.BLUE_DYE,       "Blue"),
            new C(Material.PURPLE_DYE,     "Purple"),
            new C(Material.MAGENTA_DYE,    "Magenta"),
            new C(Material.PINK_DYE,       "Pink"),
            new C(Material.WHITE_DYE,      "White"),
            new C(Material.LIGHT_GRAY_DYE, "Light Gray"),
            new C(Material.GRAY_DYE,       "Gray"),
            new C(Material.BLACK_DYE,      "Black"),
            new C(Material.BROWN_DYE,      "Brown")
        );
        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29};
        for (int i = 0; i < colors.size() && i < slots.length; i++) {
            C c = colors.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(c.m(), c.name() + " Chat Color",
                "Change your chat color to " + c.name() + "!", "Buy Color", 150, null));
        }
        back(inv); return inv;
    }

    // ── 8. Money ──────────────────────────────────────────────────────────────
    public Inventory buildMoney() {
        Inventory inv = make(CAT_MONEY);
        record M(int money, int shards) {}
        List<M> entries = List.of(
            new M(5_000,    200),
            new M(10_000,   380),
            new M(25_000,   900),
            new M(50_000,  1700),
            new M(100_000, 3200),
            new M(250_000, 7500)
        );
        int[] slots = {10,11,12,13,14,15};
        for (int i = 0; i < entries.size(); i++) {
            M e = entries.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(Material.PAPER,
                "$" + String.format("%,d", e.money()) + " Money",
                "Receive $" + String.format("%,d", e.money()) + " in-game money!",
                "Buy Money", e.shards(),
                String.format("$%.0f per Shard", (double)e.money()/e.shards())));
        }
        back(inv); return inv;
    }

    // ── 9. Gradients ──────────────────────────────────────────────────────────
    public Inventory buildGradients() {
        Inventory inv = make(CAT_GRADIENTS);
        record G(Material m, String name, String desc, int price) {}
        List<G> grads = List.of(
            new G(Material.RED_DYE,       "Sunset Gradient",    "Red \u2192 Orange \u2192 Yellow",   1200),
            new G(Material.BLUE_DYE,      "Ocean Gradient",     "Dark Blue \u2192 Cyan \u2192 White",1200),
            new G(Material.PURPLE_DYE,    "Galaxy Gradient",    "Purple \u2192 Blue \u2192 Pink",    1500),
            new G(Material.GREEN_DYE,     "Forest Gradient",    "Dark Green \u2192 Lime",            1000),
            new G(Material.PINK_DYE,      "Cherry Gradient",    "Pink \u2192 Rose \u2192 White",     1200),
            new G(Material.ORANGE_DYE,    "Lava Gradient",      "Red \u2192 Orange \u2192 Yellow",   1300),
            new G(Material.WHITE_DYE,     "Ice Gradient",       "White \u2192 Light Blue \u2192 Cyan",1100),
            new G(Material.MAGENTA_DYE,   "Neon Gradient",      "Purple \u2192 Magenta \u2192 Pink", 1800),
            new G(Material.GOLD_INGOT,    "Gold Gradient",      "Yellow \u2192 Gold \u2192 Orange",  1400),
            new G(Material.COAL,          "Shadow Gradient",    "Black \u2192 Gray \u2192 Dark Gray",1000)
        );
        int[] slots = {10,11,12,13,14,19,20,21,22,23};
        for (int i = 0; i < grads.size() && i < slots.length; i++) {
            G g = grads.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(g.m(), g.name(),
                g.desc(), "Buy Gradient", g.price(), "Applied to your rank/name tag"));
        }
        back(inv); return inv;
    }

    // ── 10. Crate Keys ────────────────────────────────────────────────────────
    public Inventory buildCrates() {
        Inventory inv = make(CAT_CRATES);
        record K(Material m, String name, String desc, int price) {}
        List<K> keys = List.of(
            new K(Material.PURPLE_CANDLE, "Common Crate Key",   "Open Common crates for basic rewards!",       250),
            new K(Material.LIME_CANDLE,   "Rare Crate Key",     "Open Rare crates for better rewards!",        500),
            new K(Material.ORANGE_CANDLE, "Epic Crate Key",     "Open Epic crates for great rewards!",         900),
            new K(Material.YELLOW_CANDLE, "Legendary Crate Key","Open Legendary crates for amazing rewards!", 1500),
            new K(Material.RED_CANDLE,    "Mythic Crate Key",   "Open Mythic crates for the rarest rewards!", 3000),
            new K(Material.TRIPWIRE_HOOK, "RuneMC Special Key", "Open the exclusive RuneMC Special crate!",   5000)
        );
        int[] slots = {10,11,12,13,14,15};
        for (int i = 0; i < keys.size(); i++) {
            K k = keys.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(k.m(), k.name(),
                k.desc(), "Buy Key", k.price(), null));
        }
        back(inv); return inv;
    }

    // ── 11. Glows ─────────────────────────────────────────────────────────────
    public Inventory buildGlows() {
        Inventory inv = make(CAT_GLOWS);
        record GL(Material m, String name, int price) {}
        List<GL> glows = List.of(
            new GL(Material.RED_DYE,        "Red Glow",        300),
            new GL(Material.ORANGE_DYE,     "Orange Glow",     300),
            new GL(Material.YELLOW_DYE,     "Yellow Glow",     300),
            new GL(Material.LIME_DYE,       "Lime Glow",       300),
            new GL(Material.GREEN_DYE,      "Green Glow",      300),
            new GL(Material.CYAN_DYE,       "Cyan Glow",       300),
            new GL(Material.LIGHT_BLUE_DYE, "Light Blue Glow", 300),
            new GL(Material.BLUE_DYE,       "Blue Glow",       300),
            new GL(Material.PURPLE_DYE,     "Purple Glow",     300),
            new GL(Material.PINK_DYE,       "Pink Glow",       300),
            new GL(Material.WHITE_DYE,      "White Glow",      300),
            new GL(Material.GRAY_DYE,       "Gray Glow",       300),
            new GL(Material.BLACK_DYE,      "Black Glow",      350),
            new GL(Material.NETHER_STAR,    "Rainbow Glow",    800),
            new GL(Material.AMETHYST_SHARD, "Crystal Glow",    600)
        );
        int[] slots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28};
        for (int i = 0; i < glows.size() && i < slots.length; i++) {
            GL g = glows.get(i);
            inv.setItem(slots[i], GuiUtil.shopItem(g.m(), g.name(),
                "Glow with " + g.name().replace(" Glow","") + " color!",
                "Buy Glow", g.price(), "Requires Glow plugin"));
        }
        back(inv); return inv;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Inventory make(String title) {
        Inventory inv = Bukkit.createInventory(null, 36,
            Component.text(title, NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        GuiUtil.fillAll(inv);
        return inv;
    }

    private void back(Inventory inv) {
        ItemStack b = new ItemStack(Material.ARROW);
        ItemMeta m  = b.getItemMeta();
        m.displayName(Component.text("\u2190 Back to Main Menu", NamedTextColor.RED)
            .decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        b.setItemMeta(m);
        inv.setItem(BACK_SLOT, b);
    }
}
