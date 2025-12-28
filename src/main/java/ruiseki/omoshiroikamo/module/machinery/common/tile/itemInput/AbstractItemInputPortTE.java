package ruiseki.omoshiroikamo.module.machinery.common.tile.itemInput;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.gtnewhorizon.gtnhlib.item.ItemTransfer;

import ruiseki.omoshiroikamo.api.io.ISidedIO;
import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.api.redstone.RedstoneMode;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractStorageTE;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.client.gui.widget.RedstoneModeWidget;

/**
 * Item Input Port TileEntity.
 * Holds slots for inputting items into machine processing.
 * Extends AbstractStorageTE to leverage existing inventory management system.
 */
public abstract class AbstractItemInputPortTE extends AbstractStorageTE implements ISidedIO {

    private final IO[] sides = new IO[6];

    public AbstractItemInputPortTE(int numInputs) {
        super(new SlotDefinition().setItemSlots(numInputs, 0));
        for (int i = 0; i < 6; i++) {
            sides[i] = IO.INPUT;
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slotDefinition.isInputSlot(slot);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if (!canInput(dir)) {
            return false;
        }
        return super.canInsertItem(slot, itemstack, side);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
        return false;
    }

    @Override
    public IO getSideIO(ForgeDirection side) {
        return sides[side.ordinal()];
    }

    @Override
    public void setSideIO(ForgeDirection side, IO state) {
        sides[side.ordinal()] = state;
        requestRenderUpdate();
        Logger.info(getSideIO(side).name());
    }

    @Override
    public IO getIOLimit() {
        return IO.INPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        if (isRedstoneActive() && inv.hasEmptySlot()) {
            ItemTransfer transfer = new ItemTransfer();
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                if (!getSideIO(direction).canInput()) {
                    continue;
                }
                TileEntity source = getPos().offset(direction)
                    .getTileEntity(worldObj);
                transfer.pull(this, direction, source);
                transfer.transfer();
            }
        }

        return super.processTasks(redstoneChecksPassed);
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        int[] sideData = new int[6];
        for (int i = 0; i < 6; i++) {
            sideData[i] = sides[i].ordinal();
        }
        root.setIntArray("sideIO", sideData);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        if (root.hasKey("sideIO")) {
            int[] sideData = root.getIntArray("sideIO");
            for (int i = 0; i < 6 && i < sideData.length; i++) {
                sides[i] = IO.values()[sideData[i]];
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("input_port_gui");

        EnumSyncValue<RedstoneMode> redstoneSyncer = new EnumSyncValue<>(
            RedstoneMode.class,
            this::getRedstoneMode,
            this::setRedstoneMode);
        syncManager.syncValue("redstoneSyncer", redstoneSyncer);

        panel.child(
            new RedstoneModeWidget(redstoneSyncer).pos(-20, 0)
                .size(18));

        syncManager.bindPlayerInventory(data.getPlayer());
        panel.bindPlayerInventory();
        int slots = slotDefinition.getItemSlots();

        int cols;
        if (slots <= 9) {
            cols = (int) Math.ceil(Math.sqrt(slots));
        } else if (slots <= 16) {
            cols = 4;
        } else if (slots <= 32) {
            cols = 8;
        } else {
            cols = 9;
        }

        int rows = (int) Math.ceil((double) slots / cols);

        panel.height(rows * 18 + 114);
        SlotGroupWidget widget = new SlotGroupWidget().coverChildren()
            .alignX(0.5f)
            .topRel(0.15f);

        for (int i = 0; i < slots; i++) {
            int x = (i % cols) * 18;
            int y = (i / cols) * 18;
            widget.child(
                new ItemSlot().slot(new ModularSlot(inv, i))
                    .pos(x, y));
        }

        panel.child(widget);

        return panel;
    }
}
