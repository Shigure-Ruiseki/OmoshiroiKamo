package ruiseki.omoshiroikamo.module.multiblock.common.init;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedStackBase;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.multiblock.common.registries.FocusableHandler;
import ruiseki.omoshiroikamo.module.multiblock.common.registries.FocusableRegistry;

public class QuantumExtractorRecipes {

    public static final int MAX_TIER = 6;

    // Legacy: kept for backward compatibility with existing code
    // that references these directly
    public static IFocusableRegistry[] oreRegistry = new IFocusableRegistry[MAX_TIER];
    public static IFocusableRegistry[] resRegistry = new IFocusableRegistry[MAX_TIER];

    // Dimension-aware registries (lazy-initialized)
    private static final Map<Integer, IFocusableRegistry[]> oreRegistryByDim = new HashMap<>();
    private static final Map<Integer, IFocusableRegistry[]> resRegistryByDim = new HashMap<>();

    // Cached config lists for reuse when creating dimension-specific registries
    private static FocusableHandler.FocusableList cachedOreList;
    private static FocusableHandler.FocusableList cachedResList;

    public static void init() {
        if (!BackportConfigs.useMultiBlock) return;

        // Initialize legacy arrays for backward compatibility (no dimension filter)
        for (int i = 0; i < MAX_TIER; i++) {
            oreRegistry[i] = new FocusableRegistry();
            resRegistry[i] = new FocusableRegistry();
        }

        loadOreConfig();
        loadResConfig();
    }

    public static IFocusableRegistry getOreRegistry(int tier, int dimId) {
        return oreRegistryByDim.computeIfAbsent(dimId, QuantumExtractorRecipes::createOreRegistryForDim)[tier];
    }

    public static IFocusableRegistry getResRegistry(int tier, int dimId) {
        return resRegistryByDim.computeIfAbsent(dimId, QuantumExtractorRecipes::createResRegistryForDim)[tier];
    }

    /**
     * Special dimension ID for "Common" filter in NEI.
     * Shows only ores with dimensions == null (no dimension restriction).
     * Must match NEIDimensionConfig.DIMENSION_COMMON
     */
    public static final int NEI_DIMENSION_COMMON = Integer.MIN_VALUE;

    // NEI-specific registries for "Common only" filter
    private static IFocusableRegistry[] neiCommonOreRegistry;
    private static IFocusableRegistry[] neiCommonResRegistry;

    /**
     * Get ore registry for NEI display with dimension filter.
     * 
     * @param tier  The miner tier
     * @param dimId The dimension ID, or NEI_DIMENSION_COMMON for common-only ores
     * @return Registry containing appropriate ores with correct probability
     *         calculation
     */
    public static IFocusableRegistry getOreRegistryForNEI(int tier, int dimId) {
        if (dimId == NEI_DIMENSION_COMMON) {
            if (neiCommonOreRegistry == null) {
                neiCommonOreRegistry = createCommonOnlyOreRegistry();
            }
            return neiCommonOreRegistry[tier];
        }
        return getOreRegistry(tier, dimId);
    }

    /**
     * Get resource registry for NEI display with dimension filter.
     */
    public static IFocusableRegistry getResRegistryForNEI(int tier, int dimId) {
        if (dimId == NEI_DIMENSION_COMMON) {
            if (neiCommonResRegistry == null) {
                neiCommonResRegistry = createCommonOnlyResRegistry();
            }
            return neiCommonResRegistry[tier];
        }
        return getResRegistry(tier, dimId);
    }

    /**
     * Creates registry containing only common ores (dimensions == null).
     */
    private static IFocusableRegistry[] createCommonOnlyOreRegistry() {
        IFocusableRegistry[] registries = new IFocusableRegistry[MAX_TIER];
        for (int tier = 0; tier < MAX_TIER; tier++) {
            registries[tier] = new FocusableRegistry();
            if (cachedOreList != null) {
                FocusableHandler.loadCommonOnlyIntoRegistry(cachedOreList, registries[tier], tier);
            }
        }
        return registries;
    }

    /**
     * Creates registry containing only common resources (dimensions == null).
     */
    private static IFocusableRegistry[] createCommonOnlyResRegistry() {
        IFocusableRegistry[] registries = new IFocusableRegistry[MAX_TIER];
        for (int tier = 0; tier < MAX_TIER; tier++) {
            registries[tier] = new FocusableRegistry();
            if (cachedResList != null) {
                FocusableHandler.loadCommonOnlyIntoRegistry(cachedResList, registries[tier], tier);
            }
        }
        return registries;
    }

    private static IFocusableRegistry[] createOreRegistryForDim(int dimId) {
        IFocusableRegistry[] registries = new IFocusableRegistry[MAX_TIER];
        for (int tier = 0; tier < MAX_TIER; tier++) {
            registries[tier] = new FocusableRegistry();
            if (cachedOreList != null) {
                FocusableHandler.loadIntoRegistry(cachedOreList, registries[tier], tier, dimId);
            }
        }
        return registries;
    }

    private static IFocusableRegistry[] createResRegistryForDim(int dimId) {
        IFocusableRegistry[] registries = new IFocusableRegistry[MAX_TIER];
        for (int tier = 0; tier < MAX_TIER; tier++) {
            registries[tier] = new FocusableRegistry();
            if (cachedResList != null) {
                FocusableHandler.loadIntoRegistry(cachedResList, registries[tier], tier, dimId);
            }
        }
        return registries;
    }

    private static void loadOreConfig() {
        File file = new File("config/" + LibMisc.MOD_ID + "/multiblock/oreList.json");

        if (file.exists()) {
            cachedOreList = FocusableHandler.loadListFromJson(file);
        } else {
            cachedOreList = getDefaultOreList();
            FocusableHandler.saveRegistryDefaultsToJson(file, cachedOreList);
        }

        // Load into legacy arrays (no dimension filter) for backward compatibility
        for (int tier = 0; tier < MAX_TIER; tier++) {
            FocusableHandler.loadIntoRegistry(cachedOreList, oreRegistry[tier], tier);
        }
    }

    private static void loadResConfig() {
        File file = new File("config/" + LibMisc.MOD_ID + "/multiblock/resourceList.json");

        if (file.exists()) {
            cachedResList = FocusableHandler.loadListFromJson(file);
        } else {
            cachedResList = getDefaultResList();
            FocusableHandler.saveRegistryDefaultsToJson(file, cachedResList);
        }

        // Load into legacy arrays (no dimension filter) for backward compatibility
        for (int tier = 0; tier < MAX_TIER; tier++) {
            FocusableHandler.loadIntoRegistry(cachedResList, resRegistry[tier], tier);
        }
    }

    /**
     * Find the first dimension ID for a given ore item.
     * Returns the first dimension from the item's config, or NEI_DIMENSION_COMMON
     * if none.
     */
    public static int findFirstOreDimension(ItemStack stack) {
        if (cachedOreList == null || stack == null) {
            return NEI_DIMENSION_COMMON;
        }
        for (FocusableHandler.FocusableEntry entry : cachedOreList.getEntries()) {
            // Try all tiers to find a matching entry (since some items are tier-restricted)
            ItemStack entryStack = null;
            for (int tier = 0; tier < MAX_TIER && entryStack == null; tier++) {
                WeightedStackBase wsb = entry.getRegistryEntry(tier);
                if (wsb != null) {
                    entryStack = wsb.getMainStack();
                }
            }
            if (entryStack != null && areStacksSameItem(entryStack, stack)) {
                // Found matching entry, return first dimension
                int[] dims = entry.getDimensions();
                if (dims != null && dims.length > 0) {
                    return dims[0];
                }
                return NEI_DIMENSION_COMMON;
            }
        }
        return NEI_DIMENSION_COMMON;
    }

    /**
     * Check if two stacks are the same item (ignoring stack size and NBT).
     */
    private static boolean areStacksSameItem(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage();
    }

    /**
     * Find the first dimension ID for a given resource item.
     * Returns the first dimension from the item's config, or NEI_DIMENSION_COMMON
     * if none.
     */
    public static int findFirstResDimension(ItemStack stack) {
        if (cachedResList == null || stack == null) {
            return NEI_DIMENSION_COMMON;
        }
        for (FocusableHandler.FocusableEntry entry : cachedResList.getEntries()) {
            // Try all tiers to find a matching entry (since some items are tier-restricted)
            ItemStack entryStack = null;
            for (int tier = 0; tier < MAX_TIER && entryStack == null; tier++) {
                WeightedStackBase wsb = entry.getRegistryEntry(tier);
                if (wsb != null) {
                    entryStack = wsb.getMainStack();
                }
            }
            if (entryStack != null && areStacksSameItem(entryStack, stack)) {
                // Found matching entry, return first dimension
                int[] dims = entry.getDimensions();
                if (dims != null && dims.length > 0) {
                    return dims[0];
                }
                return NEI_DIMENSION_COMMON;
            }
        }
        return NEI_DIMENSION_COMMON;
    }

    private static FocusableHandler.FocusableList getDefaultOreList() {
        FocusableHandler.FocusableList defaults = new FocusableHandler.FocusableList();

        // ========== COMMON (All Tiers) ==========
        defaults.addEntry(new FocusableHandler.FocusableOre("oreCoal", EnumDye.BLACK, 1000));
        defaults.addEntry(new FocusableHandler.FocusableOre("oreIron", EnumDye.WHITE, 750));
        defaults.addEntry(new FocusableHandler.FocusableOre("oreCopper", EnumDye.ORANGE, 584));
        defaults.addEntry(new FocusableHandler.FocusableOre("oreTin", EnumDye.GRAY, 602));
        defaults.addEntry(new FocusableHandler.FocusableOre("oreQuartz", EnumDye.WHITE, 560, -1));
        defaults.addEntry(new FocusableHandler.FocusableOre("glowstone", EnumDye.YELLOW, 234, -1));
        defaults.addEntry(new FocusableHandler.FocusableOre("oreSalt", EnumDye.WHITE, 160));
        defaults.addEntry(new FocusableHandler.FocusableOre("oreGraphite", EnumDye.BLACK, 190));
        defaults.addEntry(new FocusableHandler.FocusableOre("oreNiter", EnumDye.WHITE, 244));
        defaults.addEntry(new FocusableHandler.FocusableOre("oreSulfur", EnumDye.YELLOW, 222));
        defaults.addEntry(new FocusableHandler.FocusableOre("oreQuartzBlack", EnumDye.BLACK, 290));

        // ========== TIER 3+ (Slightly Rare) ==========
        // Tier weights: [0, 0, W, W, W, W]
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreRedstone", EnumDye.RED, new double[] { 0, 0, 515, 515, 515, 515 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreLapis", EnumDye.BLUE, new double[] { 0, 0, 343, 343, 343, 343 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreGold", EnumDye.YELLOW, new double[] { 0, 0, 311, 311, 311, 311 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreSilver", EnumDye.SILVER, new double[] { 0, 0, 381, 381, 381, 381 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreLead", EnumDye.PURPLE, new double[] { 0, 0, 500, 500, 500, 500 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreAluminum", EnumDye.WHITE, new double[] { 0, 0, 422, 422, 422, 422 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreNickel", EnumDye.GRAY, new double[] { 0, 0, 232, 232, 232, 232 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreZinc", EnumDye.PINK, new double[] { 0, 0, 186, 186, 186, 186 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreCertusQuartz",
                EnumDye.LIGHT_BLUE,
                new double[] { 0, 0, 187, 187, 187, 187 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreApatite",
                EnumDye.LIGHT_BLUE,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreOsmium",
                EnumDye.LIGHT_BLUE,
                new double[] { 0, 0, 251, 251, 251, 251 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreYellorite",
                EnumDye.YELLOW,
                new double[] { 0, 0, 156, 156, 156, 156 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreCinnabar", EnumDye.BROWN, new double[] { 0, 0, 190, 190, 190, 190 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreAmber", EnumDye.ORANGE, new double[] { 0, 0, 184, 184, 184, 184 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreBoron", EnumDye.SILVER, new double[] { 0, 0, 199, 199, 199, 199 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreThorium", EnumDye.BLACK, new double[] { 0, 0, 222, 222, 222, 222 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreLithium", EnumDye.WHITE, new double[] { 0, 0, 201, 201, 201, 201 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreMagnesium",
                EnumDye.WHITE,
                new double[] { 0, 0, 233, 233, 233, 233 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreResonating",
                EnumDye.BROWN,
                new double[] { 0, 0, 177, 177, 177, 177 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreSilicon", EnumDye.PURPLE, new double[] { 0, 0, 333, 333, 333, 333 }));

        // Gems (Tier 3+)
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreRuby", EnumDye.RED, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("orePeridot", EnumDye.GREEN, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreTopaz", EnumDye.ORANGE, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreTanzanite",
                EnumDye.PURPLE,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreMalachite", EnumDye.CYAN, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreSapphire", EnumDye.BLUE, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreGarnet", EnumDye.ORANGE, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreHeliodore",
                EnumDye.YELLOW,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreBeryl", EnumDye.GREEN, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreIndicolite",
                EnumDye.GREEN,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreAquamarine",
                EnumDye.BLUE,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreIolite", EnumDye.PURPLE, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreAmethyst",
                EnumDye.PURPLE,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreAgate", EnumDye.PINK, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreMorganite", EnumDye.PINK, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreOnyx", EnumDye.BLACK, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreOpal", EnumDye.WHITE, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreCarnelian", EnumDye.RED, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreSpinel", EnumDye.BROWN, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreCitrine", EnumDye.BROWN, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreJasper", EnumDye.YELLOW, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreGoldenBeryl",
                EnumDye.YELLOW,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreMoldavite",
                EnumDye.GREEN,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreTurquoise", EnumDye.CYAN, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreMoonstone", EnumDye.CYAN, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreBlueTopaz", EnumDye.BLUE, new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreVioletSapphire",
                EnumDye.PURPLE,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreLepidolite",
                EnumDye.PURPLE,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreAmetrine",
                EnumDye.PURPLE,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreBlackDiamond",
                EnumDye.BLACK,
                new double[] { 0, 0, 200, 200, 200, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreAlexandrite",
                EnumDye.WHITE,
                new double[] { 0, 0, 200, 200, 200, 200 }));

        // ========== TIER 5+ (Rare) ==========
        // Tier weights: [0, 0, 0, 0, W, W]
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreDiamond", EnumDye.CYAN, new double[] { 0, 0, 0, 0, 218, 218 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreEmerald", EnumDye.LIME, new double[] { 0, 0, 0, 0, 156, 156 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "orePlatinum",
                EnumDye.LIGHT_BLUE,
                new double[] { 0, 0, 0, 0, 150, 150 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreMithril", EnumDye.LIGHT_BLUE, new double[] { 0, 0, 0, 0, 169, 169 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreUranium", EnumDye.GREEN, new double[] { 0, 0, 0, 0, 140, 140 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreChargedCertusQuartz",
                EnumDye.LIGHT_BLUE,
                new double[] { 0, 0, 0, 0, 109, 109 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreCobalt",
                EnumDye.BLUE,
                new double[] { 0, 0, 0, 0, 163, 163 },
                null,
                -1));
        defaults.addEntry(
            new FocusableHandler.FocusableOre(
                "oreArdite",
                EnumDye.ORANGE,
                new double[] { 0, 0, 0, 0, 159, 159 },
                null,
                -1));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreFirestone", EnumDye.RED, new double[] { 0, 0, 0, 0, 113, 113 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreDraconium", EnumDye.PURPLE, new double[] { 0, 0, 0, 0, 142, 142 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreBrainstone", EnumDye.LIME, new double[] { 0, 0, 0, 0, 223, 223 }));
        defaults.addEntry(
            new FocusableHandler.FocusableBlock(
                "rftools:dimensional_shard_ore",
                0,
                EnumDye.WHITE,
                new double[] { 0, 0, 0, 0, 127, 127 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreTungsten", EnumDye.BLACK, new double[] { 0, 0, 0, 0, 192, 192 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreTitaniumIron", EnumDye.WHITE, new double[] { 0, 0, 0, 0, 223, 223 }));

        // ========== TIER 6 ONLY (Very Rare) ==========
        // Tier weights: [0, 0, 0, 0, 0, W]
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreIridium", EnumDye.WHITE, new double[] { 0, 0, 0, 0, 0, 157 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreChaos", EnumDye.WHITE, new double[] { 0, 0, 0, 0, 0, 200 }));
        defaults.addEntry(
            new FocusableHandler.FocusableOre("oreEnderEssence", EnumDye.GREEN, new double[] { 0, 0, 0, 0, 0, 200 }));

        // Crystal 0
        defaults.addEntry(
            new FocusableHandler.FocusableItem(
                LibResources.PREFIX_MOD + "crystal",
                0,
                EnumDye.CRYSTAL,
                new double[] { 200, 200, 200, 200, 200, 200 }));
        // Crystal 1
        defaults.addEntry(
            new FocusableHandler.FocusableItem(
                LibResources.PREFIX_MOD + "crystal",
                1,
                EnumDye.CRYSTAL,
                new double[] { 150, 150, 150, 150, 150, 150 }));
        // Crystal 2
        defaults.addEntry(
            new FocusableHandler.FocusableItem(
                LibResources.PREFIX_MOD + "crystal",
                2,
                EnumDye.CRYSTAL,
                new double[] { 0, 120, 120, 120, 120, 120 }));
        // Crystal 3
        defaults.addEntry(
            new FocusableHandler.FocusableItem(
                LibResources.PREFIX_MOD + "crystal",
                3,
                EnumDye.CRYSTAL,
                new double[] { 0, 0, 90, 90, 90, 90 }));
        // Crystal 4
        defaults.addEntry(
            new FocusableHandler.FocusableItem(
                LibResources.PREFIX_MOD + "crystal",
                4,
                EnumDye.CRYSTAL,
                new double[] { 0, 0, 0, 60, 60, 60 }));
        // Crystal 5
        defaults.addEntry(
            new FocusableHandler.FocusableItem(
                LibResources.PREFIX_MOD + "crystal",
                5,
                EnumDye.CRYSTAL,
                new double[] { 0, 0, 0, 0, 30, 30 }));

        // Crystal 6
        defaults.addEntry(
            new FocusableHandler.FocusableItem(
                LibResources.PREFIX_MOD + "crystal",
                6,
                EnumDye.CRYSTAL,
                new double[] { 0, 0, 0, 0, 0, 50 }));

        // Crystal 7
        defaults.addEntry(
            new FocusableHandler.FocusableItem(
                LibResources.PREFIX_MOD + "crystal",
                7,
                EnumDye.CRYSTAL,
                new double[] { 3, 3, 3, 3, 3, 3 }));

        return defaults;
    }

    private static FocusableHandler.FocusableList getDefaultResList() {
        FocusableHandler.FocusableList defaults = new FocusableHandler.FocusableList();
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:stone", 0, EnumDye.GRAY, 32));
        defaults.addEntry(new FocusableHandler.FocusableBlock("etfuturum:stone", 1, EnumDye.PINK, 30));
        defaults.addEntry(new FocusableHandler.FocusableBlock("etfuturum:stone", 3, EnumDye.WHITE, 30));
        defaults.addEntry(new FocusableHandler.FocusableBlock("etfuturum:stone", 5, EnumDye.GRAY, 30));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:gravel", 0, EnumDye.SILVER, 30));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:grass", 0, EnumDye.GREEN, 10));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:dirt", 0, EnumDye.BROWN, 20));
        defaults.addEntry(new FocusableHandler.FocusableBlock("etfuturum:coarse_dirt", 0, EnumDye.BROWN, 10));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:sand", 0, EnumDye.YELLOW, 30));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:sand", 1, EnumDye.YELLOW, 24));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:sandstone", 0, EnumDye.YELLOW, 10));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:cobblestone", 0, EnumDye.GRAY, 28));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:mossy_cobblestone", 0, EnumDye.GREEN, 6));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:obsidian", 0, EnumDye.PURPLE, 10));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:clay", 0, EnumDye.SILVER, 12));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:netherrack", 0, EnumDye.RED, 500.0, -1));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:soul_sand", 0, EnumDye.BROWN, 400.0, -1));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:mycelium", 0, EnumDye.PURPLE, 8));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:end_stone", 0, EnumDye.WHITE, 13, 1));
        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:hardened_clay", 0, EnumDye.RED, 12, 0));

        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 0, EnumDye.WHITE, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 1, EnumDye.ORANGE, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 2, EnumDye.MAGENTA, 2, 0));
        defaults.addEntry(
            new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 3, EnumDye.LIGHT_BLUE, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 4, EnumDye.YELLOW, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 5, EnumDye.LIME, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 6, EnumDye.PINK, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 7, EnumDye.GRAY, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 8, EnumDye.SILVER, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 9, EnumDye.CYAN, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 10, EnumDye.PURPLE, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 11, EnumDye.BLUE, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 12, EnumDye.BROWN, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 13, EnumDye.GREEN, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 14, EnumDye.RED, 2, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock("minecraft:stained_hardened_clay", 15, EnumDye.BLACK, 2, 0));

        defaults.addEntry(new FocusableHandler.FocusableBlock("minecraft:sponge", 0, EnumDye.YELLOW, 28, 0));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock(LibResources.PREFIX_MOD + "alabaster", 0, EnumDye.WHITE, 12));
        defaults
            .addEntry(new FocusableHandler.FocusableBlock(LibResources.PREFIX_MOD + "basalt", 0, EnumDye.BLACK, 30));
        defaults.addEntry(
            new FocusableHandler.FocusableBlock(LibResources.PREFIX_MOD + "hardened_stone", 0, EnumDye.GRAY, 20));
        defaults.addEntry(new FocusableHandler.FocusableBlock("chisel:limestone", 0, EnumDye.LIME, 30));
        defaults.addEntry(new FocusableHandler.FocusableBlock("chisel:marble", 0, EnumDye.WHITE, 30));
        defaults.addEntry(new FocusableHandler.FocusableBlock("TConstruct:CraftedSoil", 5, EnumDye.GREEN, 2));
        defaults.addEntry(new FocusableHandler.FocusableBlock("TConstruct:slime.grass", 0, EnumDye.GREEN, 1));
        defaults.addEntry(
            new FocusableHandler.FocusableBlock("appliedenergistics2:tile.BlockSkyStone", 0, EnumDye.BLACK, 2));

        // Mica (Tier-based weights)
        defaults.addEntry(
            new FocusableHandler.FocusableBlock(
                LibResources.PREFIX_MOD + "mica",
                0,
                EnumDye.WHITE,
                new double[] { 2, 4, 6, 8, 12, 16 }));

        return defaults;
    }
}
