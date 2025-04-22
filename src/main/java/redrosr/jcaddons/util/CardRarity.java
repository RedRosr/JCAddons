package redrosr.jcaddons.util;

import com.mojang.serialization.DataResult;
import net.minecraft.text.TextColor;

public enum CardRarity {
    COMMON("Common", TextColor.parse("#BAC4D1")), // gedaan
    UNCOMMON("Uncommon", TextColor.parse("#2A8B39")),
    RARE("Rare", TextColor.parse("#09679F")),
    EPIC("Epic", TextColor.parse("#832BC9")),
    LEGENDARY("Legendary", TextColor.parse("#EBB621"));

    private final String nameFallback;
    private final DataResult<TextColor> color;

    CardRarity(String nameFallback, DataResult<TextColor> color) {
        this.nameFallback = nameFallback;
        this.color = color;
    }

    public String getNameFallback() {
        return nameFallback;
    }

    public DataResult<TextColor> getColor() {
        return color;
    }
}
