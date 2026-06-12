package ruiseki.omoshiroikamo;

public class Reference {

    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = Tags.MOD_NAME;
    public static final String VERSION = Tags.VERSION;
    public static final String DEPENDENCIES = "required-after:gtnhlib@[0.11.9,);"
        + "required-after:structurelib@[1.4.38,);"
        + "required-after:modularui2@[2.3.73-1.7.10,);"
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
    public static final String PROXY_COMMON = Tags.MOD_GROUP + ".CommonProxy";
    public static final String PROXY_CLIENT = Tags.MOD_GROUP + ".ClientProxy";
    public static final String GUI_FACTORY = Tags.MOD_GROUP + ".config.OKGuiConfigFactory";

    public static final String PREFIX_MOD = Reference.MOD_ID + ":";
    public static final String PREFIX_GUI = PREFIX_MOD + "textures/gui/";
    public static final String PREFIX_BLOCK = PREFIX_MOD + "textures/blocks/";
    public static final String PREFIX_ITEM = PREFIX_MOD + "textures/items/";
    public static final String PREFIX_MODEL = PREFIX_MOD + "models/";
    public static final String CONFIG = "config.";
    public static final String TOOLTIP = "tooltip.";
    public static final String ACHIEVEMENT = "achievement." + Reference.MOD_ID + ".";
    public static final String GUI_NEI_BLANK = PREFIX_GUI + "nei/neiBlank.png";
    public static final String GUI_CHICKEN_LAYING = PREFIX_GUI + "nei/chicken/laying.png";
    public static final String GUI_CHICKEN_BREEDING = PREFIX_GUI + "nei/chicken/breeding.png";
    public static final String GUI_CHICKEN_DROPS = PREFIX_GUI + "nei/chicken/drops.png";
    public static final String GUI_CHICKEN_THROWS = PREFIX_GUI + "nei/chicken/throws.png";
    public static final String GUI_SLOT = PREFIX_GUI + "nei/slot.png";
}
