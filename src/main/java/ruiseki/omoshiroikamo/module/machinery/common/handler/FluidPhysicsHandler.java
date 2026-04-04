package ruiseki.omoshiroikamo.module.machinery.common.handler;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockLiquidBase;
import ruiseki.omoshiroikamo.module.machinery.common.fluid.EnumFluidMaterial;

/**
 * Handles physics for entities inside custom fluids.
 * Uses properties from EnumFluidMaterial (drag, density) to calculate effects.
 */
public class FluidPhysicsHandler {

    private static Field isJumpingField;

    static {
        try {
            // isJumping field in EntityLivingBase
            isJumpingField = EntityLivingBase.class.getDeclaredField("field_70703_bu");
            isJumpingField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            try {
                isJumpingField = EntityLivingBase.class.getDeclaredField("isJumping");
                isJumpingField.setAccessible(true);
            } catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            }
        }
    }

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
    public void onLivingUpdate(LivingUpdateEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if (entity == null) return;

        // Check the block the entity is in at eye level or core level
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.posY + (double) entity.getEyeHeight());
        int z = MathHelper.floor_double(entity.posZ);
        Block block = entity.worldObj.getBlock(x, y, z);

        EnumFluidMaterial mat = getMaterialForBlock(block);

        // If not found at eye level, check at feet level
        if (mat == null) {
            y = MathHelper.floor_double(entity.posY);
            block = entity.worldObj.getBlock(x, y, z);
            mat = getMaterialForBlock(block);
        }

        // Apply our custom physics if matched or inside our specific material
        if (mat != null || entity.isInsideOfMaterial(BlockLiquidBase.materialLiquid)) {
            // Reset fall distance
            entity.fallDistance = 0;

            // Handle properties from EnumFluidMaterial if available
            boolean isGaseous = mat != null && mat.isGaseous();
            float dragValue = mat != null ? mat.getDrag() : (isGaseous ? 0.02F : 0.2F);
            int densityValue = mat != null ? mat.getDensity() : 1000;

            if (!isGaseous) {
                // Apply buoyancy (scaled by density, water = 1000)
                // If density is 1000, adds 0.03D (standard water-like buoyancy)
                double buoyancy = 0.03D * (densityValue / 1000.0D);
                if (entity.motionY < 0.05D) {
                    entity.motionY += buoyancy;
                }

                // Handle swimming (moving up)
                if (isJumpingField != null) {
                    try {
                        if (isJumpingField.getBoolean(entity)) {
                            // Swim power can also be affected by viscosity/drag in the future
                            entity.motionY += 0.04D;
                        }
                    } catch (IllegalAccessException e) {
                        // Ignore
                    }
                }
            }

            // Apply resistance based on drag value
            // motion multiplier = (1.0 - drag)
            double resistance = 1.0D - dragValue;
            entity.motionX *= resistance;
            entity.motionZ *= resistance;
        }
    }
}
