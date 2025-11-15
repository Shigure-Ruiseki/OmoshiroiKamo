package ruiseki.omoshiroikamo.common.item.trait;

import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.IMobStats;
import ruiseki.omoshiroikamo.api.entity.MobTrait;
import ruiseki.omoshiroikamo.api.entity.TraitUtils;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.ItemNBTUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemSyringe extends ItemOK {

    @SideOnly(Side.CLIENT)
    protected IIcon empty, full;

    public ItemSyringe() {
        super(ModObject.itemSyringe);
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return damage == 0 ? empty : full;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        empty = register.registerIcon(LibResources.PREFIX_MOD + "syringe_empty");
        full = register.registerIcon(LibResources.PREFIX_MOD + "syringe_full");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if (target.worldObj.isRemote || !(target instanceof IMobStats stats)) {
            return false;
        }
        if (stack.getItemDamage() != 0) {
            return false;
        }

        Map<MobTrait, Integer> traits = stats.getTraits();
        if (traits == null || traits.isEmpty()) {
            return false;
        }

        NBTTagCompound tag = ItemNBTUtils.getNBT(stack);
        TraitUtils.writeTraitsNBT(traits, tag);
        stack.setTagCompound(tag);
        stack.setItemDamage(1);
        int slot = player.inventory.currentItem;
        player.inventory.mainInventory[slot] = stack.copy();

        return true;
    }

    @Override
    public void addCommonEntries(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean flag) {
        tooltip.add("meta=" + stack.getItemDamage());
        NBTTagCompound tag = stack.getTagCompound();
        tooltip.add(tag == null ? "NO NBT" : tag.toString());
    }

}
