package ruiseki.omoshiroikamo.module.multiblock.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;

public class ItemCrystal extends ItemOK {

    public ItemCrystal() {
        super(ModObject.itemCrystal.unlocalisedName);
        setTextureName("crystal");
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
        list.add(new ItemStack(this, 1, 3));
        list.add(new ItemStack(this, 1, 4));
        list.add(new ItemStack(this, 1, 5));
        list.add(new ItemStack(this, 1, 6));
        list.add(new ItemStack(this, 1, 7)); // Celestial Crystal (All Tier, low probability)
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        int meta = stack.getItemDamage();
        switch (meta) {
            case 0:
                return EnumDye.rgbToHex(138, 255, 250);
            case 1:
                return EnumDye.rgbToHex(255, 179, 71);
            case 2:
                return EnumDye.rgbToHex(11, 0, 51);
            case 3:
                return EnumDye.rgbToHex(27, 255, 212);
            case 4:
                return EnumDye.rgbToHex(28, 28, 28);
            case 5:
                return EnumDye.rgbToHex(177, 156, 217);
            case 6:
                return EnumDye.rgbToHex(128, 0, 128);
            case 7:
                return EnumDye.rgbToHex(255, 215, 0);
            default:
                return EnumDye.WHITE.getColor();
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int tier = stack.getItemDamage() + 1;
        return super.getUnlocalizedName() + ".tier_" + tier;
    }
}
