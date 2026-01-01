package ruiseki.omoshiroikamo.module.cable.common.block;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.energy.IEnergyIO;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.module.cable.common.cablePart.CablePartRegistry;

public class TECable extends TileEntityOK implements ICable {

    protected final boolean[] connections = new boolean[6];

    private final Map<ForgeDirection, ICablePart> parts = new EnumMap<>(ForgeDirection.class);

    public TECable() {}

    @Override
    public void writeCommon(NBTTagCompound tag) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            tag.setBoolean("conn_" + dir.name(), connections[dir.ordinal()]);
        }

        NBTTagCompound partsTag = new NBTTagCompound();
        for (Map.Entry<ForgeDirection, ICablePart> entry : parts.entrySet()) {
            ForgeDirection dir = entry.getKey();
            ICablePart part = entry.getValue();

            NBTTagCompound partTag = new NBTTagCompound();
            partTag.setString("id", part.getId());

            NBTTagCompound data = new NBTTagCompound();
            part.writeToNBT(data);
            partTag.setTag("data", data);

            partsTag.setTag(dir.name(), partTag);
        }

        tag.setTag("Parts", partsTag);
    }

    @Override
    public void readCommon(NBTTagCompound tag) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            connections[dir.ordinal()] = tag.getBoolean("conn_" + dir.name());
        }
        parts.clear();

        if (tag.hasKey("Parts")) {
            NBTTagCompound partsTag = tag.getCompoundTag("Parts");

            for (String key : partsTag.func_150296_c()) {
                ForgeDirection dir = ForgeDirection.valueOf(key);

                NBTTagCompound partTag = partsTag.getCompoundTag(key);
                String id = partTag.getString("id");

                ICablePart part = CablePartRegistry.create(id);
                if (part != null) {
                    part.readFromNBT(partTag.getCompoundTag("data"));
                    part.setCable(this, dir);
                    parts.put(dir, part);
                }
            }
        }

        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public ICablePart getPart(ForgeDirection side) {
        return parts.get(side);
    }

    @Override
    public void setPart(ForgeDirection side, ICablePart part) {
        if (part == null) return;

        // remove cable connection if exists
        disconnect(side);

        parts.put(side, part);
        part.setCable(this, side);
        part.onAttached();

        markDirty();
    }

    @Override
    public void removePart(ForgeDirection side) {
        ICablePart part = parts.remove(side);
        if (part != null) {
            part.onDetached();
            markDirty();
        }
    }

    @Override
    public boolean hasPart(ForgeDirection side) {
        return parts.containsKey(side);
    }

    @Override
    public Collection<ICablePart> getParts() {
        return parts.values();
    }

    @Override
    public boolean canConnect(TileEntity other, ForgeDirection side) {
        if (hasPart(side)) return false;

        return other instanceof IFluidHandler || other instanceof IInventory || other instanceof IEnergyIO;
    }

    @Override
    public void updateConnections() {
        if (worldObj == null || worldObj.isRemote) return;

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            updateSide(dir);
        }
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    private void updateSide(ForgeDirection dir) {
        int x = xCoord + dir.offsetX;
        int y = yCoord + dir.offsetY;
        int z = zCoord + dir.offsetZ;

        TileEntity te = worldObj.getTileEntity(x, y, z);

        boolean shouldConnect = canConnect(te, dir);
        if (shouldConnect) {
            reconnect(dir);
        } else {
            disconnect(dir);
        }
    }

    @Override
    public boolean isConnected(ForgeDirection side) {
        return connections[side.ordinal()];
    }

    @Override
    public void disconnect(ForgeDirection side) {
        connections[side.ordinal()] = false;

    }

    @Override
    public void reconnect(ForgeDirection side) {
        connections[side.ordinal()] = true;
    }

    @Override
    public void destroy() {
        if (worldObj == null) return;

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            int x = xCoord + dir.offsetX;
            int y = yCoord + dir.offsetY;
            int z = zCoord + dir.offsetZ;

            TileEntity te = worldObj.getTileEntity(x, y, z);
            if (te instanceof ICable other) {
                other.disconnect(dir.getOpposite());
            }
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        ICablePart part = getPart(from);
        if (part instanceof IFluidHandler handler) {
            return handler.fill(from, resource, doFill);
        }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        ICablePart part = getPart(from);
        if (part instanceof IFluidHandler handler) {
            return handler.drain(from, resource, doDrain);
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        ICablePart part = getPart(from);
        if (part instanceof IFluidHandler handler) {
            return handler.drain(from, maxDrain, doDrain);
        }
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        ICablePart part = getPart(from);
        return part instanceof IFluidHandler handler && handler.canFill(from, fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        ICablePart part = getPart(from);
        return part instanceof IFluidHandler handler && handler.canDrain(from, fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        ICablePart part = getPart(from);
        return part instanceof IFluidHandler handler ? handler.getTankInfo(from) : new FluidTankInfo[0];
    }
}
