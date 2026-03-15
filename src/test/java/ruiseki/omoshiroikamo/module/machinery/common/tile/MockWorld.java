package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;

public class MockWorld extends World {

    private final Map<ChunkCoordinates, Block> blocks = new HashMap<>();

    public MockWorld() {
        super(null, "MockWorld", null, (WorldSettings) null, null);
    }

    public void setBlock(ChunkCoordinates pos, Block block, int meta) {
        blocks.put(pos, block);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return blocks.getOrDefault(new ChunkCoordinates(x, y, z), null);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        return 0;
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    public Entity getEntityByID(int p_73045_1_) {
        return null;
    }

    // Required for 1.7.10 (SRG)
    // func_152379_p corresponds to getEntityProxy in some environments
    @Override
    public int func_152379_p() {
        return 0;
    }
}
