package ruiseki.omoshiroikamo.api.recipe.expression;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IMachineState;

/**
 * Expression that evaluates to a property of the machine (e.g., energy,
 * progress).
 */
public class MachinePropertyExpression implements IExpression {

    private final String property;

    public MachinePropertyExpression(String property) {
        this.property = property;
    }

    @Override
    public double evaluate(ConditionContext context) {
        if (context == null) return 0;
        IRecipeContext recipeContext = context.getRecipeContext();
        if (recipeContext == null) return 0;

        IMachineState state = recipeContext.getMachineState();
        if (state == null) return 0;

        switch (property.toLowerCase()) {
            case "energy":
            case "energy_stored":
            case "total_energy":
            case "energy_total":
                return state.getStoredEnergy();
            case "energy_max":
            case "energy_capacity":
            case "total_energy_max":
            case "total_energy_capacity":
                return state.getEnergyCapacity();
            case "energy_f":
            case "energy_free":
                return state.getEnergyCapacity() - state.getStoredEnergy();
            case "energy_p":
            case "energy_percent":
                long maxE = state.getEnergyCapacity();
                return maxE > 0 ? (double) state.getStoredEnergy() / maxE : 0;
            case "energy_per_tick":
                return state.getEnergyPerTick();
            case "progress":
            case "progress_percent":
                return state.getProgressPercent();
            case "is_running":
                return state.isRunning() ? 1.0 : 0.0;
            case "is_waiting":
                return state.isWaitingForOutput() ? 1.0 : 0.0;
            case "tier":
                return state.getTier();
            case "timeplaced":
                return state.getTimePlaced();
            case "timecontinue":
                return state.getTimeContinuous();
            case "recipe_count":
            case "count_recipe":
            case "recipeprocessed":
                return state.getRecipeProcessedCount();
            case "recipe_types_count":
            case "count_recipe_types":
            case "recipeprocessedtype":
                return state.getRecipeProcessedTypesCount();
            case "fluid":
            case "fluid_stored":
            case "total_fluid":
            case "fluid_total":
                return state.getStoredFluid();
            case "fluid_max":
            case "fluid_capacity":
            case "total_fluid_max":
            case "total_fluid_capacity":
                return state.getFluidCapacity();
            case "fluid_f":
            case "fluid_free":
                return state.getFluidCapacity() - state.getStoredFluid();
            case "fluid_p":
            case "fluid_percent":
                long maxF = state.getFluidCapacity();
                return maxF > 0 ? (double) state.getStoredFluid() / maxF : 0;
            case "mana":
            case "mana_stored":
            case "total_mana":
            case "mana_total":
                return state.getStoredMana();
            case "mana_max":
            case "mana_capacity":
            case "total_mana_max":
            case "total_mana_capacity":
                return state.getManaCapacity();
            case "mana_f":
            case "mana_free":
                return state.getManaCapacity() - state.getStoredMana();
            case "mana_p":
            case "mana_percent":
                long maxM = state.getManaCapacity();
                return maxM > 0 ? (double) state.getStoredMana() / maxM : 0;
            case "gas_total":
            case "gas":
                return state.getTotalStoredGas();
            case "gas_max":
            case "gas_capacity":
                return state.getGasCapacity();
            case "gas_f":
            case "gas_free":
                return state.getGasCapacity() - state.getTotalStoredGas();
            case "gas_p":
            case "gas_percent":
                long maxG = state.getGasCapacity();
                return maxG > 0 ? (double) state.getTotalStoredGas() / maxG : 0;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return property;
    }

    public static MachinePropertyExpression fromJson(com.google.gson.JsonObject json) {
        return new MachinePropertyExpression(
            json.get("property")
                .getAsString());
    }
}
