package ruiseki.omoshiroikamo.common.block.chicken;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.gtnewhorizon.gtnhlib.item.ItemTransfer;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.client.gui.modularui2.chicken.ChickenContainer;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractStorageTE;
import ruiseki.omoshiroikamo.common.util.BlockPos;
import ruiseki.omoshiroikamo.common.util.item.ItemUtils;

public class TERoostCollector extends AbstractStorageTE {

    private int searchOffset = 0;

    public TERoostCollector() {
        super(new SlotDefinition().setItemSlots(0, 27));
    }

    @Override
    protected void doUpdate() {
        super.doUpdate();
        if (!worldObj.isRemote) {}
        updateSearchOffset();
        gatherItems();
    }

    private void updateSearchOffset() {
        searchOffset = (searchOffset + 1) % 27;
    }

    private void gatherItems() {
        for (int x = -4; x < 5; x++) {
            int y = searchOffset / 9;
            int z = (searchOffset % 9) - 4;
            BlockPos targetPos = new BlockPos(xCoord + x, yCoord + y, zCoord + z, worldObj);
            gatherItemAtPos(targetPos);
        }
    }

    private void gatherItemAtPos(BlockPos pos) {
        TileEntity tileEntity = pos.getTileEntity();
        if (!(tileEntity instanceof TERoost teRoost)) {
            return;
        }
        ItemTransfer transfer = new ItemTransfer();
        transfer.pull(this, ForgeDirection.DOWN, teRoost);
        transfer.transfer();
    }

    @Override
    public String getMachineName() {
        return ModObject.blockRoostCollector.unlocalisedName;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY, float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
        ItemStack existing = inv.getStackInSlot(slot);
        if (existing != null) {
            return ItemUtils.areStackMergable(existing, itemstack);
        }
        return isItemValidForSlot(slot, itemstack);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("roost_gui");
        settings.customContainer(ChickenContainer::new);
        panel.bindPlayerInventory();

        panel.child(
            Flow.column()
                .child(
                    IKey.str(StatCollector.translateToLocal("tile." + getMachineName() + ".name"))
                        .asWidget()
                        .margin(6, 0, 5, 0)
                        .align(Alignment.TopLeft))
                .child(
                    SlotGroupWidget.builder()
                        .row("OOOOOOOOO")
                        .row("OOOOOOOOO")
                        .row("OOOOOOOOO")
                        .key('O', index -> new ItemSlot().slot(new ModularSlot(inv, index).accessibility(false, true)))
                        .build()
                        .topRel(0.15f)
                        .alignX(Alignment.CENTER)));

        panel.child(new Column().coverChildren());

        return panel;
    }
}
