package redrosr.jcaddons.features.Pots;

import com.mojang.blaze3d.systems.RenderSystem;
import redrosr.jcaddons.config.Config;
import redrosr.jcaddons.util.RegionPos;
import redrosr.jcaddons.util.RenderUtils;
import redrosr.jcaddons.util.Utils;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUsage;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class PotEsp {

    private final MinecraftClient client;
    private final ArrayList<BlockEntity> blockEntities = new ArrayList<>();
    private VertexBuffer blockBox;

    private boolean initialized = false;

    public PotEsp(MinecraftClient mc) {
        client = mc;
    }

    public void onUpdate() {
        if (client.world == null || client.player == null || !Config.get().PotESP) return; // Check for null player
        blockEntities.clear();


        if (!initialized) {
            blockBox = new VertexBuffer(GlUsage.STATIC_WRITE);
            Box bb = new Box(-0.5, 0, -0.5, 0.5, 1, 0.5);
            RenderUtils.drawSolidBox(bb, blockBox);
            initialized = true;
        }

        Stream<BlockEntity> stream = getLoadedBlockEntities()
                .filter(Objects::nonNull).map(be -> (BlockEntity)be)
                .filter(be -> (be instanceof DecoratedPotBlockEntity));

        blockEntities.addAll(stream.toList());
    }

    public void onRender(MatrixStack matrixStack, float renderTickCounter) {
        if (client.player == null || !initialized || !Config.get().PotESP) return; // Ensure player exists before rendering
        if (!Utils.inDungeon) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        matrixStack.push();

        RegionPos region = RenderUtils.getCameraRegion();
        RenderUtils.applyRegionalRenderOffset(matrixStack, region);

        renderBoxes(matrixStack, renderTickCounter, region);

        matrixStack.pop();

        RenderSystem.setShaderColor(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderBoxes(MatrixStack matrixStack, float partialTicks, RegionPos region) {
        RenderSystem.setShader(ShaderProgramKeys.POSITION);

        for (BlockEntity be : blockEntities) {
            matrixStack.push();

            Vec3d lerpedPos = Vec3d.of(be.getPos()).subtract(region.toVec3d());
            matrixStack.translate(lerpedPos.x + 0.5, lerpedPos.y, lerpedPos.z + 0.5);

            matrixStack.scale(1, 1, 1);

            RenderSystem.setShaderColor(0.372F, 0.208F, 0.169F, 0.6F);

            Matrix4f viewMatrix = matrixStack.peek().getPositionMatrix();
            Matrix4f projMatrix = RenderSystem.getProjectionMatrix();
            ShaderProgram shader = RenderSystem.getShader();
            blockBox.bind();
            blockBox.draw(viewMatrix, projMatrix, shader);
            VertexBuffer.unbind();

            matrixStack.pop();
        }
    }

    public Stream<BlockEntity> getLoadedBlockEntities()
    {
        return getLoadedChunks()
                .flatMap(chunk -> chunk.getBlockEntities().values().stream());
    }

    public Stream<WorldChunk> getLoadedChunks()
    {
        int radius = Math.max(2, client.options.getClampedViewDistance()) + 3;
        int diameter = radius * 2 + 1;

        ChunkPos center = client.player.getChunkPos();
        ChunkPos min = new ChunkPos(center.x - radius, center.z - radius);
        ChunkPos max = new ChunkPos(center.x + radius, center.z + radius);

        Stream<WorldChunk> stream = Stream.<ChunkPos> iterate(min, pos -> {

                    int x = pos.x;
                    int z = pos.z;

                    x++;

                    if(x > max.x)
                    {
                        x = min.x;
                        z++;
                    }

                    if(z > max.z)
                        throw new IllegalStateException("Stream limit didn't work.");

                    return new ChunkPos(x, z);

                }).limit(diameter * diameter)
                .filter(c -> client.world.isChunkLoaded(c.x, c.z))
                .map(c -> client.world.getChunk(c.x, c.z)).filter(Objects::nonNull);

        return stream;
    }

    public int getPots() {
        return blockEntities.size();
    }

}
