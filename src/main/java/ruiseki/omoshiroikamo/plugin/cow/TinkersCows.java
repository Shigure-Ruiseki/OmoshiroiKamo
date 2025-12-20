package ruiseki.omoshiroikamo.plugin.cow;

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

        ironCow = addCow("IronCow", 100, 0xb8b8b8, 0xffffff, SpawnType.NORMAL).setFluidString("iron.molten")
            .setLang("en_US", "Iron Cow")
            .setLang("ja_JP", "鉄の牛");
        allCows.add(ironCow);

        goldCow = addCow("GoldCow", 101, 0xffcc00, 0xffff66, SpawnType.NORMAL).setFluidString("gold.molten")
            .setLang("en_US", "Gold Cow")
            .setLang("ja_JP", "金の牛");
        allCows.add(goldCow);

        copperCow = addCow("CopperCow", 102, 0xcc6600, 0xff9955, SpawnType.NORMAL).setFluidString("copper.molten")
            .setLang("en_US", "Copper Cow")
            .setLang("ja_JP", "銅の牛");
        allCows.add(copperCow);

        tinCow = addCow("TinCow", 103, 0xccccff, 0xffffff, SpawnType.NORMAL).setFluidString("tin.molten")
            .setLang("en_US", "Tin Cow")
            .setLang("ja_JP", "錫の牛");
        allCows.add(tinCow);

        aluminiumCow = addCow("AluminiumCow", 104, 0xddeeff, 0xffffff, SpawnType.NORMAL)
            .setFluidString("aluminum.molten")
            .setLang("en_US", "Aluminium Cow")
            .setLang("ja_JP", "アルミニウムの牛");
        allCows.add(aluminiumCow);

        cobaltCow = addCow("CobaltCow", 105, 0x0022ff, 0x6688ff, SpawnType.HELL).setFluidString("cobalt.molten")
            .setLang("en_US", "Cobalt Cow")
            .setLang("ja_JP", "コバルトの牛");
        allCows.add(cobaltCow);

        arditeCow = addCow("ArditeCow", 106, 0xff6600, 0xffaa55, SpawnType.HELL).setFluidString("ardite.molten")
            .setLang("en_US", "Ardite Cow")
            .setLang("ja_JP", "アルダイトの牛");
        allCows.add(arditeCow);

        bronzeCow = addCow("BronzeCow", 107, 0xcc8844, 0xffcc99, SpawnType.NORMAL).setFluidString("bronze.molten")
            .setLang("en_US", "Bronze Cow")
            .setLang("ja_JP", "青銅の牛");
        allCows.add(bronzeCow);

        alubrassCow = addCow("AlubrassCow", 108, 0xd4b55c, 0xffe099, SpawnType.NORMAL)
            .setFluidString("aluminumbrass.molten")
            .setLang("en_US", "Alubrass Cow")
            .setLang("ja_JP", "アルミニウム黄銅の牛");
        allCows.add(alubrassCow);

        manyullynCow = addCow("ManyullynCow", 109, 0x550088, 0xaa66ff, SpawnType.HELL)
            .setFluidString("manyullyn.molten")
            .setLang("en_US", "Manyullyn Cow")
            .setLang("ja_JP", "マニュリンの牛");
        allCows.add(manyullynCow);

        obsidianCow = addCow("ObsidianCow", 110, 0x1a0f33, 0x3d2a66, SpawnType.NORMAL).setFluidString("obsidian.molten")
            .setLang("en_US", "Obsidian Cow")
            .setLang("ja_JP", "黒曜石の牛");
        allCows.add(obsidianCow);

        steelCow = addCow("SteelCow", 111, 0x555555, 0xaaaaaa, SpawnType.NORMAL).setFluidString("steel.molten")
            .setLang("en_US", "Steel Cow")
            .setLang("ja_JP", "鋼の牛");
        allCows.add(steelCow);

        glassCow = addCow("GlassCow", 112, 0xffffff, 0xddeeff, SpawnType.NORMAL).setFluidString("glass.molten")
            .setLang("en_US", "Glass Cow")
            .setLang("ja_JP", "ガラスの牛");
        allCows.add(glassCow);

        stoneCow = addCow("StoneCow", 113, 0x888888, 0xbbbbbb, SpawnType.NORMAL).setFluidString("stone.seared")
            .setLang("en_US", "Stone Cow")
            .setLang("ja_JP", "石の牛");
        allCows.add(stoneCow);

        emeraldCow = addCow("EmeraldCow", 114, 0x00cc66, 0x66ffaa, SpawnType.NORMAL).setFluidString("emerald.liquid")
            .setLang("en_US", "Emerald Cow")
            .setLang("ja_JP", "エメラルドの牛");
        allCows.add(emeraldCow);

        nickelCow = addCow("NickelCow", 115, 0xcccc99, 0xffffcc, SpawnType.NORMAL).setFluidString("nickel.molten")
            .setLang("en_US", "Nickel Cow")
            .setLang("ja_JP", "ニッケルの牛");
        allCows.add(nickelCow);

        leadCow = addCow("LeadCow", 116, 0x333366, 0x666699, SpawnType.NORMAL).setFluidString("lead.molten")
            .setLang("en_US", "Lead Cow")
            .setLang("ja_JP", "鉛の牛");
        allCows.add(leadCow);

        silverCow = addCow("SilverCow", 117, 0xcceeff, 0xffffff, SpawnType.NORMAL).setFluidString("silver.molten")
            .setLang("en_US", "Silver Cow")
            .setLang("ja_JP", "銀の牛");
        allCows.add(silverCow);

        shinyCow = addCow("ShinyCow", 118, 0xe6ffff, 0xffffff, SpawnType.NORMAL).setFluidString("platinum.molten")
            .setLang("en_US", "Shiny Cow")
            .setLang("ja_JP", "白金の牛");
        allCows.add(shinyCow);

        invarCow = addCow("InvarCow", 119, 0x99997a, 0xccccaa, SpawnType.NORMAL).setFluidString("invar.molten")
            .setLang("en_US", "Invar Cow")
            .setLang("ja_JP", "インバーの牛");
        allCows.add(invarCow);

        electrumCow = addCow("ElectrumCow", 120, 0xfff2a1, 0xffffd6, SpawnType.NORMAL).setFluidString("electrum.molten")
            .setLang("en_US", "Electrum Cow")
            .setLang("ja_JP", "エレクトラムの牛");
        allCows.add(electrumCow);

        lumiumCow = addCow("LumiumCow", 121, 0xffffcc, 0xffffff, SpawnType.NORMAL).setFluidString("lumium.molten")
            .setLang("en_US", "Lumium Cow")
            .setLang("ja_JP", "ルミウムの牛");
        allCows.add(lumiumCow);

        signalumCow = addCow("SignalumCow", 122, 0xcc3300, 0xff6644, SpawnType.NORMAL).setFluidString("signalum.molten")
            .setLang("en_US", "Signalum Cow")
            .setLang("ja_JP", "シグナラムの牛");
        allCows.add(signalumCow);

        mithrilCow = addCow("MithrilCow", 123, 0x99ccff, 0xccffff, SpawnType.NORMAL).setFluidString("mithril.molten")
            .setLang("en_US", "Mithril Cow")
            .setLang("ja_JP", "ミスリルの牛");
        allCows.add(mithrilCow);

        enderiumCow = addCow("EnderiumCow", 124, 0x006666, 0x33cccc, SpawnType.HELL).setFluidString("enderium.molten")
            .setLang("en_US", "Enderium Cow")
            .setLang("ja_JP", "エンデリウムの牛");
        allCows.add(enderiumCow);

        pigironCow = addCow("PigIronCow", 125, 0xff99aa, 0xffccd5, SpawnType.NORMAL).setFluidString("pigiron.molten")
            .setLang("en_US", "Pig Iron Cow")
            .setLang("ja_JP", "ピッグアイアンの牛");
        allCows.add(pigironCow);

        return allCows;
    }
}
