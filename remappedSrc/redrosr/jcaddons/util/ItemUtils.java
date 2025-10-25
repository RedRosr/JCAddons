package redrosr.jcaddons.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {
    public static List<Text> getLore(ItemStack stack) {
        if (stack.get(DataComponentTypes.LORE) == null) {
            return new ArrayList<>();
        }

        return stack.get(DataComponentTypes.LORE).lines();
    }

    public static String getItemRarity(ItemStack stack) {
        var lore = getLore(stack);
        if (lore == null) return null;

        for (Text line : lore) {
            if (line.getContent() instanceof TranslatableTextContent translatable) {
                String key = translatable.getKey();
                if (key.startsWith("justchunks.global.rarity.")) {
                    // Extract just "RARE" from "justchunks.global.rarity.RARE"
                    return key.substring("justchunks.global.rarity.".length());
                }
            }

            // Also check siblings — your logs show it's often in the `siblings` array
            for (Text sibling : line.getSiblings()) {
                if (sibling.getContent() instanceof TranslatableTextContent translatable) {
                    String key = translatable.getKey();
                    if (key.startsWith("justchunks.global.rarity.")) {
                        return key.substring("justchunks.global.rarity.".length());
                    }
                }
            }
        }

        return null; // Not found
    }
}
