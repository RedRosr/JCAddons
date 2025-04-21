package redrosr.jcaddons.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redrosr.jcaddons.JCAddons;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method="tick", at=@At("HEAD"))
    private void onTick(CallbackInfo ci) {
        JCAddons.onTick();
    }

}
