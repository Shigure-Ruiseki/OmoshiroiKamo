package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class MekanismCows extends BaseCowHandler {

    public static CowsRegistryItem heavywaterCow;
    public static CowsRegistryItem brineCow;
    public static CowsRegistryItem lithiumCow;

    public MekanismCows() {
        super("Mekanism", "Mekanism", "textures/entity/cows/base/");
        this.setStartID(400);
    }

    @Override
    public List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows) {

        heavywaterCow = tryAddCow(allCows, "HeavyWaterCow", 400, "heavywater", 0x1b2aff, 0x9dbdff, SpawnType.NORMAL);

        brineCow = tryAddCow(allCows, "BrineCow", 401, "brine", 0xe8e084, 0xffffcc, SpawnType.NORMAL);

        lithiumCow = tryAddCow(allCows, "LithiumCow", 402, "lithium", 0x0a2a7a, 0x4f7bd5, SpawnType.NORMAL);
        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
