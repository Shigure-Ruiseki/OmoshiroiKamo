package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

    private enum PropertyDefinition {

        // Energy properties
        ENERGY("energy", state -> (double) state.getStoredEnergy()),
        ENERGY_MAX("energy_max", state -> (double) state.getEnergyCapacity()),
        ENERGY_FREE("energy_f", state -> (double) (state.getEnergyCapacity() - state.getStoredEnergy())),
        ENERGY_PERCENT("energy_p", state -> {
            long maxE = state.getEnergyCapacity();
            return maxE > 0 ? (double) state.getStoredEnergy() / maxE : 0;
        }),
        ENERGY_PER_TICK("energy_per_tick", state -> (double) state.getEnergyPerTick()),

        // Progress properties
        PROGRESS("progress", IMachineState::getProgressPercent),

        // State properties
        IS_RUNNING("is_running", state -> state.isRunning() ? 1.0 : 0.0),
        IS_WAITING("is_waiting", state -> state.isWaitingForOutput() ? 1.0 : 0.0),

        // Machine properties
        TIER("tier", state -> (double) state.getTier()),
        TIMEPLACED("timeplaced", state -> (double) state.getTimePlaced()),
        TIMECONTINUE("timecontinue", state -> (double) state.getTimeContinuous()),
        RECIPEPROCESSED("recipeprocessed", state -> (double) state.getRecipeProcessedCount()),
        RECIPEPROCESSEDTYPE("recipeprocessedtype", state -> (double) state.getRecipeProcessedTypesCount()),

        // Fluid properties
        FLUID("fluid", state -> (double) state.getStoredFluid()),
        FLUID_MAX("fluid_max", state -> (double) state.getFluidCapacity()),
        FLUID_FREE("fluid_f", state -> (double) (state.getFluidCapacity() - state.getStoredFluid())),
        FLUID_PERCENT("fluid_p", state -> {
            long maxF = state.getFluidCapacity();
            return maxF > 0 ? (double) state.getStoredFluid() / maxF : 0;
        }),

        // Mana properties
        MANA("mana", state -> (double) state.getStoredMana()),
        MANA_MAX("mana_max", state -> (double) state.getManaCapacity()),
        MANA_FREE("mana_f", state -> (double) (state.getManaCapacity() - state.getStoredMana())),
        MANA_PERCENT("mana_p", state -> {
            long maxM = state.getManaCapacity();
            return maxM > 0 ? (double) state.getStoredMana() / maxM : 0;
        }),

        // Gas properties
        GAS("gas", state -> (double) state.getTotalStoredGas()),
        GAS_MAX("gas_max", state -> (double) state.getGasCapacity()),
        GAS_FREE("gas_f", state -> (double) (state.getGasCapacity() - state.getTotalStoredGas())),
        GAS_PERCENT("gas_p", state -> {
            long maxG = state.getGasCapacity();
            return maxG > 0 ? (double) state.getTotalStoredGas() / maxG : 0;
        });

        private final String name;
        private final Function<IMachineState, Double> extractor;

        PropertyDefinition(String name, Function<IMachineState, Double> extractor) {
            this.name = name;
            this.extractor = extractor;
        }

        public double evaluate(IMachineState state) {
            return extractor.apply(state);
        }
    }

    private static final Map<String, PropertyDefinition> PROPERTY_MAP = new HashMap<>();

    static {
        // Register all primary property names
        for (PropertyDefinition prop : PropertyDefinition.values()) {
            PROPERTY_MAP.put(prop.name.toLowerCase(), prop);
        }

        // Register energy aliases
        PROPERTY_MAP.put("energy_stored", PropertyDefinition.ENERGY);
        PROPERTY_MAP.put("total_energy", PropertyDefinition.ENERGY);
        PROPERTY_MAP.put("energy_total", PropertyDefinition.ENERGY);
        PROPERTY_MAP.put("energy_capacity", PropertyDefinition.ENERGY_MAX);
        PROPERTY_MAP.put("total_energy_max", PropertyDefinition.ENERGY_MAX);
        PROPERTY_MAP.put("total_energy_capacity", PropertyDefinition.ENERGY_MAX);
        PROPERTY_MAP.put("energy_free", PropertyDefinition.ENERGY_FREE);
        PROPERTY_MAP.put("energy_percent", PropertyDefinition.ENERGY_PERCENT);

        // Register progress aliases
        PROPERTY_MAP.put("progress_percent", PropertyDefinition.PROGRESS);

        // Register recipe count aliases
        PROPERTY_MAP.put("recipe_count", PropertyDefinition.RECIPEPROCESSED);
        PROPERTY_MAP.put("count_recipe", PropertyDefinition.RECIPEPROCESSED);
        PROPERTY_MAP.put("recipe_types_count", PropertyDefinition.RECIPEPROCESSEDTYPE);
        PROPERTY_MAP.put("count_recipe_types", PropertyDefinition.RECIPEPROCESSEDTYPE);

        // Register fluid aliases
        PROPERTY_MAP.put("fluid_stored", PropertyDefinition.FLUID);
        PROPERTY_MAP.put("total_fluid", PropertyDefinition.FLUID);
        PROPERTY_MAP.put("fluid_total", PropertyDefinition.FLUID);
        PROPERTY_MAP.put("fluid_capacity", PropertyDefinition.FLUID_MAX);
        PROPERTY_MAP.put("total_fluid_max", PropertyDefinition.FLUID_MAX);
        PROPERTY_MAP.put("total_fluid_capacity", PropertyDefinition.FLUID_MAX);
        PROPERTY_MAP.put("fluid_free", PropertyDefinition.FLUID_FREE);
        PROPERTY_MAP.put("fluid_percent", PropertyDefinition.FLUID_PERCENT);

        // Register mana aliases
        PROPERTY_MAP.put("mana_stored", PropertyDefinition.MANA);
        PROPERTY_MAP.put("total_mana", PropertyDefinition.MANA);
        PROPERTY_MAP.put("mana_total", PropertyDefinition.MANA);
        PROPERTY_MAP.put("mana_capacity", PropertyDefinition.MANA_MAX);
        PROPERTY_MAP.put("total_mana_max", PropertyDefinition.MANA_MAX);
        PROPERTY_MAP.put("total_mana_capacity", PropertyDefinition.MANA_MAX);
        PROPERTY_MAP.put("mana_free", PropertyDefinition.MANA_FREE);
        PROPERTY_MAP.put("mana_percent", PropertyDefinition.MANA_PERCENT);

        // Register gas aliases
        PROPERTY_MAP.put("gas_total", PropertyDefinition.GAS);
        PROPERTY_MAP.put("gas_capacity", PropertyDefinition.GAS_MAX);
        PROPERTY_MAP.put("gas_free", PropertyDefinition.GAS_FREE);
        PROPERTY_MAP.put("gas_percent", PropertyDefinition.GAS_PERCENT);
    }

    @Override
    public double evaluate(ConditionContext context) {
        if (context == null) return 0;
        IRecipeContext recipeContext = context.getRecipeContext();
        if (recipeContext == null) return 0;

        IMachineState state = recipeContext.getMachineState();
        if (state == null) return 0;

        PropertyDefinition prop = PROPERTY_MAP.get(property.toLowerCase());
        if (prop != null) {
            return prop.evaluate(state);
        }

        return 0;
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
