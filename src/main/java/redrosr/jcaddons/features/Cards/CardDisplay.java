package redrosr.jcaddons.features.Cards;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import redrosr.jcaddons.JCAddons;
import redrosr.jcaddons.config.Config;

import java.util.*;

public class CardDisplay {

    private final MinecraftClient client;
    private static final CardTracker cardTracker = new CardTracker();
    private static final Identifier CARD_TRACKER = Identifier.of(JCAddons.MOD_ID, "card_display");
    private static boolean initialized = false;


    public CardDisplay(MinecraftClient mc) {
        client = mc;

        if (!initialized) {
            HudLayerRegistrationCallback.EVENT.register(d ->
                d.attachLayerAfter(IdentifiedLayer.STATUS_EFFECTS, CARD_TRACKER,
                    (context, tickCounter) -> render(context))
            );
            initialized = true;
        }
    }

    private void render(DrawContext drawContext) {
        if (client.world == null || client.player == null) return;
        if (!Config.get().testText) return;

        Map<String, Integer> pickedCards = cardTracker.getPickedCards();

        // Nothing to render
        if (pickedCards.isEmpty()) {
            return;
        }

        // Convert to list for potential sorting later
        List<Map.Entry<String, Integer>> cardList = new ArrayList<>(pickedCards.entrySet());

        int screenWidth = client.getWindow().getScaledWidth();

        // Starting position (top right corner with some padding)
        int startX = screenWidth - 10;
        int startY = 10;

        // Render header
        String header = "Cards Collected:";
        int headerWidth = client.textRenderer.getWidth(header);
        drawContext.drawText(client.textRenderer, header, startX - headerWidth, startY, 0xFFFFFF, true);

        // Render each card name with count
        int yOffset = startY + 12;
        for (Map.Entry<String, Integer> entry : cardList) {
            String cardName = entry.getKey();
            int count = entry.getValue();

            String displayText = count > 1 ? cardName + " x" + count : cardName;

            int textWidth = client.textRenderer.getWidth(displayText);
            drawContext.drawText(client.textRenderer, displayText, startX - textWidth, yOffset, 0xFFFFFF, true);
            yOffset += 10; // Move down for the next card name
        }
    }

    public static void addPickedCard(String cardName) {
        cardTracker.addCard(cardName);
    }

    public static void resetCards() {
        cardTracker.reset();
    }

    public static void addTestData() {
        resetCards();

        addPickedCard("Strength Card");
        addPickedCard("Strength Card");

        addPickedCard("Speed Card");

        addPickedCard("Defense Card");
        addPickedCard("Defense Card");
        addPickedCard("Defense Card");

        addPickedCard("Healing Card");
        addPickedCard("Damage Card");
    }

    private static class CardTracker {
        // Using HashMap to store card names and counts
        private final Map<String, Integer> pickedCards = new HashMap<>();

        public void addCard(String cardName) {
            // Increment card count or add new card with count 1
            pickedCards.put(cardName, pickedCards.getOrDefault(cardName, 0) + 1);
        }

        public Map<String, Integer> getPickedCards() {
            // Return a copy to prevent modification
            return new HashMap<>(pickedCards);
        }

        public void reset() {
            pickedCards.clear();
        }
    }
}