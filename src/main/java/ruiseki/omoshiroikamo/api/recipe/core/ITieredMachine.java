package ruiseki.omoshiroikamo.api.recipe.core;

/**
 * Interface for machines that support dynamic component-based tiers.
 */
public interface ITieredMachine {

    /**
     * Get the current tier of a specific component.
     * 
     * @param componentName Name of the component (e.g., "glass", "casing")
     * @return The current tier (minimum 0)
     */
    int getComponentTier(String componentName);

    /**
     * Get the speed multiplier of the machine.
     */
    double getSpeedMultiplier();

    /**
     * Get the energy multiplier of the machine.
     */
    double getEnergyMultiplier();
}
