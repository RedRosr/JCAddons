package redrosr.jcaddons.features.Pots;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import redrosr.jcaddons.config.Config;
import redrosr.jcaddons.util.RenderUtils;
import redrosr.jcaddons.util.Utils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

public class PotEsp {

    private final MinecraftClient client;
    private final ArrayList<BlockEntity> blockEntities = new ArrayList<>();

    public PotEsp(MinecraftClient mc) {
        client = mc;
    }

    public void onUpdate() {
        if (client.world == null || client.player == null || !Config.get().PotESP) return; // Check for null player
        blockEntities.clear();

        Stream<BlockEntity> stream = getLoadedBlockEntities()
            .filter(Objects::nonNull).map(be -> be)
            .filter(be -> (be instanceof DecoratedPotBlockEntity));

        blockEntities.addAll(stream.toList());
    }

    public void onRender(MatrixStack matrixStack, float renderTickCounter) {
        if (client.player == null || !Config.get().PotESP)
            return; // Ensure player exists before rendering
        if (!Utils.inDungeon) return;

        renderBoxes(matrixStack, renderTickCounter);
    }

    private void renderBoxes(MatrixStack matrixStack, float partialTicks) {
        for (BlockEntity be : blockEntities) {
            Box potBox = new Box(be.getPos().getX(), be.getPos().getY(), be.getPos().getZ(), be.getPos().getX() + 1, be.getPos().getY() + 1, be.getPos().getZ() + 1);
            RenderUtils.drawSolidBox(matrixStack, potBox, 0x995F352B, false);
        }
    }

    public Stream<BlockEntity> getLoadedBlockEntities() {
        return getLoadedChunks()
            .flatMap(chunk -> chunk.getBlockEntities().values().stream());
    }

    public Stream<WorldChunk> getLoadedChunks() {
        int radius = Math.max(2, client.options.getClampedViewDistance()) + 3;
        int diameter = radius * 2 + 1;

        ChunkPos center = client.player.getChunkPos();
        ChunkPos min = new ChunkPos(center.x - radius, center.z - radius);
        ChunkPos max = new ChunkPos(center.x + radius, center.z + radius);

        Stream<WorldChunk> stream = Stream.iterate(min, pos -> {

                int x = pos.x;
                int z = pos.z;

                x++;

                if (x > max.x) {
                    x = min.x;
                    z++;
                }

                if (z > max.z)
                    throw new IllegalStateException("Stream limit didn't work.");

                return new ChunkPos(x, z);

            }).limit((long) diameter * diameter)
            .filter(c -> client.world.isChunkLoaded(c.x, c.z))
            .map(c -> client.world.getChunk(c.x, c.z)).filter(Objects::nonNull);

        return stream;
    }

    public int getPots() {
        return blockEntities.size();
    }

}
