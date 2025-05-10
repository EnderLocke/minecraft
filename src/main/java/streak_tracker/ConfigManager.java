package loginstreakmod;

import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static final Path CONFIG_PATH = Path.of("config/loginstreakmod_rewards.json");
    private static final Map<Integer, String> milestoneMessages = new HashMap<>();

    public static void loadMilestones() {
        try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
            JsonObject config = new Gson().fromJson(reader, JsonObject.class);
            JsonArray milestones = config.getAsJsonArray("milestones");

            for (JsonElement element : milestones) {
                JsonObject milestone = element.getAsJsonObject();
                int count = milestone.get("count").getAsInt();
                String message = milestone.get("message").getAsString();
                milestoneMessages.put(count, message);
            }

        } catch (IOException | JsonParseException e) {
            System.err.println("Failed to load milestone config: " + e.getMessage());
        }
    }

    public static String getMilestoneMessage(int loginCount) {
        return milestoneMessages.getOrDefault(loginCount, null);
    }
}
