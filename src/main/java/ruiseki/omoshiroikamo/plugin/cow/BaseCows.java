package ruiseki.omoshiroikamo.plugin.cow;

import java.util.List;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;

public class BaseCows extends BaseCowHandler {

    public static CowsRegistryItem waterCow;
    public static CowsRegistryItem lavaCow;

    public BaseCows() {
        super("Base", "Base", "textures/entity/cows/base/");
        this.setNeedsModPresent(false);
        this.setStartID(0);
    }

    @Override
    public List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows) {

        waterCow = addCow(
            allCows,
            "WaterCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("water"), 1000),
            0x000099,
            0x8080ff,
            SpawnType.NORMAL);

        lavaCow = addCow(
            allCows,
            "LavaCow",
            this.nextID(),
            new FluidStack(FluidRegistry.getFluid("lava"), 1000),
            0xcc3300,
            0xffff00,
            SpawnType.HELL);

        return allCows;
    }

    @Override
    public void registerAllParents(List<CowsRegistryItem> allCows) {

    }
}
