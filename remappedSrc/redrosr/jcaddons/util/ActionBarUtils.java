package redrosr.jcaddons.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ActionBarUtils {
    public static void sendActionBar(Text message) {
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(message, false);
    }
}
