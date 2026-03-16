package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.item.ItemTransfer;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

/**
 * Unified Item Output Port TileEntity for all tiers (0-15).
 * Uses tier field instead of separate classes for each tier.
 *
 * This replaces the legacy per-tier TE classes (TEItemOutputPortT1-T6).
 * Old TE classes are automatically remapped to this class on world load.
 */
public class TEItemOutputPort extends AbstractItemIOPortTE {

    @NBTPersist
    private int tier = 0;

    /**
     * No-arg constructor required for TE instantiation.
     * Tier will be set after construction via setTier().
     */
    public TEItemOutputPort() {
        super(0, 1); // Default 1 output slot, will be resized when tier is set
    }

    /**
     * Constructor with tier parameter.
     *
     * @param tier Tier level (0-15)
     */
    public TEItemOutputPort(int tier) {
        super(0, MachineryConfig.getItemPortSlots(tier + 1));
        this.tier = tier;
    }

    /**
     * Sets the tier and resizes inventory accordingly.
     * Called after TE creation when placed from ItemStack.
     */
    public void setTier(int tier) {
        if (this.tier != tier) {
            this.tier = tier;
            int requiredSlots = MachineryConfig.getItemPortSlots(tier + 1);
            if (inv.getSlots() != requiredSlots) {
                inv.resize(requiredSlots);
                slotDefinition.setItemSlots(0, requiredSlots);
            }
        }
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public String getLocalizedName() {
        // Use format string from lang file: tile.modularItemOutput.name=Item Output Port Tier %d
        String unlocalizedName = getUnlocalizedName() + ".name";
        String format = StatCollector.translateToLocal(unlocalizedName);
        return String.format(format, getTier() + 1);
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.OUTPUT;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if (!canOutput(dir)) {
            return false;
        }
        return super.canExtractItem(slot, itemstack, side);
    }

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        if (isRedstoneActive()) {
            ItemTransfer transfer = new ItemTransfer();
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                if (!getSideIO(direction).canOutput()) {
                    continue;
                }
                TileEntity sink = getPos().offset(direction)
                    .getTileEntity(worldObj);
                transfer.push(this, direction, sink);
                transfer.transfer();
            }
        }

        return super.processTasks(redstoneChecksPassed);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return false; // Output only, no external insertion
    }

    /**
     * Try to insert an item into the output slot.
     * Used internally by the machine controller.
     *
     * @return true if successful, false if slot is full
     */
    public boolean insertItem(ItemStack stack) {
        int outputSlot = slotDefinition.getMinItemOutput();
        if (outputSlot < 0) return false;

        ItemStack existing = inv.getStackInSlot(outputSlot);
        if (existing == null) {
            inv.setStackInSlot(outputSlot, stack.copy());
            return true;
        }

        if (existing.isItemEqual(stack) && existing.stackSize + stack.stackSize <= getInventoryStackLimit()) {
            existing.stackSize += stack.stackSize;
            inv.setStackInSlot(outputSlot, existing);
            return true;
        }

        return false;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
        }
        if (renderPass == 1) {
            if (getSideIO(side) != EnumIO.NONE) {
                return IconRegistry.getIcon("overlay_itemoutput_" + (getTier() + 1));
            }
            return null;
        }
        return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        // Ensure inventory matches tier after loading from NBT
        int requiredSlots = MachineryConfig.getItemPortSlots(tier + 1);
        if (inv.getSlots() != requiredSlots) {
            inv.resize(requiredSlots);
            slotDefinition.setItemSlots(0, requiredSlots);
        }
    }
}
