package ruiseki.omoshiroikamo.api.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public interface ICablePart {

    String getId();

    ICable getCable();

    Class<? extends ICablePart> getBasePartType();

    ForgeDirection getSide();

    void setCable(ICable cable, ForgeDirection side);

    void onAttached();

    void onDetached();

    void doUpdate();

    void onChunkUnload();

    ItemStack getItemStack();

    void writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);

    AxisAlignedBB getCollisionBox();

    default AbstractCableNetwork<?> getNetwork() {
        ICable cable = getCable();
        return cable != null ? cable.getNetwork(getBasePartType()) : null;
    }
}
