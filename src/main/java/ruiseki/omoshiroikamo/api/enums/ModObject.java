package ruiseki.omoshiroikamo.api.enums;

import ruiseki.omoshiroikamo.core.lib.LibMisc;

public enum ModObject {

    // spotless: off

    // MultiBlock
    BLOCK_CRYSTAL("block_crystal"),
    BASALT("basalt"),
    ALABASTER("alabaster"),
    HARDENED_STONE("hardened_stone"),
    MICA("mica"),
    MACHINE_BASE("machine_base"),
    BASALT_STRUCTURE("basalt_structure"),
    HARDENED_STRUCTURE("hardened_structure"),
    ALABASTER_STRUCTURE("alabaster_structure"),
    LENS("lens"),
    LASER_CORE("laser_core"),
    COLORED_LENS("colored_lens"),
    SOLAR_ARRAY("solar_array"),
    SOLAR_CELL("solar_cell"),
    QUANTUM_ORE_EXTRACTOR("quantum_ore_extractor"),
    QUANTUM_RES_EXTRACTOR("quantum_res_extractor"),
    QUANTUM_BEACON("quantum_beacon"),
    blockModifierNull,
    blockModifierAccuracy,
    blockModifierPiezo,
    blockModifierSpeed,
    blockModifierFlight,
    blockModifierNightVision,
    blockModifierHaste,
    blockModifierStrength,
    blockModifierWaterBreathing,
    blockModifierRegeneration,
    blockModifierSaturation,
    blockModifierResistance,
    blockModifierJumpBoost,
    blockModifierFireResistance,

    CRYSTAL("crystal"),
    STABILIZED_ENDER_PEAR("stabilized_ender_pear"),
    PHOTOVOLTAIC_CELL("photovoltaic_cell"),
    ASSEMBLER("assembler"),

    blockModularItemInput,
    blockModularItemOutput,
    blockModularItemOutputME,
    blockModularEnergyInput,
    blockModularEnergyOutput,
    blockModularFluidInput,
    blockModularFluidOutput,
    blockModularFluidOutputME,
    blockModularManaInput,
    blockModularManaOutput,
    blockModularGasInput,
    blockModularGasOutput,
    blockModularEssentiaInput,
    blockModularEssentiaOutput,
    blockModularEssentiaInputME,
    blockModularVisInput,
    blockModularVisOutput,
    blockVisBridge,

    itemStructureWand,
    itemWrench,

    // IDs
    CABLE("cable"),
    PROGRAMMER("programmer"),
    MENRIL_SAPLING("menril_sapling"),
    MENRIL_LOG("menril_log"),
    MENRIL_LEAVES("menril_leaves"),
    MENRIL_DOOR("menril_door"),
    MENRIL_PLANKS("menril_planks"),

    VARIABLE_CARD("variable_card"),
    MENRIL_BERRIES("menril_berries"),
    ENERGY_INTERFACE("energy_interface"),
    ENERGY_FILTER_INTERFACE("energy_filter_interface"),
    ENERGY_IMPORTER("energy_importer"),
    ENERGY_EXPORTER("energy_exporter"),
    ITEM_INTERFACE("item_interface"),
    ITEM_FILTER_INTERFACE("item_filter_interface"),
    ITEM_IMPORTER("item_importer"),
    ITEM_EXPORTER("item_exporter"),
    REDSTONE_READER("redstone_reader"),
    REDSTONE_WRITER("redstone_writer"),
    BLOCK_READER("block_reader"),
    FLUID_READER("fluid_reader"),
    INVENTORY_READER("inventory_reader"),
    STORAGE_TERMINAL("storage_terminal"),

    // Backpack
    LEATHER_BACKPACK("leather_backpack"),
    IRON_BACKPACK("iron_backpack"),
    GOLD_BACKPACK("gold_backpack"),
    DIAMOND_BACKPACK("diamond_backpack"),
    OBSIDIAN_BACKPACK("obsidian_backpack"),
    SLEEPING_BAG("sleeping_bag"),

    BACKPACK_BASE_UPGRADE("backpack_base_upgrade"),
    BACKPACK_STACK_UPGRADE("backpack_stack_upgrade"),
    BACKPACK_CRAFTING_UPGRADE("backpack_crafting_upgrade"),
    BACKPACK_MAGNET_UPGRADE("backpack_magnet_upgrade"),
    BACKPACK_ADVANCED_MAGNET_UPGRADE("backpack_advanced_magnet_upgrade"),
    BACKPACK_FEEDING_UPGRADE("backpack_feeding_upgrade"),
    BACKPACK_ADVANCED_FEEDING_UPGRADE("backpack_advanced_feeding_upgrade"),
    BACKPACK_PICKUP_UPGRADE("backpack_pickup_upgrade"),
    BACKPACK_ADVANCED_PICKUP_UPGRADE("backpack_advanced_pickup_upgrade"),
    BACKPACK_EVERLASTING_UPGRADE("backpack_everlasting_upgrade"),
    BACKPACK_INCEPTION_UPGRADE("backpack_inception_upgrade"),
    BACKPACK_FILTER_UPGRADE("backpack_filter_upgrade"),
    BACKPACK_ADVANCED_FILTER_UPGRADE("backpack_advanced_filter_upgrade"),
    BACKPACK_VOID_UPGRADE("backpack_void_upgrade"),
    BACKPACK_ADVANCED_VOID_UPGRADE("backpack_advanced_void_upgrade"),

    // Chickens
    ROOST("roost"),
    BREEDER("breeder"),
    ROOST_COLLECTOR("roost_collector"),

    CHICKEN_CATCHER("chicken_catcher"),
    CHICKEN_SPAWN_EGG("chicken_spawn_egg"),
    CHICKEN("chicken"),
    COLORED_EGG("colored_egg"),
    LIQUID_EGG("liquid_egg"),
    EMPTY_EGG("empty_egg"),
    ANALYZER("analyzer"),
    SOLID_XP("solid_xp"),
    CHICKEN_FOOD("chicken_food"),

    // Cows
    STALL("stall"),

    COW_HALTER("cow_halter"),
    COW_SPAWN_EGG("cow_spawn_egg"),

    // DML
    LOOT_FABRICATOR("loot_fabricator"),
    SIMULATION_CHAMBER("simulation_chamber"),
    MACHINE_CASING("machine_casing"),

    CREATIVE_MODEL_LEARNER("creative_model_learner"),
    DEEP_LEARNER("deep_learner"),
    DATA_MODEL("data_model"),
    DATA_MODEL_BLANK("data_model_blank"),
    PRISTINE_MATTER("pristine_matter"),
    LIVING_MATTER("living_matter"),
    POLYMER_CLAY("polymer_clay"),
    SOOT_COVERED_PLATE("soot_covered_plate"),
    SOOT_COVERED_REDSTONE("soot_covered_redstone"),;
    // spotless: on

    public final String name;

    ModObject() {
        this(null);
    }

    ModObject(String customName) {
        if (customName != null) {
            this.name = customName;
            return;
        }

        String raw = name();

        if (raw.startsWith("block")) {
            raw = raw.substring(5);
        } else if (raw.startsWith("item")) {
            raw = raw.substring(4);
        }

        this.name = Character.toLowerCase(raw.charAt(0)) + raw.substring(1);
    }

    public String getRegistryName() {
        return LibMisc.MOD_ID + ":" + name;
    }
}
