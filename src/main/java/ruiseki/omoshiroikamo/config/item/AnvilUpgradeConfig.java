package ruiseki.omoshiroikamo.config.item;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main anvil upgrade settings")
@Config.LangKey(LibResources.CONFIG + "anvilUpgradeConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.items.anvil_upgrade", configSubDirectory = LibMisc.MOD_ID)
public class AnvilUpgradeConfig {

    @Config.DefaultInt(100000)
    public static int energyTier1Capacity;

    @Config.DefaultInt(250000)
    public static int energyTier2Capacity;

    @Config.DefaultInt(500000)
    public static int energyTier3Capacity;

    @Config.DefaultInt(1000000)
    public static int energyTier4Capacity;

    @Config.DefaultInt(2500000)
    public static int energyTier5Capacity;

    @Config.DefaultInt(10)
    public static int energyTier1Cost;

    @Config.DefaultInt(10)
    public static int energyTier2Cost;

    @Config.DefaultInt(15)
    public static int energyTier3Cost;

    @Config.DefaultInt(20)
    public static int energyTier4Cost;

    @Config.DefaultInt(25)
    public static int energyTier5Cost;

}
