package com.ender.loginstreakmod;

import com.google.gson.*;
import java.util.List;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static final Path TOTAL_CONFIG_PATH = Path.of("config/totallogincheck_rewards.json");
    private static final Path STREAK_CONFIG_PATH = Path.of("config/streaklogincheck_rewards.json");

    private static final Map<Integer, MilestoneReward> totalLoginMilestones = new HashMap<>();
    private static final Map<Integer, MilestoneReward> streakLoginMilestones = new HashMap<>();

    public static void loadMilestones() {
        loadConfigFile(TOTAL_CONFIG_PATH, totalLoginMilestones);
        loadConfigFile(STREAK_CONFIG_PATH, streakLoginMilestones);
    }

    private static void loadConfigFile(Path path, Map<Integer, MilestoneReward> milestoneMap) {
        try (FileReader reader = new FileReader(path.toFile())) {
            JsonObject config = new Gson().fromJson(reader, JsonObject.class);
            JsonArray milestones = config.getAsJsonArray("milestones");

            for (JsonElement element : milestones) {
                JsonObject obj = element.getAsJsonObject();
                int count = obj.get("count").getAsInt();
                String message = obj.get("message").getAsString();

                List<MilestoneReward.ItemReward> itemRewards = new ArrayList<>();
                if (obj.has("items")) {
                    JsonArray items = obj.getAsJsonArray("items");
                    for (JsonElement itemElem : items) {
                        JsonObject itemObj = itemElem.getAsJsonObject();
                        String id = itemObj.get("id").getAsString();
                        int amount = itemObj.get("count").getAsInt();
                        itemRewards.add(new MilestoneReward.ItemReward(id, amount));
                    }
                }

                milestoneMap.put(count, new MilestoneReward(message, itemRewards));
            }

        } catch (IOException | JsonParseException e) {
            System.err.println("Failed to load config: " + path + " — " + e.getMessage());
        }
    }

    public static MilestoneReward getTotalLoginReward(int loginCount) {
        return totalLoginMilestones.get(loginCount);
    }

    public static MilestoneReward getStreakLoginReward(int streakCount) {
        return streakLoginMilestones.get(streakCount);
    }

    public static String getTotalLoginMessage(int loginCount) {
        MilestoneReward reward = totalLoginMilestones.get(loginCount);
        return reward != null ? reward.message : null;
    }

    public static String getStreakLoginMessage(int streakCount) {
        MilestoneReward reward = streakLoginMilestones.get(loginCount);
        return reward != null ? reward.message : null;
    }
}
