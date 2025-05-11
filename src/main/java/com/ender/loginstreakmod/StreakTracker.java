package com.ender.loginstreakmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

public class StreakTracker {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path DATA_DIR = FabricLoader.getInstance().getGameDir().resolve("streaks");

    public static void onPlayerJoin(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        Path playerFile = DATA_DIR.resolve(uuid.toString() + ".json");

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        int newStreak = 1;
        int totalLogins = 1;
        boolean isNewLoginToday = true;

        try {
            if (!Files.exists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }

            JsonObject data = new JsonObject();
            boolean isNew = true;

            if (Files.exists(playerFile)) {
                data = JsonParser.parseReader(Files.newBufferedReader(playerFile)).getAsJsonObject();
                isNew = false;

                LocalDate lastLogin = LocalDate.parse(data.get("lastLogin").getAsString());
                int previousStreak = data.get("streak").getAsInt();
                totalLogins = data.get("totalLogins").getAsInt();

                if (lastLogin.plusDays(1).isEqual(today)) {
                    newStreak = previousStreak + 1;
                    totalLogins += 1;
                    isNewLoginToday = true;
                } else if (lastLogin.isEqual(today)) {
                    newStreak = previousStreak;
                    isNewLoginToday = false; // already logged in today
                } else {
                    newStreak = 1;
                    totalLogins += 1;
                    isNewLoginToday = true;
                }
            }

            data.addProperty("lastLogin", today.toString());
            data.addProperty("streak", newStreak);
            data.addProperty("totalLogins", totalLogins);

            if (isNew || !data.has("firstLogin")) {
                data.addProperty("firstLogin", today.toString());
            }

            Files.writeString(playerFile, GSON.toJson(data));

            player.sendMessage(Text.literal("📅 Login Streak: " + newStreak + " day(s)!"), false);
            player.sendMessage(Text.literal("🧮 Total Logins: " + totalLogins), false);
            if (isNew) {
                player.sendMessage(Text.literal("🎉 First login recorded: " + today), false);
            }

            // Only give rewards once per new day
            if (isNewLoginToday) {
                LoginChecks.checkLogins(player, totalLogins, newStreak);
            }

        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(Text.literal("⚠ Failed to track login data."), false);
        }
    }
}