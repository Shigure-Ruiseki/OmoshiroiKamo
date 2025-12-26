package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main Deep Mob Learning Settings")
@Config.LangKey(LibResources.CONFIG + "deepMobLearningConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "general.backports.deepMobLearning",
    configSubDirectory = LibMisc.MOD_ID + "/dml", filename = "dml")
public class DMLConfig {

    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean updateMissing;

    @Config.DefaultInt(256)
    public static int lootFabricatorRfCost;

    @Config.DefaultInt(51)
    public static int lootFabricatorPrecessingTime;

    @Config.DefaultInt(2000000)
    public static int simulationChamberEnergyCapacity;

    @Config.DefaultInt(25600)
    public static int simulationChamberEnergyInMax;

    @Config.DefaultInt(301)
    public static int simulationChamberProcessingTime;
}
