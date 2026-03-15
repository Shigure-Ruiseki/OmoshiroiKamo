package ruiseki.omoshiroikamo.core.lib;

import java.util.function.Supplier;

import cpw.mods.fml.common.Loader;

public enum LibMods {

    ActuallyAdditions("ActuallyAdditions"),
    AppliedEnergistics2("appliedenergistics2"),
    AE2FluidCrafting("ae2fc"),
    ArsMagica("arsmagica2"),
    BloodMagic("AWWayofTime"),
    Baubles("Baubles"),
    BaublesExpanded("Baubles|Expanded"),
    BlockRenderer6343("blockrenderer6343"),
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
    GalacticraftCore("GalacticraftCore"),
    GalacticraftMars("GalacticraftMars"),
    HardcoreEnderExpansion("HardcoreEnderExpansion"),
    IC2("IC2"),
    JAOPCA("jaopca"),
    Mekanism("Mekanism"),
    MinefactoryReloaded("MineFactoryReloaded"),
    MorePlanets("MorePlanet"),
    Natura("Natura"),
    Netherlicious("netherlicious"),
    NotEnoughItems("NotEnoughItems"),
    NovaCraft("nova_craft"),
    Thaumcraft("Thaumcraft"),
    ThaumcraftNEIPlugin("thaumcraftneiplugin"),
    ThaumicEnergistics("thaumicenergistics"),
    TConstruct("TConstruct"),
    ThermalFoundation("ThermalFoundation"),
    ThermalExpansion("ThermalExpansion"),
    TwilightForest("TwilightForest"),
    VillageNames("VillageNames"),
    Waila("Waila"),
    Witchery("witchery"),;

    public final String modid;
    private final Supplier<Boolean> supplier;
    private Boolean loaded;

    LibMods(String modid) {
        this.modid = modid;
        this.supplier = null;
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
