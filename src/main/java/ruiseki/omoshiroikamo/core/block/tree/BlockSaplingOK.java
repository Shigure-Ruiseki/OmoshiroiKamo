package ruiseki.omoshiroikamo.core.block.tree;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.block.IBlock;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.core.world.gen.WorldGeneratorTree;

public class BlockSaplingOK extends BlockSapling implements IBlock {

    protected final String name;
    protected WorldGeneratorTree treeGenerator;

    public BlockSaplingOK(String name, WorldGeneratorTree treeGenerator) {
        super();
        this.name = name;
        this.treeGenerator = treeGenerator;
        setBlockName(name);
        float f = 0.4F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setHardness(0.0F);
        this.setStepSound(Block.soundTypeGrass);
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, name);
    }

    @Override
    public Block getBlock() {
        return this;
    }

    @Override
    public boolean isHasSubtypes() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        blockIcon = reg.registerIcon(LibResources.PREFIX_MOD + getTextureName());
    }

    public BlockSaplingOK setTextureName(String texture) {
        this.textureName = texture;
        return this;
    }

    @Override
    public String getTextureName() {
        return textureName == null ? name : textureName;
    }

    @Override
    public void func_149879_c(World world, int x, int y, int z, Random rand) {
        if (!TerrainGen.saplingGrowTree(world, rand, x, y, z)) return;
        if (world.isRemote) {
            return;
        }

        world.setBlockToAir(x, y, z);

        if (!treeGenerator.growTree(world, rand, x, y, z)) {
            world.setBlock(x, y, z, this, 0, 4);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }
}
