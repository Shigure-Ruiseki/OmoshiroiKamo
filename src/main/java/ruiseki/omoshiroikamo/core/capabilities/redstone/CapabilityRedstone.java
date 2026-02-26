package ruiseki.omoshiroikamo.core.capabilities.redstone;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.block.IDynamicRedstone;
import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityInject;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityManager;

public class CapabilityRedstone {

    @CapabilityInject(IDynamicRedstone.class)
    public static Capability<IDynamicRedstone> DYNAMIC_REDSTONE_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IDynamicRedstone.class, new Capability.IStorage<>() {

            @Override
            public NBTBase writeNBT(Capability<IDynamicRedstone> capability, IDynamicRedstone instance,
                ForgeDirection side) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("Level", instance.getRedstoneLevel());
                tag.setBoolean("Strong", instance.isStrong());
                tag.setBoolean("AllowInput", instance.isAllowRedstoneInput());
                tag.setInteger("LastPulse", instance.getLastPulseValue());
                return tag;
            }

            @Override
            public void readNBT(Capability<IDynamicRedstone> capability, IDynamicRedstone instance, ForgeDirection side,
                NBTBase nbt) {
                if (!(nbt instanceof NBTTagCompound tag)) return;
                int level = tag.getInteger("Level");
                boolean strong = tag.getBoolean("Strong");
                boolean allowInput = tag.getBoolean("AllowInput");
                int lastPulse = tag.getInteger("LastPulse");

                instance.setRedstoneLevel(level, strong);
                instance.setAllowRedstoneInput(allowInput);
                instance.setLastPulseValue(lastPulse);
            }

        }, DynamicRedstoneDefault::new);
    }
}
