package ruiseki.omoshiroikamo.api.recipe.core;

/**
 * Interface for accessing machine state in expressions.
 * Allows the API layer to access energy, progress, etc., without depending on
 * the implementation.
 */
public interface IMachineState {

    /**
     * Get the current energy stored.
     */
    long getStoredEnergy();

    /**
     * Get the maximum energy capacity.
     */
    long getEnergyCapacity();

    /**
     * Get the energy consumption or production per tick.
     */
    int getEnergyPerTick();

    /**
     * Get the current crafting progress (0.0 to 1.0).
     */
    double getProgressPercent();

    /**
     * Get the raw progress value (usually ticks or work amount).
     */
    default long getProgress() {
        return 0;
    }

    /**
     * Whether the machine is currently running a recipe.
     */
    boolean isRunning();

    /**
     * Whether the machine is waiting for output space.
     */
    boolean isWaitingForOutput();

    /**
     * Get the machine tier.
     */
    int getTier();

    /**
     * Get the total time this machine has been placed in the world (in ticks).
     */
    long getTimePlaced();

    /**
     * Get the total duration the machine has been actively processing recipes (in
     * ticks).
     */
    long getTimeContinuous();

    /**
     * Get the total number of recipes processed by this machine.
     */
    int getRecipeProcessedCount();

    /**
     * Get the number of unique types of recipes processed by this machine.
     */
    int getRecipeProcessedTypesCount();

    /**
     * Get the total fluid stored in the machine (any type).
     */
    long getStoredFluid();

    /**
     * Get the total fluid capacity of the machine.
     */
    long getFluidCapacity();

    /**
     * Get the amount of a specific fluid stored in the machine.
     * 
     * @param name Fluid name
     */
    long getStoredFluid(String name);

    /**
     * Get the current total mana stored in the machine.
     */
    long getStoredMana();

    /**
     * Get the total mana capacity of the machine.
     */
    long getManaCapacity();

    /**
     * Get the amount of a specific gas stored in the machine.
     * 
     * @param name Gas name
     */
    long getStoredGas(String name);

    /**
     * Get the total gas stored in the machine (any type).
     */
    long getTotalStoredGas();

    /**
     * Get the total gas capacity of the machine.
     */
    long getGasCapacity();

    /**
     * Get the amount of a specific essentia aspect stored in the machine.
     * 
     * @param aspect Aspect name
     */
    long getStoredEssentia(String aspect);

    /**
     * Get the total essentia capacity of the machine.
     */
    long getEssentiaCapacity();

    /**
     * Get the amount of a specific vis aspect stored in the machine.
     * 
     * @param aspect Aspect name
     */
    long getStoredVis(String aspect);

    /**
     * Get the total vis capacity of the machine.
     */
    long getVisCapacity();

    /**
     * Get the current batch size.
     */
    int getBatchSize();

    /**
     * Get the current speed multiplier.
     */
    double getSpeedMultiplier();

    /**
     * Get the current energy multiplier.
     */
    double getEnergyMultiplier();

    /**
     * Get the world tick when the current recipe started.
     */
    default long getRecipeStartTick() {
        return 0;
    }
}
