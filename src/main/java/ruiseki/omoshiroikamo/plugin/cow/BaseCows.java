package ruiseki.omoshiroikamo.plugin.cow;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class BaseCows extends BaseCowHandler {

    public static CowsRegistryItem waterCow;
    public static CowsRegistryItem lavaCow;

    public BaseCows() {
        super("Base", "Base", "textures/entity/cows/base/");
        this.setNeedsModPresent(false);
        this.setStartID(0);
    }

    @Override
    public List<CowsRegistryItem> registerCows() {
        List<CowsRegistryItem> allCows = new ArrayList<>();

        waterCow = addCow("WaterCow", this.nextID(), 0x000099, 0x8080ff, SpawnType.NORMAL).setFluidString("water")
            .setLang("en_US", "Water Cow")
            .setLang("ja_JP", "ウォーター牛");
        allCows.add(waterCow);

        lavaCow = addCow("LavaCow", this.nextID(), 0xcc3300, 0xffff00, SpawnType.HELL).setFluidString("lava")
            .setLang("en_US", "Lava Cow")
            .setLang("ja_JP", "ラヴァ牛");
        allCows.add(lavaCow);

        return allCows;
    }
}
