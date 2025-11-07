package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.enderio.core.common.util.DyeColor;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
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
    public List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> all) {

        // DYE CHICKENS
        whiteChicken = addDye(all, DyeColor.WHITE, "WhiteChicken").setDropItem(new ItemStack(Items.bone))
            .setSpawnType(SpawnType.NORMAL);

        yellowChicken = addDye(all, DyeColor.YELLOW, "YellowChicken");
        blueChicken = addDye(all, DyeColor.BLUE, "BlueChicken");
        greenChicken = addDye(all, DyeColor.GREEN, "GreenChicken");
        redChicken = addDye(all, DyeColor.RED, "RedChicken");
        blackChicken = addDye(all, DyeColor.BLACK, "BlackChicken");

        pinkChicken = addDye(all, DyeColor.PINK, "PinkChicken");
        purpleChicken = addDye(all, DyeColor.PURPLE, "PurpleChicken");
        orangeChicken = addDye(all, DyeColor.ORANGE, "OrangeChicken");
        lightBlueChicken = addDye(all, DyeColor.LIGHT_BLUE, "LightBlueChicken");
        limeChicken = addDye(all, DyeColor.LIME, "LimeChicken");
        grayChicken = addDye(all, DyeColor.GRAY, "GrayChicken");
        cyanChicken = addDye(all, DyeColor.CYAN, "CyanChicken");
        silverChicken = addDye(all, DyeColor.SILVER, "SilverDyeChicken");
        magentaChicken = addDye(all, DyeColor.MAGENTA, "MagentaChicken");

        // SMART CHICKEN
        smartChicken = addChicken(
            all,
            "SmartChicken",
            this.nextID(),
            "SmartChicken.png",
            new ItemStack(Items.egg),
            0xffffff,
            0xffff00,
            SpawnType.NONE);

        // BASE CHICKENS
        flintChicken = addChicken(
            all,
            "FlintChicken",
            this.nextID(),
            "FlintChicken.png",
            new ItemStack(Items.flint),
            0x6b6b47,
            0xa3a375,
            SpawnType.NONE);

        quartzChicken = addChicken(
            all,
            "QuartzChicken",
            this.nextID(),
            "QuartzChicken.png",
            new ItemStack(Items.quartz),
            0x4d0000,
            0x1a0000,
            SpawnType.HELL);

        logChicken = addChicken(
            all,
            "LogChicken",
            this.nextID(),
            "LogChicken.png",
            new ItemStack(Blocks.log),
            0x98846d,
            0x528358,
            SpawnType.NONE);

        sandChicken = addChicken(
            all,
            "SandChicken",
            this.nextID(),
            "SandChicken.png",
            new ItemStack(Blocks.sand),
            0xece5b1,
            0xa7a06c,
            SpawnType.NONE);

        // TIER 2
        stringChicken = addChicken(
            all,
            "StringChicken",
            this.nextID(),
            "StringChicken.png",
            new ItemStack(Items.string),
            0x331a00,
            0x800000,
            SpawnType.NONE).setDropItem(new ItemStack(Items.spider_eye));

        glowstoneChicken = addChicken(
            all,
            "GlowstoneChicken",
            this.nextID(),
            "GlowstoneChicken.png",
            new ItemStack(Items.glowstone_dust),
            0xffff66,
            0xffff00,
            SpawnType.NONE);

        gunpowderChicken = addChicken(
            all,
            "GunpowderChicken",
            this.nextID(),
            "GunpowderChicken.png",
            new ItemStack(Items.gunpowder),
            0x999999,
            0x404040,
            SpawnType.NONE);

        redstoneChicken = addChicken(
            all,
            "RedstoneChicken",
            this.nextID(),
            "RedstoneChicken.png",
            new ItemStack(Items.redstone),
            0xe60000,
            0x800000,
            SpawnType.NONE);

        glassChicken = addChicken(
            all,
            "GlassChicken",
            this.nextID(),
            "GlassChicken.png",
            new ItemStack(Blocks.glass),
            0xffffff,
            0xeeeeff,
            SpawnType.NONE);

        ironChicken = addChicken(
            all,
            "IronChicken",
            this.nextID(),
            "IronChicken.png",
            new ItemStack(Items.iron_ingot),
            0xffffcc,
            0xffcccc,
            SpawnType.NONE);

        coalChicken = addChicken(
            all,
            "CoalChicken",
            this.nextID(),
            "CoalChicken.png",
            new ItemStack(Items.coal),
            0x262626,
            0x000000,
            SpawnType.NONE);

        brownChicken = addDye(all, DyeColor.BROWN, "BrownChicken");

        // TIER 3
        goldChicken = addChicken(
            all,
            "GoldChicken",
            this.nextID(),
            "GoldChicken.png",
            new ItemStack(Items.gold_ingot),
            0xcccc00,
            0xffff80,
            SpawnType.NONE);

        snowballChicken = addChicken(
            all,
            "SnowballChicken",
            this.nextID(),
            "SnowballChicken.png",
            new ItemStack(Items.snowball),
            0x33bbff,
            0x0088cc,
            SpawnType.SNOW);

        waterChicken = addChicken(
            all,
            "WaterChicken",
            this.nextID(),
            "WaterChicken.png",
            ModItems.LIQUID_EGG.newItemStack(1, 0),
            0x000099,
            0x8080ff,
            SpawnType.NONE);

        lavaChicken = addChicken(
            all,
            "LavaChicken",
            this.nextID(),
            "LavaChicken.png",
            ModItems.LIQUID_EGG.newItemStack(1, 1),
            0xcc3300,
            0xffff00,
            SpawnType.HELL);

        clayChicken = addChicken(
            all,
            "ClayChicken",
            this.nextID(),
            "ClayChicken.png",
            new ItemStack(Items.clay_ball),
            0xcccccc,
            0xbfbfbf,
            SpawnType.NONE);

        leatherChicken = addChicken(
            all,
            "LeatherChicken",
            this.nextID(),
            "LeatherChicken.png",
            new ItemStack(Items.leather),
            0xA7A06C,
            0x919191,
            SpawnType.NONE);

        netherwartChicken = addChicken(
            all,
            "NetherwartChicken",
            this.nextID(),
            "NetherwartChicken.png",
            new ItemStack(Items.nether_wart),
            0x800000,
            0x331a00,
            SpawnType.NONE);

        // TIER 4
        diamondChicken = addChicken(
            all,
            "DiamondChicken",
            this.nextID(),
            "DiamondChicken.png",
            new ItemStack(Items.diamond),
            0x99ccff,
            0xe6f2ff,
            SpawnType.NONE);

        blazeChicken = addChicken(
            all,
            "BlazeChicken",
            this.nextID(),
            "BlazeChicken.png",
            new ItemStack(Items.blaze_rod),
            0xffff66,
            0xff3300,
            SpawnType.NONE);

        slimeChicken = addChicken(
            all,
            "SlimeChicken",
            this.nextID(),
            "SlimeChicken.png",
            new ItemStack(Items.slime_ball),
            0x009933,
            0x99ffbb,
            SpawnType.NONE);

        // TIER 5
        enderChicken = addChicken(
            all,
            "EnderChicken",
            this.nextID(),
            "EnderChicken.png",
            new ItemStack(Items.ender_pearl),
            0x001a00,
            0x001a33,
            SpawnType.NONE);

        ghastChicken = addChicken(
            all,
            "GhastChicken",
            this.nextID(),
            "GhastChicken.png",
            new ItemStack(Items.ghast_tear),
            0xffffcc,
            0xffffff,
            SpawnType.NONE);

        emeraldChicken = addChicken(
            all,
            "EmeraldChicken",
            this.nextID(),
            "EmeraldChicken.png",
            new ItemStack(Items.emerald),
            0x00cc00,
            0x003300,
            SpawnType.NONE);

        magmaChicken = addChicken(
            all,
            "MagmaChicken",
            this.nextID(),
            "MagmaChicken.png",
            new ItemStack(Items.magma_cream),
            0x1a0500,
            0x000000,
            SpawnType.NONE);

        xpChicken = addChicken(
            all,
            "XpChicken",
            this.nextID(),
            "XpChicken.png",
            new ItemStack(ModItems.SOLID_XP.get(), 1, 0),
            0x3dff1e,
            0x3ff123,
            SpawnType.NONE);

        if (LibMods.EtFuturum.isLoaded()) {
            pShardChicken = addChicken(
                all,
                "PShardChicken",
                this.nextID(),
                "PShardChicken.png",
                new ItemStack(ganymedes01.etfuturum.ModItems.PRISMARINE_SHARD.get(), 1, 0),
                0x43806e,
                0x9fcbbc,
                SpawnType.NONE);

            pCrystalChicken = addChicken(
                all,
                "PCrystalChicken",
                this.nextID(),
                "PCrystalChicken.png",
                new ItemStack(ganymedes01.etfuturum.ModItems.PRISMARINE_CRYSTALS.get(), 1, 0),
                0x4e6961,
                0xdfe9dc,
                SpawnType.NONE);
        }

        soulsandChicken = addChicken(
            all,
            "SoulSandChicken",
            this.nextID(),
            "SoulSandChicken.png",
            new ItemStack(Blocks.soul_sand, 1, 0),
            0x453125,
            0xd52f08,
            SpawnType.HELL);

        obsidianChicken = addChicken(
            all,
            "ObsidianChicken",
            this.nextID(),
            "ObsidianChicken.png",
            new ItemStack(Blocks.obsidian, 1, 0),
            0x08080e,
            0x463a60,
            SpawnType.NONE);

        return all;
    }

    @Override
    public void RegisterAllParents(List<ChickensRegistryItem> all) {

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

    private ChickensRegistryItem addDye(List<ChickensRegistryItem> all, DyeColor color, String name) {
        ChickensRegistryItem item = new ChickensRegistryItem(
            this.nextID(),
            name,
            new ResourceLocation(LibMisc.MOD_ID, "textures/entity/chicken/base/" + name + ".png"),
            new ItemStack(Items.dye, 1, color.ordinal()),
            0xf2f2f2,
            color.getColor()).setSpawnType(SpawnType.NONE);

        all.add(item);
        return item;
    }

}
