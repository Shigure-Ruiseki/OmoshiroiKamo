package ruiseki.omoshiroikamo.module.chickens.registries;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;

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
    public static ChickensRegistryItem lightGrayChicken;
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
            .setSpawnType(SpawnType.NORMAL);
        allChickens.add(whiteChicken);

        yellowChicken = addDye(EnumDye.YELLOW, "YellowChicken");
        allChickens.add(yellowChicken);

        blueChicken = addDye(EnumDye.BLUE, "BlueChicken");
        allChickens.add(blueChicken);

        greenChicken = addDye(EnumDye.GREEN, "GreenChicken");
        allChickens.add(greenChicken);

        redChicken = addDye(EnumDye.RED, "RedChicken");
        allChickens.add(redChicken);

        blackChicken = addDye(EnumDye.BLACK, "BlackChicken");
        allChickens.add(blackChicken);

        pinkChicken = addDye(EnumDye.PINK, "PinkChicken");
        allChickens.add(pinkChicken);

        purpleChicken = addDye(EnumDye.PURPLE, "PurpleChicken");
        allChickens.add(purpleChicken);

        orangeChicken = addDye(EnumDye.ORANGE, "OrangeChicken");
        allChickens.add(orangeChicken);

        lightBlueChicken = addDye(EnumDye.LIGHT_BLUE, "LightBlueChicken");
        allChickens.add(lightBlueChicken);

        limeChicken = addDye(EnumDye.LIME, "LimeChicken");
        allChickens.add(limeChicken);

        grayChicken = addDye(EnumDye.GRAY, "GrayChicken");
        allChickens.add(grayChicken);

        cyanChicken = addDye(EnumDye.CYAN, "CyanChicken");
        allChickens.add(cyanChicken);

        lightGrayChicken = addDye(EnumDye.LIGHT_GRAY, "LightGrayChicken");
        allChickens.add(lightGrayChicken);

        magentaChicken = addDye(EnumDye.MAGENTA, "MagentaChicken");
        allChickens.add(magentaChicken);

        brownChicken = addDye(EnumDye.BROWN, "BrownChicken");
        allChickens.add(brownChicken);

        // SMART CHICKEN
        smartChicken = addChicken("SmartChicken", this.nextID(), "SmartChicken", 0x1c77ff, 0xffff00, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.egg));
        allChickens.add(smartChicken);

        // BASE CHICKENS
        flintChicken = addChicken("FlintChicken", this.nextID(), "FlintChicken", 0x2a2a2a, 0xa3a375, SpawnType.NORMAL)
            .setLayItem(new ItemStack(Items.flint));
        allChickens.add(flintChicken);

        quartzChicken = addChicken("QuartzChicken", this.nextID(), "QuartzChicken", 0xeeeeee, 0x1a0000, SpawnType.HELL)
            .setLayItem(new ItemStack(Items.quartz));
        allChickens.add(quartzChicken);

        logChicken = addChicken("LogChicken", this.nextID(), "LogChicken", 0x98846d, 0x528358, SpawnType.NORMAL)
            .setLayItem(new ItemStack(Blocks.log));
        allChickens.add(logChicken);

        sandChicken = addChicken("SandChicken", this.nextID(), "SandChicken", 0xece5b1, 0xa7a06c, SpawnType.NORMAL)
            .setLayItem(new ItemStack(Blocks.sand));
        allChickens.add(sandChicken);

        // TIER 2
        stringChicken = addChicken("StringChicken", this.nextID(), "StringChicken", 0x331a00, 0x800000, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.string))
            .setDropItem(new ItemStack(Items.spider_eye));
        allChickens.add(stringChicken);

        glowstoneChicken = addChicken(
            "GlowstoneChicken",
            this.nextID(),
            "GlowstoneChicken",
            0xffff66,
            0xffff00,
            SpawnType.NONE).setLayItem(new ItemStack(Items.glowstone_dust));
        allChickens.add(glowstoneChicken);

        gunpowderChicken = addChicken(
            "GunpowderChicken",
            this.nextID(),
            "GunpowderChicken",
            0x999999,
            0x404040,
            SpawnType.NONE).setLayItem(new ItemStack(Items.gunpowder));
        allChickens.add(gunpowderChicken);

        redstoneChicken = addChicken(
            "RedstoneChicken",
            this.nextID(),
            "RedstoneChicken",
            0xe60000,
            0x800000,
            SpawnType.NONE).setLayItem(new ItemStack(Items.redstone));
        allChickens.add(redstoneChicken);

        glassChicken = addChicken("GlassChicken", this.nextID(), "GlassChicken", 0xffffff, 0xeeeeff, SpawnType.NONE)
            .setLayItem(new ItemStack(Blocks.glass));
        allChickens.add(glassChicken);

        ironChicken = addChicken("IronChicken", this.nextID(), "IronChicken", 0xffffcc, 0xffcccc, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.iron_ingot));
        allChickens.add(ironChicken);

        coalChicken = addChicken("CoalChicken", this.nextID(), "CoalChicken", 0x262626, 0x000000, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.coal));
        allChickens.add(coalChicken);

        // TIER 3
        goldChicken = addChicken("GoldChicken", this.nextID(), "GoldChicken", 0xcccc00, 0xffff80, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.gold_ingot));
        allChickens.add(goldChicken);

        snowballChicken = addChicken(
            "SnowballChicken",
            this.nextID(),
            "SnowballChicken",
            0x33bbff,
            0x0088cc,
            SpawnType.SNOW).setLayItem(new ItemStack(Items.snowball));
        allChickens.add(snowballChicken);

        waterChicken = addChicken("WaterChicken", this.nextID(), "WaterChicken", 0x000099, 0x8080ff, SpawnType.NONE)
            .setLayItem(ChickensItems.LIQUID_EGG.newItemStack(1, 0));
        allChickens.add(waterChicken);

        lavaChicken = addChicken("LavaChicken", this.nextID(), "LavaChicken", 0xcc3300, 0xffff00, SpawnType.HELL)
            .setLayItem(ChickensItems.LIQUID_EGG.newItemStack(1, 1));
        allChickens.add(lavaChicken);

        clayChicken = addChicken("ClayChicken", this.nextID(), "ClayChicken", 0xcccccc, 0xbfbfbf, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.clay_ball));
        allChickens.add(clayChicken);

        leatherChicken = addChicken(
            "LeatherChicken",
            this.nextID(),
            "LeatherChicken",
            0x222222,
            0xc5c5c5,
            SpawnType.NONE).setLayItem(new ItemStack(Items.leather));
        allChickens.add(leatherChicken);

        netherwartChicken = addChicken(
            "NetherwartChicken",
            this.nextID(),
            "NetherwartChicken",
            0x800000,
            0x331a00,
            SpawnType.NONE).setLayItem(new ItemStack(Items.nether_wart));
        allChickens.add(netherwartChicken);

        // TIER 4
        diamondChicken = addChicken(
            "DiamondChicken",
            this.nextID(),
            "DiamondChicken",
            0x99ccff,
            0xe6f2ff,
            SpawnType.NONE).setLayItem(new ItemStack(Items.diamond));
        allChickens.add(diamondChicken);

        blazeChicken = addChicken("BlazeChicken", this.nextID(), "BlazeChicken", 0xffff66, 0xff3300, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.blaze_rod));
        allChickens.add(blazeChicken);

        slimeChicken = addChicken("SlimeChicken", this.nextID(), "SlimeChicken", 0x009933, 0x99ffbb, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.slime_ball));
        allChickens.add(slimeChicken);

        // TIER 5
        enderChicken = addChicken("EnderChicken", this.nextID(), "EnderChicken", 0x001a00, 0x001a33, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.ender_pearl));
        allChickens.add(enderChicken);

        ghastChicken = addChicken("GhastChicken", this.nextID(), "GhastChicken", 0xffffcc, 0xffffff, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.ghast_tear));
        allChickens.add(ghastChicken);

        emeraldChicken = addChicken(
            "EmeraldChicken",
            this.nextID(),
            "EmeraldChicken",
            0x00cc00,
            0x003300,
            SpawnType.NONE).setLayItem(new ItemStack(Items.emerald));
        allChickens.add(emeraldChicken);

        magmaChicken = addChicken("MagmaChicken", this.nextID(), "MagmaChicken", 0xc54901, 0x7dea65, SpawnType.NONE)
            .setLayItem(new ItemStack(Items.magma_cream));
        allChickens.add(magmaChicken);

        xpChicken = addChicken("XpChicken", this.nextID(), "XpChicken", 0x3dff1e, 0x3ff123, SpawnType.NONE)
            .setLayItem(new ItemStack(ChickensItems.SOLID_XP.getItem(), 1, 0));
        allChickens.add(xpChicken);

        // EtFuturum mod
        if (LibMods.EtFuturum.isLoaded()) {
            pShardChicken = addChicken(
                "PShardChicken",
                this.nextID(),
                "PShardChicken",
                0x43806e,
                0x9fcbbc,
                SpawnType.NONE).setLayString("etfuturum:prismarine_shard");
            allChickens.add(pShardChicken);

            pCrystalChicken = addChicken(
                "PCrystalChicken",
                this.nextID(),
                "PCrystalChicken",
                0x4e6961,
                0xdfe9dc,
                SpawnType.NONE).setLayString("etfuturum:prismarine_crystals");
            allChickens.add(pCrystalChicken);
        }

        soulsandChicken = addChicken(
            "SoulSandChicken",
            this.nextID(),
            "SoulSandChicken",
            0x453125,
            0xd52f08,
            SpawnType.HELL).setLayItem(new ItemStack(Blocks.soul_sand, 1, 0));
        allChickens.add(soulsandChicken);

        obsidianChicken = addChicken(
            "ObsidianChicken",
            this.nextID(),
            "ObsidianChicken",
            0x08080e,
            0x463a60,
            SpawnType.NONE).setLayItem(new ItemStack(Blocks.obsidian, 1, 0));
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
        setParents(lightGrayChicken, grayChicken, whiteChicken);
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
            color.getColor(),
            0xf2f2f2).setSpawnType(SpawnType.NONE)
                .setLayItem(new ItemStack(Items.dye, 1, color.ordinal()));
    }

}
