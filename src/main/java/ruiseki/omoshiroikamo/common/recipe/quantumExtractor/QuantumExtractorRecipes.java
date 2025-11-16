package ruiseki.omoshiroikamo.common.recipe.quantumExtractor;

import java.io.File;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.item.IFocusableRegistry;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

public class QuantumExtractorRecipes {

    public static IFocusableRegistry quantumOreExtractorRegistry;
    public static IFocusableRegistry quantumResExtractorRegistry;

    public static void init() {
        if (!BackportConfigs.useEnvironmentalTech) {
            return;
        }
        quantumOreExtractorRegistry = new FocusableRegistry();
        quantumResExtractorRegistry = new FocusableRegistry();
        addVoidOreMinerRecipes();
        addVoidResMinerRecipes();
    }

    public static void addVoidOreMinerRecipes() {
        IFocusableRegistry reg = quantumOreExtractorRegistry;
        File file = new File("config/" + LibMisc.MOD_ID + "/quantumExtractor/ore.json");
        if (file.exists()) {
            FocusableHandler.loadRegistryFromJson(file, reg);
        } else {
            FocusableHandler.FocusableList defaults = new FocusableHandler.FocusableList();
            defaults.addEntry(new FocusableHandler.FocusableOre("oreCoal", EnumDye.BLACK, 40));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreIron", EnumDye.WHITE, 32));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreQuartz", EnumDye.WHITE, 20));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreRedstone", EnumDye.RED, 20));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreLapis", EnumDye.BLUE, 16));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreGold", EnumDye.YELLOW, 14));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreDiamond", EnumDye.CYAN, 11));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreEmerald", EnumDye.LIME, 5));
            defaults.addEntry(new FocusableHandler.FocusableOre("glowstone", EnumDye.YELLOW, 7));
            defaults.addEntry(new FocusableHandler.FocusableOre("shardLonsdaleite", EnumDye.BLACK, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreCopper", EnumDye.ORANGE, 26));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreTin", EnumDye.GRAY, 26));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreSilver", EnumDye.SILVER, 20));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreLead", EnumDye.PURPLE, 22));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreAluminum", EnumDye.WHITE, 20));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreUranium", EnumDye.GREEN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreYellorite", EnumDye.GREEN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreCertusQuartz", EnumDye.LIGHT_BLUE, 8));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreChargedCertusQuartz", EnumDye.LIGHT_BLUE, 4));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreCinnabar", EnumDye.BROWN, 10));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreAmber", EnumDye.ORANGE, 9));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreNickel", EnumDye.GRAY, 9));
            defaults.addEntry(new FocusableHandler.FocusableOre("orePlatinum", EnumDye.LIGHT_BLUE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreMithril", EnumDye.LIGHT_BLUE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreSulfur", EnumDye.YELLOW, 10));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreNiter", EnumDye.WHITE, 10));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreSaltpeter", EnumDye.WHITE, 8));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreFirestone", EnumDye.RED, 4));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreCobalt", EnumDye.BLUE, 7));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreArdite", EnumDye.ORANGE, 7));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreApatite", EnumDye.LIGHT_BLUE, 10));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreDraconium", EnumDye.PURPLE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreGraphite", EnumDye.BLACK, 12));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreQuartzBlack", EnumDye.BLACK, 14));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreRuby", EnumDye.RED, 8));
            defaults.addEntry(new FocusableHandler.FocusableOre("orePeridot", EnumDye.GREEN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreTopaz", EnumDye.ORANGE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreTanzanite", EnumDye.PURPLE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreMalachite", EnumDye.CYAN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreSapphire", EnumDye.BLUE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreGarnet", EnumDye.ORANGE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreHeliodore", EnumDye.YELLOW, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreBeryl", EnumDye.GREEN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreIndicolite", EnumDye.GREEN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreAquamarine", EnumDye.BLUE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreIolite", EnumDye.PURPLE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreAmethyst", EnumDye.PURPLE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreAgate", EnumDye.PINK, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreMorganite", EnumDye.PINK, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreOnyx", EnumDye.BLACK, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreOpal", EnumDye.WHITE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreCarnelian", EnumDye.RED, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreSpinel", EnumDye.BROWN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreCitrine", EnumDye.BROWN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreJasper", EnumDye.YELLOW, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreGoldenBeryl", EnumDye.YELLOW, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreMoldavite", EnumDye.GREEN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreTurquoise", EnumDye.CYAN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreMoonstone", EnumDye.CYAN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreBlueTopaz", EnumDye.BLUE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreVioletSapphire", EnumDye.PURPLE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreLepidolite", EnumDye.PURPLE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreAmetrine", EnumDye.PURPLE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreBlackDiamond", EnumDye.BLACK, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreAlexandrite", EnumDye.WHITE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreChaos", EnumDye.WHITE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreEnderEssence", EnumDye.GREEN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreResonating", EnumDye.BROWN, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreZinc", EnumDye.PINK, 9));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreIridium", EnumDye.WHITE, 6));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreTungsten", EnumDye.BLACK, 8));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreOsmium", EnumDye.SILVER, 8));
            defaults.addEntry(new FocusableHandler.FocusableOre("oreSalt", EnumDye.WHITE, 3));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("rftools:dimensional_shard_ore", 0, EnumDye.WHITE, 4));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("mysticalagriculture:inferium_ore", 0, EnumDye.GREEN, 6));
            defaults.addEntry(
                new FocusableHandler.FocusableBlock("mysticalagriculture:prosperity_ore", 0, EnumDye.SILVER, 4));
            FocusableHandler.saveRegistryDefaultsToJson(file, defaults);
            FocusableHandler.loadIntoRegistry(defaults, reg);
        }
    }

    public static void addVoidResMinerRecipes() {
        IFocusableRegistry reg = quantumResExtractorRegistry;
        File file = new File("config/" + LibMisc.MOD_ID + "/quantumExtractor/res.json");
        if (file.exists()) {
            FocusableHandler.loadRegistryFromJson(file, reg);
        } else {
            FocusableHandler.FocusableList defaults = new FocusableHandler.FocusableList();
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:stone", 0, EnumDye.GRAY, 16));
            defaults.addEntry(new FocusableHandler.FocusableBlock("etfuturum:stone", 1, EnumDye.PINK, 14));
            defaults.addEntry(new FocusableHandler.FocusableBlock("etfuturum:stone", 3, EnumDye.WHITE, 14));
            defaults.addEntry(new FocusableHandler.FocusableBlock("etfuturum:stone", 5, EnumDye.GRAY, 14));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:gravel", 0, EnumDye.SILVER, 14));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:grass", 0, EnumDye.GREEN, 6));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:dirt", 0, EnumDye.BROWN, 14));
            defaults.addEntry(new FocusableHandler.FocusableBlock("etfuturum:coarse_dirt", 0, EnumDye.BROWN, 6));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:sand", 0, EnumDye.YELLOW, 14));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:sand", 1, EnumDye.YELLOW, 10));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:cobblestone", 0, EnumDye.GRAY, 14));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:mossy_cobblestone", 0, EnumDye.GREEN, 6));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:obsidian", 0, EnumDye.PURPLE, 5));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:clay", 0, EnumDye.SILVER, 7));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:netherrack", 0, EnumDye.RED, 14));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:soul_sand", 0, EnumDye.BROWN, 10));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:mycelium", 0, EnumDye.PURPLE, 4));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:end_stone", 0, EnumDye.WHITE, 7));
            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:hardened_clay", 0, EnumDye.RED, 6));

            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 0, EnumDye.WHITE, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 1, EnumDye.ORANGE, 1));
            defaults.addEntry(
                new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 2, EnumDye.MAGENTA, 1));
            defaults.addEntry(
                new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 3, EnumDye.LIGHT_BLUE, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 4, EnumDye.YELLOW, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 5, EnumDye.LIME, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 6, EnumDye.PINK, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 7, EnumDye.GRAY, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 8, EnumDye.SILVER, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 9, EnumDye.CYAN, 1));
            defaults.addEntry(
                new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 10, EnumDye.PURPLE, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 11, EnumDye.BLUE, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 12, EnumDye.BROWN, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 13, EnumDye.GREEN, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 14, EnumDye.RED, 1));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 15, EnumDye.BLACK, 1));

            defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:sponge", 0, EnumDye.YELLOW, 2));
            defaults.addEntry(new FocusableHandler.FocusableBlock(LibMisc.MOD_ID + ":alabaster", 0, EnumDye.WHITE, 10));
            defaults.addEntry(new FocusableHandler.FocusableBlock(LibMisc.MOD_ID + ":basalt", 0, EnumDye.BLACK, 10));
            defaults
                .addEntry(new FocusableHandler.FocusableBlock(LibMisc.MOD_ID + ":hardened_stone", 0, EnumDye.GRAY, 10));
            defaults.addEntry(new FocusableHandler.FocusableBlock(LibMisc.MOD_ID + ":mica", 0, EnumDye.WHITE, 2));
            defaults.addEntry(new FocusableHandler.FocusableBlock("chisel:limestone", 0, EnumDye.LIME, 10));
            defaults.addEntry(new FocusableHandler.FocusableBlock("chisel:marble", 0, EnumDye.WHITE, 10));
            defaults.addEntry(new FocusableHandler.FocusableBlock("TConstruct:CraftedSoil", 5, EnumDye.GREEN, 2));
            defaults.addEntry(new FocusableHandler.FocusableBlock("TConstruct:slime.grass", 0, EnumDye.GREEN, 1));
            defaults.addEntry(
                new FocusableHandler.FocusableBlock("appliedenergistics2:tile.BlockSkyStone", 0, EnumDye.BLACK, 2));
            FocusableHandler.saveRegistryDefaultsToJson(file, defaults);
            FocusableHandler.loadIntoRegistry(defaults, reg);
        }

    }
}
