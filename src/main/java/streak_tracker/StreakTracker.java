package com.ender.loginstreakmod;

import loginstreakmod.LoginChecks;

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

        try {
            if (!Files.exists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }

            JsonObject data = new JsonObject();
            if (Files.exists(playerFile)) {
                data = JsonParser.parseReader(Files.newBufferedReader(playerFile)).getAsJsonObject();

                LocalDate lastLogin = LocalDate.parse(data.get("lastLogin").getAsString());
                int previousStreak = data.get("streak").getAsInt();
                totalLogins = data.get("totalLogins").getAsInt() + 1;

                if (lastLogin.plusDays(1).isEqual(today)) {
                    newStreak = previousStreak + 1;
                } else if (lastLogin.isEqual(today)) {
                    newStreak = previousStreak; // same day, don't increase
                    totalLogins--; // cancel out the increment
                }
                // otherwise, reset streak to 1
            }

            data.addProperty("lastLogin", today.toString());
            data.addProperty("streak", newStreak);
            data.addProperty("totalLogins", totalLogins);
            Files.writeString(playerFile, GSON.toJson(data));

            player.sendMessage(Text.literal("📅 Login Streak: " + newStreak + " day(s)!"), false);
            player.sendMessage(Text.literal("🧮 Total Logins: " + totalLogins), false);

            LoginChecks.checkMilestoneLogin(player, totalLogins);

        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(Text.literal("⚠ Failed to track login data."), false);
        }
    }
}
