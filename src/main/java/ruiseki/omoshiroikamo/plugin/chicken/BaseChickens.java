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
        whiteChicken = addDye(EnumDye.WHITE, "WhiteChicken", new String[] { "en_US:White Chicken", "ja_JP:白いニワトリ" })
            .setDropItem(new ItemStack(Items.bone))
            .setSpawnType(SpawnType.NORMAL);
        allChickens.add(whiteChicken);

        yellowChicken = addDye(
            EnumDye.YELLOW,
            "YellowChicken",
            new String[] { "en_US:Yellow Chicken", "ja_JP:黄色いニワトリ" });
        allChickens.add(yellowChicken);

        blueChicken = addDye(EnumDye.BLUE, "BlueChicken", new String[] { "en_US:Blue Chicken", "ja_JP:青いニワトリ" });
        allChickens.add(blueChicken);

        greenChicken = addDye(EnumDye.GREEN, "GreenChicken", new String[] { "en_US:Green Chicken", "ja_JP:緑のニワトリ" });
        allChickens.add(greenChicken);

        redChicken = addDye(EnumDye.RED, "RedChicken", new String[] { "en_US:Red Chicken", "ja_JP:赤いニワトリ" });
        allChickens.add(redChicken);

        blackChicken = addDye(EnumDye.BLACK, "BlackChicken", new String[] { "en_US:Black Chicken", "ja_JP:黒いニワトリ" });
        allChickens.add(blackChicken);

        pinkChicken = addDye(EnumDye.PINK, "PinkChicken", new String[] { "en_US:Pink Chicken", "ja_JP:ピンクのニワトリ" });
        allChickens.add(pinkChicken);

        purpleChicken = addDye(
            EnumDye.PURPLE,
            "PurpleChicken",
            new String[] { "en_US:Purple Chicken", "ja_JP:紫のニワトリ" });
        allChickens.add(purpleChicken);

        orangeChicken = addDye(
            EnumDye.ORANGE,
            "OrangeChicken",
            new String[] { "en_US:Orange Chicken", "ja_JP:オレンジのニワトリ" });
        allChickens.add(orangeChicken);

        lightBlueChicken = addDye(
            EnumDye.LIGHT_BLUE,
            "LightBlueChicken",
            new String[] { "en_US:Light Blue Chicken", "ja_JP:水色のニワトリ" });
        allChickens.add(lightBlueChicken);

        limeChicken = addDye(EnumDye.LIME, "LimeChicken", new String[] { "en_US:Lime Chicken", "ja_JP:黄緑のニワトリ" });
        allChickens.add(limeChicken);

        grayChicken = addDye(EnumDye.GRAY, "GrayChicken", new String[] { "en_US:Gray Chicken", "ja_JP:灰色のニワトリ" });
        allChickens.add(grayChicken);

        cyanChicken = addDye(EnumDye.CYAN, "CyanChicken", new String[] { "en_US:Cyan Chicken", "ja_JP:シアンのニワトリ" });
        allChickens.add(cyanChicken);

        silverChicken = addDye(
            EnumDye.SILVER,
            "SilverDyeChicken",
            new String[] { "en_US:Silver Chicken", "ja_JP:薄灰色のニワトリ" });
        allChickens.add(silverChicken);

        magentaChicken = addDye(
            EnumDye.MAGENTA,
            "MagentaChicken",
            new String[] { "en_US:Magenta Chicken", "ja_JP:マゼンタのニワトリ" });
        allChickens.add(magentaChicken);

        brownChicken = addDye(EnumDye.BROWN, "BrownChicken", new String[] { "en_US:Brown Chicken", "ja_JP:茶色のニワトリ" });
        allChickens.add(brownChicken);

        // SMART CHICKEN
        smartChicken = addChicken(
            "SmartChicken",
            this.nextID(),
            "SmartChicken.png",
            0xffffff,
            0xffff00,
            SpawnType.NONE,
            new String[] { "en_US:Smart Chicken", "ja_JP:スマートニワトリ" }).setLayItem(new ItemStack(Items.egg));
        allChickens.add(smartChicken);

        // BASE CHICKENS
        flintChicken = addChicken(
            "FlintChicken",
            this.nextID(),
            "FlintChicken.png",
            0x6b6b47,
            0xa3a375,
            SpawnType.NONE,
            new String[] { "en_US:Flint Chicken", "ja_JP:火打石のニワトリ" }).setLayItem(new ItemStack(Items.flint));
        allChickens.add(flintChicken);

        quartzChicken = addChicken(
            "QuartzChicken",
            this.nextID(),
            "QuartzChicken.png",
            0x4d0000,
            0x1a0000,
            SpawnType.HELL,
            new String[] { "en_US:Quartz Chicken", "ja_JP:ネザークォーツのニワトリ" }).setLayItem(new ItemStack(Items.quartz));
        allChickens.add(quartzChicken);

        logChicken = addChicken(
            "LogChicken",
            this.nextID(),
            "LogChicken.png",
            0x98846d,
            0x528358,
            SpawnType.NONE,
            new String[] { "en_US:Log Chicken", "ja_JP:原木のニワトリ" }).setLayItem(new ItemStack(Blocks.log));
        allChickens.add(logChicken);

        sandChicken = addChicken(
            "SandChicken",
            this.nextID(),
            "SandChicken.png",
            0xece5b1,
            0xa7a06c,
            SpawnType.NONE,
            new String[] { "en_US:Sand Chicken", "ja_JP:砂のニワトリ" }).setLayItem(new ItemStack(Blocks.sand));
        allChickens.add(sandChicken);

        // TIER 2
        stringChicken = addChicken(
            "StringChicken",
            this.nextID(),
            "StringChicken.png",
            0x331a00,
            0x800000,
            SpawnType.NONE,
            new String[] { "en_US:String Chicken", "ja_JP:糸のニワトリ" }).setLayItem(new ItemStack(Items.string))
                .setDropItem(new ItemStack(Items.spider_eye));
        allChickens.add(stringChicken);

        glowstoneChicken = addChicken(
            "GlowstoneChicken",
            this.nextID(),
            "GlowstoneChicken.png",
            0xffff66,
            0xffff00,
            SpawnType.NONE,
            new String[] { "en_US:Glowstone Chicken", "ja_JP:グロウストーンのニワトリ" })
                .setLayItem(new ItemStack(Items.glowstone_dust));
        allChickens.add(glowstoneChicken);

        gunpowderChicken = addChicken(
            "GunpowderChicken",
            this.nextID(),
            "GunpowderChicken.png",
            0x999999,
            0x404040,
            SpawnType.NONE,
            new String[] { "en_US:Gunpowder Chicken", "ja_JP:火薬のニワトリ" }).setLayItem(new ItemStack(Items.gunpowder));
        allChickens.add(gunpowderChicken);

        redstoneChicken = addChicken(
            "RedstoneChicken",
            this.nextID(),
            "RedstoneChicken.png",
            0xe60000,
            0x800000,
            SpawnType.NONE,
            new String[] { "en_US:Redstone Chicken", "ja_JP:レッドストーンのニワトリ" }).setLayItem(new ItemStack(Items.redstone));
        allChickens.add(redstoneChicken);

        glassChicken = addChicken(
            "GlassChicken",
            this.nextID(),
            "GlassChicken.png",
            0xffffff,
            0xeeeeff,
            SpawnType.NONE,
            new String[] { "en_US:Glass Chicken", "ja_JP:ガラスのニワトリ" }).setLayItem(new ItemStack(Blocks.glass));
        allChickens.add(glassChicken);

        ironChicken = addChicken(
            "IronChicken",
            this.nextID(),
            "IronChicken.png",
            0xffffcc,
            0xffcccc,
            SpawnType.NONE,
            new String[] { "en_US:Iron Chicken", "ja_JP:鉄のニワトリ" }).setLayItem(new ItemStack(Items.iron_ingot));
        allChickens.add(ironChicken);

        coalChicken = addChicken(
            "CoalChicken",
            this.nextID(),
            "CoalChicken.png",
            0x262626,
            0x000000,
            SpawnType.NONE,
            new String[] { "en_US:Coal Chicken", "ja_JP:石炭のニワトリ" }).setLayItem(new ItemStack(Items.coal));
        allChickens.add(coalChicken);

        // TIER 3
        goldChicken = addChicken(
            "GoldChicken",
            this.nextID(),
            "GoldChicken.png",
            0xcccc00,
            0xffff80,
            SpawnType.NONE,
            new String[] { "en_US:Gold Chicken", "ja_JP:金のニワトリ" }).setLayItem(new ItemStack(Items.gold_ingot));
        allChickens.add(goldChicken);

        snowballChicken = addChicken(
            "SnowballChicken",
            this.nextID(),
            "SnowballChicken.png",
            0x33bbff,
            0x0088cc,
            SpawnType.SNOW,
            new String[] { "en_US:Snowball Chicken", "ja_JP:雪玉のニワトリ" }).setLayItem(new ItemStack(Items.snowball));
        allChickens.add(snowballChicken);

        waterChicken = addChicken(
            "WaterChicken",
            this.nextID(),
            "WaterChicken.png",
            0x000099,
            0x8080ff,
            SpawnType.NONE,
            new String[] { "en_US:Water Chicken", "ja_JP:水のニワトリ" }).setLayItem(ModItems.LIQUID_EGG.newItemStack(1, 0));
        allChickens.add(waterChicken);

        lavaChicken = addChicken(
            "LavaChicken",
            this.nextID(),
            "LavaChicken.png",
            0xcc3300,
            0xffff00,
            SpawnType.HELL,
            new String[] { "en_US:Lava Chicken", "ja_JP:溶岩のニワトリ" }).setLayItem(ModItems.LIQUID_EGG.newItemStack(1, 1));
        allChickens.add(lavaChicken);

        clayChicken = addChicken(
            "ClayChicken",
            this.nextID(),
            "ClayChicken.png",
            0xcccccc,
            0xbfbfbf,
            SpawnType.NONE,
            new String[] { "en_US:Clay Chicken", "ja_JP:粘土のニワトリ" }).setLayItem(new ItemStack(Items.clay_ball));
        allChickens.add(clayChicken);

        leatherChicken = addChicken(
            "LeatherChicken",
            this.nextID(),
            "LeatherChicken.png",
            0xA7A06C,
            0x919191,
            SpawnType.NONE,
            new String[] { "en_US:Leather Chicken", "ja_JP:革のニワトリ" }).setLayItem(new ItemStack(Items.leather));
        allChickens.add(leatherChicken);

        netherwartChicken = addChicken(
            "NetherwartChicken",
            this.nextID(),
            "NetherwartChicken.png",
            0x800000,
            0x331a00,
            SpawnType.NONE,
            new String[] { "en_US:Nether Wart Chicken", "ja_JP:ネザーウォートのニワトリ" })
                .setLayItem(new ItemStack(Items.nether_wart));
        allChickens.add(netherwartChicken);

        // TIER 4
        diamondChicken = addChicken(
            "DiamondChicken",
            this.nextID(),
            "DiamondChicken.png",
            0x99ccff,
            0xe6f2ff,
            SpawnType.NONE,
            new String[] { "en_US:Diamond Chicken", "ja_JP:ダイヤモンドのニワトリ" }).setLayItem(new ItemStack(Items.diamond));
        allChickens.add(diamondChicken);

        blazeChicken = addChicken(
            "BlazeChicken",
            this.nextID(),
            "BlazeChicken.png",
            0xffff66,
            0xff3300,
            SpawnType.NONE,
            new String[] { "en_US:Blaze Chicken", "ja_JP:ブレイズのニワトリ" }).setLayItem(new ItemStack(Items.blaze_rod));
        allChickens.add(blazeChicken);

        slimeChicken = addChicken(
            "SlimeChicken",
            this.nextID(),
            "SlimeChicken.png",
            0x009933,
            0x99ffbb,
            SpawnType.NONE,
            new String[] { "en_US:Slime Chicken", "ja_JP:スライムのニワトリ" }).setLayItem(new ItemStack(Items.slime_ball));
        allChickens.add(slimeChicken);

        // TIER 5
        enderChicken = addChicken(
            "EnderChicken",
            this.nextID(),
            "EnderChicken.png",
            0x001a00,
            0x001a33,
            SpawnType.NONE,
            new String[] { "en_US:Ender Chicken", "ja_JP:エンダーのニワトリ" }).setLayItem(new ItemStack(Items.ender_pearl));
        allChickens.add(enderChicken);

        ghastChicken = addChicken(
            "GhastChicken",
            this.nextID(),
            "GhastChicken.png",
            0xffffcc,
            0xffffff,
            SpawnType.NONE,
            new String[] { "en_US:Ghast Chicken", "ja_JP:ガストのニワトリ" }).setLayItem(new ItemStack(Items.ghast_tear));
        allChickens.add(ghastChicken);

        emeraldChicken = addChicken(
            "EmeraldChicken",
            this.nextID(),
            "EmeraldChicken.png",
            0x00cc00,
            0x003300,
            SpawnType.NONE,
            new String[] { "en_US:Emerald Chicken", "ja_JP:エメラルドのニワトリ" }).setLayItem(new ItemStack(Items.emerald));
        allChickens.add(emeraldChicken);

        magmaChicken = addChicken(
            "MagmaChicken",
            this.nextID(),
            "MagmaChicken.png",
            0x1a0500,
            0x000000,
            SpawnType.NONE,
            new String[] { "en_US:Magma Chicken", "ja_JP:マグマのニワトリ" }).setLayItem(new ItemStack(Items.magma_cream));
        allChickens.add(magmaChicken);

        xpChicken = addChicken(
            "XpChicken",
            this.nextID(),
            "XpChicken.png",
            0x3dff1e,
            0x3ff123,
            SpawnType.NONE,
            new String[] { "en_US:XP Chicken", "ja_JP:経験値のニワトリ" })
                .setLayItem(new ItemStack(ModItems.SOLID_XP.getItem(), 1, 0));
        allChickens.add(xpChicken);

        if (LibMods.EtFuturum.isLoaded()) {
            pShardChicken = addChicken(
                "PShardChicken",
                this.nextID(),
                "PShardChicken.png",
                0x43806e,
                0x9fcbbc,
                SpawnType.NONE,
                new String[] { "en_US:Prismarine Shard Chicken", "ja_JP:プリズマリンシャードのニワトリ" })
                    .setLayItem(new ItemStack(ganymedes01.etfuturum.ModItems.PRISMARINE_SHARD.get(), 1, 0));
            allChickens.add(pShardChicken);

            pCrystalChicken = addChicken(
                "PCrystalChicken",
                this.nextID(),
                "PCrystalChicken.png",
                0x4e6961,
                0xdfe9dc,
                SpawnType.NONE,
                new String[] { "en_US:Prismarine Crystal Chicken", "ja_JP:プリズマリンクリスタルのニワトリ" })
                    .setLayItem(new ItemStack(ganymedes01.etfuturum.ModItems.PRISMARINE_CRYSTALS.get(), 1, 0));
            allChickens.add(pCrystalChicken);
        }

        soulsandChicken = addChicken(
            "SoulSandChicken",
            this.nextID(),
            "SoulSandChicken.png",
            0x453125,
            0xd52f08,
            SpawnType.HELL,
            new String[] { "en_US:Soul Sand Chicken", "ja_JP:ソウルサンドのニワトリ" })
                .setLayItem(new ItemStack(Blocks.soul_sand, 1, 0));
        allChickens.add(soulsandChicken);

        obsidianChicken = addChicken(
            "ObsidianChicken",
            this.nextID(),
            "ObsidianChicken.png",
            0x08080e,
            0x463a60,
            SpawnType.NONE,
            new String[] { "en_US:Obsidian Chicken", "ja_JP:黒曜石のニワトリ" })
                .setLayItem(new ItemStack(Blocks.obsidian, 1, 0));
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

    private ChickensRegistryItem addDye(EnumDye color, String name, String[] lang) {
        return new ChickensRegistryItem(
            this.nextID(),
            name,
            new ResourceLocation(LibMisc.MOD_ID, texturesLocation + name + ".png"),
            0xf2f2f2,
            color.getColor(),
            lang).setSpawnType(SpawnType.NONE)
                .setLayItem(new ItemStack(Items.dye, 1, color.ordinal()));
    }

}
