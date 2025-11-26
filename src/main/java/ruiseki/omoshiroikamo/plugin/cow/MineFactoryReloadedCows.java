package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

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

        steamCow = tryAddCow(allCows, "SteamCow", 600, "steam", 0xCCCCCC, 0xFFFFFF, SpawnType.NORMAL);

        sludgeCow = tryAddCow(allCows, "SludgeCow", 601, "sludge", 0x2d2d2d, 0x555555, SpawnType.HELL);

        sewageCow = tryAddCow(allCows, "SewageCow", 602, "sewage", 0x665500, 0xccaa33, SpawnType.NORMAL);

        mobEssenceCow = tryAddCow(allCows, "MobEssenceCow", 603, "mobessence", 0x33ff33, 0x99ff99, SpawnType.NORMAL);

        biofuelCow = tryAddCow(allCows, "BiofuelCow", 604, "biofuel", 0x99cc00, 0xccff66, SpawnType.NORMAL);

        meatCow = tryAddCow(allCows, "MeatCow", 605, "meat", 0xcc6666, 0xff9999, SpawnType.NORMAL);

        pinkSlimeCow = tryAddCow(allCows, "PinkSlimeCow", 606, "pinkslime", 0xff66cc, 0xff99dd, SpawnType.NORMAL);

        chocolateMilkCow = tryAddCow(
            allCows,
            "ChocolateMilkCow",
            607,
            "chocolatemilk",
            0x663300,
            0xcc9966,
            SpawnType.NORMAL);

        mushroomSoupCow = tryAddCow(
            allCows,
            "MushroomSoupCow",
            608,
            "mushroomsoup",
            0xccaa88,
            0xffddbb,
            SpawnType.NORMAL);

        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
