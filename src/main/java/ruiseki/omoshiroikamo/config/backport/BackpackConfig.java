package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Backpack Settings")
@Config.LangKey(LibResources.CONFIG + "backpackConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "general.backports.backpack",
    configSubDirectory = LibMisc.MOD_ID + "/backpack", filename = "backpack")
public class BackpackConfig {

    @Config.Comment("Main Magnet Settings")
    public static final MagnetConfig magnetConfig = new MagnetConfig();

    @Config.DefaultInt(27)
    @Config.RangeInt(min = 1)
    public static int leatherBackpackSlots;

    @Config.DefaultInt(54)
    @Config.RangeInt(min = 1)
    public static int ironBackpackSlots;

    @Config.DefaultInt(81)
    @Config.RangeInt(min = 1)
    public static int goldBackpackSlots;

    @Config.DefaultInt(108)
    @Config.RangeInt(min = 1)
    public static int diamondBackpackSlots;

    @Config.DefaultInt(120)
    @Config.RangeInt(min = 1)
    public static int obsidianBackpackSlots;

    @Config.DefaultInt(1)
    @Config.RangeInt(min = 1)
    public static int leatherUpgradeSlots;

    @Config.DefaultInt(2)
    @Config.RangeInt(min = 1)
    public static int ironUpgradeSlots;

    @Config.DefaultInt(3)
    @Config.RangeInt(min = 1)
    public static int goldUpgradeSlots;

    @Config.DefaultInt(5)
    @Config.RangeInt(min = 1)
    public static int diamondUpgradeSlots;

    @Config.DefaultInt(7)
    @Config.RangeInt(min = 1)
    public static int obsidianUpgradeSlots;

    @Config.DefaultInt(2)
    @Config.RangeInt(min = 1)
    public static int stackUpgradeTier1Mul;

    @Config.DefaultInt(4)
    @Config.RangeInt(min = 1)
    public static int stackUpgradeTier2Mul;

    @Config.DefaultInt(8)
    @Config.RangeInt(min = 1)
    public static int stackUpgradeTier3Mul;

    @Config.DefaultInt(16)
    @Config.RangeInt(min = 1)
    public static int stackUpgradeTier4Mul;

    @Config.LangKey(LibResources.CONFIG + "magnetConfig")
    public static class MagnetConfig {

        @Config.DefaultInt(5)
        public int magnetRange;
    }

}
