package ruiseki.omoshiroikamo.common.block;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.expanded.BaubleItemHelper;
import baubles.api.expanded.IBaubleExpanded;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import ruiseki.omoshiroikamo.common.entity.EntityDoppleganger;
import ruiseki.omoshiroikamo.common.util.ItemNBTHelper;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemBlockBauble extends ItemBlockOK implements IBaubleExpanded {

    private static final String TAG_HASHCODE = "playerHashcode";
    private static final String TAG_BAUBLE_UUID_MOST = "baubleUUIDMost";
    private static final String TAG_BAUBLE_UUID_LEAST = "baubleUUIDLeast";
    private boolean disableRightClickEquip;

    public ItemBlockBauble(Block blockA, Block blockB) {
        super(blockA, blockB);
        setMaxStackSize(1);
    }

    public ItemBlockBauble(Block blockA) {
        super(blockA);
        setMaxStackSize(1);
    }

    public static UUID getBaubleUUID(ItemStack stack) {
        long most = ItemNBTHelper.getLong(stack, TAG_BAUBLE_UUID_MOST, 0);
        if (most == 0) {
            UUID uuid = UUID.randomUUID();
            ItemNBTHelper.setLong(stack, TAG_BAUBLE_UUID_MOST, uuid.getMostSignificantBits());
            ItemNBTHelper.setLong(stack, TAG_BAUBLE_UUID_LEAST, uuid.getLeastSignificantBits());
            return getBaubleUUID(stack);
        }

        long least = ItemNBTHelper.getLong(stack, TAG_BAUBLE_UUID_LEAST, 0);
        return new UUID(most, least);
    }

    public static void setLastPlayerHashcode(ItemStack stack, int hash) {
        ItemNBTHelper.setInt(stack, TAG_HASHCODE, hash);
    }

    public static int getLastPlayerHashcode(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, TAG_HASHCODE, 0);
    }

    public void disableRightClickEquip() {
        this.disableRightClickEquip = false;
    }

    public void enableRightClickEquip() {
        this.disableRightClickEquip = true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (!disableRightClickEquip) {
            return itemStack;
        }

        if (!EntityDoppleganger.isTruePlayer(player)) {
            return itemStack;
        }

        if (canEquip(itemStack, player)) {
            BaubleItemHelper.onBaubleRightClick(itemStack, world, player);
        }

        return itemStack;
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack p_77630_1_) {
        return false;
    }

    // Bauble
    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return null;
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (player != null) {
            if (!player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(player, LibResources.PREFIX_MOD + "equipBauble", 0.1F, 1.3F);
            }
            onEquippedOrLoadedIntoWorld(stack, player);

            setLastPlayerHashcode(stack, player.hashCode());

        }
    }

    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
        // NO-OP
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        // NO-OP
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (getLastPlayerHashcode(stack) != player.hashCode()) {
            onEquippedOrLoadedIntoWorld(stack, player);
            setLastPlayerHashcode(stack, player.hashCode());
        }
    }

    @Override
    public String[] getBaubleTypes(ItemStack itemstack) {
        return null;
    }

    @Override
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        super.addDetailedEntries(itemstack, entityplayer, list, flag);
        String[] types = getBaubleTypes(itemstack);
        BaubleItemHelper.addSlotInformation(list, types);
    }
}
