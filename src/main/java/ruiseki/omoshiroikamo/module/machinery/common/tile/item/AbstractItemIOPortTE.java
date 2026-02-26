package ruiseki.omoshiroikamo.module.machinery.common.tile.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.enums.RedstoneMode;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.AbstractStorageTE;
import ruiseki.omoshiroikamo.core.util.SlotDefinition;
import ruiseki.omoshiroikamo.module.machinery.client.gui.widget.RedstoneModeWidget;

/**
 * Item Input Port TileEntity.
 * Holds slots for inputting items into machine processing.
 * Extends AbstractStorageTE to leverage existing inventory management system.
 * TODO: Add auto-sort
 * TODO: enable both IO from NONE side to export catalyst items like GTNH
 */
public abstract class AbstractItemIOPortTE extends AbstractStorageTE implements IModularPort, IGuiHolder<PosGuiData> {

    @NBTPersist
    protected final EnumIO[] sides = new EnumIO[6];

    // Temporary buffer for items to drop when inventory shrinks on config change
    private final List<ItemStack> pendingDrops = new ArrayList<>();

    public AbstractItemIOPortTE(int numInputs, int numOutput) {
        super(new SlotDefinition().setItemSlots(numInputs, numOutput));
        Arrays.fill(sides, EnumIO.NONE);
        // Default IO is NONE, handled by Block.onBlockPlacedBy
    }

    @Override
    public boolean isActive() {
        return false;
    }

    public abstract int getTier();

    public abstract EnumIO getIOLimit();

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public abstract IPortType.Direction getPortDirection();

    @Override
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
    }

    @Override
    public EnumIO getSideIO(ForgeDirection side) {
        if (side == ForgeDirection.UNKNOWN || side.ordinal() >= 6) {
            return EnumIO.NONE;
        }
        return sides[side.ordinal()];
    }

    @Override
    public void setSideIO(ForgeDirection side, EnumIO state) {
        sides[side.ordinal()] = state;
        forceRenderUpdate();
    }

    @Override
    public void readFromNBT(NBTTagCompound root) {
        super.readFromNBT(root);
        // ItemStackHandlerBase resizes to NBT size on load.
        int configSlots = slotDefinition.getItemSlots();
        int currentSlots = inv.getSlots();

        if (currentSlots != configSlots) {
            // If shrinking, buffer items from removed slots
            if (currentSlots > configSlots) {
                for (int i = configSlots; i < currentSlots; i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (stack != null && stack.stackSize > 0) {
                        pendingDrops.add(stack);
                    }
                }
            }
            inv.resize(configSlots);
        }
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void doUpdate() {
        super.doUpdate();
        if (!worldObj.isRemote && !pendingDrops.isEmpty()) {
            for (ItemStack stack : pendingDrops) {
                ItemStackHandlerBase.dropStack(worldObj, xCoord, yCoord, zCoord, stack);
            }
            pendingDrops.clear();
        }
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(LibMisc.MOD_ID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("item_port_gui");

        EnumSyncValue<RedstoneMode> redstoneSyncer = new EnumSyncValue<>(
            RedstoneMode.class,
            this::getRedstoneMode,
            this::setRedstoneMode);
        syncManager.syncValue("redstoneSyncer", redstoneSyncer);

        panel.child(
            new RedstoneModeWidget(redstoneSyncer).pos(-20, 2)
                .size(18)
                .excludeAreaInRecipeViewer());

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

        panel.child(new TileWidget(this.getLocalizedName()));

        panel.child(
            IKey.lang(data.getPlayer().inventory.getInventoryName())
                .asWidget()
                .pos(8, 20 + rows * 18));

        for (int i = 0; i < slots; i++) {
            int x = (i % cols) * 18;
            int y = (i / cols) * 18;
            widget.child(
                new ItemSlot().slot(new ModularSlot(inv, i).slotGroup("inv"))
                    .pos(x, y));
        }
        syncManager.registerSlotGroup("inv", slots, true);

        panel.child(widget);

        return panel;
    }

}
