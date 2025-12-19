package ruiseki.omoshiroikamo.common.item;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import baubles.api.BaubleType;
import baubles.api.expanded.BaubleItemHelper;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.common.Optional;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.common.entity.EntityDoppleganger;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@Optional.InterfaceList({
    @Optional.Interface(modid = "Baubles|Expanded", iface = "baubles.api.expanded.IBaubleExpanded"),
    @Optional.Interface(modid = "Baubles", iface = "baubles.api.IBauble"), })
public class ItemBauble extends ItemOK implements IBaubleExpanded {

    private static final String TAG_HASHCODE = "playerHashcode";
    private static final String TAG_BAUBLE_UUID_MOST = "baubleUUIDMost";
    private static final String TAG_BAUBLE_UUID_LEAST = "baubleUUIDLeast";
    private boolean disableRightClickEquip = false;

    public ItemBauble(String name) {
        super(name);
        setMaxStackSize(1);
    }

    public ItemBauble(ModObject modObject) {
        super(modObject);
    }

    public static UUID getBaubleUUID(ItemStack stack) {
        long most = ItemNBTUtils.getLong(stack, TAG_BAUBLE_UUID_MOST, 0);
        if (most == 0) {
            UUID uuid = UUID.randomUUID();
            ItemNBTUtils.setLong(stack, TAG_BAUBLE_UUID_MOST, uuid.getMostSignificantBits());
            ItemNBTUtils.setLong(stack, TAG_BAUBLE_UUID_LEAST, uuid.getLeastSignificantBits());
            return getBaubleUUID(stack);
        }

        long least = ItemNBTUtils.getLong(stack, TAG_BAUBLE_UUID_LEAST, 0);
        return new UUID(most, least);
    }

    public static void setLastPlayerHashcode(ItemStack stack, int hash) {
        ItemNBTUtils.setInt(stack, TAG_HASHCODE, hash);
    }

    public static int getLastPlayerHashcode(ItemStack stack) {
        return ItemNBTUtils.getInt(stack, TAG_HASHCODE, 0);
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
    @Optional.Method(modid = "Baubles")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return null;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
        return true;
    }

    @Override
    @Optional.Method(modid = "Baubles")
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (player != null) {
            if (!player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(player, LibResources.PREFIX_MOD + "equipBauble", 0.1F, 1.3F);
            }
            onEquippedOrLoadedIntoWorld(stack, player);
            setLastPlayerHashcode(stack, player.hashCode());
        }
    }

    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {}

    @Override
    @Optional.Method(modid = "Baubles")
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {}

    @Override
    @Optional.Method(modid = "Baubles")
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (getLastPlayerHashcode(stack) != player.hashCode()) {
            onEquippedOrLoadedIntoWorld(stack, player);
            setLastPlayerHashcode(stack, player.hashCode());
        }
    }

    @Override
    @Optional.Method(modid = "Baubles|Expanded")
    public String[] getBaubleTypes(ItemStack itemstack) {
        return null;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        super.addInformation(itemstack, entityplayer, list, flag);
        if (GuiScreen.isShiftKeyDown() && LibMods.BaublesExpanded.isLoaded()) {
            String[] types = getBaubleTypes(itemstack);
            BaubleItemHelper.addSlotInformation(list, types);
        }
    }
}
