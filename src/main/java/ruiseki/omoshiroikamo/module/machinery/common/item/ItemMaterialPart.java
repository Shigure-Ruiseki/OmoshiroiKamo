package ruiseki.omoshiroikamo.module.machinery.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Integer, IIcon> icons;

    public ItemMaterialPart(String partType) {
        super(partType);
        this.partType = partType;
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack == null) return super.getUnlocalizedName();

        EnumMaterial material = EnumMaterial.byMetadata(stack.getItemDamage());
        if (material == null) return super.getUnlocalizedName();

        return super.getUnlocalizedName() + "_" + material.getName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new HashMap<>();
        // Ingot textures are in the root modular directory,
        // while others are in their respective subdirectories.
        String folder = partType.equals("ingot") ? "" : partType + "/";
        for (EnumMaterial material : EnumMaterial.values()) {
            icons.put(
                material.getMeta(),
                register
                    .registerIcon(LibResources.PREFIX_MOD + "modular/" + folder + partType + material.getOreName()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        if (icons == null || !icons.containsKey(meta)) {
            return super.getIconFromDamage(meta);
        }
        return icons.get(meta);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (EnumMaterial material : EnumMaterial.values()) {
            list.add(new ItemStack(item, 1, material.getMeta()));
        }
    }
}
