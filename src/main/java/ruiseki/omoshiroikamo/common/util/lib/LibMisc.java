package ruiseki.omoshiroikamo.common.util.lib;

import net.minecraft.launchwrapper.Launch;

import ruiseki.omoshiroikamo.Tags;
import ruiseki.omoshiroikamo.common.util.LangUtils;

public final class LibMisc {

    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;

    public static final String VERSION = Tags.VERSION;
    public static final String DEPENDENCIES = "required-after:gtnhlib@[0.8.21,);"
        + "required-after:structurelib@[1.4.23,);"
        + "required-after:modularui2@[2.3.18-1.7.10,);"
        + "after:Baubles|Expanded;"
        + "after:Baubles;"
        + "after:NotEnoughItems;"
        + "after:Waila;"
        + "after:Botania;"
        + "after:TConstruct;"
        + "after:EnderIO;"
        + "after:ThermalFoundation;"
        + "after:MinefactoryReloaded;"
        + "after:Mekanism;"
        + "after:BigReactors;"
        + "after:ActuallyAdditions;"
        + "after:DraconicEvolution;"
        + "after:arsmagica2;"
        + "after:evilcraft;"
        + "after:extrautils2;";

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
