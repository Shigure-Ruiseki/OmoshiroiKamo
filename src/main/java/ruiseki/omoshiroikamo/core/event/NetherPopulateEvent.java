package ruiseki.omoshiroikamo.core.event;

import static net.minecraft.util.AxisAlignedBB.getBoundingBox;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;

public class NetherPopulateEvent {

    private final float chanceMultiplier;
    private final Class<? extends EntityLiving> entityClass;

    public NetherPopulateEvent(float chanceMultiplier, Class<? extends EntityLiving> entityClass) {
        this.chanceMultiplier = chanceMultiplier;
        this.entityClass = entityClass;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void populateChunk(PopulateChunkEvent.Populate event) {
        BlockPos chunkCentrePos = new BlockPos(event.chunkX * 16 + 8, 0, event.chunkZ * 16 + 8);
        BiomeGenBase biome = chunkCentrePos.getBiomeGen(event.world);
        if (biome != BiomeGenBase.hell) {
            return;
        }

        if (event.world.rand.nextFloat() < biome.getSpawningChance() * chanceMultiplier) {

            BlockPos spawnCenter = getRandomChunkPosition(event.chunkX, event.chunkZ, event.world);
            spawnCenter = findFloor(spawnCenter, event.world);

            IEntityLivingData data = null;
            data = spawn(data, spawnCenter, event.world);
            data = spawn(data, new BlockPos(spawnCenter.x + 1, spawnCenter.y, spawnCenter.z), event.world);
            data = spawn(data, new BlockPos(spawnCenter.x - 1, spawnCenter.y, spawnCenter.z), event.world);
            data = spawn(data, new BlockPos(spawnCenter.x, spawnCenter.y, spawnCenter.z + 1), event.world);
            spawn(data, new BlockPos(spawnCenter.x, spawnCenter.y, spawnCenter.z - 1), event.world);
        }
    }

    private BlockPos findFloor(BlockPos pos, World world) {
        BlockPos p = pos;
        while (p.y < 100 && !World.doesBlockHaveSolidTopSurface(world, p.x, p.y - 1, p.z)) {
            p = new BlockPos(p.x, p.y + 1, p.z);
        }
        return p;
    }

    @Nullable
    private IEntityLivingData spawn(IEntityLivingData data, BlockPos pos, World world) {
        if (!world.checkNoEntityCollision(getBoundingBox(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1))) {
            return data;
        }

        EntityLiving chicken = createEntity(world);
        chicken.setLocationAndAngles(pos.x + 0.5, pos.y, pos.z + 0.5, world.rand.nextFloat() * 360F, 0.0F);

        data = chicken.onSpawnWithEgg(data);

        world.spawnEntityInWorld(chicken);
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

        return new BlockPos(x, y, z);
    }
}
