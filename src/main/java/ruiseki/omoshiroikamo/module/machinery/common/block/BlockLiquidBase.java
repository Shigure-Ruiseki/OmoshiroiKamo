package ruiseki.omoshiroikamo.module.machinery.common.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.fluid.EnumFluidMaterial;

/**
 * Base block for all fluids (liquids and gases).
 * Uses a custom MaterialLiquid so that:
 * - BlockFluidClassic's canDisplace works correctly (Material.lava blocks it)
 * - Squids do not spawn (Material.water triggers squid spawning checks)
 */
public class BlockLiquidBase extends BlockFluidBase {

    public static final Material materialLiquid = new MaterialLiquid(MapColor.brownColor);

    private EnumFluidMaterial material;

    public BlockLiquidBase(Fluid fluid, Material ignored) {
        super(fluid, materialLiquid);
    }

    public BlockLiquidBase setMaterial(EnumFluidMaterial material) {
        this.material = material;
        return this;
    }

    /**
     * How many blocks the fluid spreads horizontally. Default = 8 (same as water).
     */
    public BlockLiquidBase setQuantaPerBlock(int quanta) {
        this.quantaPerBlock = quanta;
        return this;
    }

    @Override
    protected void applyFluidEffect(World world, int x, int y, int z, Entity entity) {
        if (world.isRemote || !(entity instanceof EntityLivingBase)) return;
        EntityLivingBase living = (EntityLivingBase) entity;

        if (material == EnumFluidMaterial.HELIUM) {
            // Refresh Jump Boost II every tick so it persists while inside
            living.addPotionEffect(new PotionEffect(Potion.jump.id, 10, 1, true));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        return material != null ? material.getColor() : super.colorMultiplier(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta) {
        return material != null ? material.getColor() : super.getRenderColor(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return getFluid().getStillIcon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        IIcon still = register.registerIcon(LibResources.PREFIX_MOD + "modular/fluid_still");
        IIcon flowing = register.registerIcon(LibResources.PREFIX_MOD + "modular/fluid_flow");
        getFluid().setIcons(still, flowing);
    }
}
