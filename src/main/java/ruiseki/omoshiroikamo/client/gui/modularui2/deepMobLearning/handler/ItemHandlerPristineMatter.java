package ruiseki.omoshiroikamo.client.gui.modularui2.deepMobLearning.handler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.client.gui.modularui2.base.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.common.item.dml.ItemPristineMatter;

public abstract class ItemHandlerPristineMatter extends ItemStackHandlerBase {

    @Nullable
    @Getter
    protected ModelRegistryItem pristineMatterData;

    public ItemHandlerPristineMatter() {
        super();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return stack != null && stack.getItem() instanceof ItemPristineMatter ? super.insertItem(slot, stack, simulate)
            : stack;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        updateMetadata();
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (slot != 0) return;

        updateMetadata();
    }

    private void updateMetadata() {
        ItemStack stack = getStackInSlot(0);

        ModelRegistryItem newMetadata = null;

        if (stack != null) {
            newMetadata = ModelRegistry.INSTANCE.getByType(stack.getItemDamage());
        }

        if (newMetadata != pristineMatterData) {
            pristineMatterData = newMetadata;
            onMetadataChanged();
        }
    }

    protected abstract void onMetadataChanged();
}
