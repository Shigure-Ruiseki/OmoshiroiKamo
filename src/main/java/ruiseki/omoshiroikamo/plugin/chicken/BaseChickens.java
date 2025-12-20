package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class BaseChickens extends BaseChickenHandler {

    public static ChickensRegistryItem smartChicken;

    public static ChickensRegistryItem whiteChicken;
    public static ChickensRegistryItem yellowChicken;
    public static ChickensRegistryItem blueChicken;
    public static ChickensRegistryItem greenChicken;
    public static ChickensRegistryItem redChicken;
    public static ChickensRegistryItem blackChicken;

    public static ChickensRegistryItem pinkChicken;
    public static ChickensRegistryItem purpleChicken;
    public static ChickensRegistryItem orangeChicken;
    public static ChickensRegistryItem lightBlueChicken;
    public static ChickensRegistryItem limeChicken;
    public static ChickensRegistryItem grayChicken;
    public static ChickensRegistryItem cyanChicken;
    public static ChickensRegistryItem silverChicken;
    public static ChickensRegistryItem magentaChicken;

    public static ChickensRegistryItem flintChicken;
    public static ChickensRegistryItem quartzChicken;
    public static ChickensRegistryItem logChicken;
    public static ChickensRegistryItem sandChicken;

    public static ChickensRegistryItem stringChicken;
    public static ChickensRegistryItem glowstoneChicken;
    public static ChickensRegistryItem gunpowderChicken;
    public static ChickensRegistryItem redstoneChicken;
    public static ChickensRegistryItem glassChicken;
    public static ChickensRegistryItem ironChicken;
    public static ChickensRegistryItem coalChicken;
    public static ChickensRegistryItem brownChicken;

    public static ChickensRegistryItem goldChicken;
    public static ChickensRegistryItem snowballChicken;
    public static ChickensRegistryItem waterChicken;
    public static ChickensRegistryItem lavaChicken;
    public static ChickensRegistryItem clayChicken;
    public static ChickensRegistryItem leatherChicken;
    public static ChickensRegistryItem netherwartChicken;

    public static ChickensRegistryItem diamondChicken;
    public static ChickensRegistryItem blazeChicken;
    public static ChickensRegistryItem slimeChicken;

    public static ChickensRegistryItem enderChicken;
    public static ChickensRegistryItem ghastChicken;
    public static ChickensRegistryItem emeraldChicken;
    public static ChickensRegistryItem magmaChicken;

    public static ChickensRegistryItem xpChicken;
    public static ChickensRegistryItem pShardChicken;
    public static ChickensRegistryItem pCrystalChicken;
    public static ChickensRegistryItem soulsandChicken;
    public static ChickensRegistryItem obsidianChicken;

    public BaseChickens() {
        super("Base", "Base", "textures/entity/chicken/base/");
        this.setNeedsModPresent(false);
        this.setStartID(0);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();

        // DYE CHICKENS
        whiteChicken = addDye(EnumDye.WHITE, "WhiteChicken").setDropItem(new ItemStack(Items.bone))
            .setLang("en_US", "White Chicken")
            .setLang("ja_JP", "白いニワトリ")
            .setSpawnType(SpawnType.NORMAL);
        allChickens.add(whiteChicken);

        yellowChicken = addDye(EnumDye.YELLOW, "YellowChicken").setLang("en_US", "Yellow Chicken")
            .setLang("ja_JP", "黄色いニワトリ");
        allChickens.add(yellowChicken);

        blueChicken = addDye(EnumDye.BLUE, "BlueChicken").setLang("en_US", "Blue Chicken")
            .setLang("ja_JP", "青いニワトリ");
        allChickens.add(blueChicken);

        greenChicken = addDye(EnumDye.GREEN, "GreenChicken").setLang("en_US", "Green Chicken")
            .setLang("ja_JP", "緑のニワトリ");
        allChickens.add(greenChicken);

        redChicken = addDye(EnumDye.RED, "RedChicken").setLang("en_US", "Red Chicken")
            .setLang("ja_JP", "赤いニワトリ");
        allChickens.add(redChicken);

        blackChicken = addDye(EnumDye.BLACK, "BlackChicken").setLang("en_US", "Black Chicken")
            .setLang("ja_JP", "黒いニワトリ");
        allChickens.add(blackChicken);

        pinkChicken = addDye(EnumDye.PINK, "PinkChicken").setLang("en_US", "Pink Chicken")
            .setLang("ja_JP", "ピンクのニワトリ");
        allChickens.add(pinkChicken);

        purpleChicken = addDye(EnumDye.PURPLE, "PurpleChicken").setLang("en_US", "Purple Chicken")
            .setLang("ja_JP", "紫のニワトリ");
        allChickens.add(purpleChicken);

        orangeChicken = addDye(EnumDye.ORANGE, "OrangeChicken").setLang("en_US", "Orange Chicken")
            .setLang("ja_JP", "オレンジのニワトリ");
        allChickens.add(orangeChicken);

        lightBlueChicken = addDye(EnumDye.LIGHT_BLUE, "LightBlueChicken").setLang("en_US", "Light Blue Chicken")
            .setLang("ja_JP", "水色のニワトリ");
        allChickens.add(lightBlueChicken);

        limeChicken = addDye(EnumDye.LIME, "LimeChicken").setLang("en_US", "Lime Chicken")
            .setLang("ja_JP", "黄緑のニワトリ");
        allChickens.add(limeChicken);

        grayChicken = addDye(EnumDye.GRAY, "GrayChicken").setLang("en_US", "Gray Chicken")
            .setLang("ja_JP", "灰色のニワトリ");
        allChickens.add(grayChicken);

        cyanChicken = addDye(EnumDye.CYAN, "CyanChicken").setLang("en_US", "Cyan Chicken")
            .setLang("ja_JP", "シアンのニワトリ");
        allChickens.add(cyanChicken);

        silverChicken = addDye(EnumDye.SILVER, "SilverDyeChicken").setLang("en_US", "Silver Chicken")
            .setLang("ja_JP", "薄灰色のニワトリ");
        allChickens.add(silverChicken);

        magentaChicken = addDye(EnumDye.MAGENTA, "MagentaChicken").setLang("en_US", "Magenta Chicken")
            .setLang("ja_JP", "マゼンタのニワトリ");
        allChickens.add(magentaChicken);

        brownChicken = addDye(EnumDye.BROWN, "BrownChicken").setLang("en_US", "Brown Chicken")
            .setLang("ja_JP", "茶色のニワトリ");
        allChickens.add(brownChicken);

        // SMART CHICKEN
        smartChicken = addChicken("SmartChicken", this.nextID(), "SmartChicken", 0xffffff, 0xffff00, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.egg))
            .setLang("en_US", "Smart Chicken")
            .setLang("ja_JP", "スマートニワトリ");
        allChickens.add(smartChicken);

        // BASE CHICKENS
        flintChicken = addChicken("FlintChicken", this.nextID(), "FlintChicken", 0x6b6b47, 0xa3a375, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.flint))
            .setLang("en_US", "Flint Chicken")
            .setLang("ja_JP", "火打石のニワトリ");
        allChickens.add(flintChicken);

        quartzChicken = addChicken("QuartzChicken", this.nextID(), "QuartzChicken", 0x4d0000, 0x1a0000, SpawnType.HELL)
            .setLayItem(new ItemStack(Items.quartz))
            .setLang("en_US", "Quartz Chicken")
            .setLang("ja_JP", "ネザークォーツのニワトリ");
        allChickens.add(quartzChicken);

        logChicken = addChicken("LogChicken", this.nextID(), "LogChicken", 0x98846d, 0x528358, SpawnType.NONE)
            .setLayItem(new ItemStack(Blocks.log))
            .setLang("en_US", "Log Chicken")
            .setLang("ja_JP", "原木のニワトリ");
        allChickens.add(logChicken);

        sandChicken = addChicken("SandChicken", this.nextID(), "SandChicken", 0xece5b1, 0xa7a06c, SpawnType.NONE)
            .setLayItem(new ItemStack(Blocks.sand))
            .setLang("en_US", "Sand Chicken")
            .setLang("ja_JP", "砂のニワトリ");
        allChickens.add(sandChicken);

        // TIER 2
        stringChicken = addChicken("StringChicken", this.nextID(), "StringChicken", 0x331a00, 0x800000, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.string))
            .setDropItem(new ItemStack(Items.spider_eye))
            .setLang("en_US", "String Chicken")
            .setLang("ja_JP", "糸のニワトリ");
        allChickens.add(stringChicken);

        glowstoneChicken = addChicken(
            "GlowstoneChicken",
            this.nextID(),
            "GlowstoneChicken",
            0xffff66,
            0xffff00,
            SpawnType.NONE).setLayItem(new ItemStack(Items.glowstone_dust))
                .setLang("en_US", "Glowstone Chicken")
                .setLang("ja_JP", "グロウストーンのニワトリ");
        allChickens.add(glowstoneChicken);

        gunpowderChicken = addChicken(
            "GunpowderChicken",
            this.nextID(),
            "GunpowderChicken",
            0x999999,
            0x404040,
            SpawnType.NONE).setLayItem(new ItemStack(Items.gunpowder))
                .setLang("en_US", "Gunpowder Chicken")
                .setLang("ja_JP", "火薬のニワトリ");
        allChickens.add(gunpowderChicken);

        redstoneChicken = addChicken(
            "RedstoneChicken",
            this.nextID(),
            "RedstoneChicken",
            0xe60000,
            0x800000,
            SpawnType.NONE).setLayItem(new ItemStack(Items.redstone))
                .setLang("en_US", "Redstone Chicken")
                .setLang("ja_JP", "レッドストーンのニワトリ");
        allChickens.add(redstoneChicken);

        glassChicken = addChicken("GlassChicken", this.nextID(), "GlassChicken", 0xffffff, 0xeeeeff, SpawnType.NONE)
            .setLayItem(new ItemStack(Blocks.glass))
            .setLang("en_US", "Glass Chicken")
            .setLang("ja_JP", "ガラスのニワトリ");
        allChickens.add(glassChicken);

        ironChicken = addChicken("IronChicken", this.nextID(), "IronChicken", 0xffffcc, 0xffcccc, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.iron_ingot))
            .setLang("en_US", "Iron Chicken")
            .setLang("ja_JP", "鉄のニワトリ");
        allChickens.add(ironChicken);

        coalChicken = addChicken("CoalChicken", this.nextID(), "CoalChicken", 0x262626, 0x000000, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.coal))
            .setLang("en_US", "Coal Chicken")
            .setLang("ja_JP", "石炭のニワトリ");
        allChickens.add(coalChicken);

        // TIER 3
        goldChicken = addChicken("GoldChicken", this.nextID(), "GoldChicken", 0xcccc00, 0xffff80, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.gold_ingot))
            .setLang("en_US", "Gold Chicken")
            .setLang("ja_JP", "金のニワトリ");
        allChickens.add(goldChicken);

        snowballChicken = addChicken(
            "SnowballChicken",
            this.nextID(),
            "SnowballChicken",
            0x33bbff,
            0x0088cc,
            SpawnType.SNOW).setLayItem(new ItemStack(Items.snowball))
                .setLang("en_US", "Snowball Chicken")
                .setLang("ja_JP", "雪玉のニワトリ");
        allChickens.add(snowballChicken);

        waterChicken = addChicken("WaterChicken", this.nextID(), "WaterChicken", 0x000099, 0x8080ff, SpawnType.NONE)
            .setLayItem(ModItems.LIQUID_EGG.newItemStack(1, 0))
            .setLang("en_US", "Water Chicken")
            .setLang("ja_JP", "水のニワトリ");
        allChickens.add(waterChicken);

        lavaChicken = addChicken("LavaChicken", this.nextID(), "LavaChicken", 0xcc3300, 0xffff00, SpawnType.HELL)
            .setLayItem(ModItems.LIQUID_EGG.newItemStack(1, 1))
            .setLang("en_US", "Lava Chicken")
            .setLang("ja_JP", "溶岩のニワトリ");
        allChickens.add(lavaChicken);

        clayChicken = addChicken("ClayChicken", this.nextID(), "ClayChicken", 0xcccccc, 0xbfbfbf, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.clay_ball))
            .setLang("en_US", "Clay Chicken")
            .setLang("ja_JP", "粘土のニワトリ");
        allChickens.add(clayChicken);

        leatherChicken = addChicken(
            "LeatherChicken",
            this.nextID(),
            "LeatherChicken",
            0xA7A06C,
            0x919191,
            SpawnType.NONE).setLayItem(new ItemStack(Items.leather))
                .setLang("en_US", "Leather Chicken")
                .setLang("ja_JP", "革のニワトリ");
        allChickens.add(leatherChicken);

        netherwartChicken = addChicken(
            "NetherwartChicken",
            this.nextID(),
            "NetherwartChicken",
            0x800000,
            0x331a00,
            SpawnType.NONE).setLayItem(new ItemStack(Items.nether_wart))
                .setLang("en_US", "Nether Wart Chicken")
                .setLang("ja_JP", "ネザーウォートのニワトリ");
        allChickens.add(netherwartChicken);

        // TIER 4
        diamondChicken = addChicken(
            "DiamondChicken",
            this.nextID(),
            "DiamondChicken",
            0x99ccff,
            0xe6f2ff,
            SpawnType.NONE).setLayItem(new ItemStack(Items.diamond))
                .setLang("en_US", "Diamond Chicken")
                .setLang("ja_JP", "ダイヤモンドのニワトリ");
        allChickens.add(diamondChicken);

        blazeChicken = addChicken("BlazeChicken", this.nextID(), "BlazeChicken", 0xffff66, 0xff3300, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.blaze_rod))
            .setLang("en_US", "Blaze Chicken")
            .setLang("ja_JP", "ブレイズのニワトリ");
        allChickens.add(blazeChicken);

        slimeChicken = addChicken("SlimeChicken", this.nextID(), "SlimeChicken", 0x009933, 0x99ffbb, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.slime_ball))
            .setLang("en_US", "Slime Chicken")
            .setLang("ja_JP", "スライムのニワトリ");
        allChickens.add(slimeChicken);

        // TIER 5
        enderChicken = addChicken("EnderChicken", this.nextID(), "EnderChicken", 0x001a00, 0x001a33, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.ender_pearl))
            .setLang("en_US", "Ender Chicken")
            .setLang("ja_JP", "エンダーのニワトリ");
        allChickens.add(enderChicken);

        ghastChicken = addChicken("GhastChicken", this.nextID(), "GhastChicken", 0xffffcc, 0xffffff, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.ghast_tear))
            .setLang("en_US", "Ghast Chicken")
            .setLang("ja_JP", "ガストのニワトリ");
        allChickens.add(ghastChicken);

        emeraldChicken = addChicken(
            "EmeraldChicken",
            this.nextID(),
            "EmeraldChicken",
            0x00cc00,
            0x003300,
            SpawnType.NONE).setLayItem(new ItemStack(Items.emerald))
                .setLang("en_US", "Emerald Chicken")
                .setLang("ja_JP", "エメラルドのニワトリ");
        allChickens.add(emeraldChicken);

        magmaChicken = addChicken("MagmaChicken", this.nextID(), "MagmaChicken", 0x1a0500, 0x000000, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.magma_cream))
            .setLang("en_US", "Magma Chicken")
            .setLang("ja_JP", "マグマのニワトリ");
        allChickens.add(magmaChicken);

        xpChicken = addChicken("XpChicken", this.nextID(), "XpChicken", 0x3dff1e, 0x3ff123, SpawnType.NONE)
            .setLayItem(new ItemStack(ModItems.SOLID_XP.getItem(), 1, 0))
            .setLang("en_US", "XP Chicken")
            .setLang("ja_JP", "経験値のニワトリ");
        allChickens.add(xpChicken);

        // EtFuturum mod
        if (LibMods.EtFuturum.isLoaded()) {
            pShardChicken = addChicken(
                "PShardChicken",
                this.nextID(),
                "PShardChicken",
                0x43806e,
                0x9fcbbc,
                SpawnType.NONE).setLayString("etfuturum:prismarine_shard")
                    .setLang("en_US", "Prismarine Shard Chicken")
                    .setLang("ja_JP", "プリズマリンシャードのニワトリ");
            allChickens.add(pShardChicken);

            pCrystalChicken = addChicken(
                "PCrystalChicken",
                this.nextID(),
                "PCrystalChicken",
                0x4e6961,
                0xdfe9dc,
                SpawnType.NONE).setLayString("etfuturum:prismarine_crystals")
                    .setLang("en_US", "Prismarine Crystal Chicken")
                    .setLang("ja_JP", "プリズマリンクリスタルのニワトリ");
            allChickens.add(pCrystalChicken);
        }

        soulsandChicken = addChicken(
            "SoulSandChicken",
            this.nextID(),
            "SoulSandChicken",
            0x453125,
            0xd52f08,
            SpawnType.HELL).setLayItem(new ItemStack(Blocks.soul_sand, 1, 0))
                .setLang("en_US", "Soul Sand Chicken")
                .setLang("ja_JP", "ソウルサンドのニワトリ");
        allChickens.add(soulsandChicken);

        obsidianChicken = addChicken(
            "ObsidianChicken",
            this.nextID(),
            "ObsidianChicken",
            0x08080e,
            0x463a60,
            SpawnType.NONE).setLayItem(new ItemStack(Blocks.obsidian, 1, 0))
                .setLang("en_US", "Obsidian Chicken")
                .setLang("ja_JP", "黒曜石のニワトリ");
        allChickens.add(obsidianChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> all) {

        // === Dye ===
        setParents(pinkChicken, redChicken, whiteChicken);
        setParents(purpleChicken, blueChicken, redChicken);
        setParents(orangeChicken, redChicken, yellowChicken);
        setParents(lightBlueChicken, whiteChicken, blueChicken);
        setParents(limeChicken, greenChicken, whiteChicken);
        setParents(grayChicken, blackChicken, whiteChicken);
        setParents(cyanChicken, blueChicken, greenChicken);
        setParents(silverChicken, grayChicken, whiteChicken);
        setParents(magentaChicken, purpleChicken, pinkChicken);

        // === T2 ===
        setParents(stringChicken, blackChicken, logChicken);
        setParents(glowstoneChicken, quartzChicken, yellowChicken);
        setParents(gunpowderChicken, sandChicken, flintChicken);
        setParents(redstoneChicken, redChicken, sandChicken);
        setParents(glassChicken, quartzChicken, redstoneChicken);
        setParents(ironChicken, flintChicken, whiteChicken);
        setParents(coalChicken, flintChicken, logChicken);
        setParents(brownChicken, redChicken, greenChicken);

        // === T3 ===
        setParents(goldChicken, ironChicken, yellowChicken);
        setParents(snowballChicken, blueChicken, logChicken);
        setParents(waterChicken, gunpowderChicken, snowballChicken);
        setParents(lavaChicken, coalChicken, quartzChicken);
        setParents(clayChicken, snowballChicken, sandChicken);
        setParents(leatherChicken, stringChicken, brownChicken);
        setParents(netherwartChicken, brownChicken, glowstoneChicken);

        // === T4 ===
        setParents(diamondChicken, glassChicken, goldChicken);
        setParents(blazeChicken, goldChicken, lavaChicken);
        setParents(slimeChicken, clayChicken, greenChicken);

        // === T5 ===
        setParents(enderChicken, diamondChicken, netherwartChicken);
        setParents(ghastChicken, whiteChicken, blazeChicken);
        setParents(emeraldChicken, diamondChicken, greenChicken);
        setParents(magmaChicken, slimeChicken, blazeChicken);

        // extra
        setParents(xpChicken, emeraldChicken, greenChicken);
        setParents(obsidianChicken, waterChicken, lavaChicken);
        setParents(pShardChicken, waterChicken, blueChicken);
        setParents(pCrystalChicken, waterChicken, emeraldChicken);
    }

    private ChickensRegistryItem addDye(EnumDye color, String name) {
        return new ChickensRegistryItem(
            this.nextID(),
            name,
            new ResourceLocation(LibMisc.MOD_ID, texturesLocation + name + ".png"),
            0xf2f2f2,
            color.getColor()).setSpawnType(SpawnType.NONE)
                .setLayItem(new ItemStack(Items.dye, 1, color.ordinal()));
    }

}
