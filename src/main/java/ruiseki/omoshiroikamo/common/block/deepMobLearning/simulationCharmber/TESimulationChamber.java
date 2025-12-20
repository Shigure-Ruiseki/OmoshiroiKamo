package ruiseki.omoshiroikamo.common.block.deepMobLearning.simulationCharmber;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;

public class TESimulationChamber extends AbstractTE {

    private final ItemStackHandler inputDataModel = new ItemStackHandler() {

        @Override
        protected void onContentsChanged(int slot) {
            // onDataModelChanged();
        }
    };

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        return false;
    }
}
