package ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon;

import static ruiseki.omoshiroikamo.client.render.block.JsonModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMultiBlockBlock;

public class BlockQuantumBeacon extends AbstractMultiBlockBlock<TEQuantumBeacon> {

    protected BlockQuantumBeacon() {
        super(ModObject.blockQuantumBeacon, TEQuantumBeacon.class);
        this.setLightLevel(0.2F);
    }

    public static BlockQuantumBeacon create() {
        return new BlockQuantumBeacon();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockQuantumBeacon.class, name);
        GameRegistry.registerTileEntity(TEQuantumBeaconT1.class, "TENanoBotBeaconT1TileEntity");
        GameRegistry.registerTileEntity(TEQuantumBeaconT2.class, "TENanoBotBeaconT2TileEntity");
        GameRegistry.registerTileEntity(TEQuantumBeaconT3.class, "TENanoBotBeaconT3TileEntity");
        GameRegistry.registerTileEntity(TEQuantumBeaconT4.class, "TENanoBotBeaconT4TileEntity");
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
        list.add(new ItemStack(this, 1, 3));
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderColor(int meta) {
        int rgb;
        switch (meta) {
            case 0:
                rgb = DyeColor.YELLOW.getColor();
                break;
            case 1:
                rgb = DyeColor.LIGHT_BLUE.getColor();
                break;
            case 2:
                rgb = DyeColor.CYAN.getColor();
                break;
            case 3:
                rgb = DyeColor.WHITE.getColor();
                break;
            default:
                rgb = DyeColor.WHITE.getColor();
                break;
        }
        return (0xFF << 24) | ((rgb & 0xFF) << 16) | (rgb & 0xFF00) | ((rgb >> 16) & 0xFF);
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return this.getRenderColor(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        switch (meta) {
            case 3:
                return new TEQuantumBeaconT4();
            case 2:
                return new TEQuantumBeaconT3();
            case 1:
                return new TEQuantumBeaconT2();
            default:
                return new TEQuantumBeaconT1();
        }
    }

    public static class ItemBlockQuantumBeacon extends ItemBlockOK {

        public ItemBlockQuantumBeacon(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

    }

}
