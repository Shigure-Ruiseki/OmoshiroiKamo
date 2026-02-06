package ruiseki.omoshiroikamo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.CoreClient;
import ruiseki.omoshiroikamo.core.ModuleManager;
import ruiseki.omoshiroikamo.core.client.util.TextureLoader;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.BackpackClient;
import ruiseki.omoshiroikamo.module.chickens.ChickensClient;
import ruiseki.omoshiroikamo.module.cows.CowsClient;
import ruiseki.omoshiroikamo.module.dml.DMLClient;
import ruiseki.omoshiroikamo.module.ids.IDsClient;
import ruiseki.omoshiroikamo.module.machinery.MachineryClient;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketStructureTint;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;
import ruiseki.omoshiroikamo.module.multiblock.MultiBlockClient;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public ClientProxy() {}

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ModuleManager.register(new CoreClient());
        ModuleManager.register(new ChickensClient());
        ModuleManager.register(new CowsClient());
        ModuleManager.register(new DMLClient());
        ModuleManager.register(new BackpackClient());
        ModuleManager.register(new MultiBlockClient());
        ModuleManager.register(new IDsClient());
        ModuleManager.register(new MachineryClient());

        ModuleManager.preInitClient(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ModuleManager.initClient(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        ModuleManager.postInitClient(event);
        TextureLoader.loadFromConfig(LibMisc.MOD_ID, LibMisc.MOD_NAME + " Runtime Textures", OmoshiroiKamo.class);
    }

    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public World getClientWorld() {
        return FMLClientHandler.instance()
            .getClient().theWorld;
    }

    @Override
    public long getTickCount() {
        return clientTickCount;
    }

    @Override
    public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
        if (entityPlayer instanceof EntityPlayerMP) {
            return ((EntityPlayerMP) entityPlayer).theItemInWorldManager.getBlockReachDistance();
        }
        return super.getReachDistanceForPlayer(entityPlayer);
    }

    @Override
    protected void onClientTick() {
        if (!Minecraft.getMinecraft()
            .isGamePaused() && Minecraft.getMinecraft().theWorld != null) {
            ++clientTickCount;
        }
    }

    @Override
    public void handleStructureTint(PacketStructureTint message) {
        Minecraft.getMinecraft()
            .func_152344_a(() -> {
                World world = Minecraft.getMinecraft().theWorld;
                if (world == null || world.provider.dimensionId != message.getDimensionId()) {
                    return;
                }

                if (message.isClear()) {
                    // Clear colors and trigger re-render
                    StructureTintCache.clearAll(world, message.getPositions());
                } else {
                    // Set colors
                    for (ChunkCoordinates pos : message.getPositions()) {
                        StructureTintCache.put(world, pos.posX, pos.posY, pos.posZ, message.getColor());
                    }
                }

                // Trigger block re-renders
                for (ChunkCoordinates pos : message.getPositions()) {
                    world.markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
                }
            });
    }
}
