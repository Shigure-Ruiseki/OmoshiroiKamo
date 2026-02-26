package ruiseki.omoshiroikamo.api.condition;

import com.google.gson.JsonObject;

/**
 * Interface for conditions that must be met for a process (e.g., breeding,
 * recipe) to execute.
 */
public interface ICondition {

    /**
     * Checks if the condition is met in the given context.
     * 
     * @param context The context providing world and position information.
     * @return true if met, false otherwise.
     */
    boolean isMet(ConditionContext context);

    /**
     * Returns a localized description of the condition for tooltips and GUIs.
     * 
     * @return Localized description.
     */
    String getDescription();

    /**
     * Serializes this condition to a JsonObject.
     * 
     * @param json The target JSON object.
     */
    void write(JsonObject json);
}
