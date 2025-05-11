package com.ender.loginstreakmod;

import com.google.gson.*;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    // Updated paths to be used as resources
    private static final String TOTAL_CONFIG_PATH = "/config/totallogincheck_rewards.json";
    private static final String STREAK_CONFIG_PATH = "/config/streaklogincheck_rewards.json";

    private static final Map<Integer, MilestoneReward> totalLoginMilestones = new HashMap<>();
    private static final Map<Integer, MilestoneReward> streakLoginMilestones = new HashMap<>();

    // Load milestones from resources
    public static void loadMilestones() {
        loadConfigFile(TOTAL_CONFIG_PATH, totalLoginMilestones);
        loadConfigFile(STREAK_CONFIG_PATH, streakLoginMilestones);
    }

    // Load configuration file from resources
    private static void loadConfigFile(String resourcePath, Map<Integer, MilestoneReward> milestoneMap) {
        try (InputStream inputStream = ConfigManager.class.getResourceAsStream(resourcePath);
             InputStreamReader reader = new InputStreamReader(inputStream)) {

            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            JsonObject config = new Gson().fromJson(reader, JsonObject.class);
            JsonArray milestones = config.getAsJsonArray("milestones");

            // Parse each milestone and store it in the map
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
            System.err.println("Failed to load config: " + resourcePath + " — " + e.getMessage());
        }
    }

    // Retrieve milestone reward for total logins
    public static MilestoneReward getTotalLoginReward(int loginCount) {
        return totalLoginMilestones.get(loginCount);
    }

    // Retrieve milestone reward for streak logins
    public static MilestoneReward getStreakLoginReward(int streakCount) {
        return streakLoginMilestones.get(streakCount);
    }

    // Get message for total login milestone
    public static String getTotalLoginMessage(int loginCount) {
        MilestoneReward reward = totalLoginMilestones.get(loginCount);
        return reward != null ? reward.message : null;
    }

    // Get message for streak login milestone
    public static String getStreakLoginMessage(int streakCount) {
        MilestoneReward reward = streakLoginMilestones.get(streakCount);
        return reward != null ? reward.message : null;
    }
}