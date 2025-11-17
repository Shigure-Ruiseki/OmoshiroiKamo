package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class BaseCows extends BaseCowHandler {

    public static CowsRegistryItem waterCow;
    public static CowsRegistryItem lavaCow;
    public static CowsRegistryItem ironCow;
    public static CowsRegistryItem goldCow;
    public static CowsRegistryItem copperCow;
    public static CowsRegistryItem tinCow;
    public static CowsRegistryItem aluminiumCow;
    public static CowsRegistryItem cobaltCow;
    public static CowsRegistryItem arditeCow;
    public static CowsRegistryItem bronzeCow;
    public static CowsRegistryItem alubrassCow;
    public static CowsRegistryItem manyullynCow;
    public static CowsRegistryItem obsidianCow;
    public static CowsRegistryItem steelCow;
    public static CowsRegistryItem glassCow;
    public static CowsRegistryItem stoneCow;
    public static CowsRegistryItem emeraldCow;
    public static CowsRegistryItem quartzCow;
    public static CowsRegistryItem nickelCow;
    public static CowsRegistryItem leadCow;
    public static CowsRegistryItem silverCow;
    public static CowsRegistryItem shinyCow;
    public static CowsRegistryItem invarCow;
    public static CowsRegistryItem electrumCow;
    public static CowsRegistryItem lumiumCow;
    public static CowsRegistryItem signalumCow;
    public static CowsRegistryItem mithrilCow;
    public static CowsRegistryItem enderiumCow;
    public static CowsRegistryItem pigironCow;
    public static CowsRegistryItem nutrientDistillationCow;
    public static CowsRegistryItem hootchCow;
    public static CowsRegistryItem rocketFuelCow;
    public static CowsRegistryItem fireWaterCow;
    public static CowsRegistryItem liquidSunshineCow;
    public static CowsRegistryItem cloudSeedCow;
    public static CowsRegistryItem cloudSeedConcentratedCow;
    public static CowsRegistryItem enderDistillationCow;
    public static CowsRegistryItem vaporOfLevityCow;
    public static CowsRegistryItem oilCow;
    public static CowsRegistryItem fuelCow;
    public static CowsRegistryItem redplasmaCow;
    public static CowsRegistryItem heavywaterCow;
    public static CowsRegistryItem brineCow;
    public static CowsRegistryItem lithiumCow;
    public static CowsRegistryItem yelloriumCow;
    public static CowsRegistryItem cyaniteCow;
    public static CowsRegistryItem steamCow;
    public static CowsRegistryItem sludgeCow;
    public static CowsRegistryItem sewageCow;
    public static CowsRegistryItem mobEssenceCow;
    public static CowsRegistryItem biofuelCow;
    public static CowsRegistryItem meatCow;
    public static CowsRegistryItem pinkSlimeCow;
    public static CowsRegistryItem chocolateMilkCow;
    public static CowsRegistryItem mushroomSoupCow;

    public BaseCows() {
        super("Base", "Base", "textures/entity/cows/base/");
        this.setNeedsModPresent(false);
        this.setStartID(0);
    }

    @Override
    public List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows) {

        waterCow = addCow(
            allCows,
            "WaterCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("water"), 1000),
            0x000099,
            0x8080ff,
            SpawnType.NORMAL);

        lavaCow = addCow(
            allCows,
            "LavaCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("lava"), 1000),
            0xcc3300,
            0xffff00,
            SpawnType.HELL);

        ironCow = addCow(
            allCows,
            "IronCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("iron.molten"), 1000),
            0xb8b8b8,
            0xffffff,
            SpawnType.NORMAL);

        goldCow = addCow(
            allCows,
            "GoldCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("gold.molten"), 1000),
            0xffcc00,
            0xffff66,
            SpawnType.NORMAL);

        copperCow = addCow(
            allCows,
            "CopperCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("copper.molten"), 1000),
            0xcc6600,
            0xff9955,
            SpawnType.NORMAL);

        tinCow = addCow(
            allCows,
            "TinCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("tin.molten"), 1000),
            0xccccff,
            0xffffff,
            SpawnType.NORMAL);

        aluminiumCow = addCow(
            allCows,
            "AluminiumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("aluminum.molten"), 1000),
            0xddeeff,
            0xffffff,
            SpawnType.NORMAL);

        cobaltCow = addCow(
            allCows,
            "CobaltCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("cobalt.molten"), 1000),
            0x0022ff,
            0x6688ff,
            SpawnType.HELL);

        arditeCow = addCow(
            allCows,
            "ArditeCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("ardite.molten"), 1000),
            0xff6600,
            0xffaa55,
            SpawnType.HELL);

        bronzeCow = addCow(
            allCows,
            "BronzeCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("bronze.molten"), 1000),
            0xcc8844,
            0xffcc99,
            SpawnType.NORMAL);

        alubrassCow = addCow(
            allCows,
            "AlubrassCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("aluminumbrass.molten"), 1000),
            0xd4b55c,
            0xffe099,
            SpawnType.NORMAL);

        manyullynCow = addCow(
            allCows,
            "ManyullynCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("manyullyn.molten"), 1000),
            0x550088,
            0xaa66ff,
            SpawnType.HELL);

        obsidianCow = addCow(
            allCows,
            "ObsidianCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("obsidian.molten"), 1000),
            0x1a0f33,
            0x3d2a66,
            SpawnType.NORMAL);

        steelCow = addCow(
            allCows,
            "SteelCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("steel.molten"), 1000),
            0x555555,
            0xaaaaaa,
            SpawnType.NORMAL);

        glassCow = addCow(
            allCows,
            "GlassCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("glass.molten"), 1000),
            0xffffff,
            0xddeeff,
            SpawnType.NORMAL);

        stoneCow = addCow(
            allCows,
            "StoneCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("stone.seared"), 1000),
            0x888888,
            0xbbbbbb,
            SpawnType.NORMAL);

        emeraldCow = addCow(
            allCows,
            "EmeraldCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("emerald.liquid"), 1000),
            0x00cc66,
            0x66ffaa,
            SpawnType.NORMAL);

        quartzCow = addCow(
            allCows,
            "QuartzCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("quartz.molten"), 1000),
            0xfff0e6,
            0xffffff,
            SpawnType.NORMAL);

        nickelCow = addCow(
            allCows,
            "NickelCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("nickel.molten"), 1000),
            0xcccc99,
            0xffffcc,
            SpawnType.NORMAL);

        leadCow = addCow(
            allCows,
            "LeadCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("lead.molten"), 1000),
            0x333366,
            0x666699,
            SpawnType.NORMAL);

        silverCow = addCow(
            allCows,
            "SilverCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("silver.molten"), 1000),
            0xcceeff,
            0xffffff,
            SpawnType.NORMAL);

        shinyCow = addCow(
            allCows,
            "ShinyCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("platinum.molten"), 1000),
            0xe6ffff,
            0xffffff,
            SpawnType.NORMAL);

        invarCow = addCow(
            allCows,
            "InvarCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("invar.molten"), 1000),
            0x99997a,
            0xccccaa,
            SpawnType.NORMAL);

        electrumCow = addCow(
            allCows,
            "ElectrumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("electrum.molten"), 1000),
            0xfff2a1,
            0xffffd6,
            SpawnType.NORMAL);

        lumiumCow = addCow(
            allCows,
            "LumiumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("lumium.molten"), 1000),
            0xffffcc,
            0xffffff,
            SpawnType.NORMAL);

        signalumCow = addCow(
            allCows,
            "SignalumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("signalum.molten"), 1000),
            0xcc3300,
            0xff6644,
            SpawnType.NORMAL);

        mithrilCow = addCow(
            allCows,
            "MithrilCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("mithril.molten"), 1000),
            0x99ccff,
            0xccffff,
            SpawnType.NORMAL);

        enderiumCow = addCow(
            allCows,
            "EnderCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("ender"), 1000),
            0x006666,
            0x33cccc,
            SpawnType.HELL);

        enderiumCow = addCow(
            allCows,
            "EnderiumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("enderium.molten"), 1000),
            0x006666,
            0x33cccc,
            SpawnType.HELL);

        pigironCow = addCow(
            allCows,
            "PigIronCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("pigiron.molten"), 1000),
            0xff99aa,
            0xffccd5,
            SpawnType.NORMAL);

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

        oilCow = addCow(
            allCows,
            "OilCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("oil"), 1000),
            0x1F1A12,
            0x3A352A,
            SpawnType.NORMAL);

        fuelCow = addCow(
            allCows,
            "FuelCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("fuel"), 1000),
            0xE5CC00,
            0xFFF280,
            SpawnType.NORMAL);

        redplasmaCow = addCow(
            allCows,
            "RedPlasmaCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("redplasma"), 1000),
            0xCC0000,
            0xFF6666,
            SpawnType.HELL);

        heavywaterCow = addCow(
            allCows,
            "HeavyWaterCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("heavywater"), 1000),
            0x1b2aff,
            0x9dbdff,
            SpawnType.NORMAL);

        brineCow = addCow(
            allCows,
            "BrineCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("brine"), 1000),
            0xe8e084,
            0xffffcc,
            SpawnType.NORMAL);

        lithiumCow = addCow(
            allCows,
            "LithiumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("lithium"), 1000),
            0x0a2a7a,
            0x4f7bd5,
            SpawnType.NORMAL);

        yelloriumCow = addCow(
            allCows,
            "YelloriumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("yellorium"), 1000),
            0xE5FF00,
            0xA6A600,
            SpawnType.NORMAL);

        cyaniteCow = addCow(
            allCows,
            "CyaniteCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("cyanite"), 1000),
            0x66CCFF,
            0x3399CC,
            SpawnType.NORMAL);

        steamCow = addCow(
            allCows,
            "SteamCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("steam"), 1000),
            0xCCCCCC,
            0xFFFFFF,
            SpawnType.NORMAL);

        sludgeCow = addCow(
            allCows,
            "SludgeCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("sludge"), 1000),
            0x2d2d2d,
            0x555555,
            SpawnType.HELL);

        sewageCow = addCow(
            allCows,
            "SewageCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("sewage"), 1000),
            0x665500,
            0xccaa33,
            SpawnType.NORMAL);

        mobEssenceCow = addCow(
            allCows,
            "MobEssenceCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("mobessence"), 1000),
            0x33ff33,
            0x99ff99,
            SpawnType.NORMAL);

        biofuelCow = addCow(
            allCows,
            "BiofuelCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("biofuel"), 1000),
            0x99cc00,
            0xccff66,
            SpawnType.NORMAL);

        meatCow = addCow(
            allCows,
            "MeatCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("meat"), 1000),
            0xcc6666,
            0xff9999,
            SpawnType.NORMAL);

        pinkSlimeCow = addCow(
            allCows,
            "PinkSlimeCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("pinkslime"), 1000),
            0xff66cc,
            0xff99dd,
            SpawnType.NORMAL);

        chocolateMilkCow = addCow(
            allCows,
            "ChocolateMilkCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("chocolatemilk"), 1000),
            0x663300,
            0xcc9966,
            SpawnType.NORMAL);

        mushroomSoupCow = addCow(
            allCows,
            "MushroomSoupCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("mushroomsoup"), 1000),
            0xccaa88,
            0xffddbb,
            SpawnType.NORMAL);

        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
