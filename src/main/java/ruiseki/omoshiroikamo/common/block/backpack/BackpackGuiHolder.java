package ruiseki.omoshiroikamo.common.block.backpack;

import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.ceilDiv;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.common.util.item.BaublesUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public abstract class BackpackGuiHolder {

    protected final BackpackHandler handler;
    protected static final int SLOT_SIZE = 18;
    protected final int rowSize;
    protected final int colSize;

    public BackpackGuiHolder(BackpackHandler handler) {
        this.handler = handler;

        int size = handler.getSlots();
        this.rowSize = size > 81 ? 12 : 9;
        this.colSize = ceilDiv(size, rowSize);

    }

    protected BackpackPanel createPanel(PanelSyncManager syncManager, UISettings settings, EntityPlayer player,
        TileEntity tileEntity) {
        return BackpackPanel.defaultPanel(
            syncManager,
            settings,
            player,
            tileEntity,
            handler,
            14 + rowSize * SLOT_SIZE,
            112 + colSize * SLOT_SIZE);
    }

    protected void addCommonWidgets(BackpackPanel panel, EntityPlayer player) {
        panel.addBackpackInventorySlots();
        panel.addUpgradeSlots();
        panel.addUpgradeTabs();
        panel.addTexts(player);
    }

    public static final class TileEntityGuiHolder extends BackpackGuiHolder implements IGuiHolder<PosGuiData> {

        public TileEntityGuiHolder(BackpackHandler handler) {
            super(handler);
        }

        @Override
        public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
            TileEntity tileEntity = data.getTileEntity();
            BackpackPanel panel = createPanel(syncManager, settings, data.getPlayer(), tileEntity);
            addCommonWidgets(panel, data.getPlayer());
            return panel;
        }
    }

    public static final class ItemStackGuiHolder extends BackpackGuiHolder
        implements IGuiHolder<PlayerInventoryGuiData> {

        public ItemStackGuiHolder(BackpackHandler handler) {
            super(handler);
        }

        @Override
        public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
            BackpackPanel panel = createPanel(syncManager, settings, data.getPlayer(), null);

            addCommonWidgets(panel, data.getPlayer());
            panel.modifyPlayerSlot(syncManager, data.getInventoryType(), data.getSlotIndex(), data.getPlayer());

            syncManager.addCloseListener(player -> {
                if (!(player instanceof EntityPlayerMP)) {
                    return;
                }
                ItemStack backpackItem = handler.getBackpack();
                ItemStack current;

                if (data.getInventoryType() == InventoryTypes.PLAYER) {
                    current = player.inventory.mainInventory[data.getSlotIndex()];
                    if (current != null && current.getItem() instanceof BlockBackpack.ItemBackpack) {
                        player.inventory.mainInventory[data.getSlotIndex()] = backpackItem;
                    }
                } else if (data.getInventoryType() == InventoryTypes.BAUBLES && LibMods.Baubles.isLoaded()) {
                    current = BaublesUtils.instance()
                        .getBaubles(player)
                        .getStackInSlot(data.getSlotIndex());
                    if (current != null && current.getItem() instanceof BlockBackpack.ItemBackpack) {
                        BaublesUtils.instance()
                            .getBaubles(player)
                            .setInventorySlotContents(data.getSlotIndex(), backpackItem);
                    }
                }
            });
            return panel;
        }
    }
}
