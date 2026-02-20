package ruiseki.omoshiroikamo.core.fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.capabilities.ICapabilityProvider;
import ruiseki.omoshiroikamo.core.common.capabilities.CapabilityFluidHandler;

/**
 * FluidHandlerItemStack is a template capability provider for ItemStacks.
 * Data is stored directly in the vanilla NBT, in the same way as the old ItemFluidContainer.
 *
 * Additional examples are provided to enable consumable fluid containers (see {@link Consumable}),
 * fluid containers with different empty and full items (see {@link SwapEmpty},
 */
public class FluidHandlerItemStack implements IFluidHandlerItem, ICapabilityProvider {

    public static final String FLUID_NBT_KEY = "Fluid";

    @Nonnull
    protected ItemStack container;
    protected int capacity;

    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public FluidHandlerItemStack(@Nonnull ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    @Nonnull
    @Override
    public ItemStack getContainer() {
        return container;
    }

    @Nullable
    public FluidStack getFluid() {
        NBTTagCompound tagCompound = container.getTagCompound();
        if (tagCompound == null || !tagCompound.hasKey(FLUID_NBT_KEY)) {
            return null;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag(FLUID_NBT_KEY));
    }

    protected void setFluid(FluidStack fluid) {
        if (!container.hasTagCompound()) {
            container.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound fluidTag = new NBTTagCompound();
        fluid.writeToNBT(fluidTag);
        container.getTagCompound()
            .setTag(FLUID_NBT_KEY, fluidTag);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { new FluidTankInfo(getFluid(), capacity) };
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return fill(resource, doFill);
    }

    public int fill(FluidStack resource, boolean doFill) {
        if (container.stackSize != 1 || resource == null
            || resource.amount <= 0
            || !canFill(null, resource.getFluid())) {
            return 0;
        }

        FluidStack contained = getFluid();
        if (contained == null) {
            int fillAmount = Math.min(capacity, resource.amount);

            if (doFill) {
                FluidStack filled = resource.copy();
                filled.amount = fillAmount;
                setFluid(filled);
            }

            return fillAmount;
        } else {
            if (contained.isFluidEqual(resource)) {
                int fillAmount = Math.min(capacity - contained.amount, resource.amount);

                if (doFill && fillAmount > 0) {
                    contained.amount += fillAmount;
                    setFluid(contained);
                }

                return fillAmount;
            }

            return 0;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null) return null;
        return drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return drain(maxDrain, doDrain);
    }

    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (container.stackSize != 1 || resource == null
            || resource.amount <= 0
            || !resource.isFluidEqual(getFluid())) {
            return null;
        }
        return drain(resource.amount, doDrain);
    }

    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (container.stackSize != 1 || maxDrain <= 0) return null;

        FluidStack contained = getFluid();
        if (contained == null || contained.amount <= 0) return null;

        int drainAmount = Math.min(maxDrain, contained.amount);

        FluidStack drained = contained.copy();
        drained.amount = drainAmount;

        if (doDrain) {
            contained.amount -= drainAmount;

            if (contained.amount <= 0) {
                setContainerToEmpty();
            } else {
                setFluid(contained);
            }
        }

        return drained;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    /**
     * Override this method for special handling.
     * Can be used to swap out or destroy the container.
     */
    protected void setContainerToEmpty() {
        container.getTagCompound()
            .removeTag(FLUID_NBT_KEY);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable ForgeDirection facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable ForgeDirection facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? (T) this : null;
    }

    /**
     * Destroys the container item when it's emptied.
     */
    public static class Consumable extends FluidHandlerItemStack {

        public Consumable(ItemStack container, int capacity) {
            super(container, capacity);
        }

        @Override
        protected void setContainerToEmpty() {
            super.setContainerToEmpty();
            container.stackSize--;
            if (container.stackSize <= 0) {
                container = null;
            }
        }
    }

    /**
     * Swaps the container item for a different one when it's emptied.
     */
    public static class SwapEmpty extends FluidHandlerItemStack {

        private final ItemStack emptyContainer;

        public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity) {
            super(container, capacity);
            this.emptyContainer = emptyContainer;
        }

        @Override
        protected void setContainerToEmpty() {
            super.setContainerToEmpty();
            container = emptyContainer.copy();
        }
    }

}
