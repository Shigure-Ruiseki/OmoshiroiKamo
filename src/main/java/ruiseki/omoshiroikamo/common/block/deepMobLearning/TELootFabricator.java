package ruiseki.omoshiroikamo.common.block.deepMobLearning;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractStorageTE;

public class TELootFabricator extends AbstractStorageTE {

    public TELootFabricator() {
        super(new SlotDefinition());
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return false;
    }
}
