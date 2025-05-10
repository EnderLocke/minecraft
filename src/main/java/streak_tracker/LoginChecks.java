package loginstreakmod;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class LoginChecks {

    public static void checkLogins(ServerPlayerEntity player, int totalLogins) {
        String message = ConfigManager.getMilestoneMessage(totalLogins);
        if (message != null) {
            player.sendMessage(Text.literal(message), false);
            // Later: reward logic here
        }
    }

}
