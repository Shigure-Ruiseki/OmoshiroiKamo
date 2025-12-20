package ruiseki.omoshiroikamo.plugin.cow;

import java.util.ArrayList;
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
    public List<CowsRegistryItem> registerCows() {
        List<CowsRegistryItem> allCows = new ArrayList<>();

        steamCow = addCow("SteamCow", 600, 0xCCCCCC, 0xFFFFFF, SpawnType.NORMAL).setFluidString("steam")
            .setLang("en_US", "Steam Cow")
            .setLang("ja_JP", "スチーム牛");
        allCows.add(steamCow);

        sludgeCow = addCow("SludgeCow", 601, 0x2d2d2d, 0x555555, SpawnType.HELL).setFluidString("sludge")
            .setLang("en_US", "Sludge Cow")
            .setLang("ja_JP", "スラッジ牛");
        allCows.add(sludgeCow);

        sewageCow = addCow("SewageCow", 602, 0x665500, 0xccaa33, SpawnType.NORMAL).setFluidString("sewage")
            .setLang("en_US", "Sewage Cow")
            .setLang("ja_JP", "下水牛");
        allCows.add(sewageCow);

        mobEssenceCow = addCow("MobEssenceCow", 603, 0x33ff33, 0x99ff99, SpawnType.NORMAL).setFluidString("mobessence")
            .setLang("en_US", "Mob Essence Cow")
            .setLang("ja_JP", "モブエッセンス牛");
        allCows.add(mobEssenceCow);

        biofuelCow = addCow("BiofuelCow", 604, 0x99cc00, 0xccff66, SpawnType.NORMAL).setFluidString("biofuel")
            .setLang("en_US", "Biofuel Cow")
            .setLang("ja_JP", "バイオ燃料牛");
        allCows.add(biofuelCow);

        meatCow = addCow("MeatCow", 605, 0xcc6666, 0xff9999, SpawnType.NORMAL).setFluidString("meat")
            .setLang("en_US", "Meat Cow")
            .setLang("ja_JP", "肉牛");
        allCows.add(meatCow);

        pinkSlimeCow = addCow("PinkSlimeCow", 606, 0xff66cc, 0xff99dd, SpawnType.NORMAL).setFluidString("pinkslime")
            .setLang("en_US", "Pink Slime Cow")
            .setLang("ja_JP", "ピンクスライム牛");
        allCows.add(pinkSlimeCow);

        chocolateMilkCow = addCow("ChocolateMilkCow", 607, 0x663300, 0xcc9966, SpawnType.NORMAL)
            .setFluidString("chocolatemilk")
            .setLang("en_US", "Chocolate Milk Cow")
            .setLang("ja_JP", "チョコレートミルク牛");
        allCows.add(chocolateMilkCow);

        mushroomSoupCow = addCow("MushroomSoupCow", 608, 0xccaa88, 0xffddbb, SpawnType.NORMAL)
            .setFluidString("mushroomsoup")
            .setLang("en_US", "Mushroom Soup Cow")
            .setLang("ja_JP", "マッシュルームスープ牛");
        allCows.add(mushroomSoupCow);

        return allCows;
    }
}
