package ruiseki.omoshiroikamo.core.capabilities.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityInject;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityManager;

public class CapabilityItemHandler {

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IItemHandler.class, new Capability.IStorage<>() {

            @Override
            public NBTBase writeNBT(Capability<IItemHandler> capability, IItemHandler instance, ForgeDirection side) {
                NBTTagList nbtTagList = new NBTTagList();

                int size = instance.getSlots();

                for (int i = 0; i < size; i++) {

                    ItemStack stack = instance.getStackInSlot(i);

                    if (stack != null) {
                        NBTTagCompound itemTag = new NBTTagCompound();

                        itemTag.setInteger("Slot", i);

                        stack.writeToNBT(itemTag);

                        nbtTagList.appendTag(itemTag);
                    }

                }
                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<IItemHandler> capability, IItemHandler instance, ForgeDirection side,
                NBTBase base) {

                if (!(instance instanceof IItemHandlerModifiable itemHandlerModifiable))
                    throw new RuntimeException("IItemHandler instance does not implement IItemHandlerModifiable");

                NBTTagList tagList = (NBTTagList) base;

                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
                    int j = itemTags.getInteger("Slot");

                    if (j >= 0 && j < instance.getSlots()) {
                        itemHandlerModifiable.setStackInSlot(j, ItemStack.loadItemStackFromNBT(itemTags));
                    }
                }
            }
        }, ItemStackHandler::new);
    }
}
