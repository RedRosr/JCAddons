package redrosr.jcaddons.features.Cards;

import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import redrosr.jcaddons.JCAddons;
import redrosr.jcaddons.config.Config;
import redrosr.jcaddons.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardDisplay {

    private static final CardTracker cardTracker = new CardTracker();
    private static final Identifier CARD_TRACKER = Identifier.of(JCAddons.MOD_ID, "card_display");
    private static boolean initialized = false;
    private final MinecraftClient client;
    private boolean wasInDungeon = false;


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

    public static void handleSlotClick(MinecraftClient client, int slotId, ScreenHandler handler, Text screenTitle) {
        if (!GuiUtils.isInventory(screenTitle, "justchunks.gui.dungeon.selectBuff.")) return;
        if (slotId < 0 || slotId >= handler.slots.size()) return;

        Slot slot = handler.slots.get(slotId);
        ItemStack stack = slot.getStack();

        if (stack.isEmpty()) return;

        Text cardNameFormatted = stack.getFormattedName();
        List<Text> cardLore = ItemUtils.getLore(stack);
        String cardRarityString = ItemUtils.getItemRarity(stack);

        if (client.player != null) {
            CardRarity cardRarity = null;
            try {
                cardRarity = CardRarity.valueOf(cardRarityString);
            } catch (IllegalArgumentException e) {
                // Handle case when rarity string doesn't match any enum value
                JCAddons.LOGGER.warn("Unknown card rarity: " + cardRarityString);
            }

            CardDisplay.addPickedCard(cardNameFormatted, cardRarity);
        }
    }

    public static void addPickedCard(Text cardName, CardRarity rarity) {
        cardTracker.addCard(cardName, rarity);
    }

    public static void resetCards() {
        cardTracker.reset();
    }

    private void render(DrawContext drawContext) {
        if (client.world == null || client.player == null || !Config.get().PickedCardsDisplay) return;

        Map<Text, CardTracker.CardEntry> pickedCards = cardTracker.getPickedCards();
        if (pickedCards.isEmpty()) return;

        // Convert to list for sorting
        List<Map.Entry<Text, CardTracker.CardEntry>> cardList = new ArrayList<>(pickedCards.entrySet());

        // Sort by rarity (Legendary -> Common)
        cardList.sort((entry1, entry2) -> {
            CardRarity rarity1 = entry1.getValue().rarity;
            CardRarity rarity2 = entry2.getValue().rarity;

            // Handle null rarities (put them at the end)
            if (rarity1 == null && rarity2 == null) return 0;
            if (rarity1 == null) return 1;
            if (rarity2 == null) return -1;

            // Reverse order (higher ordinal = higher rarity)
            return Integer.compare(rarity2.ordinal(), rarity1.ordinal());
        });

        int screenWidth = client.getWindow().getScaledWidth();

        // Starting position (top right corner with some padding)
        int startX = screenWidth - 10;
        int startY = 10;

        // Render header
        Text header = ChatUtils.formatText("&lCards Collected:");
        int headerWidth = client.textRenderer.getWidth(header);
        drawContext.drawText(client.textRenderer, header, startX - headerWidth, startY, 0xFFFFFF, true);

        // Render each card name with count
        int yOffset = startY + 12;
        for (Map.Entry<Text, CardTracker.CardEntry> entry : cardList) {
            Text cardName = entry.getKey();
            int count = entry.getValue().count;

            Text displayText = count > 1
                ? Text.empty().append(cardName).append(" x" + count)
                : cardName;


            int textWidth = client.textRenderer.getWidth(displayText);
            drawContext.drawText(client.textRenderer, displayText, startX - textWidth, yOffset, 0xFFFFFF, true);
            yOffset += 10; // Move down for the next card name
        }

        boolean inDungeon = Utils.inDungeon;
        if (wasInDungeon && !inDungeon && !pickedCards.isEmpty()) {
            resetCards();
        }
        wasInDungeon = inDungeon;
    }

    private static class CardTracker {
        // Using HashMap to store card names, counts, and rarities
        private final Map<Text, CardEntry> pickedCards = new HashMap<>();

        public void addCard(Text cardName, CardRarity rarity) {
            Text styledName = cardName;

            // Apply the color from the rarity
            if (rarity != null) {
                DataResult<TextColor> colorResult = rarity.getColor();
                if (colorResult.result().isPresent()) {
                    TextColor color = colorResult.result().get();
                    styledName = Text.literal(cardName.getString()).setStyle(
                        cardName.getStyle().withColor(color).withItalic(false)
                    );
                }
            }

            CardEntry entry = pickedCards.getOrDefault(styledName, new CardEntry(0, rarity));
            entry.count++;
            pickedCards.put(styledName, entry);
        }

        public final Map<Text, CardEntry> getPickedCards() {
            return new HashMap<>(pickedCards);
        }

        public void reset() {
            pickedCards.clear();
        }

        // Helper class to store both count and rarity
        private static class CardEntry {
            int count;
            CardRarity rarity;

            CardEntry(int count, CardRarity rarity) {
                this.count = count;
                this.rarity = rarity;
            }
        }
    }
}