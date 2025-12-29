package ruiseki.omoshiroikamo.core.lib;

import java.util.function.Supplier;

import cpw.mods.fml.common.Loader;

public enum LibMods {

    ActuallyAdditions("ActuallyAdditions"),
    AppliedEnergistics2("appliedenergistics2"),
    Baubles("Baubles"),
    BaublesExpanded("Baubles|Expanded"),
    BigReactors("BigReactors"),
    BogoSorter("bogosorter"),
    Botania("Botania"),
    BuildCraftEnergy("BuildCraft|Energy"),
    CoFHLib("CoFHLib"),
    CoFHCore("CoFHCore"),
    CraftingTweaks("craftingtweaks"),
    CraftTweaker("MineTweaker3"),
    DraconicEvolution("DraconicEvolution"),
    EtFuturum("etfuturum"),
    EnderIO("EnderIO"),
    IC2("IC2"),
    Mekanism("Mekanism"),
    MinefactoryReloaded("MinefactoryReloaded"),
    NotEnoughItems("NotEnoughItems"),
    TConstruct("TConstruct"),
    ThermalFoundation("ThermalFoundation"),
    Waila("Waila"),;

    public final String modid;
    private final Supplier<Boolean> supplier;
    private Boolean loaded;

    LibMods(String modid) {
        this.modid = modid;
        this.supplier = null;
    }

    LibMods(Supplier<Boolean> supplier) {
        this.supplier = supplier;
        this.modid = null;
    }

    public boolean isLoaded() {
        if (loaded == null) {
            if (supplier != null) {
                loaded = supplier.get();
            } else if (modid != null) {
                loaded = Loader.isModLoaded(modid);
            } else {
                loaded = false;
            }
        }
        return loaded;
    }
}
