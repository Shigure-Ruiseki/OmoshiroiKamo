package ruiseki.omoshiroikamo.module.backpack.client.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.widgets.slot.InventoryCraftingWrapper;

import cpw.mods.fml.common.FMLCommonHandler;
import lombok.Setter;
import ruiseki.omoshiroikamo.core.client.gui.slot.ModularCraftingMatrixSlot;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackHandler;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.ICraftingUpgrade;

public class ModularBackpackCraftingSlot extends ModularCraftingMatrixSlot {

    @Setter
    private InventoryCraftingWrapper craftMatrix;
    private int amountCrafted;
    private final BackpackHandler handler;
    private final int slotIndex;

    public ModularBackpackCraftingSlot(IItemHandler itemHandler, int index, BackpackHandler handler, int slotIndex) {
        super(itemHandler, index);
        this.handler = handler;
        this.slotIndex = slotIndex;
        setActive(false);
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.amountCrafted += Math.min(amount, this.getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    @Override
    protected void onCrafting(ItemStack stack, int amount) {
        this.amountCrafted += amount;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    @Override
    protected void onCrafting(ItemStack p_75208_1_) {
        p_75208_1_.onCrafting(getPlayer().worldObj, getPlayer(), this.amountCrafted);
        this.amountCrafted = 0;

        if (p_75208_1_.getItem() == Item.getItemFromBlock(Blocks.crafting_table)) {
            getPlayer().addStat(AchievementList.buildWorkBench, 1);
        }

        if (p_75208_1_.getItem() instanceof ItemPickaxe) {
            getPlayer().addStat(AchievementList.buildPickaxe, 1);
        }

        if (p_75208_1_.getItem() == Item.getItemFromBlock(Blocks.furnace)) {
            getPlayer().addStat(AchievementList.buildFurnace, 1);
        }

        if (p_75208_1_.getItem() instanceof ItemHoe) {
            getPlayer().addStat(AchievementList.buildHoe, 1);
        }

        if (p_75208_1_.getItem() == Items.bread) {
            getPlayer().addStat(AchievementList.makeBread, 1);
        }

        if (p_75208_1_.getItem() == Items.cake) {
            getPlayer().addStat(AchievementList.bakeCake, 1);
        }

        if (p_75208_1_.getItem() instanceof ItemPickaxe
            && ((ItemPickaxe) p_75208_1_.getItem()).func_150913_i() != Item.ToolMaterial.WOOD) {
            getPlayer().addStat(AchievementList.buildBetterPickaxe, 1);
        }

        if (p_75208_1_.getItem() instanceof ItemSword) {
            getPlayer().addStat(AchievementList.buildSword, 1);
        }

        if (p_75208_1_.getItem() == Item.getItemFromBlock(Blocks.enchanting_table)) {
            getPlayer().addStat(AchievementList.enchantments, 1);
        }

        if (p_75208_1_.getItem() == Item.getItemFromBlock(Blocks.bookshelf)) {
            getPlayer().addStat(AchievementList.bookcase, 1);
        }
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        if (stack != null && stack.getItem() != null) {
            FMLCommonHandler.instance()
                .firePlayerCraftingEvent(player, stack, craftMatrix);
            onCrafting(stack);
        }

        ICraftingUpgrade wrapper = handler != null ? handler.gatherCapabilityUpgrades(ICraftingUpgrade.class)
            .get(slotIndex) : null;

        for (int i = 0; i < craftMatrix.getSizeInventory() - 1; i++) {
            ItemStack slotStack = craftMatrix.getStackInSlot(i);
            if (slotStack == null) continue;

            ItemStack original = slotStack.copy();
            boolean extractedFromHandler = false;

            if (wrapper != null && wrapper.isUseBackpack()) {
                ItemStack extracted = handler.extractItem(slotStack, 1, false);
                if (extracted != null) {
                    extractedFromHandler = true;
                }
            }

            if (!extractedFromHandler) {
                slotStack.stackSize--;
                if (slotStack.stackSize <= 0) slotStack = null;
                craftMatrix.setInventorySlotContents(i, slotStack);
            }

            if (original.getItem()
                .hasContainerItem(original)) {
                ItemStack cont = original.getItem()
                    .getContainerItem(original);
                if (cont != null && cont.isItemStackDamageable() && cont.getItemDamage() > cont.getMaxDamage()) {
                    MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, cont));
                } else if (cont != null) {
                    if (craftMatrix.getStackInSlot(i) == null) {
                        craftMatrix.setInventorySlotContents(i, cont);
                    } else if (!player.inventory.addItemStackToInventory(cont)) {
                        player.dropPlayerItemWithRandomChoice(cont, false);
                    }
                }
            }
        }

        craftMatrix.notifyContainer();
    }

    public void updateResult(ItemStack stack) {
        if (!getPlayer().worldObj.isRemote) {
            putStack(stack);
            getSyncHandler().forceSyncItem();
        }
    }
}
