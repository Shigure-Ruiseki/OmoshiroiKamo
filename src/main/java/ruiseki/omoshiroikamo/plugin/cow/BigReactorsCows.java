package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class BigReactorsCows extends BaseCowHandler {

    public static CowsRegistryItem yelloriumCow;
    public static CowsRegistryItem cyaniteCow;
    public static CowsRegistryItem steamCow;

    public BigReactorsCows() {
        super("BigReactors", "Big Reactors", "textures/entity/cows/base/");
        this.setStartID(500);
    }

    @Override
    public List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows) {

        yelloriumCow = addCow(
            allCows,
            "YelloriumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("yellorium"), 1000),
            0xE5FF00,
            0xA6A600,
            SpawnType.NORMAL);

        cyaniteCow = addCow(
            allCows,
            "CyaniteCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("cyanite"), 1000),
            0x66CCFF,
            0x3399CC,
            SpawnType.NORMAL);

        steamCow = addCow(
            allCows,
            "SteamCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("steam"), 1000),
            0xCCCCCC,
            0xFFFFFF,
            SpawnType.NORMAL);
        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
