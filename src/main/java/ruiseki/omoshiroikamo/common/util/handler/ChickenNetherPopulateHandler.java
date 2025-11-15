package ruiseki.omoshiroikamo.common.util.handler;

import static net.minecraft.util.AxisAlignedBB.getBoundingBox;

import javax.annotation.Nullable;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;

public class ChickenNetherPopulateHandler {

    private static float chanceMultiplier;

    public ChickenNetherPopulateHandler(float chanceMultiplier) {
        this.chanceMultiplier = chanceMultiplier;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void populateChunk(PopulateChunkEvent.Populate event) {
        com.gtnewhorizon.gtnhlib.blockpos.BlockPos chunkCentrePos = new BlockPos(
            event.chunkX * 16 + 8,
            0,
            event.chunkZ * 16 + 8);
        BiomeGenBase biome = event.world.getBiomeGenForCoords(chunkCentrePos.x, chunkCentrePos.z);
        if (biome != BiomeGenBase.hell) {
            return;
        }

        if (event.world.rand.nextFloat() < biome.getSpawningChance() * chanceMultiplier) {

            BlockPos spawnCenter = getRandomChunkPosition(event.world, event.chunkX, event.chunkZ);
            spawnCenter = findFloor(event.world, spawnCenter);

            IEntityLivingData data = null;
            data = spawn(event.world, data, spawnCenter);
            data = spawn(event.world, data, new BlockPos(spawnCenter.x + 1, spawnCenter.y, spawnCenter.z));
            data = spawn(event.world, data, new BlockPos(spawnCenter.x - 1, spawnCenter.y, spawnCenter.z));
            data = spawn(event.world, data, new BlockPos(spawnCenter.x, spawnCenter.y, spawnCenter.z + 1));
            spawn(event.world, data, new BlockPos(spawnCenter.x, spawnCenter.y, spawnCenter.z - 1));
        }
    }

    private BlockPos findFloor(World world, BlockPos pos) {
        BlockPos p = pos;
        while (p.y < 100 && !World.doesBlockHaveSolidTopSurface(world, p.x, p.y - 1, p.z)) {
            p = new BlockPos(p.x, p.y + 1, p.z);
        }
        return p;
    }

    @Nullable
    private IEntityLivingData spawn(World world, IEntityLivingData data, BlockPos pos) {
        if (!world.checkNoEntityCollision(getBoundingBox(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1))) {
            return data;
        }

        EntityChickensChicken chicken = new EntityChickensChicken(world);
        chicken.setLocationAndAngles(pos.x + 0.5, pos.y, pos.z + 0.5, world.rand.nextFloat() * 360F, 0.0F);

        data = chicken.onSpawnWithEgg(data);

        world.spawnEntityInWorld(chicken);
        return data;
    }

    private BlockPos getRandomChunkPosition(World world, int chunkX, int chunkZ) {
        int x = chunkX * 16 + world.rand.nextInt(16);
        int z = chunkZ * 16 + world.rand.nextInt(16);

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        int height = chunk.getTopFilledSegment() + 16;
        int y = world.rand.nextInt(height);

        return new BlockPos(x, y, z);
    }
}
