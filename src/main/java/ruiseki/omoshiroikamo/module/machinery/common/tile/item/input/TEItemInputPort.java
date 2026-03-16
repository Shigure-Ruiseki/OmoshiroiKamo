package ruiseki.omoshiroikamo.module.machinery.common.tile.item.input;

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
 * Unified Item Input Port TileEntity for all tiers (0-15).
 * Uses tier field instead of separate classes for each tier.
 *
 * This replaces the legacy per-tier TE classes (TEItemInputPortT1-T6).
 * Old TE classes are automatically remapped to this class on world load.
 */
public class TEItemInputPort extends AbstractItemIOPortTE {

    @NBTPersist
    private int tier = 0;

    /**
     * No-arg constructor required for TE instantiation.
     * Tier will be set after construction via setTier().
     */
    public TEItemInputPort() {
        super(1, 0); // Default 1 slot, will be resized when tier is set
    }

    /**
     * Constructor with tier parameter.
     *
     * @param tier Tier level (0-15)
     */
    public TEItemInputPort(int tier) {
        super(MachineryConfig.getItemPortSlots(tier + 1), 0);
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
                slotDefinition.setItemSlots(requiredSlots, 0);
            }
        }
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public String getLocalizedName() {
        // Use format string from lang file: tile.modularItemInput.name=Item Input Port Tier %d
        String unlocalizedName = getUnlocalizedName() + ".name";
        String format = StatCollector.translateToLocal(unlocalizedName);
        return String.format(format, getTier() + 1);
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
    public EnumIO getIOLimit() {
        return EnumIO.INPUT;
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
    public Direction getPortDirection() {
        return Direction.INPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
        }
        if (renderPass == 1) {
            if (getSideIO(side) != EnumIO.NONE) {
                return IconRegistry.getIcon("overlay_iteminput_" + (getTier() + 1));
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
        int currentSlots = getInventorySize();

        if (currentSlots != requiredSlots) {
            // If shrinking, buffer items from removed slots for dropping
            if (currentSlots > requiredSlots) {
                for (int i = requiredSlots; i < currentSlots; i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (stack != null && stack.stackSize > 0) {
                        addPendingDrop(stack);
                    }
                }
            }
            inv.resize(requiredSlots);
            slotDefinition.setItemSlots(requiredSlots, 0);
        }
    }
}
