package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

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
    public static CowsRegistryItem quartzCow;
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

        ironCow = addCow(
            allCows,
            "IronCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("iron.molten"), 1000),
            0xb8b8b8,
            0xffffff,
            SpawnType.NORMAL);

        goldCow = addCow(
            allCows,
            "GoldCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("gold.molten"), 1000),
            0xffcc00,
            0xffff66,
            SpawnType.NORMAL);

        copperCow = addCow(
            allCows,
            "CopperCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("copper.molten"), 1000),
            0xcc6600,
            0xff9955,
            SpawnType.NORMAL);

        tinCow = addCow(
            allCows,
            "TinCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("tin.molten"), 1000),
            0xccccff,
            0xffffff,
            SpawnType.NORMAL);

        aluminiumCow = addCow(
            allCows,
            "AluminiumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("aluminum.molten"), 1000),
            0xddeeff,
            0xffffff,
            SpawnType.NORMAL);

        cobaltCow = addCow(
            allCows,
            "CobaltCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("cobalt.molten"), 1000),
            0x0022ff,
            0x6688ff,
            SpawnType.HELL);

        arditeCow = addCow(
            allCows,
            "ArditeCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("ardite.molten"), 1000),
            0xff6600,
            0xffaa55,
            SpawnType.HELL);

        bronzeCow = addCow(
            allCows,
            "BronzeCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("bronze.molten"), 1000),
            0xcc8844,
            0xffcc99,
            SpawnType.NORMAL);

        alubrassCow = addCow(
            allCows,
            "AlubrassCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("aluminumbrass.molten"), 1000),
            0xd4b55c,
            0xffe099,
            SpawnType.NORMAL);

        manyullynCow = addCow(
            allCows,
            "ManyullynCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("manyullyn.molten"), 1000),
            0x550088,
            0xaa66ff,
            SpawnType.HELL);

        obsidianCow = addCow(
            allCows,
            "ObsidianCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("obsidian.molten"), 1000),
            0x1a0f33,
            0x3d2a66,
            SpawnType.NORMAL);

        steelCow = addCow(
            allCows,
            "SteelCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("steel.molten"), 1000),
            0x555555,
            0xaaaaaa,
            SpawnType.NORMAL);

        glassCow = addCow(
            allCows,
            "GlassCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("glass.molten"), 1000),
            0xffffff,
            0xddeeff,
            SpawnType.NORMAL);

        stoneCow = addCow(
            allCows,
            "StoneCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("stone.seared"), 1000),
            0x888888,
            0xbbbbbb,
            SpawnType.NORMAL);

        emeraldCow = addCow(
            allCows,
            "EmeraldCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("emerald.liquid"), 1000),
            0x00cc66,
            0x66ffaa,
            SpawnType.NORMAL);

        quartzCow = addCow(
            allCows,
            "QuartzCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("quartz.molten"), 1000),
            0xfff0e6,
            0xffffff,
            SpawnType.NORMAL);

        nickelCow = addCow(
            allCows,
            "NickelCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("nickel.molten"), 1000),
            0xcccc99,
            0xffffcc,
            SpawnType.NORMAL);

        leadCow = addCow(
            allCows,
            "LeadCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("lead.molten"), 1000),
            0x333366,
            0x666699,
            SpawnType.NORMAL);

        silverCow = addCow(
            allCows,
            "SilverCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("silver.molten"), 1000),
            0xcceeff,
            0xffffff,
            SpawnType.NORMAL);

        shinyCow = addCow(
            allCows,
            "ShinyCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("platinum.molten"), 1000),
            0xe6ffff,
            0xffffff,
            SpawnType.NORMAL);

        invarCow = addCow(
            allCows,
            "InvarCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("invar.molten"), 1000),
            0x99997a,
            0xccccaa,
            SpawnType.NORMAL);

        electrumCow = addCow(
            allCows,
            "ElectrumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("electrum.molten"), 1000),
            0xfff2a1,
            0xffffd6,
            SpawnType.NORMAL);

        lumiumCow = addCow(
            allCows,
            "LumiumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("lumium.molten"), 1000),
            0xffffcc,
            0xffffff,
            SpawnType.NORMAL);

        signalumCow = addCow(
            allCows,
            "SignalumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("signalum.molten"), 1000),
            0xcc3300,
            0xff6644,
            SpawnType.NORMAL);

        mithrilCow = addCow(
            allCows,
            "MithrilCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("mithril.molten"), 1000),
            0x99ccff,
            0xccffff,
            SpawnType.NORMAL);

        enderiumCow = addCow(
            allCows,
            "EnderCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("ender"), 1000),
            0x006666,
            0x33cccc,
            SpawnType.HELL);

        enderiumCow = addCow(
            allCows,
            "EnderiumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("enderium.molten"), 1000),
            0x006666,
            0x33cccc,
            SpawnType.HELL);

        pigironCow = addCow(
            allCows,
            "PigIronCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("pigiron.molten"), 1000),
            0xff99aa,
            0xffccd5,
            SpawnType.NORMAL);

        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
