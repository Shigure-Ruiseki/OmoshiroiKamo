package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class TinkersCows extends BaseCowHandler {

    public static CowsRegistryItem ironCow;
    public static CowsRegistryItem goldCow;
    public static CowsRegistryItem copperCow;
    public static CowsRegistryItem tinCow;
    public static CowsRegistryItem aluminiumCow;
    public static CowsRegistryItem cobaltCow;
    public static CowsRegistryItem arditeCow;
    public static CowsRegistryItem bronzeCow;
    public static CowsRegistryItem alubrassCow;
    public static CowsRegistryItem manyullynCow;
    public static CowsRegistryItem obsidianCow;
    public static CowsRegistryItem steelCow;
    public static CowsRegistryItem glassCow;
    public static CowsRegistryItem stoneCow;
    public static CowsRegistryItem emeraldCow;
    public static CowsRegistryItem nickelCow;
    public static CowsRegistryItem leadCow;
    public static CowsRegistryItem silverCow;
    public static CowsRegistryItem shinyCow;
    public static CowsRegistryItem invarCow;
    public static CowsRegistryItem electrumCow;
    public static CowsRegistryItem lumiumCow;
    public static CowsRegistryItem signalumCow;
    public static CowsRegistryItem mithrilCow;
    public static CowsRegistryItem enderiumCow;
    public static CowsRegistryItem pigironCow;

    public TinkersCows() {
        super("TConstruct", "Tinkers Construct", "textures/entity/cows/base/");
        this.setStartID(100);
    }

    @Override
    public List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows) {

        ironCow = tryAddCow(allCows, "IronCow", 100, "iron.molten", 0xb8b8b8, 0xffffff, SpawnType.NORMAL);
        goldCow = tryAddCow(allCows, "GoldCow", 101, "gold.molten", 0xffcc00, 0xffff66, SpawnType.NORMAL);
        copperCow = tryAddCow(allCows, "CopperCow", 102, "copper.molten", 0xcc6600, 0xff9955, SpawnType.NORMAL);
        tinCow = tryAddCow(allCows, "TinCow", 103, "tin.molten", 0xccccff, 0xffffff, SpawnType.NORMAL);
        aluminiumCow = tryAddCow(allCows, "AluminiumCow", 104, "aluminum.molten", 0xddeeff, 0xffffff, SpawnType.NORMAL);
        cobaltCow = tryAddCow(allCows, "CobaltCow", 105, "cobalt.molten", 0x0022ff, 0x6688ff, SpawnType.HELL);
        arditeCow = tryAddCow(allCows, "ArditeCow", 106, "ardite.molten", 0xff6600, 0xffaa55, SpawnType.HELL);
        bronzeCow = tryAddCow(allCows, "BronzeCow", 107, "bronze.molten", 0xcc8844, 0xffcc99, SpawnType.NORMAL);
        alubrassCow = tryAddCow(
            allCows,
            "AlubrassCow",
            108,
            "aluminumbrass.molten",
            0xd4b55c,
            0xffe099,
            SpawnType.NORMAL);
        manyullynCow = tryAddCow(allCows, "ManyullynCow", 109, "manyullyn.molten", 0x550088, 0xaa66ff, SpawnType.HELL);
        obsidianCow = tryAddCow(allCows, "ObsidianCow", 110, "obsidian.molten", 0x1a0f33, 0x3d2a66, SpawnType.NORMAL);
        steelCow = tryAddCow(allCows, "SteelCow", 111, "steel.molten", 0x555555, 0xaaaaaa, SpawnType.NORMAL);
        glassCow = tryAddCow(allCows, "GlassCow", 112, "glass.molten", 0xffffff, 0xddeeff, SpawnType.NORMAL);
        stoneCow = tryAddCow(allCows, "StoneCow", 113, "stone.seared", 0x888888, 0xbbbbbb, SpawnType.NORMAL);
        emeraldCow = tryAddCow(allCows, "EmeraldCow", 114, "emerald.liquid", 0x00cc66, 0x66ffaa, SpawnType.NORMAL);
        nickelCow = tryAddCow(allCows, "NickelCow", 115, "nickel.molten", 0xcccc99, 0xffffcc, SpawnType.NORMAL);
        leadCow = tryAddCow(allCows, "LeadCow", 116, "lead.molten", 0x333366, 0x666699, SpawnType.NORMAL);
        silverCow = tryAddCow(allCows, "SilverCow", 117, "silver.molten", 0xcceeff, 0xffffff, SpawnType.NORMAL);
        shinyCow = tryAddCow(allCows, "ShinyCow", 118, "platinum.molten", 0xe6ffff, 0xffffff, SpawnType.NORMAL);
        invarCow = tryAddCow(allCows, "InvarCow", 119, "invar.molten", 0x99997a, 0xccccaa, SpawnType.NORMAL);
        electrumCow = tryAddCow(allCows, "ElectrumCow", 120, "electrum.molten", 0xfff2a1, 0xffffd6, SpawnType.NORMAL);
        lumiumCow = tryAddCow(allCows, "LumiumCow", 121, "lumium.molten", 0xffffcc, 0xffffff, SpawnType.NORMAL);
        signalumCow = tryAddCow(allCows, "SignalumCow", 122, "signalum.molten", 0xcc3300, 0xff6644, SpawnType.NORMAL);
        mithrilCow = tryAddCow(allCows, "MithrilCow", 123, "mithril.molten", 0x99ccff, 0xccffff, SpawnType.NORMAL);
        enderiumCow = tryAddCow(allCows, "EnderiumCow", 124, "enderium.molten", 0x006666, 0x33cccc, SpawnType.HELL);
        pigironCow = tryAddCow(allCows, "PigIronCow", 125, "pigiron.molten", 0xff99aa, 0xffccd5, SpawnType.NORMAL);

        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
