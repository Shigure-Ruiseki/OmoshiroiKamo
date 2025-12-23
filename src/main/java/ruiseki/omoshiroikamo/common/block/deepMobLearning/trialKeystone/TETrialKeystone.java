package ruiseki.omoshiroikamo.common.block.deepMobLearning.trialKeystone;

import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;

public class TETrialKeystone extends AbstractTE {

    public TETrialKeystone() {}

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        return false;
    }
}
