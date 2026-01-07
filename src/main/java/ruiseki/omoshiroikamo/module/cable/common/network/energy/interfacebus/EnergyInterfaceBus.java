package ruiseki.omoshiroikamo.module.cable.common.network.energy.interfacebus;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyPart;

public class EnergyInterfaceBus extends AbstractPart implements IEnergyPart {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    @Override
    public String getId() {
        return "energy_interface_bus";
    }

    @Override
    public Class<? extends ICablePart> getBasePartType() {
        return IEnergyPart.class;
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }

    @Override
    public void doUpdate() {}

    @Override
    public void onChunkUnload() {

    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.ENERGY_INTERFACE_BUS.newItemStack();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {

    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {

    }

    @Override
    public EnumIO getIO() {
        return EnumIO.NONE;
    }

    @Override
    public AxisAlignedBB getCollisionBox() {
        return switch (side) {
            case WEST -> AxisAlignedBB.getBoundingBox(0f, W_MIN, W_MIN, DEPTH, W_MAX, W_MAX);
            case EAST -> AxisAlignedBB.getBoundingBox(1f - DEPTH, W_MIN, W_MIN, 1f, W_MAX, W_MAX);
            case DOWN -> AxisAlignedBB.getBoundingBox(W_MIN, 0f, W_MIN, W_MAX, DEPTH, W_MAX);
            case UP -> AxisAlignedBB.getBoundingBox(W_MIN, 1f - DEPTH, W_MIN, W_MAX, 1f, W_MAX);
            case NORTH -> AxisAlignedBB.getBoundingBox(W_MIN, W_MIN, 0f, W_MAX, W_MAX, DEPTH);
            case SOUTH -> AxisAlignedBB.getBoundingBox(W_MIN, W_MIN, 1f - DEPTH, W_MAX, W_MAX, 1f);
            default -> null;
        };
    }

    @Override
    public ResourceLocation getIcon() {
        return new ResourceLocation(LibResources.PREFIX_ITEM + "cable/energy_interface_bus.png");
    }

    @Override
    public int pushEnergy(int amount, boolean simulate) {
        TileEntity te = getTargetTE();
        if (!(te instanceof IEnergyReceiver h)) return 0;

        return h.receiveEnergy(side.getOpposite(), amount, simulate);
    }

    @Override
    public int pullEnergy(int amount, boolean simulate) {
        TileEntity te = getTargetTE();
        if (!(te instanceof IEnergyProvider h)) return 0;

        return h.extractEnergy(side.getOpposite(), amount, simulate);
    }

    @Override
    public int getTransferLimit() {
        return 1000;
    }
}
