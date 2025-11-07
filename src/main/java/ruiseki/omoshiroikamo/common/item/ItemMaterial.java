package ruiseki.omoshiroikamo.common.item;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.material.MaterialEntry;
import ruiseki.omoshiroikamo.api.material.MaterialRegistry;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.OreDictUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemMaterial extends ItemOK {

    @SideOnly(Side.CLIENT)
    protected IIcon ingotIcon, nuggetIcon, plateIcon, rodIcon, dustIcon, gearIcon;

    public ItemMaterial() {
        super(ModObject.itemItemMaterial);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public static void registerOre() {
        for (MaterialEntry entry : MaterialRegistry.all()) {
            String matName = entry.getUnlocalizedName();
            int meta = entry.meta;

            registerMaterialOreDict(matName, meta);
            registerMaterialConversionRecipes(matName, meta);

            if ("Carbon Steel".equalsIgnoreCase(entry.getName())) {
                registerMaterialOreDict("Steel", meta);
                registerMaterialConversionRecipes("Steel", meta);
            }

        }
    }

    private static void registerMaterialOreDict(String name, int meta) {
        ModItems.MATERIAL.newItemStack(1, LibResources.BASE + meta);
        OreDictionary
            .registerOre("ingot" + capitalize(name), ModItems.MATERIAL.newItemStack(1, LibResources.BASE + meta));
        OreDictionary
            .registerOre("nugget" + capitalize(name), ModItems.MATERIAL.newItemStack(1, LibResources.META1 + meta));
        OreDictionary
            .registerOre("plate" + capitalize(name), ModItems.MATERIAL.newItemStack(1, LibResources.META2 + meta));
        OreDictionary
            .registerOre("rod" + capitalize(name), ModItems.MATERIAL.newItemStack(1, LibResources.META3 + meta));
        OreDictionary
            .registerOre(uncapitalize(name) + "Rod", ModItems.MATERIAL.newItemStack(1, LibResources.META3 + meta));
        OreDictionary
            .registerOre("stick" + capitalize(name), ModItems.MATERIAL.newItemStack(1, LibResources.META3 + meta));
        OreDictionary
            .registerOre("dust" + capitalize(name), ModItems.MATERIAL.newItemStack(1, LibResources.META4 + meta));
        OreDictionary
            .registerOre("gear" + capitalize(name), ModItems.MATERIAL.newItemStack(1, LibResources.META5 + meta));
    }

    private static void registerMaterialConversionRecipes(String oreBaseName, int meta) {
        ItemStack ingot = ModItems.MATERIAL.newItemStack(1, meta);
        ItemStack nugget = ModItems.MATERIAL.newItemStack(1, LibResources.META1 + meta);
        ItemStack nugget9 = nugget.copy();
        nugget9.stackSize = 9;
        ItemStack plate = ModItems.MATERIAL.newItemStack(1, LibResources.META2 + meta);
        ItemStack rod = ModItems.MATERIAL.newItemStack(1, LibResources.META3 + meta);
        ItemStack dust = ModItems.MATERIAL.newItemStack(1, LibResources.META4 + meta);
        ItemStack gear = ModItems.MATERIAL.newItemStack(1, LibResources.META5 + meta);
        ItemStack block = ModBlocks.MATERIAL.newItemStack(1, meta);

        String ingotOre = "ingot" + capitalize(oreBaseName);
        String nuggetOre = "nugget" + capitalize(oreBaseName);
        String plateOre = "plate" + capitalize(oreBaseName);
        String rodOre = "rod" + capitalize(oreBaseName);
        String[] altRodOres = { uncapitalize(oreBaseName) + "Rod", "stick" + capitalize(oreBaseName) };
        String dustOre = "dust" + capitalize(oreBaseName);
        String gearOre = "gear" + capitalize(oreBaseName);
        String blockOre = "block" + capitalize(oreBaseName);

        GameRegistry.addRecipe(new ShapedOreRecipe(ingot, "NNN", "NNN", "NNN", 'N', nugget));
        GameRegistry.addRecipe(new ShapedOreRecipe(block, "III", "III", "III", 'I', ingot));
        GameRegistry.addRecipe(new ShapelessOreRecipe(nugget9, ingot));

        OreDictUtils.registerOreDictConversionToOreDict(ingot, ingotOre);
        OreDictUtils.registerOreDictConversionToOreDict(nugget, nuggetOre);
        OreDictUtils.registerOreDictConversionToOreDict(plate, plateOre);
        OreDictUtils.registerOreDictConversionToOreDict(rod, rodOre);
        for (String altRod : altRodOres) {
            OreDictUtils.registerOreDictConversionToOreDict(rod, altRod);
        }
        OreDictUtils.registerOreDictConversionToOreDict(dust, dustOre);
        OreDictUtils.registerOreDictConversionToOreDict(gear, gearOre);
        OreDictUtils.registerOreDictConversionToOreDict(block, blockOre);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        int baseMeta = meta % LibResources.META1;
        MaterialEntry material = MaterialRegistry.fromMeta(baseMeta);
        String base = super.getUnlocalizedName(stack);

        String type;
        if (meta >= LibResources.META5) {
            type = "gear";
        } else if (meta >= LibResources.META4) {
            type = "dust";
        } else if (meta >= LibResources.META3) {
            type = "rod";
        } else if (meta >= LibResources.META2) {
            type = "plate";
        } else if (meta >= LibResources.META1) {
            type = "nugget";
        } else {
            type = "ingot";
        }

        String mat = material.getUnlocalizedName();
        return base + "." + type + "." + mat;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MaterialEntry materialEntry : MaterialRegistry.all()) {
            int meta = materialEntry.meta;
            // ingot
            list.add(new ItemStack(this, 1, meta));
            // nugget
            list.add(new ItemStack(this, 1, LibResources.META1 + meta));
            // plate
            list.add(new ItemStack(this, 1, LibResources.META2 + meta));
            // rod
            list.add(new ItemStack(this, 1, LibResources.META3 + meta));
            // dust
            list.add(new ItemStack(this, 1, LibResources.META4 + meta));
            // gear
            list.add(new ItemStack(this, 1, LibResources.META5 + meta));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage >= LibResources.META5) {
            return gearIcon;
        }
        if (damage >= LibResources.META4) {
            return dustIcon;
        }
        if (damage >= LibResources.META3) {
            return rodIcon;
        }
        if (damage >= LibResources.META2) {
            return plateIcon;
        }
        if (damage >= LibResources.META1) {
            return nuggetIcon;
        }
        return ingotIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        int meta = stack.getItemDamage() % LibResources.META1;
        MaterialEntry mat = MaterialRegistry.fromMeta(meta);
        return mat.getColor();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg) {
        ingotIcon = reg.registerIcon(LibResources.PREFIX_MOD + "material_ingot");
        nuggetIcon = reg.registerIcon(LibResources.PREFIX_MOD + "material_nugget");
        plateIcon = reg.registerIcon(LibResources.PREFIX_MOD + "material_plate");
        rodIcon = reg.registerIcon(LibResources.PREFIX_MOD + "material_rod");
        dustIcon = reg.registerIcon(LibResources.PREFIX_MOD + "material_dust");
        gearIcon = reg.registerIcon(LibResources.PREFIX_MOD + "material_gear");
    }

}
