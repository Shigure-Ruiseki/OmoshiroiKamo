package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class MekanismCows extends BaseCowHandler {

    public static CowsRegistryItem heavywaterCow;
    public static CowsRegistryItem brineCow;
    public static CowsRegistryItem lithiumCow;

    public MekanismCows() {
        super("Mekanism", "Mekanism", "textures/entity/cows/base/");
        this.setStartID(400);
    }

    @Override
    public List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows) {

        heavywaterCow = addCow(
            allCows,
            "HeavyWaterCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("heavywater"), 1000),
            0x1b2aff,
            0x9dbdff,
            SpawnType.NORMAL);

        brineCow = addCow(
            allCows,
            "BrineCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("brine"), 1000),
            0xe8e084,
            0xffffcc,
            SpawnType.NORMAL);

        lithiumCow = addCow(
            allCows,
            "LithiumCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("lithium"), 1000),
            0x0a2a7a,
            0x4f7bd5,
            SpawnType.NORMAL);
        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
