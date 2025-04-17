package freck.chunksaddons;

import com.mojang.blaze3d.systems.RenderSystem;
import freck.chunksaddons.features.PotEsp;
import freck.chunksaddons.util.RegionPos;
import freck.chunksaddons.util.RenderUtils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChunksAddons implements ModInitializer {
	public static final String MOD_ID = "chunksaddons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftClient minecraftClient;

	private static PotEsp potEsp;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing ChunksAddons");
		minecraftClient = MinecraftClient.getInstance();

		potEsp = new PotEsp(minecraftClient);
	}

	public static void onTick() {
		if (minecraftClient.player == null || minecraftClient.world == null) return;

		potEsp.onUpdate();
	}

	public static void onRender(MatrixStack matrixStack, float renderTickCounter) {
		potEsp.onRender(matrixStack, renderTickCounter);
	}
}