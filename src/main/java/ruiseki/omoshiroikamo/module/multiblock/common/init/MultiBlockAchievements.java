package ruiseki.omoshiroikamo.module.multiblock.common.init;

import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.enableMultiBlock;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.omoshiroikamo.core.common.achievement.AchievementEntry;
import ruiseki.omoshiroikamo.core.common.achievement.AchievementTrigger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public enum MultiBlockAchievements {

    CRAFT_ASSEMBLER("craft_assembler", 0, 0, MultiBlockItems.ASSEMBLER.newItemStack(), null, false, enableMultiBlock),

    ASSEMBLE_SOLAR_ARRAY_T1("assemble_solar_array_t1", 1, 2, MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 0),
        CRAFT_ASSEMBLER, false, enableMultiBlock),
    ASSEMBLE_SOLAR_ARRAY_T4("assemble_solar_array_t4", 3, 2, MultiBlockBlocks.SOLAR_ARRAY.newItemStack(1, 3),
        ASSEMBLE_SOLAR_ARRAY_T1, true, enableMultiBlock),

    ASSEMBLE_VOID_ORE_MINER_T1("assemble_void_ore_miner_t1", -1, 2,
        MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0), CRAFT_ASSEMBLER, false, enableMultiBlock),
    ASSEMBLE_VOID_ORE_MINER_T4("assemble_void_ore_miner_t4", -3, 2,
        MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 3), ASSEMBLE_VOID_ORE_MINER_T1, true, enableMultiBlock),

    ASSEMBLE_VOID_RES_MINER_T1("assemble_void_res_miner_t1", -1, 4,
        MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 0), CRAFT_ASSEMBLER, false, enableMultiBlock),
    ASSEMBLE_VOID_RES_MINER_T4("assemble_void_res_miner_t4", -3, 4,
        MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, 3), ASSEMBLE_VOID_RES_MINER_T1, true, enableMultiBlock),

    ASSEMBLE_NANO_BOT_BEACON_T1("assemble_nano_bot_beacon_t1", -1, 6,
        MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 0), CRAFT_ASSEMBLER, false, enableMultiBlock),
    ASSEMBLE_NANO_BOT_BEACON_T4("assemble_nano_bot_beacon_t4", -3, 6,
        MultiBlockBlocks.QUANTUM_BEACON.newItemStack(1, 3), ASSEMBLE_NANO_BOT_BEACON_T1, true, enableMultiBlock),

    CRAFT_MODIFIER_CORE("craft_modifier_core", 7, 0, MultiBlockBlocks.MODIFIER_NULL.newItemStack(), null, false,
            enableMultiBlock),
    CRAFT_MODIFIER_SPEED("craft_modifier_speed", 6, 2, MultiBlockBlocks.MODIFIER_SPEED.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_ACCURACY("craft_modifier_accuracy", 6, 4, MultiBlockBlocks.MODIFIER_ACCURACY.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_FLIGHT("craft_modifier_flight", 6, 6, MultiBlockBlocks.MODIFIER_FLIGHT.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_RESISTANCE("craft_modifier_resistance", 6, 8, MultiBlockBlocks.MODIFIER_RESISTANCE.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_HASTE("craft_modifier_haste", 6, 10, MultiBlockBlocks.MODIFIER_HASTE.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_NIGHT_VISION("craft_modifier_night_vision", 6, 12,
        MultiBlockBlocks.MODIFIER_NIGHT_VISION.newItemStack(), CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_REGEN("craft_modifier_regen", 6, 14, MultiBlockBlocks.MODIFIER_REGENERATION.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_PIEZO("craft_modifier_piezo", 8, 2, MultiBlockBlocks.MODIFIER_PIEZO.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_JUMP_BOOST("craft_modifier_jump_boost", 8, 4, MultiBlockBlocks.MODIFIER_JUMP_BOOST.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_FIRE_RES("craft_modifier_fire_res", 8, 8, MultiBlockBlocks.MODIFIER_FIRE_RESISTANCE.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_STRENGTH("craft_modifier_strength", 8, 10, MultiBlockBlocks.MODIFIER_STRENGTH.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_WATER_BREATHING("craft_modifier_water_breathing", 8, 12,
        MultiBlockBlocks.MODIFIER_WATER_BREATHING.newItemStack(), CRAFT_MODIFIER_CORE, false, enableMultiBlock),
    CRAFT_MODIFIER_SATURATION("craft_modifier_saturation", 8, 14, MultiBlockBlocks.MODIFIER_SATURATION.newItemStack(),
        CRAFT_MODIFIER_CORE, false, enableMultiBlock),

    CRAFT_COLORED_LENS("craft_colored_lens", 10, 3, MultiBlockBlocks.COLORED_LENS.newItemStack(), null, false,
            enableMultiBlock);

    public static final MultiBlockAchievements[] VALUES = values();

    private final String name;
    private final int x, y;
    private final ItemStack icon;
    private final MultiBlockAchievements parent;
    private final boolean special;
    private Achievement achievement;
    private final boolean enabled;

    MultiBlockAchievements(String name, int x, int y, ItemStack icon, MultiBlockAchievements parent, boolean special,
        boolean enabled) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.icon = icon;
        this.parent = parent;
        this.special = special;
        this.enabled = enabled;
    }

    public Achievement get() {
        return achievement;
    }

    public void create() {
        if (!enabled) {
            return;
        }
        Achievement parentAch = parent != null ? parent.get() : null;
        this.achievement = new AchievementEntry(name, x, y, icon, parentAch);
        if (special) {
            achievement.setSpecial();
        }
    }

    public static void preInit() {
        for (MultiBlockAchievements entry : values()) {
            entry.create();
        }

        AchievementPage page = new AchievementPage(
            LibMisc.MOD_NAME,
            Arrays.stream(values())
                .map(MultiBlockAchievements::get)
                .toArray(Achievement[]::new));
        AchievementPage.registerAchievementPage(page);

        FMLCommonHandler.instance()
            .bus()
            .register(new AchievementTrigger());
    }
}
