package loginstreakmod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

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
            Identifier itemId = new Identifier(itemReward.id);
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
