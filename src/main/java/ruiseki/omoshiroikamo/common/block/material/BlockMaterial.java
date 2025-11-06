package ruiseki.omoshiroikamo.common.block.material;

import static org.apache.commons.lang3.StringUtils.capitalize;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.material.MaterialEntry;
import ruiseki.omoshiroikamo.api.material.MaterialRegistry;
import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;

public class BlockMaterial extends BlockOK {

    @SideOnly(Side.CLIENT)
    protected IIcon icon;

    protected BlockMaterial() {
        super(ModObject.blockBlockMaterial);
        setBlockTextureName("material_block");
    }

    public static BlockMaterial create() {
        return new BlockMaterial();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockMaterial.class, name);

        for (MaterialEntry entry : MaterialRegistry.all()) {
            String matName = entry.getUnlocalizedName();
            int meta = entry.meta;

            registerMaterialOreDict(matName, meta);

            if ("Carbon Steel".equalsIgnoreCase(entry.getName())) {
                registerMaterialOreDict("Steel", meta);
            }
        }
    }

    private void registerMaterialOreDict(String name, int meta) {
        OreDictionary.registerOre("block" + capitalize(name), new ItemStack(this, 1, meta));
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return MaterialRegistry.fromMeta(meta)
            .getHardness();
    }

    @Override
    public float getExplosionResistance(Entity exploder, World world, int x, int y, int z, double explosionX,
        double explosionY, double explosionZ) {
        int meta = world.getBlockMetadata(x, y, z);
        return MaterialRegistry.fromMeta(meta)
            .getResistance();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MaterialEntry materialEntry : MaterialRegistry.all()) {
            int meta = materialEntry.meta;
            list.add(new ItemStack(this, 1, meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        MaterialEntry mat = MaterialRegistry.fromMeta(meta);
        return mat.getColor();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(net.minecraft.world.IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        MaterialEntry mat = MaterialRegistry.fromMeta(meta);
        return mat.getColor();
    }

    public static class ItemBlockMaterial extends ItemBlockOK {

        public ItemBlockMaterial(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int meta = stack.getItemDamage();
            MaterialEntry mat = MaterialRegistry.fromMeta(meta);
            String matName = mat.getUnlocalizedName();
            return super.getUnlocalizedName() + "." + matName;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
            for (MaterialEntry materialEntry : MaterialRegistry.all()) {
                int meta = materialEntry.meta;
                list.add(new ItemStack(this, 1, meta));
            }
        }

    }

}
