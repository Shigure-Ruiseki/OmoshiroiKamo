package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class MineFactoryReloadedCows extends BaseCowHandler {

    public static CowsRegistryItem steamCow;
    public static CowsRegistryItem sludgeCow;
    public static CowsRegistryItem sewageCow;
    public static CowsRegistryItem mobEssenceCow;
    public static CowsRegistryItem biofuelCow;
    public static CowsRegistryItem meatCow;
    public static CowsRegistryItem pinkSlimeCow;
    public static CowsRegistryItem chocolateMilkCow;
    public static CowsRegistryItem mushroomSoupCow;

    public MineFactoryReloadedCows() {
        super("Base", "Base", "textures/entity/cows/base/");
        this.setStartID(600);
    }

    @Override
    public List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows) {

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
