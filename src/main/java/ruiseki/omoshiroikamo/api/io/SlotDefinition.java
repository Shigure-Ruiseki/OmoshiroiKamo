package ruiseki.omoshiroikamo.api.io;

public class SlotDefinition {

    private int minUpgradeSlot = -1;
    private int maxUpgradeSlot = -1;

    private int minItemInputSlot = -1;
    private int maxItemInputSlot = -1;
    private int minItemOutputSlot = -1;
    private int maxItemOutputSlot = -1;

    private int minFluidInputSlot = -1;
    private int maxFluidInputSlot = -1;
    private int minFluidOutputSlot = -1;
    private int maxFluidOutputSlot = -1;

    public SlotDefinition() {
    }

    /**
     * Item slots
     */
    public SlotDefinition setItemSlots(int numInputs, int numOutputs) {
        this.minItemInputSlot = 0;
        this.maxItemInputSlot = numInputs > 0 ? numInputs - 1 : -1;
        this.minItemOutputSlot = numOutputs > 0 ? numInputs : -1;
        this.maxItemOutputSlot = numOutputs > 0 ? numInputs + numOutputs - 1 : -1;
        return this;
    }

    /**
     * Fluid slots
     */
    public SlotDefinition setFluidSlots(int numInputs, int numOutputs) {
        this.minFluidInputSlot = 0;
        this.maxFluidInputSlot = numInputs > 0 ? numInputs - 1 : -1;
        this.minFluidOutputSlot = numOutputs > 0 ? numInputs : -1;
        this.maxFluidOutputSlot = numOutputs > 0 ? numInputs + numOutputs - 1 : -1;
        return this;
    }

    /**
     * Upgrade slots
     */
    public SlotDefinition setUpgradeSlots(int numSlots) {
        if (numSlots > 0) {
            int start = 0;
            if (maxItemOutputSlot >= 0) {
                start = maxItemOutputSlot + 1;
            }
            if (maxFluidOutputSlot >= 0) {
                start = Math.max(start, maxFluidOutputSlot + 1);
            }
            this.minUpgradeSlot = start;
            this.maxUpgradeSlot = start + numSlots - 1;
        } else {
            this.minUpgradeSlot = -1;
            this.maxUpgradeSlot = -1;
        }
        return this;
    }

    /**
     * Getters
     */
    public int getMinItemInput() {
        return minItemInputSlot;
    }

    public int getMaxItemInput() {
        return maxItemInputSlot;
    }

    public int getMinItemOutput() {
        return minItemOutputSlot;
    }

    public int getMaxItemOutput() {
        return maxItemOutputSlot;
    }

    public int getMinFluidInput() {
        return minFluidInputSlot;
    }

    public int getMaxFluidInput() {
        return maxFluidInputSlot;
    }

    public int getMinFluidOutput() {
        return minFluidOutputSlot;
    }

    public int getMaxFluidOutput() {
        return maxFluidOutputSlot;
    }

    public int getMinUpgrade() {
        return minUpgradeSlot;
    }

    public int getMaxUpgrade() {
        return maxUpgradeSlot;
    }

    public int getItemInputs() {
        if (minItemInputSlot < 0) {
            return 0;
        }
        return maxItemInputSlot - minItemInputSlot + 1;
    }

    public int getItemOutputs() {
        if (minItemOutputSlot < 0) {
            return 0;
        }
        return maxItemOutputSlot - minItemOutputSlot + 1;
    }

    public int getItemSlots() {
        return getItemInputs() + getItemOutputs();
    }

    public int[] getAllItemSlots() {
        int inputCount = getItemInputs();
        int outputCount = getItemOutputs();
        if (inputCount == 0 && outputCount == 0) {
            return new int[0];
        }

        int[] all = new int[inputCount + outputCount];

        for (int i = 0; i < inputCount; i++) {
            all[i] = minItemInputSlot + i;
        }

        for (int i = 0; i < outputCount; i++) {
            all[inputCount + i] = minItemOutputSlot + i;
        }

        return all;
    }

    public int getFluidInputs() {
        if (minFluidInputSlot < 0) {
            return 0;
        }
        return maxFluidInputSlot - minFluidInputSlot + 1;
    }

    public int getFluidOutputs() {
        if (minFluidOutputSlot < 0) {
            return 0;
        }
        return maxFluidOutputSlot - minFluidOutputSlot + 1;
    }

    public int getFluidSlots() {
        return getFluidInputs() + getFluidOutputs();
    }

    public int getUpgradeSlots() {
        if (minUpgradeSlot < 0) {
            return 0;
        }
        return maxUpgradeSlot - minUpgradeSlot + 1;
    }

    /**
     * Helper
     */
    public boolean isInputSlot(int slot) {
        return slot >= minItemInputSlot && slot <= maxItemInputSlot;
    }

    public boolean isOutputSlot(int slot) {
        return slot >= minItemOutputSlot && slot <= maxItemOutputSlot;
    }

    public boolean isFluidInputSlot(int slot) {
        return slot >= minFluidInputSlot && slot <= maxFluidInputSlot;
    }

    public boolean isFluidOutputSlot(int slot) {
        return slot >= minFluidOutputSlot && slot <= maxFluidOutputSlot;
    }

    public boolean isUpgradeSlot(int slot) {
        return slot >= minUpgradeSlot && slot <= maxUpgradeSlot;
    }
}
