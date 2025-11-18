package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class EnderIOCows extends BaseCowHandler {

    public static CowsRegistryItem nutrientDistillationCow;
    public static CowsRegistryItem hootchCow;
    public static CowsRegistryItem rocketFuelCow;
    public static CowsRegistryItem fireWaterCow;
    public static CowsRegistryItem liquidSunshineCow;
    public static CowsRegistryItem cloudSeedCow;
    public static CowsRegistryItem cloudSeedConcentratedCow;
    public static CowsRegistryItem enderDistillationCow;
    public static CowsRegistryItem vaporOfLevityCow;

    public EnderIOCows() {
        super("EnderIO", "EnderIO", "textures/entity/cows/base/");
        this.setStartID(200);
    }

    @Override
    public List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows) {

        nutrientDistillationCow = addCow(
            allCows,
            "NutrientDistillationCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("nutrient_distillation"), 1000),
            0x4e2a04,
            0xd3a156,
            SpawnType.NORMAL);

        hootchCow = addCow(
            allCows,
            "HootchCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("hootch"), 1000),
            0x8c6239,
            0xf2d9ac,
            SpawnType.NORMAL);

        rocketFuelCow = addCow(
            allCows,
            "RocketFuelCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("rocket_fuel"), 1000),
            0xffff33,
            0xffcc00,
            SpawnType.NORMAL);

        fireWaterCow = addCow(
            allCows,
            "FireWaterCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("fire_water"), 1000),
            0xff3300,
            0xffff66,
            SpawnType.HELL);

        liquidSunshineCow = addCow(
            allCows,
            "LiquidSunshineCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("liquid_sunshine"), 1000),
            0xffff66,
            0xffffff,
            SpawnType.NORMAL);

        cloudSeedCow = addCow(
            allCows,
            "CloudSeedCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("cloud_seed"), 1000),
            0xa0c4ff,
            0xcaf0f8,
            SpawnType.SNOW);

        cloudSeedConcentratedCow = addCow(
            allCows,
            "CloudSeedConcentratedCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("cloud_seed_concentrated"), 1000),
            0x5390d9,
            0x90e0ef,
            SpawnType.SNOW);

        enderDistillationCow = addCow(
            allCows,
            "EnderDistillationCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("ender_distillation"), 1000),
            0x006666,
            0x33cccc,
            SpawnType.HELL);

        vaporOfLevityCow = addCow(
            allCows,
            "VaporOfLevityCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("vapor_of_levity"), 1000),
            0xccffff,
            0xffffff,
            SpawnType.NORMAL);

        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
