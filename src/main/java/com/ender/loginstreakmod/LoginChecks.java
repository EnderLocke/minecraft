package com.ender.loginstreakmod;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;

public class LoginChecks {

    public static void checkLogins(ServerPlayerEntity player, int totalLogins, int streakCount) {
        giveMilestoneReward(player, ConfigManager.getTotalLoginReward(totalLogins));
        giveMilestoneReward(player, ConfigManager.getStreakLoginReward(streakCount));
    }

    private static void giveMilestoneReward(ServerPlayerEntity player, MilestoneReward reward) {
        if (reward == null) return;

        // Send message
        if (reward.message != null) {
            player.sendMessage(Text.literal(reward.message), false);
        }

        // Give items
        for (MilestoneReward.ItemReward itemReward : reward.items) {
            Identifier itemId = Identifier.tryParse(itemReward.id);
            if (itemId == null) {
                System.err.println("Invalid item ID: " + itemReward.id);
                continue;
            }
            Item item = Registries.ITEM.get(itemId);
            if (item != null) {
                ItemStack stack = new ItemStack(item, itemReward.count);
                player.getInventory().insertStack(stack);
            } else {
                System.err.println("Unknown item ID: " + itemReward.id);
            }
        }
    }
}
