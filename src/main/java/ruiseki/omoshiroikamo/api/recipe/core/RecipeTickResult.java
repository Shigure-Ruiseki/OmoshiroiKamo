package ruiseki.omoshiroikamo.api.recipe.core;

/**
 * Result of a single recipe tick execution.
 */
public enum RecipeTickResult {
    IDLE,
    CONTINUE,
    NO_ENERGY,
    READY_OUTPUT,
    WAITING_OUTPUT,
    NO_INPUT,
    NO_MATCHING_RECIPE,
    OUTPUT_FULL,
    PAUSED,
    NO_MANA,
    BLOCK_MISSING,
    BLOCK_OUTPUT_FULL
}
