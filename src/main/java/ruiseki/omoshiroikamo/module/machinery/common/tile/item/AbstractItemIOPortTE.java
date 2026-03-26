package ruiseki.omoshiroikamo.module.machinery.common.tile.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.enums.RedstoneMode;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
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

    public abstract void setTier(int tier);

    public abstract EnumIO getIOLimit();

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public abstract IPortType.Direction getPortDirection();

    @Override
    public String getLocalizedName() {
        return LangHelpers.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
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
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    /**
     * Adds an item stack to pending drops buffer.
     * Items in this buffer will be dropped in the world on next update.
     */
    protected void addPendingDrop(ItemStack stack) {
        if (stack != null && stack.stackSize > 0) {
            pendingDrops.add(stack);
        }
    }

    /**
     * Gets the current inventory size.
     * Helper method for subclasses.
     */
    protected int getInventorySize() {
        return getSizeInventory();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        // Dynamically generate slot array to handle tier changes
        int size = getSizeInventory();
        int[] slots = new int[size];
        for (int i = 0; i < size; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public void onContentsChange(int slot) {
        if (worldObj != null && !worldObj.isRemote) {
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

        IntSyncValue sortTrigger = new IntSyncValue(() -> 0, val -> { if (val > 0) sortInventory(); });
        syncManager.syncValue("sortTrigger", sortTrigger);

        // Sort Button
        panel.child(
            new ButtonWidget<>().size(18)
                .pos(-20, 24)
                .overlay(OKGuiTextures.SOLID_DOWN_ARROW_ICON)
                .onMousePressed(button -> {
                    if (button == 0) {
                        Interactable.playButtonClickSound();
                        sortTrigger.setValue(sortTrigger.getValue() + 1);
                        return true;
                    }
                    return false;
                })
                .tooltipStatic(t -> t.addLine(IKey.lang("gui.sort_inventory"))));

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
        syncManager.registerSlotGroup(new SlotGroup("inv", slots, 100, true));

        panel.child(widget);

        return panel;
    }

    public void sortInventory() {
        int min = slotDefinition.getMinItemOutput();
        int max = slotDefinition.getMaxItemOutput();
        if (min < 0) {
            // If it's pure input port (input ports usually have different min/max though)
            min = slotDefinition.getMinItemInput();
            max = slotDefinition.getMaxItemInput();
        }
        if (min < 0) return;

        List<ItemStack> allItems = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null) {
                allItems.add(stack.copy());
                setInventorySlotContents(i, null);
            }
        }

        if (allItems.isEmpty()) return;

        // Group items by their identity while preserving discovery order
        Map<ItemKey, Long> counts = new LinkedHashMap<>();
        for (ItemStack stack : allItems) {
            ItemKey key = new ItemKey(stack);
            counts.put(key, counts.getOrDefault(key, 0L) + stack.stackSize);
        }

        List<ItemStack> merged = new ArrayList<>();
        for (Map.Entry<ItemKey, Long> entry : counts.entrySet()) {
            ItemKey key = entry.getKey();
            long total = entry.getValue();
            int maxStack = key.toStack(1)
                .getMaxStackSize();

            while (total > 0) {
                int size = (int) Math.min(total, maxStack);
                merged.add(key.toStack(size));
                total -= size;
            }
        }

        // Write back
        for (int i = 0; i < merged.size() && (min + i) <= max; i++) {
            setInventorySlotContents(min + i, merged.get(i));
        }

        markDirty();
        if (worldObj != null && !worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void accept(IRecipeVisitor visitor) {}

    private static class ItemKey {

        final Item item;
        final int meta;
        final NBTTagCompound tag;

        ItemKey(ItemStack stack) {
            this.item = stack.getItem();
            this.meta = stack.getItemDamage();
            this.tag = stack.hasTagCompound() ? (NBTTagCompound) stack.getTagCompound()
                .copy() : null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemKey other)) return false;
            return item == other.item && meta == other.meta
                && (tag == null ? other.tag == null : tag.equals(other.tag));
        }

        @Override
        public int hashCode() {
            int result = item.hashCode();
            result = 31 * result + meta;
            result = 31 * result + (tag != null ? tag.hashCode() : 0);
            return result;
        }

        ItemStack toStack(int size) {
            ItemStack stack = new ItemStack(item, size, meta);
            if (tag != null) {
                stack.setTagCompound((NBTTagCompound) tag.copy());
            }
            return stack;
        }
    }

    @Override
    public int getAssignedIndex() {
        return assignedIndex;
    }

    @Override
    public void setAssignedIndex(int index) {
        this.assignedIndex = index;
    }
}
