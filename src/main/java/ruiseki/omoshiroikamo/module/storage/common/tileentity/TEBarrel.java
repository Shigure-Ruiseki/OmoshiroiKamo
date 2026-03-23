package ruiseki.omoshiroikamo.module.storage.common.tileentity;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import net.minecraft.init.Blocks;

import ruiseki.omoshiroikamo.core.block.state.IOpenState;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;

public class TEBarrel extends TEStorage implements IOpenState {

    @NBTPersist
    private boolean open = false;

    public TEBarrel(int slots, int upgradeSlots) {
        super(slots, upgradeSlots);
    }

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

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new BarrelPanel(data, syncManager, settings, this, wrapper);
    }
}
