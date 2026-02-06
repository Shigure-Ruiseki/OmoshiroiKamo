package ruiseki.omoshiroikamo.core.lib;

import ruiseki.omoshiroikamo.Tags;
import ruiseki.omoshiroikamo.core.common.util.LangUtils;

public final class LibMisc {

    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;

    public static final String VERSION = Tags.VERSION;
    public static final String DEPENDENCIES = "required-after:gtnhlib@[0.9.5,);"
        + "required-after:structurelib@[1.4.23,);"
        + "required-after:modularui2@[2.3.35-1.7.10,);"
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
        + "after:DraconicEvolution;";

    // Proxy Constants
    public static final String PROXY_COMMON = Tags.MOD_GROUP + ".CommonProxy";
    public static final String PROXY_CLIENT = Tags.MOD_GROUP + ".ClientProxy";
    public static final String GUI_FACTORY = Tags.MOD_GROUP + ".config.OKGuiConfigFactory";
    public static final LangUtils LANG = new LangUtils();
}
