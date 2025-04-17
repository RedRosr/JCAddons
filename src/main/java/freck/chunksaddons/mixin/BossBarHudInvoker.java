package freck.chunksaddons.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.entity.boss.BossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BossBarHud.class)
public interface BossBarHudInvoker {
    @Invoker("renderBossBar")
    void callRenderBossBar(DrawContext context, int x, int y, BossBar bossBar);
}
