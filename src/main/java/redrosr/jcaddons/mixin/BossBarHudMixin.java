package redrosr.jcaddons.mixin;

import org.spongepowered.asm.mixin.Unique;
import redrosr.jcaddons.JCAddons;
import redrosr.jcaddons.config.Config;
import redrosr.jcaddons.util.DungeonType;
import redrosr.jcaddons.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

    @Unique
    private boolean previousDungeonState = false;
    @Unique
    private long dungeonStateChangeTime = 0;
    @Unique
    private static final long DUNGEON_STATE_CHANGE_DELAY = 500; // 500 millisecond delay

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void filterBossBars(DrawContext context, CallbackInfo ci) {
        BossBarHud self = (BossBarHud)(Object)this;
        MinecraftClient client = JCAddons.minecraftClient;

        if (client == null) return;

        Profiler profiler = Profilers.get();
        profiler.push("bossHealth");

        int screenWidth = context.getScaledWindowWidth();
        int y = 12;

        boolean currentFrameDungeonMatch = false;

        // Check for "Already voted" in bossbar
        for (ClientBossBar bossBar : getBossBars(self).values()) {
            String name = bossBar.getName().getString();

            if (name.equals("Already voted? Use /vote!") && Config.get().RemoveAds) {
                continue;
            }

            // Check if player is in a dungeon
            for (DungeonType type : DungeonType.values()) {
                if (name.contains(type.getNameFallback())) {
                    currentFrameDungeonMatch = true;
                    break;
                }
            }

            int x = screenWidth / 2 - 91;
            ((BossBarHudInvoker) self).callRenderBossBar(context, x, y, bossBar);

            int textWidth = client.textRenderer.getWidth(bossBar.getName());
            int textX = screenWidth / 2 - textWidth / 2;
            int textY = y - 9;

            context.drawTextWithShadow(client.textRenderer, bossBar.getName(), textX, textY, 0xFFFFFF);

            y += 19;

            if (y >= context.getScaledWindowHeight() / 3) {
                break;
            }
        }

        long currentTime = System.currentTimeMillis();

        if (currentFrameDungeonMatch != previousDungeonState) {
            if (dungeonStateChangeTime == 0) {
                dungeonStateChangeTime = currentTime;
            }
            else if (currentTime - dungeonStateChangeTime > DUNGEON_STATE_CHANGE_DELAY) {
                Utils.inDungeon = currentFrameDungeonMatch;
                previousDungeonState = currentFrameDungeonMatch;
                dungeonStateChangeTime = 0; // Reset the timer
            }
        } else {
            dungeonStateChangeTime = 0;
        }

        if (!previousDungeonState && currentFrameDungeonMatch) {
            Utils.inDungeon = true;
            previousDungeonState = true;
        }

        if (Utils.inDungeon && JCAddons.potEsp.getPots() != 0) {
            // Render custom Bar for amount of pots left.
            int textWidth = client.textRenderer.getWidth("Pots left: " + JCAddons.potEsp.getPots());
            int textX = screenWidth / 2 - textWidth / 2;
            int textY = y - 9;
            context.drawTextWithShadow(client.textRenderer, "Pots left: " + JCAddons.potEsp.getPots(), textX, textY, 0xA8A8A8);
        }

        profiler.pop();
        ci.cancel();
    }

    @Unique
    @SuppressWarnings("unchecked")
    private Map<UUID, ClientBossBar> getBossBars(BossBarHud self) {
        return ((BossBarHudAccessor) self).getBossBars();
    }

}

