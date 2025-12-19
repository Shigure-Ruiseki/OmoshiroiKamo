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
            this.nextID(),
            "BasalzRodChicken.png",
            0x980000,
            0x6E6664,
            SpawnType.NONE,
            new String[] { "en_US:Basalz Rod Chicken", "ja_JP:バサルズロッドのニワトリ" }).setLayString("ore:rodBasalz");
        allChickens.add(basalzRodChicken);

        blitzRodChicken = addChicken(
            "BlitzRodChicken",
            this.nextID(),
            "BlitzRodChicken.png",
            0xECE992,
            0x66E5EF,
            SpawnType.NONE,
            new String[] { "en_US:Blitz Rod Chicken", "ja_JP:ブリッツロッドのニワトリ" }).setLayString("ore:rodBlitz");
        allChickens.add(blitzRodChicken);

        blizzRodChicken = addChicken(
            "BlizzRodChicken",
            this.nextID(),
            "BlizzRodChicken.png",
            0x88E0FF,
            0x1D3B95,
            SpawnType.NONE,
            new String[] { "en_US:Blizz Rod Chicken", "ja_JP:ブリズロッドのニワトリ" }).setLayString("ore:rodBlizz");
        allChickens.add(blizzRodChicken);

        cinnabarChicken = addChicken(
            "CinnabarChicken",
            this.nextID(),
            "CinnabarChicken.png",
            0xE49790,
            0x9B3229,
            SpawnType.NONE,
            new String[] { "en_US:Cinnabar Chicken", "ja_JP:辰砂のニワトリ" }).setLayString("ore:crystalCinnabar");
        allChickens.add(cinnabarChicken);

        enderiumChicken = addChicken(
            "EnderiumChicken",
            this.nextID(),
            "EnderiumChicken.png",
            0x127575,
            0x0A4849,
            SpawnType.NONE,
            new String[] { "en_US:Enderium Chicken", "ja_JP:エンダリウムのニワトリ" }).setLayString("ore:nuggetEnderium");
        allChickens.add(enderiumChicken);

        lumiumChicken = addChicken(
            "LumiumChicken",
            this.nextID(),
            "LumiumChicken.png",
            0xEEF4DF,
            0xF4B134,
            SpawnType.NONE,
            new String[] { "en_US:Lumium Chicken", "ja_JP:ルミウムのニワトリ" }).setLayString("ore:ingotLumium");
        allChickens.add(lumiumChicken);

        mithrilChicken = addChicken(
            "MithrilChicken",
            this.nextID(),
            "MithrilChicken.png",
            0x5A89A8,
            0xA7FFFF,
            SpawnType.NONE,
            new String[] { "en_US:Mithril Chicken", "ja_JP:ミスリルのニワトリ" }).setLayString("ore:ingotMithril");
        allChickens.add(mithrilChicken);

        signalumChicken = addChicken(
            "SignalumChicken",
            this.nextID(),
            "SignalumChicken.png",
            0xFFA424,
            0xC63200,
            SpawnType.NONE,
            new String[] { "en_US:Signalum Chicken", "ja_JP:シグナルムのニワトリ" }).setLayString("ore:ingotSignalum");
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
