package ruiseki.omoshiroikamo.module.cows.common.registries;

import java.util.ArrayList;
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
    public List<CowsRegistryItem> registerCows() {
        List<CowsRegistryItem> allCows = new ArrayList<>();

        ironCow = addCow("IronCow", 100, 0xb8b8b8, 0xffffff, SpawnType.NORMAL).setFluidString("iron.molten");
        allCows.add(ironCow);

        goldCow = addCow("GoldCow", 101, 0xffcc00, 0xffff66, SpawnType.NORMAL).setFluidString("gold.molten");
        allCows.add(goldCow);

        copperCow = addCow("CopperCow", 102, 0xcc6600, 0xff9955, SpawnType.NORMAL).setFluidString("copper.molten");
        allCows.add(copperCow);

        tinCow = addCow("TinCow", 103, 0xccccff, 0xffffff, SpawnType.NORMAL).setFluidString("tin.molten");
        allCows.add(tinCow);

        aluminiumCow = addCow("AluminiumCow", 104, 0xddeeff, 0xffffff, SpawnType.NORMAL)
            .setFluidString("aluminum.molten");
        allCows.add(aluminiumCow);

        cobaltCow = addCow("CobaltCow", 105, 0x0022ff, 0x6688ff, SpawnType.HELL).setFluidString("cobalt.molten");
        allCows.add(cobaltCow);

        arditeCow = addCow("ArditeCow", 106, 0xff6600, 0xffaa55, SpawnType.HELL).setFluidString("ardite.molten");
        allCows.add(arditeCow);

        bronzeCow = addCow("BronzeCow", 107, 0xcc8844, 0xffcc99, SpawnType.NORMAL).setFluidString("bronze.molten");
        allCows.add(bronzeCow);

        alubrassCow = addCow("AlubrassCow", 108, 0xd4b55c, 0xffe099, SpawnType.NORMAL)
            .setFluidString("aluminumbrass.molten");
        allCows.add(alubrassCow);

        manyullynCow = addCow("ManyullynCow", 109, 0x550088, 0xaa66ff, SpawnType.HELL)
            .setFluidString("manyullyn.molten");
        allCows.add(manyullynCow);

        obsidianCow = addCow("ObsidianCow", 110, 0x1a0f33, 0x3d2a66, SpawnType.NORMAL)
            .setFluidString("obsidian.molten");
        allCows.add(obsidianCow);

        steelCow = addCow("SteelCow", 111, 0x555555, 0xaaaaaa, SpawnType.NORMAL).setFluidString("steel.molten");
        allCows.add(steelCow);

        glassCow = addCow("GlassCow", 112, 0xffffff, 0xddeeff, SpawnType.NORMAL).setFluidString("glass.molten");
        allCows.add(glassCow);

        stoneCow = addCow("StoneCow", 113, 0x888888, 0xbbbbbb, SpawnType.NORMAL).setFluidString("stone.seared");
        allCows.add(stoneCow);

        emeraldCow = addCow("EmeraldCow", 114, 0x00cc66, 0x66ffaa, SpawnType.NORMAL).setFluidString("emerald.liquid");
        allCows.add(emeraldCow);

        nickelCow = addCow("NickelCow", 115, 0xcccc99, 0xffffcc, SpawnType.NORMAL).setFluidString("nickel.molten");
        allCows.add(nickelCow);

        leadCow = addCow("LeadCow", 116, 0x333366, 0x666699, SpawnType.NORMAL).setFluidString("lead.molten");
        allCows.add(leadCow);

        silverCow = addCow("SilverCow", 117, 0xcceeff, 0xffffff, SpawnType.NORMAL).setFluidString("silver.molten");
        allCows.add(silverCow);

        shinyCow = addCow("ShinyCow", 118, 0xe6ffff, 0xffffff, SpawnType.NORMAL).setFluidString("platinum.molten");
        allCows.add(shinyCow);

        invarCow = addCow("InvarCow", 119, 0x99997a, 0xccccaa, SpawnType.NORMAL).setFluidString("invar.molten");
        allCows.add(invarCow);

        electrumCow = addCow("ElectrumCow", 120, 0xfff2a1, 0xffffd6, SpawnType.NORMAL)
            .setFluidString("electrum.molten");
        allCows.add(electrumCow);

        lumiumCow = addCow("LumiumCow", 121, 0xffffcc, 0xffffff, SpawnType.NORMAL).setFluidString("lumium.molten");
        allCows.add(lumiumCow);

        signalumCow = addCow("SignalumCow", 122, 0xcc3300, 0xff6644, SpawnType.NORMAL)
            .setFluidString("signalum.molten");
        allCows.add(signalumCow);

        mithrilCow = addCow("MithrilCow", 123, 0x99ccff, 0xccffff, SpawnType.NORMAL).setFluidString("mithril.molten");
        allCows.add(mithrilCow);

        enderiumCow = addCow("EnderiumCow", 124, 0x006666, 0x33cccc, SpawnType.HELL).setFluidString("enderium.molten");
        allCows.add(enderiumCow);

        pigironCow = addCow("PigIronCow", 125, 0xff99aa, 0xffccd5, SpawnType.NORMAL).setFluidString("pigiron.molten");
        allCows.add(pigironCow);

        return allCows;
    }
}
