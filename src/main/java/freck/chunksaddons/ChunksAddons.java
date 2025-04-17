package freck.chunksaddons;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunksAddons implements ModInitializer {
	public static final String MOD_ID = "chunksaddons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftClient minecraftClient;


	@Override
	public void onInitialize() {
		LOGGER.info("Initializing ChunksAddons");
		minecraftClient = MinecraftClient.getInstance();
	}

	public static void onTick() {

	}
}