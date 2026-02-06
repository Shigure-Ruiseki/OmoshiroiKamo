package ruiseki.omoshiroikamo.module.cows.common.registries;

import java.util.ArrayList;
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
    public List<CowsRegistryItem> registerCows() {
        List<CowsRegistryItem> allCows = new ArrayList<>();

        yelloriumCow = addCow("YelloriumCow", 500, 0xE5FF00, 0xA6A600, SpawnType.NORMAL).setFluidString("yellorium");
        allCows.add(yelloriumCow);

        cyaniteCow = addCow("CyaniteCow", 501, 0x66CCFF, 0x3399CC, SpawnType.NORMAL).setFluidString("cyanite");
        allCows.add(cyaniteCow);

        steamCow = addCow("SteamCow", 502, 0xCCCCCC, 0xFFFFFF, SpawnType.NORMAL).setFluidString("steam");
        allCows.add(steamCow);

        return allCows;
    }
}
