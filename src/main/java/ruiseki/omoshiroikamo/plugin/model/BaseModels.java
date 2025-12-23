package ruiseki.omoshiroikamo.plugin.model;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.model.ModelRegistryItem;

public class BaseModels extends BaseModelHandler {

    public BaseModels() {
        super("Base", "Base", "base/");
        this.setNeedsModPresent(false);
        this.setStartID(0);
    }

    @Override
    public List<ModelRegistryItem> registerModels() {
        List<ModelRegistryItem> allModels = new ArrayList<>();

        ModelRegistryItem blaze = addModel(
            "Blaze",
            nextID(),
            "blaze",
            "Blaze",
            10f,
            1,
            0,
            0,
            new String[] { "Bring buckets of water, and watch in despair as it evaporates, and everything is on fire.",
                "You are on fire." }).setAssociatedMobs(new String[] { "Blaze" })
                    .setLivingMatter("hellish")
                    .setSimulationRFCost(256)
                    .setLang("en_US", "§bBlaze Data Model§r")
                    .setLang("ja_JP", "§bブレイズデータモデル§r")
                    .setPristineLang("en_US", "Pristine Blaze Matter")
                    .setPristineLang("ja_JP", "純粋なブレイズマター")
                    .setCraftingStrings(new String[] { "minecraft:blaze_powder" })
                    .setLootStrings(new String[] { "minecraft:blaze_rod,22", "ThermalFoundation:material,32,771" });
        allModels.add(blaze);

        ModelRegistryItem creeper = addModel(
            "Creeper",
            this.nextID(),
            "creeper",
            "Creeper",
            10f,
            1,
            0,
            0,
            new String[] { "Will blow up your base if left unattended." }).setAssociatedMobs(new String[] { "Creeper" })
                .setLivingMatter("overworldian")
                .setSimulationRFCost(80)
                .setLang("en_US", "§bCreeper Data Model§r")
                .setLang("ja_JP", "§bクリーパーデータモデル§r")
                .setPristineLang("en_US", "Pristine Creeper Matter")
                .setPristineLang("ja_JP", "純粋なクリーパーマター")
                .setCraftingStrings(new String[] { "minecraft:gunpowder" })
                .setLootStrings(new String[] { "minecraft:gunpowder,64", "minecraft:skull,6,4" });
        allModels.add(creeper);

        ModelRegistryItem dragon = addModel(
            "Ender Dragon",
            nextID(),
            "dragon",
            "EnderDragon",
            100f,
            1,
            0,
            0,
            new String[] { "Resides in the End, does not harbor treasure.",
                "Destroy its crystals and break the cycle!" }).setAssociatedMobs(new String[] { "EnderDragon" })
                    .setLivingMatter("extraterrestrial")
                    .setSimulationRFCost(2560)
                    .setLang("en_US", "§bEnder Dragon Data Model§r")
                    .setLang("ja_JP", "§bエンダードラゴンデータモデル§r")
                    .setPristineLang("en_US", "Pristine Ender Dragon Matter")
                    .setPristineLang("ja_JP", "純粋なエンダードラゴンマター")
                    .setCraftingStrings(new String[] { "minecraft:dragon_egg" })
                    .setLootStrings(
                        new String[] { "minecraft:dragon_breath,32", "minecraft:dragon_egg,1",
                            "DraconicEvolution:dragon_heart,1", "DraconicEvolution:draconium_dust,64" });
        allModels.add(dragon);

        ModelRegistryItem enderman = addModel(
            "Enderman",
            nextID(),
            "enderman",
            "Enderman",
            20f,
            1,
            0,
            0,
            new String[] { "Friendly unless provoked, dislikes rain.", "Teleports short distances." })
                .setAssociatedMobs(new String[] { "Enderman", "etfuturum.endermite" })
                .setLivingMatter("extraterrestrial")
                .setSimulationRFCost(512)
                .setLang("en_US", "§bEnderman Data Model§r")
                .setLang("ja_JP", "§bエンダーマンデータモデル§r")
                .setPristineLang("en_US", "Pristine Enderman Matter")
                .setPristineLang("ja_JP", "純粋なエンダーマンマター")
                .setCraftingStrings(new String[] { "minecraft:ender_pearl" })
                .setLootStrings(
                    new String[] { "minecraft:ender_pearl,6", "minecraft:end_crystal,1",
                        "EnderIO:block_enderman_skull,2" });
        allModels.add(enderman);

        ModelRegistryItem ghast = addModel(
            "Ghast",
            nextID(),
            "ghast",
            "Ghast",
            10f,
            1,
            0,
            0,
            new String[] { "If you hear something that sounds like a crying llama, you're probably hearing a ghast." })
                .setAssociatedMobs(new String[] { "Ghast" })
                .setLivingMatter("hellish")
                .setSimulationRFCost(372)
                .setLang("en_US", "§bGhast Data Model§r")
                .setLang("ja_JP", "§bガストデータモデル§r")
                .setPristineLang("en_US", "Pristine Ghast Matter")
                .setPristineLang("ja_JP", "純粋なガストマター")
                .setCraftingStrings(new String[] { "minecraft:ghast_tear" })
                .setLootStrings(new String[] { "minecraft:ghast_tear,8" });
        allModels.add(ghast);

        ModelRegistryItem skeleton = addModel(
            "Skeleton",
            this.nextID(),
            "skeleton",
            "Skeleton",
            10f,
            1,
            0,
            0,
            new String[] { "A formidable archer, which seem to be running some sort of cheat engine",
                "A shield could prove useful" })
                    .setAssociatedMobs(new String[] { "Skeleton", "TwilightForest.Skeleton Druid" })
                    .setLivingMatter("overworldian")
                    .setSimulationRFCost(80)
                    .setLang("en_US", "§bSkeleton Data Model§r")
                    .setLang("ja_JP", "§bスケルトンデータモデル§r")
                    .setPristineLang("en_US", "Pristine Skeleton Matter")
                    .setPristineLang("ja_JP", "純粋なスケルトンマター")
                    .setCraftingStrings(new String[] { "minecraft:bone" })
                    .setLootStrings(new String[] { "minecraft:bone,64", "minecraft:arrow,64", "minecraft:skull,6" });
        allModels.add(skeleton);

        ModelRegistryItem slime = addModel(
            "Slime",
            nextID(),
            "slime",
            "Slime",
            8f,
            1,
            0,
            0,
            new String[] { "The bouncing bouncer", "bounces, bounces and bounces",
                "Bounces and bou- squish! -\"A new slime haiku\"" })
                    .setAssociatedMobs(new String[] { "Slime", "LavaSlime" })
                    .setLivingMatter("overworldian")
                    .setSimulationRFCost(150)
                    .setLang("en_US", "§bSlime Data Model§r")
                    .setLang("ja_JP", "§bスライムデータモデル§r")
                    .setPristineLang("en_US", "Pristine Slime Matter")
                    .setPristineLang("ja_JP", "純粋なスライムマター")
                    .setCraftingStrings(new String[] { "minecraft:slime_ball" })
                    .setLootStrings(new String[] { "minecraft:slime_ball,32" });
        allModels.add(slime);

        ModelRegistryItem spider = addModel(
            "Spider",
            nextID(),
            "spider",
            "Spider",
            8f,
            1,
            0,
            0,
            new String[] { "Nocturnal douchebags, beware!", "Drops strands of string for some reason." })
                .setAssociatedMobs(
                    new String[] { "Spider", "CaveSpider", "TwilightForest.Swarm Spider", "TwilightForest.Hedge Spider",
                        "TwilightForest.King Spider" })
                .setLivingMatter("overworldian")
                .setSimulationRFCost(80)
                .setLang("en_US", "§bSpider Data Model§r")
                .setLang("ja_JP", "§bスパイダーデータモデル§r")
                .setPristineLang("en_US", "Pristine Spider Matter")
                .setPristineLang("ja_JP", "純粋なスパイダーマター")
                .setCraftingStrings(new String[] { "minecraft:spider_eye" })
                .setLootStrings(new String[] { "minecraft:spider_eye,16", "minecraft:string,64", "minecraft:web,8" });
        allModels.add(spider);

        ModelRegistryItem witch = addModel(
            "Witch",
            nextID(),
            "witch",
            "Witch",
            13f,
            1,
            0,
            0,
            new String[] { "Affinity with potions and concoctions.", "Likes cats.", "Beware!" })
                .setAssociatedMobs(new String[] { "Witch" })
                .setLivingMatter("overworldian")
                .setSimulationRFCost(120)
                .setLang("en_US", "§bWitch Data Model§r")
                .setLang("ja_JP", "§bウィッチデータモデル§r")
                .setPristineLang("en_US", "Pristine Witch Matter")
                .setPristineLang("ja_JP", "純粋なウィッチマター")
                .setCraftingStrings(new String[] { "minecraft:glass_bottle" })
                .setLootStrings(
                    new String[] { "minecraft:redstone,32", "minecraft:glowstone_dust,32", "minecraft:sugar,64" });
        allModels.add(witch);

        ModelRegistryItem wither = addModel(
            "Wither",
            nextID(),
            "wither",
            "WitherBoss",
            150f,
            1,
            0,
            0,
            new String[] { "Do not approach this enemy. Run!", "I mean it has 3 heads, what could possibly go wrong?" })
                .setAssociatedMobs(new String[] { "WitherBoss" })
                .setLivingMatter("extraterrestrial")
                .setSimulationRFCost(2048)
                .setLang("en_US", "§bWither Data Model§r")
                .setLang("ja_JP", "§bウィザーデータモデル§r")
                .setPristineLang("en_US", "Pristine Wither Matter")
                .setPristineLang("ja_JP", "純粋なウィザーマター")
                .setCraftingStrings(new String[] { "minecraft:nether_star" })
                .setLootStrings(new String[] { "minecraft:nether_star,3" });
        allModels.add(wither);

        ModelRegistryItem witherSkeleton = addModel(
            "Wither Skeleton",
            nextID(),
            "wither_skeleton",
            "witherSkeleton",
            10f,
            1,
            0,
            0,
            new String[] { "Inflicts the Wither effect.", "Bring milk!" })
                .setAssociatedMobs(new String[] { "witherSkeleton" })
                .setLivingMatter("hellish")
                .setSimulationRFCost(880)
                .setLang("en_US", "§bWither Skeleton Data Model§r")
                .setLang("ja_JP", "§bウィザースケルトンデータモデル§r")
                .setPristineLang("en_US", "Pristine Wither Skeleton Matter")
                .setPristineLang("ja_JP", "純粋なウィザースケルトンマター")
                .setCraftingStrings(new String[] { "minecraft:skull,1,1" })
                .setLootStrings(new String[] { "minecraft:skull,18,1", "minecraft:coal,64" });
        allModels.add(witherSkeleton);

        ModelRegistryItem zombie = addModel(
            "Zombie",
            nextID(),
            "zombie",
            "Zombie",
            10f,
            1,
            0,
            0,
            new String[] { "They go moan in the night.", "Does not understand the need for personal space." })
                .setAssociatedMobs(
                    new String[] { "Zombie", "etfuturum.husk", "PigZombie", "TwilightForest.Loyal Zombie",
                        "etfuturum.villager_zombie" })
                .setLivingMatter("overworldian")
                .setSimulationRFCost(80)
                .setLang("en_US", "§bZombie Data Model§r")
                .setLang("ja_JP", "§bゾンビデータモデル§r")
                .setPristineLang("en_US", "Pristine Zombie Matter")
                .setPristineLang("ja_JP", "純粋なゾンビマター")
                .setCraftingStrings(new String[] { "minecraft:rotten_flesh" })
                .setLootStrings(
                    new String[] { "minecraft:rotten_flesh,64", "minecraft:iron_ingot,16", "minecraft:carrot,32",
                        "minecraft:potato,32" });
        allModels.add(zombie);

        return allModels;
    }
}
