package ruiseki.omoshiroikamo.module.ids.common.cableNet.part.crafting;

import java.util.ArrayList;
import java.util.List;

public class CraftingPlan {

    private final List<CraftingStep> steps = new ArrayList<>();

    public void addStep(ICraftingPattern pattern, long times) {
        steps.add(new CraftingStep(pattern, times));
    }

    public List<CraftingStep> getSteps() {
        return steps;
    }

    public boolean isEmpty() {
        return steps.isEmpty();
    }
}
