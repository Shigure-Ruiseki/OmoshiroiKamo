package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IPortType;
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

    private static class PropertyDefinition {

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
        // Energy properties
        registerStaticProperty("energy", state -> (double) state.getStoredEnergy());
        registerStaticProperty("energy_max", state -> (double) state.getEnergyCapacity());
        registerStaticProperty("energy_f", state -> (double) (state.getEnergyCapacity() - state.getStoredEnergy()));
        registerStaticProperty("energy_p", state -> {
            long maxE = state.getEnergyCapacity();
            return maxE > 0 ? (double) state.getStoredEnergy() / maxE : 0;
        });
        registerStaticProperty("energy_per_tick", state -> (double) state.getEnergyPerTick());

        // Progress properties
        registerStaticProperty("progress", IMachineState::getProgressPercent);

        // State properties
        registerStaticProperty("is_running", state -> state.isRunning() ? 1.0 : 0.0);
        registerStaticProperty("is_waiting", state -> state.isWaitingForOutput() ? 1.0 : 0.0);

        // Machine properties
        registerStaticProperty("tier", state -> (double) state.getTier());
        registerStaticProperty("timeplaced", state -> (double) state.getTimePlaced());
        registerStaticProperty("timecontinue", state -> (double) state.getTimeContinuous());
        registerStaticProperty("recipeprocessed", state -> (double) state.getRecipeProcessedCount());
        registerStaticProperty("recipeprocessedtype", state -> (double) state.getRecipeProcessedTypesCount());

        // Fluid properties
        registerStaticProperty("fluid", state -> (double) state.getStoredFluid());
        registerStaticProperty("fluid_max", state -> (double) state.getFluidCapacity());
        registerStaticProperty("fluid_f", state -> (double) (state.getFluidCapacity() - state.getStoredFluid()));
        registerStaticProperty("fluid_p", state -> {
            long maxF = state.getFluidCapacity();
            return maxF > 0 ? (double) state.getStoredFluid() / maxF : 0;
        });

        // Mana properties
        registerStaticProperty("mana", state -> (double) state.getStoredMana());
        registerStaticProperty("mana_max", state -> (double) state.getManaCapacity());
        registerStaticProperty("mana_f", state -> (double) (state.getManaCapacity() - state.getStoredMana()));
        registerStaticProperty("mana_p", state -> {
            long maxM = state.getManaCapacity();
            return maxM > 0 ? (double) state.getStoredMana() / maxM : 0;
        });

        // Gas properties
        registerStaticProperty("gas", state -> (double) state.getTotalStoredGas());
        registerStaticProperty("gas_max", state -> (double) state.getGasCapacity());
        registerStaticProperty("gas_f", state -> (double) (state.getGasCapacity() - state.getTotalStoredGas()));
        registerStaticProperty("gas_p", state -> {
            long maxG = state.getGasCapacity();
            return maxG > 0 ? (double) state.getTotalStoredGas() / maxG : 0;
        });

        // Essentia/Vis
        registerStaticProperty("essentia_max", state -> (double) state.getEssentiaCapacity());
        registerStaticProperty("vis_max", state -> (double) state.getVisCapacity());

        // Structural properties
        registerStaticProperty("batch", state -> (double) state.getBatchSize());
        registerStaticProperty("speed_multi", IMachineState::getSpeedMultiplier);
        registerStaticProperty("energy_multi", IMachineState::getEnergyMultiplier);

        // Register energy aliases
        alias("energy_stored", "energy");
        alias("total_energy", "energy");
        alias("energy_total", "energy");
        alias("energy_capacity", "energy_max");
        alias("total_energy_max", "energy_max");
        alias("total_energy_capacity", "energy_max");
        alias("energy_free", "energy_f");
        alias("energy_percent", "energy_p");

        // Register progress aliases
        alias("progress_percent", "progress");

        // Register recipe count aliases
        alias("recipe_count", "recipeprocessed");
        alias("count_recipe", "recipeprocessed");
        alias("recipe_types_count", "recipeprocessedtype");
        alias("count_recipe_type", "recipeprocessedtype");
        alias("count_recipe_types", "recipeprocessedtype");

        alias("fluid_stored", "fluid");
        alias("total_fluid", "fluid");
        alias("fluid_total", "fluid");
        alias("fluid_capacity", "fluid_max");
        alias("total_fluid_max", "fluid_max");
        alias("total_fluid_capacity", "fluid_max");
        alias("fluid_free", "fluid_f");
        alias("fluid_percent", "fluid_p");

        alias("mana_stored", "mana");
        alias("total_mana", "mana");
        alias("mana_total", "mana");
        alias("mana_capacity", "mana_max");
        alias("total_mana_max", "mana_max");
        alias("total_mana_capacity", "mana_max");
        alias("mana_free", "mana_f");
        alias("mana_percent", "mana_p");

        // Register gas aliases
        alias("gas_total", "gas");
        alias("total_gas", "gas");
        alias("gas_capacity", "gas_max");
        alias("gas_free", "gas_f");
        alias("gas_percent", "gas_p");

        // Register essentia/vis aliases
        alias("essentia_capacity", "essentia_max");
        alias("vis_capacity", "vis_max");

        // Structural aliases
        alias("batch_size", "batch");
        alias("current_batch", "batch");
        alias("speed_multiplier", "speed_multi");
        alias("multiplier_speed", "speed_multi");
        alias("energy_multiplier", "energy_multi");
        alias("multiplier_energy", "energy_multi");

        // Item properties
        registerItemProperties();
    }

    private static void registerStaticProperty(String name, Function<IMachineState, Double> extractor) {
        PROPERTY_MAP.put(name.toLowerCase(), new PropertyDefinition(name, extractor));
    }

    private static void alias(String alias, String target) {
        PropertyDefinition def = PROPERTY_MAP.get(target.toLowerCase());
        if (def != null) PROPERTY_MAP.put(alias.toLowerCase(), def);
    }

    private static void registerItemProperties() {
        // Totals
        registerStaticProperty("item", state -> (double) state.getItemCount(IPortType.Direction.BOTH, null));
        alias("item_total", "item");
        registerStaticProperty("item_in", state -> (double) state.getItemCount(IPortType.Direction.INPUT, null));
        registerStaticProperty("item_out", state -> (double) state.getItemCount(IPortType.Direction.OUTPUT, null));

        // Max (Slots)
        registerStaticProperty("item_max", state -> (double) state.getItemSlotCount(IPortType.Direction.BOTH, false));
        alias("item_capacity", "item_max");
        registerStaticProperty(
            "item_in_max",
            state -> (double) state.getItemSlotCount(IPortType.Direction.INPUT, false));
        registerStaticProperty(
            "item_out_max",
            state -> (double) state.getItemSlotCount(IPortType.Direction.OUTPUT, false));

        // Free (Space)
        registerStaticProperty("item_f", state -> (double) state.getItemSpace(IPortType.Direction.BOTH, null));
        alias("item_free", "item_f");
        alias("item_space", "item_f");
        registerStaticProperty("item_f_in", state -> (double) state.getItemSpace(IPortType.Direction.INPUT, null));
        registerStaticProperty("item_f_out", state -> (double) state.getItemSpace(IPortType.Direction.OUTPUT, null));

        // Percent
        registerStaticProperty("item_percent", state -> {
            int max = state.getItemSlotCount(IPortType.Direction.BOTH, false);
            return max > 0 ? (double) state.getItemCount(IPortType.Direction.BOTH, null) / (max * 64.0) : 0;
        });
        alias("item_p", "item_percent");
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
