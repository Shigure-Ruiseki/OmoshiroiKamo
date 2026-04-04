package ruiseki.omoshiroikamo.module.machinery.client.handler;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockLiquidBase;
import ruiseki.omoshiroikamo.module.machinery.common.fluid.EnumFluidMaterial;

/**
 * Handles underwater/fluid fog effects on the client side.
 * Tints fog based on the fluid material at the eye level.
 * Supports shared fluids from other mods.
 */
@SideOnly(Side.CLIENT)
public class FluidFogHandler {

    private EnumFluidMaterial getMaterialForBlock(Block block) {
        if (block instanceof BlockLiquidBase base) {
            return base.getFluidMaterial();
        }

        // Try to look up the fluid for the block
        Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
        if (fluid == null && block instanceof BlockFluidBase base) {
            fluid = base.getFluid();
        }

        if (fluid != null) {
            String name = fluid.getName();
            if (name != null) {
                for (EnumFluidMaterial mat : EnumFluidMaterial.values()) {
                    if (mat.getName()
                        .equalsIgnoreCase(name)) {
                        return mat;
                    }
                }
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        Entity entity = event.entity;
        double eyeY = entity.posY + (double) entity.getEyeHeight();
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(eyeY);
        int z = MathHelper.floor_double(entity.posZ);

        Block block = entity.worldObj.getBlock(x, y, z);
        EnumFluidMaterial mat = getMaterialForBlock(block);
        if (mat != null) {
            int color = mat.getColor();
            event.red = (float) (color >> 16 & 255) / 255.0F;
            event.green = (float) (color >> 8 & 255) / 255.0F;
            event.blue = (float) (color & 255) / 255.0F;
        }
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        Entity entity = event.entity;
        double eyeY = entity.posY + (double) entity.getEyeHeight();
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(eyeY);
        int z = MathHelper.floor_double(entity.posZ);

        Block block = entity.worldObj.getBlock(x, y, z);
        EnumFluidMaterial mat = getMaterialForBlock(block);
        if (mat != null) {
            event.density = mat.isGaseous() ? 0.05F : 0.15F;
        }
    }
}
