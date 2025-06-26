package redrosr.jcaddons.util;

import com.mojang.serialization.DataResult;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.ColorHelper;

public enum CardRarity {
    COMMON("Common", 0xFFBAC4D1),
    UNCOMMON("Uncommon", 0xFF2A8B39),
    RARE("Rare", 0xFF09679F),
    EPIC("Epic", 0xFF832BC9),
    LEGENDARY("Legendary", 0xFFEBB621);

    private final String nameFallback;
    private final int color;

    CardRarity(String nameFallback, int color) {
        this.nameFallback = nameFallback;
        this.color = color;
    }

    public String getNameFallback() {
        return nameFallback;
    }

    public int getColor() {
        return color;
    }
}
