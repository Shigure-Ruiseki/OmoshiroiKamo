package ruiseki.omoshiroikamo.common.init;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.omoshiroikamo.common.achievement.AchievementEntry;
import ruiseki.omoshiroikamo.common.achievement.AchievementTrigger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public enum ModAchievements {

    CRAFT_ASSEMBLER("craft_assembler", 0, 0, ModItems.ASSEMBLER.newItemStack(), null),

    ASSEMBLE_SOLAR_ARRAY_T1("assemble_solar_array_t1", 1, 2, ModBlocks.SOLAR_ARRAY.newItemStack(1, 0), CRAFT_ASSEMBLER),
    ASSEMBLE_SOLAR_ARRAY_T4("assemble_solar_array_t4", 3, 2, ModBlocks.SOLAR_ARRAY.newItemStack(1, 3),
        ASSEMBLE_SOLAR_ARRAY_T1, true),

    ASSEMBLE_VOID_ORE_MINER_T1("assemble_void_ore_miner_t1", -1, 2, ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0),
        CRAFT_ASSEMBLER),
    ASSEMBLE_VOID_ORE_MINER_T4("assemble_void_ore_miner_t4", -3, 2, ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 3),
        ASSEMBLE_VOID_ORE_MINER_T1, true),

    ASSEMBLE_VOID_RES_MINER_T1("assemble_void_res_miner_t1", -1, 4, ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0),
        CRAFT_ASSEMBLER),
    ASSEMBLE_VOID_RES_MINER_T4("assemble_void_res_miner_t4", -3, 4, ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 3),
        ASSEMBLE_VOID_RES_MINER_T1, true),

    ASSEMBLE_NANO_BOT_BEACON_T1("assemble_nano_bot_beacon_t1", -1, 6, ModBlocks.QUANTUM_BEACON.newItemStack(1, 0),
        CRAFT_ASSEMBLER),
    ASSEMBLE_NANO_BOT_BEACON_T4("assemble_nano_bot_beacon_t4", -3, 6, ModBlocks.QUANTUM_BEACON.newItemStack(1, 3),
        ASSEMBLE_NANO_BOT_BEACON_T1, true),

    CRAFT_MODIFIER_CORE("craft_modifier_core", 7, 0, ModBlocks.MODIFIER_NULL.newItemStack(), null),
    CRAFT_MODIFIER_SPEED("craft_modifier_speed", 6, 2, ModBlocks.MODIFIER_SPEED.newItemStack(), CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_ACCURACY("craft_modifier_accuracy", 6, 4, ModBlocks.MODIFIER_ACCURACY.newItemStack(),
        CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_FLIGHT("craft_modifier_flight", 6, 6, ModBlocks.MODIFIER_FLIGHT.newItemStack(), CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_RESISTANCE("craft_modifier_resistance", 6, 8, ModBlocks.MODIFIER_RESISTANCE.newItemStack(),
        CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_HASTE("craft_modifier_haste", 6, 10, ModBlocks.MODIFIER_HASTE.newItemStack(), CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_NIGHT_VISION("craft_modifier_night_vision", 6, 12, ModBlocks.MODIFIER_NIGHT_VISION.newItemStack(),
        CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_REGEN("craft_modifier_regen", 6, 14, ModBlocks.MODIFIER_REGENERATION.newItemStack(),
        CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_PIEZO("craft_modifier_piezo", 8, 2, ModBlocks.MODIFIER_PIEZO.newItemStack(), CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_JUMP_BOOST("craft_modifier_jump_boost", 8, 4, ModBlocks.MODIFIER_JUMP_BOOST.newItemStack(),
        CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_FIRE_RES("craft_modifier_fire_res", 8, 8, ModBlocks.MODIFIER_FIRE_RESISTANCE.newItemStack(),
        CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_STRENGTH("craft_modifier_strength", 8, 10, ModBlocks.MODIFIER_STRENGTH.newItemStack(),
        CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_WATER_BREATHING("craft_modifier_water_breathing", 8, 12,
        ModBlocks.MODIFIER_WATER_BREATHING.newItemStack(), CRAFT_MODIFIER_CORE),
    CRAFT_MODIFIER_SATURATION("craft_modifier_saturation", 8, 14, ModBlocks.MODIFIER_SATURATION.newItemStack(),
        CRAFT_MODIFIER_CORE),

    CRAFT_COLORED_LENS("craft_colored_lens", 10, 3, ModBlocks.LASER_LENS.newItemStack(), null);

    public static final ModAchievements[] VALUES = values();

    private final String name;
    private final int x, y;
    private final ItemStack icon;
    private final ModAchievements parent;
    private final boolean special;
    private Achievement achievement;

    ModAchievements(String name, int x, int y, ItemStack icon, ModAchievements parent) {
        this(name, x, y, icon, parent, false);
    }

    ModAchievements(String name, int x, int y, ItemStack icon, ModAchievements parent, boolean special) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.icon = icon;
        this.parent = parent;
        this.special = special;
    }

    public Achievement get() {
        return achievement;
    }

    public void create() {
        Achievement parentAch = parent != null ? parent.get() : null;
        this.achievement = new AchievementEntry(name, x, y, icon, parentAch);
        if (special) {
            achievement.setSpecial();
        }
    }

    public static void preInit() {
        for (ModAchievements entry : values()) {
            entry.create();
        }

        AchievementPage page = new AchievementPage(
            LibMisc.MOD_NAME,
            Arrays.stream(values())
                .map(ModAchievements::get)
                .toArray(Achievement[]::new));
        AchievementPage.registerAchievementPage(page);

        FMLCommonHandler.instance()
            .bus()
            .register(new AchievementTrigger());
    }
}
