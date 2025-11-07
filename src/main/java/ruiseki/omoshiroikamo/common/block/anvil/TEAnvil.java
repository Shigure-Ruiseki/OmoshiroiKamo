package ruiseki.omoshiroikamo.common.block.anvil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTaskTE;
import ruiseki.omoshiroikamo.common.item.ItemHammer;

public class TEAnvil extends AbstractTaskTE {

    public TEAnvil() {
        super(new SlotDefinition().setItemSlots(1, 9));
    }

    @Override
    public String getMachineName() {
        return ModObject.blockAnvil.unlocalisedName;
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        if (world.isRemote) {
            return true;
        }

        ItemStack held = player.getHeldItem();
        boolean isSneaking = player.isSneaking();

        if (held == null && isSneaking) {
            boolean didAnything = false;

            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack != null) {
                    if (!player.inventory.addItemStackToInventory(stack)) {
                        player.dropPlayerItemWithRandomChoice(stack, false);
                    }
                    inv.setStackInSlot(i, null);
                    didAnything = true;
                }
            }

            if (didAnything) {
                markDirty();
                world.markBlockForUpdate(xCoord, yCoord, zCoord);
                player.inventoryContainer.detectAndSendChanges();
            }
            return didAnything;
        }

        if (held == null) {
            for (int i = 1; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack != null) {
                    if (!player.inventory.addItemStackToInventory(stack)) {
                        player.dropPlayerItemWithRandomChoice(stack, false);
                    }
                    inv.setStackInSlot(i, null);
                    markDirty();
                    world.markBlockForUpdate(xCoord, yCoord, zCoord);
                    player.inventoryContainer.detectAndSendChanges();
                    return true;
                }
            }

            ItemStack input = inv.getStackInSlot(0);
            if (input != null) {
                if (!player.inventory.addItemStackToInventory(input)) {
                    player.dropPlayerItemWithRandomChoice(input, false);
                }
                inv.setStackInSlot(0, null);
                markDirty();
                world.markBlockForUpdate(xCoord, yCoord, zCoord);
                player.inventoryContainer.detectAndSendChanges();
                return true;
            }

            return false;
        }

        ItemStack existing = inv.getStackInSlot(0);

        if (held.getItem() instanceof ItemHammer) {
            if (existing != null) {
                this.addStage(1);
                held.damageItem(1, player);
                return true;
            } else {
                return false;
            }
        }

        int maxStackSize = held.getMaxStackSize();

        if (canInsertItem(0, held, side.ordinal())) {
            if (existing == null) {
                int insertCount = Math.min(held.stackSize, maxStackSize);
                ItemStack insert = held.copy();
                insert.stackSize = insertCount;
                inv.setStackInSlot(0, insert);
                held.stackSize -= insertCount;
            } else if (existing.isItemEqual(held) && ItemStack.areItemStackTagsEqual(existing, held)) {
                int canAdd = Math.min(held.stackSize, maxStackSize - existing.stackSize);
                if (canAdd <= 0) {
                    return false;
                }

                existing.stackSize += canAdd;
                inv.setStackInSlot(0, existing);
                held.stackSize -= canAdd;
            } else {
                return false;
            }

            if (held.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            } else {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, held);
            }

            markDirty();
            world.markBlockForUpdate(xCoord, yCoord, zCoord);
            player.inventoryContainer.detectAndSendChanges();
            return true;
        }

        return false;
    }

    @Override
    public void doUpdate() {
        super.doUpdate();
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
    }

    @Override
    protected boolean processTasks(boolean redstoneChecksPassed) {
        if (currentTask == null) {
            stage = 0f;
        }
        return super.processTasks(redstoneChecksPassed);
    }

    @Override
    protected void taskComplete() {
        super.taskComplete();
        stage = 0f;
        if (!worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack contents) {
        super.setInventorySlotContents(slot, contents);
        markDirty();
        if (!worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }
}
