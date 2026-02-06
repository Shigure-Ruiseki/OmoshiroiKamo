package ruiseki.omoshiroikamo.module.multiblock.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;

public class ItemCrystal extends ItemOK {

    public static final int VARIATIONS = 8;

    public ItemCrystal() {
        super(ModObject.itemCrystal.unlocalisedName);
        setTextureName("crystal");
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (int i = 0; i < VARIATIONS; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        int meta = stack.getItemDamage();
        return switch (meta) {
            case 0 -> EnumDye.rgbToHex(138, 255, 250);
            case 1 -> EnumDye.rgbToHex(255, 179, 71);
            case 2 -> EnumDye.rgbToHex(11, 0, 51);
            case 3 -> EnumDye.rgbToHex(27, 255, 212);
            case 4 -> EnumDye.rgbToHex(28, 28, 28);
            case 5 -> EnumDye.rgbToHex(177, 156, 217);
            case 6 -> EnumDye.rgbToHex(128, 0, 128);
            case 7 -> EnumDye.rgbToHex(255, 215, 0);
            default -> EnumDye.WHITE.getColor();
        };
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int tier = stack.getItemDamage() + 1;
        return super.getUnlocalizedName() + ".tier_" + tier;
    }
}
