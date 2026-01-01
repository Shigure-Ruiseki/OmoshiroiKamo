package ruiseki.omoshiroikamo.module.cable.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.ICable;
import ruiseki.omoshiroikamo.api.block.ICableProps;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;

public class TECable extends AbstractTE implements ICableProps {

    protected final boolean[] connections = new boolean[6];

    public TECable() {}

    @Override
    public void writeCommon(NBTTagCompound tag) {
        super.writeCommon(tag);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            tag.setBoolean("conn_" + dir.name(), connections[dir.ordinal()]);
        }
    }

    @Override
    public void readCommon(NBTTagCompound tag) {
        super.readCommon(tag);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            connections[dir.ordinal()] = tag.getBoolean("conn_" + dir.name());
        }
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public boolean canConnect(ICable other, ForgeDirection side) {
        return other != null;
    }

    @Override
    public void updateConnections() {
        if (worldObj == null || worldObj.isRemote) return;

        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            updateSide(dir);
        }
        markDirty();
    }

    private void updateSide(ForgeDirection dir) {
        int x = xCoord + dir.offsetX;
        int y = yCoord + dir.offsetY;
        int z = zCoord + dir.offsetZ;

        TileEntity te = worldObj.getTileEntity(x, y, z);

        boolean shouldConnect =
            te instanceof ICable &&
                ((ICable) te).canConnect(this, dir.getOpposite());

        boolean isConnected = connections[dir.ordinal()];

        if (shouldConnect && !isConnected) {
            reconnect(dir);
        } else if (!shouldConnect && isConnected) {
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
    public ItemStack getItemStack() {
        return new ItemStack(getBlockType());
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        return false;
    }
}
