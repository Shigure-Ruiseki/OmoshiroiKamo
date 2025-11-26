package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class BigReactorsCows extends BaseCowHandler {

    public static CowsRegistryItem yelloriumCow;
    public static CowsRegistryItem cyaniteCow;
    public static CowsRegistryItem steamCow;

    public BigReactorsCows() {
        super("BigReactors", "Big Reactors", "textures/entity/cows/base/");
        this.setStartID(500);
    }

    @Override
    public List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows) {

        yelloriumCow = tryAddCow(allCows, "YelloriumCow", 500, "yellorium", 0xE5FF00, 0xA6A600, SpawnType.NORMAL);

        cyaniteCow = tryAddCow(allCows, "CyaniteCow", 501, "cyanite", 0x66CCFF, 0x3399CC, SpawnType.NORMAL);

        steamCow = tryAddCow(allCows, "SteamCow", 502, "steam", 0xCCCCCC, 0xFFFFFF, SpawnType.NORMAL);
        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
