package redrosr.jcaddons.features.Cards;

import com.mojang.serialization.DataResult;
//import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
//import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
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
        JCAddons.LOGGER.info("Initializing CardDisplay 1");
        if (!initialized) {
            JCAddons.LOGGER.info("Initializing CardDisplay 2");
            HudElementRegistry.attachElementAfter(
                VanillaHudElements.STATUS_EFFECTS,
                CARD_TRACKER,
                (context, tickCounter) -> render(context)
            );
            initialized = true;
        }
    }
    public static void handleSlotClick(MinecraftClient client, int slotId, ScreenHandler handler, Text screenTitle) {
        JCAddons.LOGGER.info("handleSlotClick called - slotId: {}, screenTitle: {}", slotId, screenTitle.getString());

        if (!GuiUtils.isInventory(screenTitle, "justchunks.gui.dungeon.selectBuff.")) {
            JCAddons.LOGGER.info("Not a dungeon buff selection screen, returning");
            return;
        }

        if (slotId < 0 || slotId >= handler.slots.size()) {
            JCAddons.LOGGER.info("Invalid slot ID: {} (handler has {} slots)", slotId, handler.slots.size());
            return;
        }

        Slot slot = handler.slots.get(slotId);
        ItemStack stack = slot.getStack();

        if (stack.isEmpty()) {
            JCAddons.LOGGER.info("Clicked slot {} is empty", slotId);
            return;
        }

        Text cardNameFormatted = stack.getFormattedName();
        List<Text> cardLore = ItemUtils.getLore(stack);
        String cardRarityString = ItemUtils.getItemRarity(stack);

        JCAddons.LOGGER.info("Processing card - Name: {}, Rarity: {}", cardNameFormatted.getString(), cardRarityString);

        if (client.player != null) {
            CardRarity cardRarity = null;
            try {
                cardRarity = CardRarity.valueOf(cardRarityString);
                JCAddons.LOGGER.info("Successfully parsed card rarity: {}", cardRarity);
            } catch (IllegalArgumentException e) {
                // Handle case when rarity string doesn't match any enum value
                JCAddons.LOGGER.warn("Unknown card rarity: " + cardRarityString);
            }

            CardDisplay.addPickedCard(cardNameFormatted, cardRarity);
            JCAddons.LOGGER.info("Added card to display: {} ({})", cardNameFormatted.getString(), cardRarity);
        } else {
            JCAddons.LOGGER.warn("Client player is null, cannot process card");
        }
    }
//    public static void handleSlotClick(MinecraftClient client, int slotId, ScreenHandler handler, Text screenTitle) {
//        if (!GuiUtils.isInventory(screenTitle, "justchunks.gui.dungeon.selectBuff.")) return;
//        if (slotId < 0 || slotId >= handler.slots.size()) return;
//
//        Slot slot = handler.slots.get(slotId);
//        ItemStack stack = slot.getStack();
//
//        if (stack.isEmpty()) return;
//
//        Text cardNameFormatted = stack.getFormattedName();
//        List<Text> cardLore = ItemUtils.getLore(stack);
//        String cardRarityString = ItemUtils.getItemRarity(stack);
//
//        if (client.player != null) {
//            CardRarity cardRarity = null;
//            try {
//                cardRarity = CardRarity.valueOf(cardRarityString);
//            } catch (IllegalArgumentException e) {
//                // Handle case when rarity string doesn't match any enum value
//                JCAddons.LOGGER.warn("Unknown card rarity: " + cardRarityString);
//            }
//
//            CardDisplay.addPickedCard(cardNameFormatted, cardRarity);
//        }
//    }

    public static void addPickedCard(Text cardName, CardRarity rarity) {
        cardTracker.addCard(cardName, rarity);
    }

    public static void resetCards() {
        cardTracker.reset();
    }

    private void render(DrawContext drawContext) {
        if (client.world == null || client.player == null) {
            JCAddons.LOGGER.info("Skipping render: world or player is null");
            return;
        }
        if (!Config.get().PickedCardsDisplay) {
            JCAddons.LOGGER.info("Skipping render: PickedCardsDisplay is disabled");
            return;
        }

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
        drawContext.drawText(client.textRenderer, header, startX - headerWidth, startY, Colors.WHITE, true);

        // Render each card name with count
        int yOffset = startY + 12;
        for (Map.Entry<Text, CardTracker.CardEntry> entry : cardList) {
            Text cardName = entry.getKey();
            int count = entry.getValue().count;

            Text displayText = count > 1
                ? Text.empty().append(cardName).append(" x" + count)
                : cardName;


            int textWidth = client.textRenderer.getWidth(displayText);
            drawContext.drawText(client.textRenderer, displayText, startX - textWidth, yOffset, Colors.WHITE, true);
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
                int color = rarity.getColor();
                styledName = Text.literal(cardName.getString()).setStyle(
                    Style.EMPTY.withColor(color).withItalic(false)
                );
            } else {
                styledName = Text.literal(styledName.toString()).setStyle(Style.EMPTY.withItalic(false));
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