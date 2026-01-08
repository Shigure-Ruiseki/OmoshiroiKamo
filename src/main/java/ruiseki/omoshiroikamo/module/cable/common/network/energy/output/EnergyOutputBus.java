package ruiseki.omoshiroikamo.module.cable.common.network.energy.output;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import cofh.api.energy.IEnergyReceiver;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.EnergyNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyPart;

public class EnergyOutputBus extends AbstractPart implements IEnergyPart {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    @Override
    public String getId() {
        return "energy_output_bus";
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

        int limit = getTransferLimit();

        for (IEnergyPart iFace : network.interfaces) {
            if (iFace.getChannel() != this.getChannel()) continue;
            if (limit <= 0) break;

            int canPull = iFace.pullEnergy(limit, true);
            if (canPull <= 0) continue;

            int canPush = pushEnergy(canPull, true);
            if (canPush <= 0) continue;

            int pulled = iFace.pullEnergy(canPush, false);
            if (pulled <= 0) continue;

            pushEnergy(pulled, false);

            limit -= pulled;
        }
    }

    @Override
    public void onChunkUnload() {

    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.ENERGY_OUTPUT_BUS.newItemStack();
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.OUTPUT;
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
        return new ResourceLocation(LibResources.PREFIX_ITEM + "cable/energy_output_bus.png");
    }

    @Override
    public int pushEnergy(int amount, boolean simulate) {
        TileEntity te = getTargetTE();
        if (!(te instanceof IEnergyReceiver h)) return 0;

        return h.receiveEnergy(side.getOpposite(), amount, simulate);
    }

    @Override
    public int pullEnergy(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int getTransferLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        EnergyNetwork network = (EnergyNetwork) getNetwork();
        if (network == null || network.interfaces == null || network.interfaces.isEmpty()) return 0;

        int remaining = Math.min(amount, getTransferLimit());
        int extracted = 0;

        for (IEnergyPart iFace : network.interfaces) {
            if (iFace.getChannel() != this.getChannel()) continue;
            if (remaining <= 0) break;

            if (iFace == this) continue;

            int canPull = iFace.pullEnergy(remaining, true);
            if (canPull <= 0) continue;

            int pulled = simulate ? canPull : iFace.pullEnergy(canPull, false);

            extracted += pulled;
            remaining -= pulled;
        }

        return extracted;
    }

}
