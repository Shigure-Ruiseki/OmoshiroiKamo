package ruiseki.omoshiroikamo.core.capabilities.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.core.capabilities.AttachCapabilitiesEvent;
import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityInject;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityManager;
import ruiseki.omoshiroikamo.core.fluid.FluidHandlerItemStack;
import ruiseki.omoshiroikamo.core.fluid.IFluidHandlerItem;
import ruiseki.omoshiroikamo.core.fluid.SmartTank;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class CapabilityFluidHandler {

    @CapabilityInject(IFluidHandler.class)
    public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = null;
    @CapabilityInject(IFluidHandlerItem.class)
    public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(
            IFluidHandler.class,
            new DefaultFluidHandlerStorage<>(),
            () -> new SmartTank(FluidContainerRegistry.BUCKET_VOLUME));

        CapabilityManager.INSTANCE.register(
            IFluidHandlerItem.class,
            new DefaultFluidHandlerStorage<>(),
            () -> new FluidHandlerItemStack(null, 0));

        MinecraftForge.EVENT_BUS.register(new CapabilityFluidHandler());
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject() == null) return;
        if (event.getType() == ItemStack.class) {
            ItemStack stack = event.getObject();
            if (stack.getItem() instanceof IFluidContainerItem containerItem) {
                int capacity = containerItem.getCapacity(stack);
                event.addCapability(
                    new ResourceLocation(LibMisc.MOD_ID, "fluid_handler"),
                    new FluidHandlerItemStack(stack, capacity));
            }
        }
    }

    private static class DefaultFluidHandlerStorage<T extends IFluidHandler> implements Capability.IStorage<T> {

        @Override
        public NBTBase writeNBT(Capability<T> capability, T instance, ForgeDirection side) {
            if (!(instance instanceof IFluidTank tank))
                throw new RuntimeException("IFluidHandler instance does not implement IFluidTank");
            NBTTagCompound nbt = new NBTTagCompound();
            FluidStack fluid = tank.getFluid();
            if (fluid != null) {
                fluid.writeToNBT(nbt);
            } else {
                nbt.setString("Empty", "");
            }
            nbt.setInteger("Capacity", tank.getCapacity());
            return nbt;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, ForgeDirection side, NBTBase nbt) {
            if (!(instance instanceof FluidTank tank))
                throw new RuntimeException("IFluidHandler instance is not instance of FluidTank");
            NBTTagCompound tags = (NBTTagCompound) nbt;
            tank.setCapacity(tags.getInteger("Capacity"));
            tank.readFromNBT(tags);
        }
    }
}
