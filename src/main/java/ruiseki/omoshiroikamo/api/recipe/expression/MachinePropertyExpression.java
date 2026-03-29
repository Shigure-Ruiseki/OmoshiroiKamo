package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Expression that evaluates to a property of the machine (e.g., energy,
 * progress).
 */
public class MachinePropertyExpression implements IExpression {

    private final String propertyName;
    private static final Map<String, PropertyDefinition> definitions = new HashMap<>();

    public MachinePropertyExpression(String propertyName) {
        this.propertyName = propertyName.toLowerCase();
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        String cacheKey = "prop_" + propertyName;
        EvaluationValue cached = context.getCachedValue(cacheKey);
        if (cached != null) {
            return cached;
        }

        PropertyDefinition def = definitions.get(propertyName);
        if (def != null) {
            EvaluationValue value = def.getter.apply(context);
            context.setCachedValue(cacheKey, value);
            return value;
        }
        return EvaluationValue.ZERO;
    }

    @Override
    public String toString() {
        return propertyName;
    }

    public static class PropertyDefinition {

        public final String name;
        public final Function<ConditionContext, EvaluationValue> getter;

        public PropertyDefinition(String name, Function<ConditionContext, EvaluationValue> getter) {
            this.name = name;
            this.getter = getter;
        }
    }

    private static void register(String name, Function<ConditionContext, EvaluationValue> getter) {
        definitions.put(name.toLowerCase(), new PropertyDefinition(name, getter));
    }

    private static void alias(String alias, String target) {
        PropertyDefinition targetDef = definitions.get(target.toLowerCase());
        if (targetDef != null) {
            definitions.put(alias.toLowerCase(), targetDef);
        }
    }

    static {
        // Core properties
        register(
            "energy",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getStoredEnergy()));
        register(
            "energy_max",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getEnergyCapacity()));
        register("energy_p", ctx -> {
            long stored = ctx.getRecipeContext()
                .getMachineState()
                .getStoredEnergy();
            long max = ctx.getRecipeContext()
                .getMachineState()
                .getEnergyCapacity();
            return new EvaluationValue(max > 0 ? (double) stored / max : 0);
        });
        register("energy_f", ctx -> {
            long stored = ctx.getRecipeContext()
                .getMachineState()
                .getStoredEnergy();
            long max = ctx.getRecipeContext()
                .getMachineState()
                .getEnergyCapacity();
            return new EvaluationValue(max - stored);
        });

        register(
            "fluid",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getStoredFluid()));
        register(
            "fluid_max",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getFluidCapacity()));
        register("fluid_p", ctx -> {
            long stored = ctx.getRecipeContext()
                .getMachineState()
                .getStoredFluid();
            long max = ctx.getRecipeContext()
                .getMachineState()
                .getFluidCapacity();
            return new EvaluationValue(max > 0 ? (double) stored / max : 0);
        });
        register("fluid_f", ctx -> {
            long stored = ctx.getRecipeContext()
                .getMachineState()
                .getStoredFluid();
            long max = ctx.getRecipeContext()
                .getMachineState()
                .getFluidCapacity();
            return new EvaluationValue(max - stored);
        });

        register(
            "mana",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getStoredMana()));
        register(
            "mana_max",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getManaCapacity()));
        register("mana_p", ctx -> {
            long stored = ctx.getRecipeContext()
                .getMachineState()
                .getStoredMana();
            long max = ctx.getRecipeContext()
                .getMachineState()
                .getManaCapacity();
            return new EvaluationValue(max > 0 ? (double) stored / max : 0);
        });
        register("mana_f", ctx -> {
            long stored = ctx.getRecipeContext()
                .getMachineState()
                .getStoredMana();
            long max = ctx.getRecipeContext()
                .getMachineState()
                .getManaCapacity();
            return new EvaluationValue(max - stored);
        });

        register(
            "item",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getItemCount(IPortType.Direction.BOTH, null)));
        register(
            "item_max",
            ctx -> new EvaluationValue(
                (double) ctx.getRecipeContext()
                    .getMachineState()
                    .getItemSlotCount(IPortType.Direction.BOTH, false) * 64));
        register("item_p", ctx -> {
            double count = ctx.getRecipeContext()
                .getMachineState()
                .getItemCount(IPortType.Direction.BOTH, null);
            double limit = ctx.getRecipeContext()
                .getMachineState()
                .getItemSlotCount(IPortType.Direction.BOTH, false) * 64.0;
            return new EvaluationValue(limit > 0 ? count / limit : 0);
        });
        register(
            "item_f",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getItemSpace(IPortType.Direction.BOTH, null)));

        register(
            "progress",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getProgressPercent()));
        register(
            "is_running",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .isRunning()));
        register(
            "is_waiting",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .isWaitingForOutput()));
        register(
            "tier",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getTier()));
        register(
            "batch",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getBatchSize()));
        register(
            "speed_multi",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getSpeedMultiplier()));
        register(
            "energy_multi",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getEnergyMultiplier()));

        register(
            "timeplaced",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getTimePlaced()));
        register(
            "timecontinue",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getTimeContinuous()));
        register(
            "recipeprocessed",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getRecipeProcessedCount()));
        register(
            "recipeprocessedtype",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getRecipeProcessedTypesCount()));

        // Resource capacities
        register(
            "essentia_max",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getEssentiaCapacity()));
        register(
            "vis_max",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getVisCapacity()));
        register(
            "gas_max",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getMachineState()
                    .getGasCapacity()));

        // Environment
        register(
            "facing",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getFacing()
                    .ordinal()));
        register(
            "world_seed",
            ctx -> new EvaluationValue(
                ctx.getRecipeContext()
                    .getWorld()
                    .getSeed()));

        // Aliases
        alias("power", "energy");
        alias("power_p", "energy_p");
        alias("energy_stored", "energy");
        alias("energy_total", "energy");
        alias("total_energy", "energy");

        alias("energy_capacity", "energy_max");
        alias("total_energy_max", "energy_max");
        alias("total_energy_capacity", "energy_max");

        alias("energy_free", "energy_f");
        alias("energy_percent", "energy_p");

        alias("mana_stored", "mana");
        alias("mana_total", "mana");
        alias("total_mana", "mana");
        alias("mana_capacity", "mana_max");
        alias("total_mana_max", "mana_max");
        alias("total_mana_capacity", "mana_max");
        alias("mana_free", "mana_f");
        alias("mana_percent", "mana_p");

        alias("fluid_stored", "fluid");
        alias("fluid_total", "fluid");
        alias("total_fluid", "fluid");
        alias("fluid_capacity", "fluid_max");
        alias("total_fluid_max", "fluid_max");
        alias("total_fluid_capacity", "fluid_max");
        alias("fluid_free", "fluid_f");
        alias("fluid_percent", "fluid_p");

        alias("gas_total", "gas");
        alias("total_gas", "gas");
        alias("gas_capacity", "gas_max");
        alias("gas_free", "gas_f");
        alias("gas_percent", "gas_p");

        alias("essentia_capacity", "essentia_max");
        alias("vis_capacity", "vis_max");

        alias("item_total", "item");
        alias("item_capacity", "item_max");
        alias("item_space", "item_f");

        alias("progress_percent", "progress");
        alias("batch_size", "batch");
        alias("current_batch", "batch");

        alias("recipe_count", "recipeprocessed");
        alias("count_recipe", "recipeprocessed");
        alias("recipe_types_count", "recipeprocessedtype");
        alias("count_recipe_type", "recipeprocessedtype");
        alias("count_recipe_types", "recipeprocessedtype");

        alias("speed_multiplier", "speed_multi");
        alias("multiplier_speed", "speed_multi");
        alias("energy_multiplier", "energy_multi");
        alias("multiplier_energy", "energy_multi");
    }

    public static MachinePropertyExpression fromJson(JsonObject json) {
        return new MachinePropertyExpression(
            json.get("property")
                .getAsString());
    }
}
