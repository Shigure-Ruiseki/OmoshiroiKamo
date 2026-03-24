package ruiseki.omoshiroikamo.module.backpack.common.block;

import static ruiseki.omoshiroikamo.core.common.util.MathUtils.ceilDiv;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.factory.inventory.InventoryType;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;

public abstract class BackpackGuiHolder {

    protected final BackpackWrapper wrapper;
    protected final int rowSize;
    protected final int colSize;

    public BackpackGuiHolder(BackpackWrapper wrapper) {
        this.wrapper = wrapper;

        int size = wrapper.getSlots();
        this.rowSize = size > 81 ? 12 : 9;
        this.colSize = ceilDiv(size, rowSize);

    }

    protected BackpackPanel createPanel(PanelSyncManager syncManager, UISettings settings, EntityPlayer player,
        TileEntity tileEntity, InventoryType type, Integer backpackSlotIndex) {

        int width = 20 + rowSize * ItemSlot.SIZE;
        int height = 115 + colSize * ItemSlot.SIZE;

        if (backpackSlotIndex != null) wrapper.setSlotIndex(backpackSlotIndex);
        if (type != null) wrapper.setType(type);

        return new BackpackPanel(player, tileEntity, syncManager, settings, wrapper, width, height, backpackSlotIndex);
    }

    protected void addCommonWidgets(BackpackPanel panel) {
        panel.addSortingButtons();
        panel.addTransferButtons();
        panel.addBackpackInventorySlots();
        panel.addSearchBar();
        panel.addUpgradeSlots();
        panel.addSettingTab();
        panel.addUpgradeTabs();
        panel.addTexts();
    }

    public static final class TileEntityGuiHolder extends BackpackGuiHolder implements IGuiHolder<SidedPosGuiData> {

        public TileEntityGuiHolder(BackpackWrapper wrapper) {
            super(wrapper);
        }

        @Override
        public ModularScreen createScreen(SidedPosGuiData data, ModularPanel mainPanel) {
            return new ModularScreen(LibMisc.MOD_ID, mainPanel);
        }

        @Override
        public ModularPanel buildUI(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
            TileEntity tileEntity = data.getTileEntity();
            BackpackPanel panel = createPanel(syncManager, settings, data.getPlayer(), tileEntity, null, null);
            addCommonWidgets(panel);
            return panel;
        }
    }

    public static final class ItemStackGuiHolder extends BackpackGuiHolder
        implements IGuiHolder<PlayerInventoryGuiData> {

        public ItemStackGuiHolder(BackpackWrapper wrapper) {
            super(wrapper);
        }

        @Override
        public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
            return new ModularScreen(LibMisc.MOD_ID, mainPanel);
        }

        @Override
        public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
            BackpackPanel panel = createPanel(
                syncManager,
                settings,
                data.getPlayer(),
                null,
                data.getInventoryType(),
                data.getSlotIndex());
            addCommonWidgets(panel);
            panel.modifyPlayerSlot(syncManager, data.getInventoryType(), data.getSlotIndex(), data.getPlayer());

            return panel;
        }
    }
}
