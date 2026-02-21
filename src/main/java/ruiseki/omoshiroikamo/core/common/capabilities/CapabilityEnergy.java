package ruiseki.omoshiroikamo.core.common.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyStorage;
import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityInject;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityManager;
import ruiseki.omoshiroikamo.core.energy.EnergyStorage;

public class CapabilityEnergy {

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> ENERGY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IEnergyStorage.class, new Capability.IStorage<IEnergyStorage>() {

            @Override
            public NBTBase writeNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance,
                ForgeDirection side) {
                return new NBTTagInt(instance.getEnergyStored());
            }

            @Override
            public void readNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, ForgeDirection side,
                NBTBase nbt) {
                if (!(instance instanceof EnergyStorage)) throw new IllegalArgumentException(
                    "Can not deserialize to an instance that isn't the default implementation");
                ((EnergyStorage) instance).setEnergyStorage(((NBTTagInt) nbt).func_150287_d());
            }
        }, () -> new EnergyStorage(1000));
    }
}
