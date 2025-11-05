package ruiseki.omoshiroikamo.common.init;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.common.OKCreativeTab;
import ruiseki.omoshiroikamo.common.fluid.BlockFluidOk;
import ruiseki.omoshiroikamo.common.fluid.FluidMaterialRegister;
import ruiseki.omoshiroikamo.common.fluid.FluidRegister;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class ModFluids {

    public static String toCapactityString(IFluidTank tank) {
        if (tank == null) {
            return "0/0 " + MB();
        }
        return tank.getFluidAmount() + "/" + tank.getCapacity() + " " + MB();
    }

    public static String MB() {
        return LibMisc.LANG.localize("fluid.millibucket.abr");
    }

    public static void preInit() {
        FluidMaterialRegister.init();
        FluidRegister.init();
    }

    public static Fluid registerFluid(String name, String fluidName, String blockName, String texture, int density,
        int viscosity, int temperature, Material material) {
        // create the new fluid
        Fluid fluid = new Fluid(fluidName).setDensity(density)
            .setViscosity(viscosity)
            .setTemperature(temperature);

        if (material == Material.lava) {
            fluid.setLuminosity(12);
        }

        // register it if it's not already existing
        boolean isFluidPreRegistered = !FluidRegistry.registerFluid(fluid);

        // register our fluid block for the fluid
        // this constructor implicitly does fluid.setBlock to it, that's why it's not called separately
        BlockFluidOk block = new BlockFluidOk(fluid, material, texture);
        block.setBlockName(blockName);
        GameRegistry.registerBlock(block, blockName);
        OKCreativeTab.addToTab(block);

        fluid.setBlock(block);
        block.setFluid(fluid);

        // if the fluid was already registered we use that one instead
        if (isFluidPreRegistered) {
            fluid = FluidRegistry.getFluid(fluidName);
            // don't change the fluid icons of already existing fluids
            if (fluid.getBlock() != null) {
                block.suppressOverwritingFluidIcons();
            }
            // if no block is registered with an existing liquid, we set our own
            else {
                fluid.setBlock(block);
            }
        }

        return fluid;
    }
}
