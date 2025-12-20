package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class ThermalChickens extends BaseChickenHandler {

    public static ChickensRegistryItem basalzRodChicken;
    public static ChickensRegistryItem blitzRodChicken;
    public static ChickensRegistryItem blizzRodChicken;
    public static ChickensRegistryItem cinnabarChicken;
    public static ChickensRegistryItem enderiumChicken;
    public static ChickensRegistryItem lumiumChicken;
    public static ChickensRegistryItem mithrilChicken;
    public static ChickensRegistryItem signalumChicken;

    public ThermalChickens() {
        super("ThermalFoundation", "Thermal Foundation", "textures/entity/chicken/thermal/");
        setStartID(400);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();

        basalzRodChicken = addChicken(
            "BasalzRodChicken",
            nextID(),
            "BasalzRodChicken",
            0x980000,
            0x6E6664,
            SpawnType.NONE).setLayString("ore:rodBasalz")
                .setLang("en_US", "Basalz Rod Chicken")
                .setLang("ja_JP", "バサルズロッドのニワトリ");
        allChickens.add(basalzRodChicken);

        blitzRodChicken = addChicken("BlitzRodChicken", nextID(), "BlitzRodChicken", 0xECE992, 0x66E5EF, SpawnType.NONE)
            .setLayString("ore:rodBlitz")
            .setLang("en_US", "Blitz Rod Chicken")
            .setLang("ja_JP", "ブリッツロッドのニワトリ");
        allChickens.add(blitzRodChicken);

        blizzRodChicken = addChicken("BlizzRodChicken", nextID(), "BlizzRodChicken", 0x88E0FF, 0x1D3B95, SpawnType.NONE)
            .setLayString("ore:rodBlizz")
            .setLang("en_US", "Blizz Rod Chicken")
            .setLang("ja_JP", "ブリズロッドのニワトリ");
        allChickens.add(blizzRodChicken);

        cinnabarChicken = addChicken("CinnabarChicken", nextID(), "CinnabarChicken", 0xE49790, 0x9B3229, SpawnType.NONE)
            .setLayString("ore:crystalCinnabar")
            .setLang("en_US", "Cinnabar Chicken")
            .setLang("ja_JP", "辰砂のニワトリ");
        allChickens.add(cinnabarChicken);

        enderiumChicken = addChicken("EnderiumChicken", nextID(), "EnderiumChicken", 0x127575, 0x0A4849, SpawnType.NONE)
            .setLayString("ore:nuggetEnderium")
            .setLang("en_US", "Enderium Chicken")
            .setLang("ja_JP", "エンダリウムのニワトリ");
        allChickens.add(enderiumChicken);

        lumiumChicken = addChicken("LumiumChicken", nextID(), "LumiumChicken", 0xEEF4DF, 0xF4B134, SpawnType.NONE)
            .setLayString("ore:ingotLumium")
            .setLang("en_US", "Lumium Chicken")
            .setLang("ja_JP", "ルミウムのニワトリ");
        allChickens.add(lumiumChicken);

        mithrilChicken = addChicken("MithrilChicken", nextID(), "MithrilChicken", 0x5A89A8, 0xA7FFFF, SpawnType.NONE)
            .setLayString("ore:ingotMithril")
            .setLang("en_US", "Mithril Chicken")
            .setLang("ja_JP", "ミスリルのニワトリ");
        allChickens.add(mithrilChicken);

        signalumChicken = addChicken("SignalumChicken", nextID(), "SignalumChicken", 0xFFA424, 0xC63200, SpawnType.NONE)
            .setLayString("ore:ingotSignalum")
            .setLang("en_US", "Signalum Chicken")
            .setLang("ja_JP", "シグナルムのニワトリ");
        allChickens.add(signalumChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {

        setParents(basalzRodChicken, MetalsChickens.saltpeterChicken, BaseChickens.blazeChicken);
        setParents(blitzRodChicken, basalzRodChicken, MetalsChickens.sulfurChicken);
        setParents(blizzRodChicken, blitzRodChicken, BaseChickens.snowballChicken);
        setParents(cinnabarChicken, BaseChickens.redstoneChicken, BaseChickens.diamondChicken);
        setParents(signalumChicken, MetalsChickens.copperChicken, MetalsChickens.silverOreChicken);
        setParents(enderiumChicken, MetalsChickens.platinumChicken, BaseChickens.enderChicken);
        setParents(lumiumChicken, MetalsChickens.tinChicken, BaseChickens.glowstoneChicken);
        setParents(mithrilChicken, MetalsChickens.nickelChicken, BaseChickens.goldChicken);
    }
}
