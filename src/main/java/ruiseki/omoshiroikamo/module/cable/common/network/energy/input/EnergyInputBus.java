package ruiseki.omoshiroikamo.module.cable.common.network.energy.input;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import cofh.api.energy.IEnergyProvider;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.EnergyNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyPart;

public class EnergyInputBus extends AbstractPart implements IEnergyPart {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    @Override
    public String getId() {
        return "energy_input_bus";
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
    public void doUpdate() {

        tickCounter++;
        if (tickCounter < tickInterval) return;
        tickCounter = 0;

        EnergyNetwork network = (EnergyNetwork) getNetwork();
        if (network == null || network.interfaces == null || network.interfaces.isEmpty()) return;

        int remaining = pullEnergy(getTransferLimit(), true);
        if (remaining <= 0) return;

        for (IEnergyPart iFace : network.interfaces) {
            if (iFace.getChannel() != this.getChannel()) continue;
            if (remaining <= 0) break;

            int canPush = iFace.pushEnergy(remaining, true);
            if (canPush <= 0) continue;

            int pulled = pullEnergy(canPush, false);
            if (pulled <= 0) break;

            iFace.pushEnergy(pulled, false);
            remaining -= pulled;
        }
    }

    @Override
    public void onChunkUnload() {

    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.ENERGY_INPUT_BUS.newItemStack();
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.INPUT;
    }

    @Override
    public AxisAlignedBB getCollisionBox() {
        return switch (getSide()) {
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
        return new ResourceLocation(LibResources.PREFIX_ITEM + "cable/energy_input_bus.png");
    }

    @Override
    public int pushEnergy(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int pullEnergy(int amount, boolean simulate) {
        TileEntity te = getTargetTE();
        if (!(te instanceof IEnergyProvider h)) return 0;

        return h.extractEnergy(getSide().getOpposite(), amount, simulate);
    }

    @Override
    public int getTransferLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        if (amount <= 0) return 0;

        EnergyNetwork network = (EnergyNetwork) getNetwork();
        if (network == null || network.interfaces == null || network.interfaces.isEmpty()) return 0;

        int remaining = Math.min(amount, getTransferLimit());
        int accepted = 0;

        for (IEnergyPart iFace : network.interfaces) {
            if (iFace.getChannel() != this.getChannel()) continue;
            if (remaining <= 0) break;
            int canPush = iFace.pushEnergy(remaining, true);
            if (canPush <= 0) continue;

            int pushed = simulate ? canPush : iFace.pushEnergy(canPush, false);

            accepted += pushed;
            remaining -= pushed;
        }

        return accepted;
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        return 0;
    }

}
