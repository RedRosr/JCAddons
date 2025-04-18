package freck.chunksaddons.features.Pots;

import freck.chunksaddons.ChunksAddons;
import freck.chunksaddons.Config.Config;
import freck.chunksaddons.util.ActionBarUtils;
import freck.chunksaddons.util.ChatUtils;
import freck.chunksaddons.util.Utils;
import net.minecraft.client.MinecraftClient;

public class PotActionBar {

    private final MinecraftClient client;

    public PotActionBar(MinecraftClient mc) {
        client = mc;
    }
    public void onUpdate() {
        if (client.world == null || client.player == null || !Config.get().PotsWarningActionBar) return;

        int potsLeft = ChunksAddons.potEsp.getPots();
        if (potsLeft >= 1 && Utils.inDungeon){
            ActionBarUtils.sendActionBar(ChatUtils.formatText("&kWW &4&l!! &r&cDon't Forget The Pots &4&l!! &r&kWW"));
        }
    }
}
