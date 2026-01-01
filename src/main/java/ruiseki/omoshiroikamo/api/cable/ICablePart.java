package ruiseki.omoshiroikamo.api.cable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public interface ICablePart {

    String getId();

    ICable getCable();

    ForgeDirection getSide();

    void setCable(ICable cable, ForgeDirection side);

    void onAttached();

    void onDetached();

    void update();

    ItemStack getItemStack();

    void writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);
}
