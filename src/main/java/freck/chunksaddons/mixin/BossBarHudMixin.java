package freck.chunksaddons.mixin;

import freck.chunksaddons.ChunksAddons;
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

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void filterBossBars(DrawContext context, CallbackInfo ci) {
        BossBarHud self = (BossBarHud)(Object)this;
        MinecraftClient client = ChunksAddons.minecraftClient;

        if (client == null) return;

        Profiler profiler = Profilers.get();
        profiler.push("bossHealth");

        int screenWidth = context.getScaledWindowWidth();
        int y = 12;

        for (ClientBossBar bossBar : getBossBars(self).values()) {
            String name = bossBar.getName().getString();

            if (name.equals("Already voted? Use /vote!")) {
                continue;
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

        profiler.pop();
        ci.cancel();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ClientBossBar> getBossBars(BossBarHud self) {
        return ((BossBarHudAccessor) self).getBossBars();
    }

}

