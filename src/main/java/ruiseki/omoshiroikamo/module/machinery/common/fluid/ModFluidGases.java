package ruiseki.omoshiroikamo.module.machinery.common.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.world.gen.SimpleMinableWorldGenerator;
import ruiseki.omoshiroikamo.core.world.gen.WorldGenMinableExtended;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockFluidBase;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockLiquidBase;
import ruiseki.omoshiroikamo.module.machinery.common.item.ItemBlockFluid;
import ruiseki.omoshiroikamo.module.machinery.common.world.gen.WorldGenGasPocket;

/**
 * Registration class for all fluid materials.
 * All fluids use BlockLiquidBase (vanilla water-style flow).
 * Flow direction is determined by density: positive = down, negative = up.
 */
public class ModFluidGases {

    public static final Map<EnumFluidMaterial, Fluid> FLUIDS = new HashMap<>();
    public static final Map<EnumFluidMaterial, BlockFluidBase> BLOCKS = new HashMap<>();

    public static void preInit() {
        for (EnumFluidMaterial mat : EnumFluidMaterial.values()) {
            // 1. Fluid registration
            Fluid fluid = new FluidOK(mat).setDensity(mat.getDensity())
                .setTemperature(mat.getTemperature())
                .setGaseous(mat.isGaseous())
                .setViscosity(mat.getViscosity());

            if (!FluidRegistry.registerFluid(fluid)) {
                fluid = FluidRegistry.getFluid(mat.getName());
                Logger.info("Using existing fluid for " + mat.getName());
            }
            FLUIDS.put(mat, fluid);

            // 2. Block registration
            String blockName = (mat.isGaseous() ? "gas." : "liquid.") + mat.getName();
            BlockFluidBase block = (BlockFluidBase) new BlockLiquidBase(fluid, null).setMaterial(mat)
                .setQuantaPerBlock(mat.getQuantaPerBlock())
                .setDrag(mat.getDrag())
                .setBlockName(blockName);

            GameRegistry.registerBlock(block, ItemBlockFluid.class, block.getUnlocalizedName());
            BLOCKS.put(mat, block);

            // 3. Associate block with fluid (skip if another mod already claimed it)
            if (fluid.getBlock() == null) {
                fluid.setBlock(block);
                Logger.info("Associated " + mat.getName() + " with our block.");
            } else {
                Logger.info("Fluid " + mat.getName() + " already has a block from another mod.");
            }
        }

        registerWorldGen();
    }

    private static void registerWorldGen() {
        List<WorldGenMinableExtended> generators = new ArrayList<>();

        if (BLOCKS.containsKey(EnumFluidMaterial.HELIUM)) {
            generators.add(new WorldGenGasPocket(BLOCKS.get(EnumFluidMaterial.HELIUM), 20, 1, 10, 60));
        }
        if (BLOCKS.containsKey(EnumFluidMaterial.CHLORINE)) {
            generators.add(new WorldGenGasPocket(BLOCKS.get(EnumFluidMaterial.CHLORINE), 15, 1, 5, 30));
        }
        if (BLOCKS.containsKey(EnumFluidMaterial.FLUORINE)) {
            generators.add(new WorldGenGasPocket(BLOCKS.get(EnumFluidMaterial.FLUORINE), 10, 1, 5, 20));
        }

        if (!generators.isEmpty()) {
            GameRegistry
                .registerWorldGenerator(new SimpleMinableWorldGenerator(OmoshiroiKamo.instance, generators), 10);
        }
    }
}
