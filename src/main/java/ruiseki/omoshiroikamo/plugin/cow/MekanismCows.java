package ruiseki.omoshiroikamo.plugin.cow;

import java.util.ArrayList;
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
    public List<CowsRegistryItem> registerCows() {
        List<CowsRegistryItem> allCows = new ArrayList<>();

        heavywaterCow = addCow("HeavyWaterCow", 400, 0x1b2aff, 0x9dbdff, SpawnType.NORMAL).setFluidString("heavywater")
            .setLang("en_US", "Heavy Water Cow")
            .setLang("ja_JP", "重水牛");
        allCows.add(heavywaterCow);

        brineCow = addCow("BrineCow", 401, 0xe8e084, 0xffffcc, SpawnType.NORMAL).setFluidString("brine")
            .setLang("en_US", "Brine Cow")
            .setLang("ja_JP", "塩水牛");
        allCows.add(brineCow);

        lithiumCow = addCow("LithiumCow", 402, 0x0a2a7a, 0x4f7bd5, SpawnType.NORMAL).setFluidString("lithium")
            .setLang("en_US", "Lithium Cow")
            .setLang("ja_JP", "リチウム牛");
        allCows.add(lithiumCow);

        return allCows;
    }
}
