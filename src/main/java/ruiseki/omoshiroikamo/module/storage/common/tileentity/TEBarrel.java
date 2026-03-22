package ruiseki.omoshiroikamo.module.storage.common.tileentity;

import net.minecraft.init.Blocks;

import lombok.experimental.Delegate;
import ruiseki.omoshiroikamo.core.block.state.IOpenState;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;

public class TEBarrel extends TileEntityOK implements IOpenState, TileEntityOK.ITickingTile {

    @NBTPersist
    private boolean open = false;

    @Delegate
    private final ITickingTile ticking = new TickingTileComponent(this);

    public TEBarrel() {}

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
        this.onSendUpdate();
        this.worldObj.notifyBlockOfNeighborChange(xCoord, yCoord, zCoord, Blocks.air);
    }
}
