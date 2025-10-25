package redrosr.jcaddons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redrosr.jcaddons.commands.JCAddonsCommand;
import redrosr.jcaddons.config.Config;
import redrosr.jcaddons.features.Cards.CardDisplay;
import redrosr.jcaddons.features.Pots.PotActionBar;
import redrosr.jcaddons.features.Pots.PotEsp;


public class JCAddons implements ClientModInitializer {
    public static final String MOD_ID = "jcaddons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static MinecraftClient minecraftClient;

    public static PotEsp potEsp;
    public static PotActionBar potActionBar;
    public static CardDisplay cardDisplay;
    public static JCAddonsCommand JCAddonsCommand;

    public static void onTick() {
        if (minecraftClient.player == null || minecraftClient.world == null) return;

        potEsp.onUpdate();
        potActionBar.onUpdate();
    }

    public static void onRender(MatrixStack matrixStack, float renderTickCounter) {
        potEsp.onRender(matrixStack, renderTickCounter);
    }


    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing JCAddons");
        minecraftClient = MinecraftClient.getInstance();

        WorldRenderEvents.BEFORE_ENTITIES.register((context) -> {
            MatrixStack matrixStack = context.matrixStack();
            float tickDelta = context.tickCounter().getDynamicDeltaTicks();
            JCAddons.onRender(matrixStack, tickDelta);
        });

        Config.HANDLER.load();

        potEsp = new PotEsp(minecraftClient);
        potActionBar = new PotActionBar(minecraftClient);
        cardDisplay = new CardDisplay(minecraftClient);
        JCAddonsCommand  = new JCAddonsCommand();

    }
}