package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class ThermalChickens extends BaseChickenHandler {

    public static ChickensRegistryItem basalzRodChicken = null;
    public static ChickensRegistryItem blitzRodChicken = null;
    public static ChickensRegistryItem blizzRodChicken = null;
    public static ChickensRegistryItem cinnabarChicken = null;
    public static ChickensRegistryItem enderiumChicken = null;
    public static ChickensRegistryItem lumiumChicken = null;
    public static ChickensRegistryItem mithrilChicken = null;
    public static ChickensRegistryItem signalumChicken = null;

    public ThermalChickens() {
        super("ThermalFoundation", "Thermal Foundation", "textures/entity/chicken/thermal/");
        setStartID(400);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> allChickens) {
        basalzRodChicken = addChicken(
            allChickens,
            "BasalzRodChicken",
            this.nextID(),
            "BasalzRodChicken.png",
            this.getFirstOreDictionary("rodBasalz"),
            0x980000,
            0x6E6664,
            SpawnType.NONE);

        blitzRodChicken = addChicken(
            allChickens,
            "BlitzRodChicken",
            this.nextID(),
            "BlitzRodChicken.png",
            this.getFirstOreDictionary("rodBlitz"),
            0xECE992,
            0x66E5EF,
            SpawnType.NONE);

        blizzRodChicken = addChicken(
            allChickens,
            "BlizzRodChicken",
            this.nextID(),
            "BlizzRodChicken.png",
            this.getFirstOreDictionary("rodBlizz"),
            0x88E0FF,
            0x1D3B95,
            SpawnType.NONE);

        cinnabarChicken = addChicken(
            allChickens,
            "CinnabarChicken",
            this.nextID(),
            "CinnabarChicken.png",
            this.getFirstOreDictionary("crystalCinnabar"),
            0xE49790,
            0x9B3229,
            SpawnType.NONE);

        enderiumChicken = addChicken(
            allChickens,
            "EnderiumChicken",
            this.nextID(),
            "EnderiumChicken.png",
            this.getFirstOreDictionary("nuggetEnderium"),
            0x127575,
            0x0A4849,
            SpawnType.NONE);

        lumiumChicken = addChicken(
            allChickens,
            "LumiumChicken",
            this.nextID(),
            "LumiumChicken.png",
            this.getFirstOreDictionary("ingotLumium"),
            0xEEF4DF,
            0xF4B134,
            SpawnType.NONE);

        mithrilChicken = addChicken(
            allChickens,
            "MithrilChicken",
            this.nextID(),
            "MithrilChicken.png",
            this.getFirstOreDictionary("ingotMithril"),
            0x5A89A8,
            0xA7FFFF,
            SpawnType.NONE);

        signalumChicken = addChicken(
            allChickens,
            "SignalumChicken",
            this.nextID(),
            "SignalumChicken.png",
            this.getFirstOreDictionary("ingotSignalum"),
            0xFFA424,
            0xC63200,
            SpawnType.NONE);

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
