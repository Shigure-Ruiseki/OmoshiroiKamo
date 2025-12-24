package ruiseki.omoshiroikamo.core.common.handler;

import static net.minecraft.util.AxisAlignedBB.getBoundingBox;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.api.block.BlockPos;

public class NetherPopulateHandler {

    private final float chanceMultiplier;
    private final Class<? extends EntityLiving> entityClass;

    public NetherPopulateHandler(float chanceMultiplier, Class<? extends EntityLiving> entityClass) {
        this.chanceMultiplier = chanceMultiplier;
        this.entityClass = entityClass;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void populateChunk(PopulateChunkEvent.Populate event) {
        BlockPos chunkCentrePos = new BlockPos(event.chunkX * 16 + 8, 0, event.chunkZ * 16 + 8, event.world);
        BiomeGenBase biome = chunkCentrePos.getBiomeGen();
        if (biome != BiomeGenBase.hell) {
            return;
        }

        if (event.world.rand.nextFloat() < biome.getSpawningChance() * chanceMultiplier) {

            BlockPos spawnCenter = getRandomChunkPosition(event.chunkX, event.chunkZ, event.world);
            spawnCenter = findFloor(spawnCenter);

            IEntityLivingData data = null;
            data = spawn(data, spawnCenter);
            data = spawn(data, new BlockPos(spawnCenter.x + 1, spawnCenter.y, spawnCenter.z, spawnCenter.world));
            data = spawn(data, new BlockPos(spawnCenter.x - 1, spawnCenter.y, spawnCenter.z, spawnCenter.world));
            data = spawn(data, new BlockPos(spawnCenter.x, spawnCenter.y, spawnCenter.z + 1, spawnCenter.world));
            spawn(data, new BlockPos(spawnCenter.x, spawnCenter.y, spawnCenter.z - 1, spawnCenter.world));
        }
    }

    private BlockPos findFloor(BlockPos pos) {
        BlockPos p = pos;
        while (p.y < 100 && !World.doesBlockHaveSolidTopSurface(p.getWorld(), p.x, p.y - 1, p.z)) {
            p = new BlockPos(p.x, p.y + 1, p.z, p.getWorld());
        }
        return p;
    }

    @Nullable
    private IEntityLivingData spawn(IEntityLivingData data, BlockPos pos) {
        if (!pos.getWorld()
            .checkNoEntityCollision(getBoundingBox(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1))) {
            return data;
        }

        EntityLiving chicken = createEntity(pos.getWorld());
        chicken.setLocationAndAngles(pos.x + 0.5, pos.y, pos.z + 0.5, pos.getWorld().rand.nextFloat() * 360F, 0.0F);

        data = chicken.onSpawnWithEgg(data);

        pos.getWorld()
            .spawnEntityInWorld(chicken);
        return data;
    }

    public EntityLiving createEntity(World world) {
        try {
            return entityClass.getConstructor(World.class)
                .newInstance(world);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BlockPos getRandomChunkPosition(int chunkX, int chunkZ, World world) {
        int x = chunkX * 16 + world.rand.nextInt(16);
        int z = chunkZ * 16 + world.rand.nextInt(16);

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        int height = chunk.getTopFilledSegment() + 16;
        int y = world.rand.nextInt(height);

        return new BlockPos(x, y, z, world);
    }
}
