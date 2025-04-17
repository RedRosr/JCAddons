package freck.chunksaddons.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ActionBarUtils {
    public static void sendActionBar(String message) {
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal(message), false);
    }
}
