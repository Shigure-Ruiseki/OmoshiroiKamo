package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.Reference;

@Config.Comment("Main Deep Mob Learning Settings")
@Config.LangKey(Reference.CONFIG + "deepMobLearningConfig")
@Config(modid = Reference.MOD_ID, category = "dml", configSubDirectory = Reference.MOD_ID + "/dml", filename = "dml")
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
