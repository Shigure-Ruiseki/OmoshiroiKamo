package ruiseki.omoshiroikamo.core.event;

import net.minecraft.world.World;

@FunctionalInterface
public interface WorldUnloadCallback {

    void onWorldUnload(World world);
}
