package ruiseki.omoshiroikamo.plugin.cow;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class EnderIOCows extends BaseCowHandler {

    public static CowsRegistryItem nutrientDistillationCow;
    public static CowsRegistryItem hootchCow;
    public static CowsRegistryItem rocketFuelCow;
    public static CowsRegistryItem fireWaterCow;
    public static CowsRegistryItem liquidSunshineCow;
    public static CowsRegistryItem cloudSeedCow;
    public static CowsRegistryItem cloudSeedConcentratedCow;
    public static CowsRegistryItem enderDistillationCow;
    public static CowsRegistryItem vaporOfLevityCow;

    public EnderIOCows() {
        super("EnderIO", "EnderIO", "textures/entity/cows/base/");
        this.setStartID(200);
    }

    @Override
    public List<CowsRegistryItem> registerCows() {
        List<CowsRegistryItem> allCows = new ArrayList<>();

        nutrientDistillationCow = addCow("NutrientDistillationCow", 200, 0x4e2a04, 0xd3a156, SpawnType.NORMAL)
            .setFluidString("nutrient_distillation")
            .setLang("en_US", "Nutrient Distillation Cow")
            .setLang("ja_JP", "栄養蒸留牛");
        allCows.add(nutrientDistillationCow);

        hootchCow = addCow("HootchCow", 201, 0x8c6239, 0xf2d9ac, SpawnType.NORMAL).setFluidString("hootch")
            .setLang("en_US", "Hootch Cow")
            .setLang("ja_JP", "フーチ牛");
        allCows.add(hootchCow);

        rocketFuelCow = addCow("RocketFuelCow", 202, 0xffff33, 0xffcc00, SpawnType.NORMAL).setFluidString("rocket_fuel")
            .setLang("en_US", "Rocket Fuel Cow")
            .setLang("ja_JP", "ロケット燃料牛");
        allCows.add(rocketFuelCow);

        fireWaterCow = addCow("FireWaterCow", 203, 0xff3300, 0xffff66, SpawnType.HELL).setFluidString("fire_water")
            .setLang("en_US", "Fire Water Cow")
            .setLang("ja_JP", "ファイアウォーター牛");
        allCows.add(fireWaterCow);

        liquidSunshineCow = addCow("LiquidSunshineCow", 204, 0xffff66, 0xffffff, SpawnType.NORMAL)
            .setFluidString("liquid_sunshine")
            .setLang("en_US", "Liquid Sunshine Cow")
            .setLang("ja_JP", "リキッドサンシャイン牛");
        allCows.add(liquidSunshineCow);

        cloudSeedCow = addCow("CloudSeedCow", 205, 0xa0c4ff, 0xcaf0f8, SpawnType.SNOW).setFluidString("cloud_seed")
            .setLang("en_US", "Cloud Seed Cow")
            .setLang("ja_JP", "クラウドシード牛");
        allCows.add(cloudSeedCow);

        cloudSeedConcentratedCow = addCow("CloudSeedConcentratedCow", 206, 0x5390d9, 0x90e0ef, SpawnType.SNOW)
            .setFluidString("cloud_seed_concentrated")
            .setLang("en_US", "Cloud Seed Concentrated Cow")
            .setLang("ja_JP", "濃縮クラウドシード牛");
        allCows.add(cloudSeedConcentratedCow);

        enderDistillationCow = addCow("EnderDistillationCow", 207, 0x006666, 0x33cccc, SpawnType.HELL)
            .setFluidString("ender_distillation")
            .setLang("en_US", "Ender Distillation Cow")
            .setLang("ja_JP", "エンダー蒸留牛");
        allCows.add(enderDistillationCow);

        vaporOfLevityCow = addCow("VaporOfLevityCow", 208, 0xccffff, 0xffffff, SpawnType.NORMAL)
            .setFluidString("vapor_of_levity")
            .setLang("en_US", "Vapor of Levity Cow")
            .setLang("ja_JP", "軽さの蒸気牛");
        allCows.add(vaporOfLevityCow);

        return allCows;
    }
}
