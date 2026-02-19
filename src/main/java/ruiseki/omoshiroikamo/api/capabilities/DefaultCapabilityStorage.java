package ruiseki.omoshiroikamo.api.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A default capability storage implementation when no NBT persistence is required.
 * 
 * @author rubensworks
 */
public class DefaultCapabilityStorage<T> implements Capability.IStorage<T> {

    @Override
    public NBTBase writeNBT(Capability<T> capability, T instance, ForgeDirection side) {
        return null;
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, ForgeDirection side, NBTBase nbt) {

    }
}
