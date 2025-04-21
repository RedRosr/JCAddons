package redrosr.jcaddons.features.Pots;

import net.minecraft.client.MinecraftClient;
import redrosr.jcaddons.JCAddons;
import redrosr.jcaddons.config.Config;
import redrosr.jcaddons.util.ActionBarUtils;
import redrosr.jcaddons.util.ChatUtils;
import redrosr.jcaddons.util.Utils;

public class PotActionBar {

    private final MinecraftClient client;

    public PotActionBar(MinecraftClient mc) {
        client = mc;
    }
    public void onUpdate() {
        if (client.world == null || client.player == null || !Config.get().PotsWarningActionBar) return;

        int potsLeft = JCAddons.potEsp.getPots();
        if (potsLeft >= 1 && Utils.inDungeon){
            ActionBarUtils.sendActionBar(ChatUtils.formatText("&kWW &4&l!! &r&cDon't Forget The Pots &4&l!! &r&kWW"));
        }
    }
}
