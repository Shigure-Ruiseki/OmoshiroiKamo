package ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMBBlock;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockQuantumBeacon extends AbstractMBBlock<TEQuantumBeacon> {

    @SideOnly(Side.CLIENT)
    IIcon cont_tier;

    protected BlockQuantumBeacon() {
        super(ModObject.blockQuantumBeacon.unlocalisedName, TEQuantumBeacon.class);
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
        registerBlockColor();
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
    public void registerBlockIcons(IIconRegister reg) {
        cont_tier = reg.registerIcon(LibResources.PREFIX_MOD + "cont_tier");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.cont_tier;
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
