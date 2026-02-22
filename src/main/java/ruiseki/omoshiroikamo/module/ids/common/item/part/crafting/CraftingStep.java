package ruiseki.omoshiroikamo.module.ids.common.item.part.crafting;

public class CraftingStep {

    private final ICraftingPattern pattern;
    private final long times;

    public CraftingStep(ICraftingPattern pattern, long times) {
        this.pattern = pattern;
        this.times = times;
    }

    public ICraftingPattern getPattern() {
        return pattern;
    }

    public long getTimes() {
        return times;
    }
}
