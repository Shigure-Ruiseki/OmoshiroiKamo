package ruiseki.omoshiroikamo.module.machinery.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibResources;

/**
 * Define the type of intermediate material.
 * This is a template of new materials.
 */
public class ItemMaterialPart extends ItemOK {

    private final String partType; // "ingot", "plate", "gear"

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public ItemMaterialPart(String partType) {
        super(partType);
        this.partType = partType;
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        EnumMaterial material = EnumMaterial.byMetadata(stack.getItemDamage());
        return super.getUnlocalizedName() + "_" + material.getName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        EnumMaterial[] materials = EnumMaterial.values();
        icons = new IIcon[materials.length];
        for (int i = 0; i < materials.length; i++) {
            icons[i] = register.registerIcon(LibResources.PREFIX_MOD + partType + "_" + materials[i].getName());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (icons == null || meta < 0 || meta >= icons.length) {
            return super.getIconFromDamage(meta);
        }
        return icons[meta];
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (EnumMaterial material : EnumMaterial.values()) {
            list.add(new ItemStack(item, 1, material.getMeta()));
        }
    }
}
