package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockLaserCore extends BlockOK {

    @SideOnly(Side.CLIENT)
    IIcon laser_core;

    protected BlockLaserCore() {
        super(ModObject.blockLaserCore.unlocalisedName);
    }

    public static BlockLaserCore create() {
        return new BlockLaserCore();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        laser_core = reg.registerIcon(LibResources.PREFIX_MOD + "laser_core");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.laser_core;
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

}
