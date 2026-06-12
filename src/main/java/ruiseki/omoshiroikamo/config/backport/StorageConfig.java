package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.core.lib.Reference;

@Config.Comment("Main Storage Settings")
@Config.LangKey(LibResources.CONFIG + "storageConfig")
@Config(
    modid = Reference.MOD_ID,
    category = "storage",
    configSubDirectory = Reference.MOD_ID + "/storage",
    filename = "storage")
public class StorageConfig {

    @Config.DefaultInt(27)
    @Config.RangeInt(min = 1)
    public static int baseSlots;

    @Config.DefaultInt(54)
    @Config.RangeInt(min = 1)
    public static int ironSlots;

    @Config.DefaultInt(81)
    @Config.RangeInt(min = 1)
    public static int goldSlots;

    @Config.DefaultInt(108)
    @Config.RangeInt(min = 1)
    public static int diamondSlots;

    @Config.DefaultInt(120)
    @Config.RangeInt(min = 1)
    public static int obsidianSlots;

    @Config.DefaultInt(1)
    @Config.RangeInt(min = 1)
    public static int baseUpgradeSlots;

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

    @Config.DefaultInt(33554431)
    @Config.RangeInt(min = 1)
    public static int stackUpgradeTierOmegaMul;

}
