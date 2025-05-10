package loginstreakmod;

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

        try {
            if (!Files.exists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }

            JsonObject data = new JsonObject();
            if (Files.exists(playerFile)) {
                data = JsonParser.parseReader(Files.newBufferedReader(playerFile)).getAsJsonObject();

                LocalDate lastLogin = LocalDate.parse(data.get("lastLogin").getAsString());
                int streak = data.get("streak").getAsInt();

                if (lastLogin.plusDays(1).isEqual(today)) {
                    newStreak = streak + 1;
                } else if (lastLogin.isEqual(today)) {
                    newStreak = streak; // already logged in today
                }
                // if not consecutive, it resets to 1
            }

            data.addProperty("lastLogin", today.toString());
            data.addProperty("streak", newStreak);
            Files.writeString(playerFile, GSON.toJson(data));

            player.sendMessage(Text.literal("📅 Login Streak: " + newStreak + " day(s)!"), false);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(Text.literal("⚠ Failed to track login streak."), false);
        }
    }
}
