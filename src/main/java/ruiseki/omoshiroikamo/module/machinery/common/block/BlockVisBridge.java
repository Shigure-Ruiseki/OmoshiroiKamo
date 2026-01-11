package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.TileVisBridge;

/**
 * Vis Bridge block - bridges Vis from Vis Output Port to Thaumcraft Vis
 * network.
 * When placed, it absorbs Vis from the block face that was clicked.
 */
public class BlockVisBridge extends Block {

    public BlockVisBridge() {
        super(Material.iron);
        setBlockName(ModObject.blockVisBridge.unlocalisedName);
        setHardness(5.0F);
        setResistance(10.0F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 0);
        // setCreativeTab(LibMisc.MACHINERY_TAB);
        // Use base port texture for now
        setBlockTextureName(LibMisc.MOD_ID + ":modularmachineryOverlay/base_modularports");
    }

    public static BlockVisBridge create() {
        BlockVisBridge block = new BlockVisBridge();
        GameRegistry.registerBlock(block, ModObject.blockVisBridge.unlocalisedName);
        GameRegistry.registerTileEntity(TileVisBridge.class, ModObject.blockVisBridge.unlocalisedName + "_TE");
        return block;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileVisBridge();
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
        int metadata) {
        // Store the clicked side in metadata temporarily
        return side;
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int metadata) {
        if (world.isRemote) return;

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileVisBridge) {
            // Use the clicked side (stored in metadata) as absorption direction
            ForgeDirection clickedSide = ForgeDirection.getOrientation(metadata);
            ((TileVisBridge) te).setAbsorptionDirection(clickedSide.getOpposite());
        }
        // Reset metadata to 0
        world.setBlockMetadataWithNotify(x, y, z, 0, 2);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileVisBridge) {
            ((TileVisBridge) te).removeThisNode();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        return false;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this, 1, 0));
        return drops;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (world.isRemote) return;

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileVisBridge) {
            ((TileVisBridge) te).onNeighborBlockChange(world, x, y, z, neighbor);
        }
    }
}
