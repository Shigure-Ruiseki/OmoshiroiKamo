package ruiseki.omoshiroikamo.common.recipe;

import static ruiseki.omoshiroikamo.api.enums.EnumDye.DYE_ORE_NAMES;
import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.BACKPACK_SLOTS;
import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.UPGRADE_SLOTS;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;
import ruiseki.omoshiroikamo.config.backport.BackpackConfig;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

public class BlockRecipes {

    public static void init() {
        if (BackportConfigs.useBackpack) {
            // Leather Backpack
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModBlocks.BACKPACK_BASE.newItemStack(),
                    "SLS",
                    "SCS",
                    "LLL",
                    'S',
                    new ItemStack(Items.string, 1, 1),
                    'L',
                    "itemLeather",
                    'C',
                    new ItemStack(Blocks.chest, 1, 1)));

            // Iron Backpack
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModBlocks.BACKPACK_IRON.newItemStack(),
                    "CCC",
                    "CBC",
                    "CCC",
                    'C',
                    "ingotIron",
                    'B',
                    ModBlocks.BACKPACK_BASE.get()).withInt(UPGRADE_SLOTS, BackpackConfig.ironUpgradeSlots)
                        .withInt(BACKPACK_SLOTS, BackpackConfig.ironBackpackSlots)
                        .allowNBTFrom(ModBlocks.BACKPACK_BASE.newItemStack())
                        .allowAllTags());

            // Gold Backpack
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModBlocks.BACKPACK_GOLD.newItemStack(),
                    "CCC",
                    "CBC",
                    "CCC",
                    'C',
                    "ingotGold",
                    'B',
                    ModBlocks.BACKPACK_IRON.get()).withInt(UPGRADE_SLOTS, BackpackConfig.goldUpgradeSlots)
                        .withInt(BACKPACK_SLOTS, BackpackConfig.goldBackpackSlots)
                        .allowNBTFrom(ModBlocks.BACKPACK_IRON.newItemStack())
                        .allowAllTags());

            // Diamond Backpack
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModBlocks.BACKPACK_DIAMOND.newItemStack(),
                    "CCC",
                    "CBC",
                    "CCC",
                    'C',
                    "gemDiamond",
                    'B',
                    ModBlocks.BACKPACK_GOLD.get()).withInt(UPGRADE_SLOTS, BackpackConfig.diamondUpgradeSlots)
                        .withInt(BACKPACK_SLOTS, BackpackConfig.diamondBackpackSlots)
                        .allowNBTFrom(ModBlocks.BACKPACK_GOLD.newItemStack())
                        .allowAllTags());

            // Obsidian Backpack
            GameRegistry.addRecipe(
                new NBTShapedOreRecipe(
                    ModBlocks.BACKPACK_OBSIDIAN.newItemStack(),
                    "CSC",
                    "SBS",
                    "CSC",
                    'S',
                    "itemNetherStar",
                    'C',
                    "blockObsidian",
                    'B',
                    ModBlocks.BACKPACK_DIAMOND.get()).withInt(UPGRADE_SLOTS, BackpackConfig.obsidianUpgradeSlots)
                        .withInt(BACKPACK_SLOTS, BackpackConfig.obsidianBackpackSlots)
                        .allowNBTFrom(ModBlocks.BACKPACK_DIAMOND.newItemStack())
                        .allowAllTags());

            BackpackDyeRecipes recipes = new BackpackDyeRecipes();
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    String accentOre = EnumDye.DYE_ORE_NAMES[i];
                    String mainOre = EnumDye.DYE_ORE_NAMES[j];
                    int accentColor = EnumDye.fromIndex(i)
                        .getColor();
                    int mainColor = EnumDye.fromIndex(j)
                        .getColor();

                    recipes.registerDyeRecipes(
                        ModBlocks.BACKPACK_BASE.newItemStack(),
                        accentOre,
                        mainOre,
                        accentColor,
                        mainColor);
                    recipes.registerDyeRecipes(
                        ModBlocks.BACKPACK_IRON.newItemStack(),
                        accentOre,
                        mainOre,
                        accentColor,
                        mainColor);
                    recipes.registerDyeRecipes(
                        ModBlocks.BACKPACK_GOLD.newItemStack(),
                        accentOre,
                        mainOre,
                        accentColor,
                        mainColor);
                    recipes.registerDyeRecipes(
                        ModBlocks.BACKPACK_DIAMOND.newItemStack(),
                        accentOre,
                        mainOre,
                        accentColor,
                        mainColor);
                    recipes.registerDyeRecipes(
                        ModBlocks.BACKPACK_OBSIDIAN.newItemStack(),
                        accentOre,
                        mainOre,
                        accentColor,
                        mainColor);
                }
            }
        }

        if (BackportConfigs.useEnvironmentalTech) {

            // Hardened Stone
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.BLOCK_HARDENED_STONE.get(),
                    "SCS",
                    "CSC",
                    "SCS",
                    'S',
                    "stone",
                    'C',
                    "cobblestone"));

            // Hardened Stone
            GameRegistry.addRecipe(new ShapelessOreRecipe(ModBlocks.BLOCK_HARDENED_STONE.get(), "stoneHardened"));

            // Basalt
            GameRegistry.addRecipe(new ShapelessOreRecipe(ModBlocks.BLOCK_BASALT.get(), "stoneBasalt"));

            // Alabaster
            GameRegistry.addRecipe(new ShapelessOreRecipe(ModBlocks.BLOCK_ALABASTER.get(), "stoneAlabaster"));

            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 0),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 0)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 1),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 1)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 2),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 2)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.BASALT_STRUCTURE.newItemStack(1, 3),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 3)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 0),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 4)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 1),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 5)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 2),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 6)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.HARDENED_STRUCTURE.newItemStack(1, 3),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 7)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 0),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 8)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 1),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 9)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 2),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 10)));
            GameRegistry.addRecipe(
                new ShapelessOreRecipe(
                    ModBlocks.ALABASTER_STRUCTURE.newItemStack(1, 3),
                    ModBlocks.STRUCTURE_FRAME.newItemStack(1, 11)));

            // Solar Cell
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_CELL.get(),
                    "GGG",
                    "CCC",
                    "RIR",
                    'G',
                    "blockGlass",
                    'I',
                    "ingotIron",
                    'R',
                    "dustRedstone",
                    'C',
                    ModItems.PHOTOVOLTAIC_CELL.get()));

            // Solar Array Tier 1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 0),
                    "GLG",
                    "LCL",
                    "GLG",
                    'G',
                    "blockGold",
                    'L',
                    "blockLapis",
                    'C',
                    ModBlocks.SOLAR_CELL.get()));

            // Solar Array Tier 2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 1),
                    "GAG",
                    "LCL",
                    "GAG",
                    'G',
                    "blockDiamond",
                    'L',
                    "blockLapis",
                    'A',
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 0),
                    'C',
                    ModBlocks.SOLAR_CELL.get()));

            // Solar Array Tier 3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 2),
                    "GAG",
                    "LCL",
                    "GAG",
                    'G',
                    ModItems.STABILIZED_ENDER_PEAR.get(),
                    'L',
                    ModBlocks.BLOCK_MICA.get(),
                    'A',
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 1),
                    'C',
                    ModBlocks.SOLAR_CELL.get()));

            // Solar Array Tier 4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 3),
                    "GAG",
                    "LCL",
                    "GAG",
                    'G',
                    "itemNetherStar",
                    'L',
                    ModBlocks.BLOCK_MICA.get(),
                    'A',
                    ModBlocks.SOLAR_ARRAY.newItemStack(1, 2),
                    'C',
                    ModBlocks.SOLAR_CELL.get()));

            // Quantum Ore Tier 1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0),
                    "GQG",
                    "GLG",
                    "ICD",
                    'G',
                    "blockGold",
                    'L',
                    ModBlocks.LENS.get(),
                    'Q',
                    "oreQuartz",
                    'I',
                    "oreDiamond",
                    'D',
                    "oreQuartz",
                    'C',
                    ModBlocks.LASER_CORE.get()));

            // Quantum Ore Tier 2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 1),
                    "GQG",
                    "GLG",
                    "QCQ",
                    'G',
                    "blockDiamond",
                    'L',
                    ModBlocks.LENS.get(),
                    'Q',
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0),
                    'C',
                    ModBlocks.LASER_CORE.get()));

            // Quantum Ore Tier 3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 2),
                    "EQE",
                    "ELE",
                    "MQM",
                    'E',
                    ModItems.STABILIZED_ENDER_PEAR.get(),
                    'L',
                    ModBlocks.LENS.get(),
                    'Q',
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 1),
                    'M',
                    ModBlocks.BLOCK_MICA.get()));

            // Quantum Ore Tier 4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 3),
                    "EQE",
                    "ELE",
                    "MQM",
                    'E',
                    "itemNetherStar",
                    'L',
                    ModBlocks.LENS.get(),
                    'Q',
                    ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 2),
                    'M',
                    ModBlocks.BLOCK_MICA.get()));

            // Quantum Res Tier 1
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0),
                    "GQG",
                    "GLG",
                    "ICD",
                    'G',
                    "blockGold",
                    'L',
                    ModBlocks.LENS.get(),
                    'Q',
                    new ItemStack(Blocks.end_stone, 1, 0),
                    'I',
                    "stoneMossy",
                    'D',
                    "netherrack",
                    'C',
                    ModBlocks.LASER_CORE.get()));

            // Quantum Res Tier 2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 1),
                    "GQG",
                    "GLG",
                    "QCQ",
                    'G',
                    "blockDiamond",
                    'L',
                    ModBlocks.LENS.get(),
                    'Q',
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0),
                    'C',
                    ModBlocks.LASER_CORE.get()));

            // Quantum Res Tier 3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 2),
                    "EQE",
                    "ELE",
                    "MQM",
                    'E',
                    ModItems.STABILIZED_ENDER_PEAR.get(),
                    'L',
                    ModBlocks.LENS.get(),
                    'Q',
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 1),
                    'M',
                    ModBlocks.BLOCK_MICA.get()));

            // Quantum Res Tier 4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 3),
                    "EQE",
                    "ELE",
                    "MQM",
                    'E',
                    "itemNetherStar",
                    'L',
                    ModBlocks.LENS.get(),
                    'Q',
                    ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 2),
                    'M',
                    ModBlocks.BLOCK_MICA.get()));

            // Quantum Beacon Tier 1
            if (!LibMods.EtFuturum.isLoaded()) {
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModBlocks.QUANTUM_BEACON.newItemStack(1, 0),
                        "GPG",
                        "GNG",
                        "BCB",
                        'P',
                        Items.potionitem,
                        'G',
                        "blockGold",
                        'N',
                        ModBlocks.MODIFIER_NULL.get(),
                        'C',
                        Blocks.beacon,
                        'B',
                        Items.brewing_stand));
            }

            // Quantum Beacon Tier 2
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 1),
                    "GCG",
                    "GNG",
                    "BCB",
                    'G',
                    "blockDiamond",
                    'N',
                    ModBlocks.MODIFIER_NULL.get(),
                    'C',
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 0),
                    'B',
                    Items.brewing_stand));

            // Quantum Beacon Tier 3
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 2),
                    "GCG",
                    "GNG",
                    "BCB",
                    'G',
                    ModItems.STABILIZED_ENDER_PEAR.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get(),
                    'C',
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 1),
                    'B',
                    ModBlocks.BLOCK_MICA.get()));

            // Quantum Beacon Tier 4
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 3),
                    "GCG",
                    "GNG",
                    "BCB",
                    'G',
                    "itemNetherStar",
                    'N',
                    ModBlocks.MODIFIER_NULL.get(),
                    'C',
                    ModBlocks.QUANTUM_BEACON.newItemStack(1, 2),
                    'B',
                    ModBlocks.BLOCK_MICA.get()));

            // Clear Lens
            GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.LENS.get(), "G G", "GGG", "G G", 'G', "blockGlass"));

            // Color Lens
            for (int i = 0; i < DYE_ORE_NAMES.length; i++) {
                GameRegistry.addRecipe(
                    new ShapelessOreRecipe(
                        ModBlocks.COLORED_LENS.newItemStack(1, i),
                        ModBlocks.LENS.get(),
                        DYE_ORE_NAMES[i]));
                GameRegistry
                    .addRecipe(new ShapelessOreRecipe(ModBlocks.LENS.get(), ModBlocks.COLORED_LENS.newItemStack(1, i)));
            }

            // Laser Core
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.LASER_CORE.get(),
                    "GRG",
                    "I I",
                    "GRG",
                    'G',
                    "blockGlass",
                    'R',
                    "dustRedstone",
                    'I',
                    "ingotIron"));

            // Null Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_NULL.get(),
                    "SGS",
                    "GIG",
                    "SGS",
                    'S',
                    "stone",
                    'G',
                    "blockGlass",
                    'I',
                    "blockIron"));

            // Piezo Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_PIEZO.get(),
                    "RMR",
                    "MNM",
                    "IMI",
                    'R',
                    "blockRedstone",
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get(),
                    'I',
                    "blockIron"));

            // Speed Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_SPEED.get(),
                    "GRG",
                    "MNM",
                    "GRG",
                    'R',
                    "blockRedstone",
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get(),
                    'G',
                    "blockGold"));

            // Accuracy Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_ACCURACY.get(),
                    "RMR",
                    "MNM",
                    "MDM",
                    'R',
                    "blockRedstone",
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get(),
                    'D',
                    "blockDiamond"));

            // Flight Modifier
            if (!LibMods.EtFuturum.isLoaded()) {
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModBlocks.MODIFIER_FLIGHT.get(),
                        "PFP",
                        "MNM",
                        "LFL",
                        'P',
                        Blocks.sticky_piston,
                        'M',
                        ModBlocks.BLOCK_MICA.get(),
                        'F',
                        ModBlocks.STRUCTURE_FRAME.newItemStack(1, 3),
                        'L',
                        "itemLeather",
                        'N',
                        ModBlocks.MODIFIER_NULL.get()));
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModBlocks.MODIFIER_FLIGHT.get(),
                        "PFP",
                        "MNM",
                        "LFL",
                        'P',
                        Blocks.sticky_piston,
                        'M',
                        ModBlocks.BLOCK_MICA.get(),
                        'F',
                        ModBlocks.STRUCTURE_FRAME.newItemStack(1, 7),
                        'L',
                        "itemLeather",
                        'N',
                        ModBlocks.MODIFIER_NULL.get()));
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModBlocks.MODIFIER_FLIGHT.get(),
                        "PFP",
                        "MNM",
                        "LFL",
                        'P',
                        Blocks.sticky_piston,
                        'M',
                        ModBlocks.BLOCK_MICA.get(),
                        'F',
                        ModBlocks.STRUCTURE_FRAME.newItemStack(1, 11),
                        'L',
                        "itemLeather",
                        'N',
                        ModBlocks.MODIFIER_NULL.get()));
            }

            // Night Vision Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_NIGHT_VISION.get(),
                    "MPM",
                    "PNP",
                    "MPM",
                    'P',
                    new ItemStack(Items.potionitem, 1, 8198),
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get()));

            // Strength Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_STRENGTH.get(),
                    "MPM",
                    "PNP",
                    "MPM",
                    'P',
                    new ItemStack(Items.potionitem, 1, 8201),
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get()));

            // Water Breathing Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_WATER_BREATHING.get(),
                    "MPM",
                    "PNP",
                    "MPM",
                    'P',
                    new ItemStack(Items.potionitem, 1, 8205),
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get()));

            // Regeneration Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_REGENERATION.get(),
                    "MPM",
                    "PNP",
                    "MPM",
                    'P',
                    new ItemStack(Items.potionitem, 1, 8193),
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get()));

            // Fire Resistance Modifier
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_FIRE_RESISTANCE.get(),
                    "MPM",
                    "PNP",
                    "MPM",
                    'P',
                    new ItemStack(Items.potionitem, 1, 8195),
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get()));

            // Jump Boost Modifier
            if (!LibMods.EtFuturum.isLoaded()) {
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModBlocks.MODIFIER_JUMP_BOOST.get(),
                        "MRM",
                        "PNP",
                        "MRM",
                        'P',
                        "slimeball",
                        'R',
                        Blocks.piston,
                        'M',
                        ModBlocks.BLOCK_MICA.get(),
                        'N',
                        ModBlocks.MODIFIER_NULL.get()));
            }

            // Resistance Modifier
            ItemStack prot4Book = new ItemStack(Items.enchanted_book);
            prot4Book.addEnchantment(Enchantment.protection, 4);
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_FIRE_RESISTANCE.get(),
                    "MPM",
                    "ONO",
                    "MPM",
                    'O',
                    "blockObsidian",
                    'P',
                    prot4Book,
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get()));

            // Haste Modifier
            ItemStack eff5Book = new ItemStack(Items.enchanted_book);
            eff5Book.addEnchantment(Enchantment.efficiency, 5);
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MODIFIER_HASTE.get(),
                    "MPM",
                    "RNR",
                    "MGM",
                    'R',
                    "blockRedstone",
                    'G',
                    "ingotGold",
                    'P',
                    eff5Book,
                    'M',
                    ModBlocks.BLOCK_MICA.get(),
                    'N',
                    ModBlocks.MODIFIER_NULL.get()));

            // Saturation Modifier
            if (!LibMods.EtFuturum.isLoaded()) {
                GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                        ModBlocks.MODIFIER_SATURATION.get(),
                        "MAM",
                        "CNC",
                        "MAM",
                        'A',
                        Items.golden_apple,
                        'C',
                        Items.golden_carrot,
                        'M',
                        ModBlocks.BLOCK_MICA.get(),
                        'N',
                        ModBlocks.MODIFIER_NULL.get()));
            }

            // Machine Base Basalt
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MACHINE_BASE.newItemStack(1, 0),
                    "RBR",
                    "BQB",
                    "NBN",
                    'R',
                    "dustRedstone",
                    'B',
                    "barsIron",
                    'N',
                    "nuggetGold",
                    'Q',
                    "stoneBasalt"));

            // Machine Base Hardened Stone
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MACHINE_BASE.newItemStack(1, 1),
                    "RBR",
                    "BQB",
                    "NBN",
                    'R',
                    "dustRedstone",
                    'B',
                    "barsIron",
                    'N',
                    "nuggetGold",
                    'Q',
                    "stoneHardened"));

            // Machine Base Alabaster
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.MACHINE_BASE.newItemStack(1, 2),
                    "RBR",
                    "BQB",
                    "NBN",
                    'R',
                    "dustRedstone",
                    'B',
                    "barsIron",
                    'N',
                    "nuggetGold",
                    'Q',
                    "stoneAlabaster"));
        }

        if (BackportConfigs.useChicken) {

            // Roost
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.ROOST.get(),
                    "WWW",
                    "W W",
                    "HHH",
                    'W',
                    "plankWood",
                    'H',
                    Blocks.hay_block));

            // Breeder
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.BREEDER.get(),
                    "WWW",
                    "WSW",
                    "HHH",
                    'W',
                    "plankWood",
                    'S',
                    Items.wheat_seeds,
                    'H',
                    Blocks.hay_block));

            // Roost Collector
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.ROOST_COLLECTOR.get(),
                    "WCW",
                    "WHW",
                    "WOW",
                    'W',
                    "plankWood",
                    'C',
                    Items.egg,
                    'O',
                    "chestWood",
                    'H',
                    "blockHopper"));
        }

        if (BackportConfigs.useCow) {

            // Stall
            GameRegistry.addRecipe(
                new ShapedOreRecipe(
                    ModBlocks.STALL.get(),
                    "B B",
                    "BHB",
                    "GGG",
                    'B',
                    Blocks.iron_bars,
                    'H',
                    Blocks.hay_block,
                    'G',
                    new ItemStack(Blocks.stained_hardened_clay, 1, 7)));

        }
    }
}
