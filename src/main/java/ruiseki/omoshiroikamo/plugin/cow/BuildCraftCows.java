package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class BuildCraftCows extends BaseCowHandler {

    public static CowsRegistryItem oilCow;
    public static CowsRegistryItem fuelCow;
    public static CowsRegistryItem redplasmaCow;

    public BuildCraftCows() {
        super("BuildCraft", "BuildCraft", "textures/entity/cows/base/");
        this.setStartID(300);
    }

    @Override
    public List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows) {

        oilCow = tryAddCow(allCows, "OilCow", 300, "oil", 0x1F1A12, 0x3A352A, SpawnType.NORMAL);

        fuelCow = tryAddCow(allCows, "FuelCow", 301, "fuel", 0xE5CC00, 0xFFF280, SpawnType.NORMAL);

        redplasmaCow = tryAddCow(allCows, "RedPlasmaCow", 302, "redplasma", 0xCC0000, 0xFF6666, SpawnType.HELL);

        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
