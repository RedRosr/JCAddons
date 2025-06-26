package redrosr.jcaddons.util;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import redrosr.jcaddons.JCAddons;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;

public class RenderUtils {

    public static final RenderPipeline ESP_QUADS_PIPELINE = RenderPipelines
            .register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation(Identifier.of("wurst:pipeline/wurst_esp_quads"))
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).build());

    public static final RenderPipeline QUADS_PIPELINE = RenderPipelines
            .register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation(Identifier.of("wurst:pipeline/wurst_quads"))
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .build());

    public static final RenderLayer.MultiPhase QUADS = RenderLayer.of(
            "wurst:quads", 1536, false, true, QUADS_PIPELINE,
            RenderLayer.MultiPhaseParameters.builder().build(false));

    public static final RenderLayer.MultiPhase ESP_QUADS = RenderLayer.of(
            "wurst:esp_quads", 1536, false, true, ESP_QUADS_PIPELINE,
            RenderLayer.MultiPhaseParameters.builder().build(false));

    public static void drawSolidBox(MatrixStack matrices, Box box, int color,
                                    boolean depthTest)
    {
        VertexConsumerProvider.Immediate vcp = getVCP();
        RenderLayer layer = depthTest ? QUADS : ESP_QUADS;
        VertexConsumer buffer = vcp.getBuffer(layer);

        drawSolidBox(matrices, buffer, box.offset(getCameraPos().negate()),
                color);

        vcp.draw(layer);
    }

    public static void drawSolidBox(VertexConsumer buffer, Box box, int color)
    {
        drawSolidBox(new MatrixStack(), buffer, box, color);
    }

    public static void drawSolidBox(MatrixStack matrices, VertexConsumer buffer,
                                    Box box, int color)
    {
        MatrixStack.Entry entry = matrices.peek();
        float x1 = (float)box.minX;
        float y1 = (float)box.minY;
        float z1 = (float)box.minZ;
        float x2 = (float)box.maxX;
        float y2 = (float)box.maxY;
        float z2 = (float)box.maxZ;

        buffer.vertex(entry, x1, y1, z1).color(color);
        buffer.vertex(entry, x2, y1, z1).color(color);
        buffer.vertex(entry, x2, y1, z2).color(color);
        buffer.vertex(entry, x1, y1, z2).color(color);

        buffer.vertex(entry, x1, y2, z1).color(color);
        buffer.vertex(entry, x1, y2, z2).color(color);
        buffer.vertex(entry, x2, y2, z2).color(color);
        buffer.vertex(entry, x2, y2, z1).color(color);

        buffer.vertex(entry, x1, y1, z1).color(color);
        buffer.vertex(entry, x1, y2, z1).color(color);
        buffer.vertex(entry, x2, y2, z1).color(color);
        buffer.vertex(entry, x2, y1, z1).color(color);

        buffer.vertex(entry, x2, y1, z1).color(color);
        buffer.vertex(entry, x2, y2, z1).color(color);
        buffer.vertex(entry, x2, y2, z2).color(color);
        buffer.vertex(entry, x2, y1, z2).color(color);

        buffer.vertex(entry, x1, y1, z2).color(color);
        buffer.vertex(entry, x2, y1, z2).color(color);
        buffer.vertex(entry, x2, y2, z2).color(color);
        buffer.vertex(entry, x1, y2, z2).color(color);

        buffer.vertex(entry, x1, y1, z1).color(color);
        buffer.vertex(entry, x1, y1, z2).color(color);
        buffer.vertex(entry, x1, y2, z2).color(color);
        buffer.vertex(entry, x1, y2, z1).color(color);
    }

    public static VertexConsumerProvider.Immediate getVCP()
    {
        return JCAddons.minecraftClient.getBufferBuilders().getEntityVertexConsumers();
    }

    public static Vec3d getCameraPos() {
        Camera camera = JCAddons.minecraftClient.getBlockEntityRenderDispatcher().camera;
        if (camera == null) return Vec3d.ZERO;

        return camera.getPos();
    }

    public static BlockPos getCameraBlockPos() {
        Camera camera = JCAddons.minecraftClient.getBlockEntityRenderDispatcher().camera;
        if (camera == null) return BlockPos.ORIGIN;

        return camera.getBlockPos();
    }

    public static RegionPos getCameraRegion() {
        return RegionPos.of(getCameraBlockPos());
    }

    public static void applyRegionalRenderOffset(MatrixStack matrixStack, RegionPos region) {
        Vec3d offset = region.toVec3d().subtract(getCameraPos());
        matrixStack.translate(offset.x, offset.y, offset.z);
    }
}
