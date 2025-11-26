package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

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

        nutrientDistillationCow = tryAddCow(
            allCows,
            "NutrientDistillationCow",
            200,
            "nutrient_distillation",
            0x4e2a04,
            0xd3a156,
            SpawnType.NORMAL);

        hootchCow = tryAddCow(allCows, "HootchCow", 201, "hootch", 0x8c6239, 0xf2d9ac, SpawnType.NORMAL);

        rocketFuelCow = tryAddCow(allCows, "RocketFuelCow", 202, "rocket_fuel", 0xffff33, 0xffcc00, SpawnType.NORMAL);

        fireWaterCow = tryAddCow(allCows, "FireWaterCow", 203, "fire_water", 0xff3300, 0xffff66, SpawnType.HELL);

        liquidSunshineCow = tryAddCow(
            allCows,
            "LiquidSunshineCow",
            204,
            "liquid_sunshine",
            0xffff66,
            0xffffff,
            SpawnType.NORMAL);

        cloudSeedCow = tryAddCow(allCows, "CloudSeedCow", 205, "cloud_seed", 0xa0c4ff, 0xcaf0f8, SpawnType.SNOW);

        cloudSeedConcentratedCow = tryAddCow(
            allCows,
            "CloudSeedConcentratedCow",
            206,
            "cloud_seed_concentrated",
            0x5390d9,
            0x90e0ef,
            SpawnType.SNOW);

        enderDistillationCow = tryAddCow(
            allCows,
            "EnderDistillationCow",
            207,
            "ender_distillation",
            0x006666,
            0x33cccc,
            SpawnType.HELL);

        vaporOfLevityCow = tryAddCow(
            allCows,
            "VaporOfLevityCow",
            208,
            "vapor_of_levity",
            0xccffff,
            0xffffff,
            SpawnType.NORMAL);

        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
