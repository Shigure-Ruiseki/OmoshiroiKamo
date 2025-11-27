package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@Config.Comment("Main Backpack Settings")
@Config.LangKey(LibResources.CONFIG + "backpackConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.backports.backpack", configSubDirectory = LibMisc.MOD_ID)
public class BackpackConfig {

    @Config.Comment("Main FeedingC Settings")
    public static final FeedingConfig feedingConfig = new FeedingConfig();

    @Config.Comment("Main Magnet Settings")
    public static final MagnetConfig magnetConfig = new MagnetConfig();

    @Config.DefaultInt(27)
    public static int leatherBackpackSlots;

    @Config.DefaultInt(54)
    public static int ironBackpackSlots;

    @Config.DefaultInt(81)
    public static int goldBackpackSlots;

    @Config.DefaultInt(108)
    public static int diamondBackpackSlots;

    @Config.DefaultInt(120)
    public static int obsidianBackpackSlots;

    @Config.DefaultInt(1)
    public static int leatherUpgradeSlots;

    @Config.DefaultInt(2)
    public static int ironUpgradeSlots;

    @Config.DefaultInt(3)
    public static int goldUpgradeSlots;

    @Config.DefaultInt(5)
    public static int diamondUpgradeSlots;

    @Config.DefaultInt(7)
    public static int obsidianUpgradeSlots;

    @Config.DefaultInt(2)
    public static int stackUpgradeTier1Mul;

    @Config.DefaultInt(4)
    public static int stackUpgradeTier2Mul;

    @Config.DefaultInt(8)
    public static int stackUpgradeTier3Mul;

    @Config.DefaultInt(16)
    public static int stackUpgradeTier4Mul;

    @Config.LangKey(LibResources.CONFIG + "feedingConfig")
    public static class FeedingConfig {

        @Config.DefaultBoolean(true)
        public boolean feedingAllowInMainInventory;

        @Config.DefaultBoolean(true)
        public boolean feedingAllowInBaublesSlot;
    }

    @Config.LangKey(LibResources.CONFIG + "magnetConfig")
    public static class MagnetConfig {

        @Config.DefaultInt(5)
        public int magnetRange;

        @Config.DefaultInt(20)
        public int magnetMaxItems;

        @Config.DefaultBoolean(true)
        public boolean magnetAllowInMainInventory;

        @Config.DefaultBoolean(true)
        public boolean magnetAllowInBaublesSlot;
    }

}
