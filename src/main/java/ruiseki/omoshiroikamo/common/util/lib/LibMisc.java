package ruiseki.omoshiroikamo.common.util.lib;

import net.minecraft.launchwrapper.Launch;

import ruiseki.omoshiroikamo.Tags;
import ruiseki.omoshiroikamo.common.util.LangUtils;

public final class LibMisc {

    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String VERSION = Tags.VERSION;
    public static final String DEPENDENCIES = "required-after:Baubles|Expanded;" + "required-after:endercore;"
        + "required-after:structurelib;"
        + "required-after:modularui2;"
        + "required-after:neid;"
        + "after:NotEnoughItems;"
        + "after:Waila;"
        + "after:TConstruct;";

    // Proxy Constants
    public static final String PROXY_COMMON = Tags.MOD_GROUP + ".CommonProxy";
    public static final String PROXY_CLIENT = Tags.MOD_GROUP + ".ClientProxy";
    public static final String GUI_FACTORY = Tags.MOD_GROUP + ".config.OKGuiConfigFactory";
    public static final LangUtils LANG = new LangUtils();

    public static final boolean SNAPSHOT_BUILD = Boolean.parseBoolean(Tags.SNAPSHOT_BUILD);
    public static final boolean DEV_ENVIRONMENT = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    public static final String VERSION_URL = System.getProperty(
        MOD_ID + ".versionUrl",
        "https://raw.githubusercontent.com/Shigure-Ruiseki/OmoshiroiKamo/master/updatejson/update.json");

}
