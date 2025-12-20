package ruiseki.omoshiroikamo.common.block.deepMobLearning.lootFabricator;

import ruiseki.omoshiroikamo.api.crafting.CraftingState;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMachine;

public class TELootFabricator extends AbstractMachine {

    public TELootFabricator() {
        super(100, 100);
    }

    @Override
    protected int getCraftingDuration() {
        return 0;
    }

    @Override
    protected void finishCrafting() {

    }

    @Override
    public int getCraftingEnergyCost() {
        return 0;
    }

    @Override
    protected CraftingState updateCraftingState() {
        return null;
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
