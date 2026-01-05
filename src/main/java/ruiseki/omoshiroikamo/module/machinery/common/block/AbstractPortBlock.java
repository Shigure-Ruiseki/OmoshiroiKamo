package ruiseki.omoshiroikamo.module.machinery.common.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.api.modular.ISidedTexture;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTieredBlock;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;

public abstract class AbstractPortBlock<T extends AbstractTE> extends AbstractTieredBlock<T> implements IModularBlock {

    public int renderPass;
    public static IIcon baseIcon;

    @SafeVarargs
    protected AbstractPortBlock(String name, Class<? extends TileEntity>... teClasses) {
        super(name, teClasses);
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        renderPass = pass;
        return pass < 2;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return baseIcon;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (renderPass == 1 && tile instanceof ISidedTexture sided) {
            return sided.getTexture(ForgeDirection.getOrientation(side), renderPass);
        }

        return baseIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        baseIcon = reg.registerIcon(LibResources.PREFIX_MOD + getTextureName());
        registerPortOverlays(reg);
    }

    public abstract void registerPortOverlays(IIconRegister reg);

    protected abstract Class<? extends AbstractPortItemBlock> getItemBlockClass();

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {}

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {}
}
