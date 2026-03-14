package dev.shardsystem.managers;

import dev.shardsystem.ShardSystem;
import org.bukkit.Bukkit;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DiscordLogger {

    private final ShardSystem plugin;
    private static final int GREEN  = 0x57F287;
    private static final int RED    = 0xED4245;
    private static final int BLUE   = 0x5865F2;
    private static final int YELLOW = 0xFEE75C;

    private static final DateTimeFormatter GERMAN_TIME = DateTimeFormatter
        .ofPattern("dd.MM.yyyy HH:mm:ss", Locale.GERMANY)
        .withZone(ZoneId.of("Europe/Berlin"));

    public DiscordLogger(ShardSystem plugin) { this.plugin = plugin; }

    public void logShardsGiven(String giver, String target, int amount, int newBal) {
        String time = GERMAN_TIME.format(Instant.now()) + " (CET/CEST)";
        String json = "{\"embeds\":[{"
            + "\"title\":\"\\uD83D\\uDC8E Shards Given\","
            + "\"color\":" + GREEN + ","
            + "\"fields\":["
            + f("Recipient",       "**" + esc(target) + "**",                        true)  + ","
            + f("Given by",        "**" + esc(giver) + "**",                         true)  + ","
            + f("Amount",          "**" + String.format("%,d", amount) + " Shards**",true)  + ","
            + f("New Balance",     "**" + String.format("%,d", newBal) + " Shards**",true)  + ","
            + f("Time (German)",   "**" + esc(time) + "**",                          false)
            + "],"
            + "\"footer\":{\"text\":\"ShardSystem v3 \\u2022 RuneMC\"},"
            + "\"timestamp\":\"" + Instant.now() + "\""
            + "}]}";
        post(json);
    }

    public void logPurchase(String player, String category, String item, int price, int remaining) {
        String time = GERMAN_TIME.format(Instant.now()) + " (CET/CEST)";
        String json = "{\"embeds\":[{"
            + "\"title\":\"\\uD83D\\uDED2 Shop Purchase\","
            + "\"color\":" + GREEN + ","
            + "\"fields\":["
            + f("Player",        "**" + esc(player) + "**",                         true)  + ","
            + f("Category",      "**" + esc(category) + "**",                       true)  + ","
            + f("Item",          "**" + esc(item) + "**",                           false) + ","
            + f("Cost",          "**" + String.format("%,d", price) + " Shards**",     true)  + ","
            + f("Balance After", "**" + String.format("%,d", remaining) + " Shards**", true)  + ","
            + f("Time (German)", "**" + esc(time) + "**",                           false)
            + "],"
            + "\"footer\":{\"text\":\"ShardSystem v3 \\u2022 RuneMC\"},"
            + "\"timestamp\":\"" + Instant.now() + "\""
            + "}]}";
        post(json);
    }

    public void logAdminAdded(String exec, String target) {
        simple(BLUE, "Shard Admin Added",
            "**" + esc(target) + "** added by **" + esc(exec) + "**");
    }
    public void logAdminRemoved(String exec, String target) {
        simple(RED, "Shard Admin Removed",
            "**" + esc(target) + "** removed by **" + esc(exec) + "**");
    }
    public void logShopItemAdded(String admin, String item, int price) {
        simple(YELLOW, "Shop Item Added",
            "**" + esc(admin) + "** added **" + esc(item) + "** for **" + price + " Shards**");
    }
    public void logShopItemRemoved(String admin, String item) {
        simple(RED, "Shop Item Removed",
            "**" + esc(admin) + "** removed **" + esc(item) + "**");
    }

    private String f(String name, String value, boolean inline) {
        return "{\"name\":\"" + esc(name) + "\",\"value\":\"" + esc(value) + "\",\"inline\":" + inline + "}";
    }

    private void simple(int color, String title, String desc) {
        String json = "{\"embeds\":[{"
            + "\"title\":\"" + esc(title) + "\","
            + "\"description\":\"" + esc(desc) + "\","
            + "\"color\":" + color + ","
            + "\"timestamp\":\"" + Instant.now() + "\","
            + "\"footer\":{\"text\":\"ShardSystem v3 \\u2022 RuneMC\"}"
            + "}]}";
        post(json);
    }

    private void post(String json) {
        String url = plugin.getConfig().getString("discord-webhook-url", "");
        if (url.isBlank() || url.contains("YOUR_")) return;
        final String fu = url, fj = json;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(fu).openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-Type", "application/json");
                c.setRequestProperty("User-Agent", "ShardSystem");
                c.setDoOutput(true); c.setConnectTimeout(5000); c.setReadTimeout(5000);
                try (OutputStream os = c.getOutputStream()) { os.write(fj.getBytes(StandardCharsets.UTF_8)); }
                c.getResponseCode(); c.disconnect();
            } catch (Exception e) { plugin.getLogger().warning("[Discord] " + e.getMessage()); }
        });
    }

    private String esc(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n");
    }
}
