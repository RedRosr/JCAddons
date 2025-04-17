package freck.chunksaddons;

import freck.chunksaddons.features.Pots.PotActionBar;
import freck.chunksaddons.features.Pots.PotEsp;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunksAddons implements ModInitializer {
	public static final String MOD_ID = "chunksaddons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftClient minecraftClient;

	public static PotEsp potEsp;
	public static PotActionBar potActionBar;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing ChunksAddons");
		minecraftClient = MinecraftClient.getInstance();

		potEsp = new PotEsp(minecraftClient);
		potActionBar = new PotActionBar(minecraftClient);
	}

	public static void onTick() {
		if (minecraftClient.player == null || minecraftClient.world == null) return;
		potEsp.onUpdate();
		potActionBar.onUpdate();
	}

	public static void onRender(MatrixStack matrixStack, float renderTickCounter) {
		potEsp.onRender(matrixStack, renderTickCounter);
	}
}