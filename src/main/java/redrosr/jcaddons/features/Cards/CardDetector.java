package redrosr.jcaddons.features.Cards;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.text.Text;
import redrosr.jcaddons.JCAddons;
import redrosr.jcaddons.util.GuiUtils;

public class CardDetector {
    private final MinecraftClient client;
    private Screen lastScreen = null;
    private boolean isCardInventory = false;

    public CardDetector(MinecraftClient mc) {
        client = mc;
    }

    public void onUpdate() {
        if (client.world == null || client.player == null) return;

        Screen currentScreen = client.currentScreen;

        // If screen changed to container screen, check if it might be a card inventory
        if (currentScreen instanceof GenericContainerScreen containerScreen && currentScreen != lastScreen) {
            Text title = containerScreen.getTitle();


            isCardInventory = GuiUtils.isInventory(title, "justchunks.gui.dungeon.selectBuff.");
            JCAddons.LOGGER.info("isCardSelectionInventory: {}", isCardInventory);

        }

        lastScreen = currentScreen;
    }


}