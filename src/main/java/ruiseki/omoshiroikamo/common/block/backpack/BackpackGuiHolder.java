package ruiseki.omoshiroikamo.common.block.backpack;

import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.ceilDiv;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.factory.inventory.InventoryType;
import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

public abstract class BackpackGuiHolder {

    protected final BackpackHandler handler;
    protected final int rowSize;
    protected final int colSize;

    public BackpackGuiHolder(BackpackHandler handler) {
        this.handler = handler;

        int size = handler.getSlots();
        this.rowSize = size > 81 ? 12 : 9;
        this.colSize = ceilDiv(size, rowSize);

    }

    protected BackpackPanel createPanel(PanelSyncManager syncManager, UISettings settings, EntityPlayer player,
        TileEntity tileEntity, InventoryType type, Integer backpackSlotIndex) {

        int screenWidth = Minecraft.getMinecraft().displayWidth;
        int minWidth = 14 + 9 * ItemSlot.SIZE;
        int maxWidth = 14 + rowSize * ItemSlot.SIZE;
        int width = 6 + Math.max(minWidth, Math.min(maxWidth, screenWidth / 4));
        int height = 115 + colSize * ItemSlot.SIZE;

        return BackpackPanel.defaultPanel(
            syncManager,
            settings,
            player,
            tileEntity,
            handler,
            width,
            height,
            type == InventoryTypes.PLAYER ? backpackSlotIndex : null);
    }

    protected void addCommonWidgets(BackpackPanel panel, EntityPlayer player) {
        panel.addSortingButtons();
        panel.addTransferButtons();
        panel.addBackpackInventorySlots();
        panel.addSearchBar();
        panel.addUpgradeSlots();
        panel.addSettingTab();
        panel.addUpgradeTabs();
        panel.addTexts();
    }

    public static final class TileEntityGuiHolder extends BackpackGuiHolder implements IGuiHolder<PosGuiData> {

        public TileEntityGuiHolder(BackpackHandler handler) {
            super(handler);
        }

        @Override
        public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
            TileEntity tileEntity = data.getTileEntity();
            BackpackPanel panel = createPanel(syncManager, settings, data.getPlayer(), tileEntity, null, null);
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
            BackpackPanel panel = createPanel(
                syncManager,
                settings,
                data.getPlayer(),
                null,
                data.getInventoryType(),
                data.getSlotIndex());
            addCommonWidgets(panel, data.getPlayer());
            panel.modifyPlayerSlot(syncManager, data.getInventoryType(), data.getSlotIndex(), data.getPlayer());

            syncManager.addCloseListener(player -> {
                if (!(player instanceof EntityPlayerMP)) {
                    return;
                }

                ItemStack used = data.getUsedItemStack();
                NBTTagCompound tag = handler.getBackpack()
                    .getTagCompound();

                if (used != null) {
                    used.setTagCompound(tag);
                } else {
                    ItemStack slotStack = player.inventory.getStackInSlot(data.getSlotIndex());
                    if (slotStack != null) {
                        slotStack.setTagCompound(tag);
                    }
                }
            });

            return panel;
        }
    }
}
