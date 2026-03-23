package ruiseki.omoshiroikamo.module.storage.common.tileentity;

import net.minecraft.init.Blocks;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.omoshiroikamo.core.block.state.IOpenState;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;

public class TEBarrel extends TEStorage implements IOpenState {

    @NBTPersist
    private boolean open = false;

    public TEBarrel() {
        super();
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
        int baseRowSize = wrapper.getSlots() > 81 ? 12 : 9;
        int width = 20 + baseRowSize * ItemSlot.SIZE;
        StoragePanel panel = new BarrelPanel(data, syncManager, settings, this, wrapper, width);
        panel.addSortingButtons();
        panel.addTransferButtons();
        panel.addStorageInventorySlots();
        panel.addSearchBar();
        panel.addUpgradeSlots();
        panel.addSettingTab();
        panel.addUpgradeTabs();
        panel.addTexts();
        return panel;
    }
}
