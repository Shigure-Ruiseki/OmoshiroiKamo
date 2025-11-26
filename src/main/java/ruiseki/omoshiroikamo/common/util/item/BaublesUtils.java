package ruiseki.omoshiroikamo.common.util.item;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class BaublesUtils {

    public static enum WhoAmI {

        SPCLIENT,
        MPCLIENT,
        SPSERVER,
        MPSERVER,
        OTHER;

        public static BaublesUtils.WhoAmI whoAmI(World world) {
            Side side = FMLCommonHandler.instance()
                .getSide();
            if (side == Side.CLIENT) {
                if (Minecraft.getMinecraft()
                    .isIntegratedServerRunning()) {
                    if (world.isRemote) {
                        return SPCLIENT;
                    } else {
                        return SPSERVER;
                    }
                } else {
                    return MPCLIENT;
                }
            } else if (side == Side.SERVER) {
                if (MinecraftServer.getServer()
                    .isDedicatedServer()) {
                    return MPSERVER;
                } else if (Minecraft.getMinecraft()
                    .isIntegratedServerRunning()) {
                        return SPSERVER;
                    }
            }
            return OTHER;
        }
    }

    private static final BaublesUtils instance = new BaublesUtils();

    private BaublesUtils() {}

    public static BaublesUtils instance() {
        return instance;
    }

    /**
     * Do NOT modify this inventory on the client side of a singleplayer game!
     * <p>
     * Wrap it in a ShadowInventory if you need to.
     */
    public IInventory getBaubles(EntityPlayer player) {
        if (!LibMods.Baubles.isLoaded()) return null;
        return BaublesApi.getBaubles(player);
    }

    private static boolean failedDirectAccess = false;

    public void disableCallbacks(IInventory baubles, boolean b) {
        if (!failedDirectAccess) {
            try {
                Class<?> inventoryBaubles = Class.forName("baubles.common.container.InventoryBaubles");
                Field blockEvents = inventoryBaubles.getDeclaredField("blockEvents");
                blockEvents.set(baubles, b);
            } catch (Throwable t) {
                Logger.info("Failed to access Baubles internals: " + t);
                failedDirectAccess = true;
            }
        }
    }
}
