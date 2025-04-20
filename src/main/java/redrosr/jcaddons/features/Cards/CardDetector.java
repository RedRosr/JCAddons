package redrosr.jcaddons.features.Cards;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import redrosr.jcaddons.JCAddons;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

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
        if (currentScreen instanceof GenericContainerScreen && currentScreen != lastScreen) {
            GenericContainerScreen containerScreen = (GenericContainerScreen) currentScreen;
            Text title = containerScreen.getTitle();


            isCardInventory = isCardSelectionInventory(title);
            JCAddons.LOGGER.info("isCardSelectionInventory: {}", isCardInventory);

        }

        lastScreen = currentScreen;
    }

    private boolean isCardSelectionInventory(Text title) {
        // Flatten all children/siblings recursively
        List<Text> flatText = flattenText(title);
        JCAddons.LOGGER.info("Flat text: " + flatText);
        for (Text t : flatText) {
            if (t.getContent() instanceof TranslatableTextContent translatable) {
                String key = translatable.getKey();
                if (key.startsWith("justchunks.gui.dungeon.selectBuff.")) {

                    return true;
                }
            }
        }
        return false;
    }

    private List<Text> flattenText(Text root) {
        List<Text> result = new ArrayList<>();
        Deque<Text> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Text current = queue.poll();
            result.add(current);
            queue.addAll(current.getSiblings());
        }

        return result;
    }

}