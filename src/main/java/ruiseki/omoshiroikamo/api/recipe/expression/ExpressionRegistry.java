package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Registry for variables and functions used in ExpressionParser.
 */
public class ExpressionRegistry {

    private static final Map<String, Function<String, IExpression>> VARIABLE_REGISTRY = new HashMap<>();
    private static final Map<String, IFunctionFactory> FUNCTION_REGISTRY = new HashMap<>();

    static {
        // --- World Properties ---
        registerWorldProperty("day");
        registerWorldProperty("total_days");
        registerWorldProperty("time");
        registerWorldProperty("moon_phase");
        registerVariable("moon", name -> new WorldPropertyExpression("moon_phase"));
        registerWorldProperty("x");
        registerWorldProperty("y");
        registerWorldProperty("z");
        registerWorldProperty("dimension");
        registerWorldProperty("light");
        registerWorldProperty("light_block");
        registerWorldProperty("light_sky");
        registerWorldProperty("temp");
        registerWorldProperty("humidity");
        registerWorldProperty("is_day");
        registerWorldProperty("is_night");
        registerWorldProperty("is_raining");
        registerWorldProperty("is_thundering");
        registerWorldProperty("can_see_sky");
        registerWorldProperty("can_see_void");
        registerWorldProperty("tick");
        registerWorldProperty("recipe_tick");
        registerWorldProperty("progress_tick");
        registerWorldProperty("redstone");
        registerWorldProperty("seed");

        // --- Machine Properties ---
        registerMachineProperty("energy");
        registerMachineProperty("energy_stored");
        registerMachineProperty("energy_total");
        registerMachineProperty("total_energy");
        registerMachineProperty("energy_max");
        registerMachineProperty("energy_capacity");
        registerMachineProperty("total_energy_max");
        registerMachineProperty("total_energy_capacity");
        registerMachineProperty("energy_f");
        registerMachineProperty("energy_free");
        registerMachineProperty("energy_p");
        registerMachineProperty("energy_percent");
        registerMachineProperty("energy_per_tick");
        registerMachineProperty("progress");
        registerMachineProperty("progress_percent");
        registerMachineProperty("is_running");
        registerMachineProperty("is_waiting");
        registerMachineProperty("tier");
        registerMachineProperty("timeplaced");
        registerMachineProperty("timecontinue");
        registerMachineProperty("recipeprocessed");
        registerMachineProperty("recipe_count");
        registerMachineProperty("count_recipe");
        registerMachineProperty("recipeprocessedtype");
        registerMachineProperty("recipe_types_count");
        registerMachineProperty("count_recipe_type");
        registerMachineProperty("count_recipe_types");
        registerMachineProperty("mana");
        registerMachineProperty("mana_stored");
        registerMachineProperty("mana_total");
        registerMachineProperty("total_mana");
        registerMachineProperty("mana_max");
        registerMachineProperty("mana_capacity");
        registerMachineProperty("total_mana_max");
        registerMachineProperty("total_mana_capacity");
        registerMachineProperty("mana_f");
        registerMachineProperty("mana_free");
        registerMachineProperty("mana_p");
        registerMachineProperty("mana_percent");
        registerMachineProperty("fluid");
        registerMachineProperty("fluid_stored");
        registerMachineProperty("fluid_total");
        registerMachineProperty("total_fluid");
        registerMachineProperty("fluid_max");
        registerMachineProperty("fluid_capacity");
        registerMachineProperty("total_fluid_max");
        registerMachineProperty("total_fluid_capacity");
        registerMachineProperty("fluid_f");
        registerMachineProperty("fluid_free");
        registerMachineProperty("fluid_p");
        registerMachineProperty("fluid_percent");
        registerMachineProperty("gas");
        registerMachineProperty("gas_total");
        registerMachineProperty("total_gas");
        registerMachineProperty("gas_max");
        registerMachineProperty("gas_capacity");
        registerMachineProperty("gas_f");
        registerMachineProperty("gas_free");
        registerMachineProperty("gas_p");
        registerMachineProperty("gas_percent");
        registerMachineProperty("essentia_max");
        registerMachineProperty("essentia_capacity");
        registerMachineProperty("vis_max");
        registerMachineProperty("vis_capacity");

        // --- Structural Properties ---
        registerMachineProperty("batch");
        registerMachineProperty("batch_size");
        registerMachineProperty("current_batch");
        registerMachineProperty("speed_multi");
        registerMachineProperty("speed_multiplier");
        registerMachineProperty("multiplier_speed");
        registerMachineProperty("energy_multi");
        registerMachineProperty("energy_multiplier");
        registerMachineProperty("multiplier_energy");

        // --- Constants ---
        registerVariable("pi", name -> new ConstantExpression(Math.PI));
        registerVariable("e", name -> new ConstantExpression(Math.E));

        // --- Functions ---
        registerFunction("nbt", (args, parser) -> {
            if (args.isEmpty()) throw parser.error("nbt() requires at least one argument");
            IExpression firstArg = args.get(0);
            if (firstArg instanceof StringLiteralExpression) {
                return new NbtExpression(((StringLiteralExpression) firstArg).getStringValue(), 0.0);
            }
            throw parser.error("nbt() first argument must be a string literal");
        });

        registerFunction("essentia", (args, parser) -> {
            if (args.isEmpty()) throw parser.error("essentia() requires 1 argument (aspect name)");
            return new ResourceFunctionExpression(ResourceFunctionExpression.Type.ESSENTIA, args.get(0));
        });

        registerFunction("vis", (args, parser) -> {
            if (args.isEmpty()) throw parser.error("vis() requires 1 argument (aspect name)");
            return new ResourceFunctionExpression(ResourceFunctionExpression.Type.VIS, args.get(0));
        });

        registerFunction("gas", (args, parser) -> {
            if (args.isEmpty() || args.size() > 1) {
                throw parser.error("gas() function requires 1 argument (gas name)");
            }
            return new ResourceFunctionExpression(ResourceFunctionExpression.Type.GAS, args.get(0));
        });

        registerFunction("fluid", (args, parser) -> {
            if (args.isEmpty() || args.size() > 1) {
                throw parser.error("fluid() function requires 1 argument (fluid name)");
            }
            return new ResourceFunctionExpression(ResourceFunctionExpression.Type.FLUID, args.get(0));
        });

        // Math Functions
        for (String mathFunc : MathFunctionExpression.SUPPORTED_FUNCTIONS) {
            registerFunction(mathFunc, (args, parser) -> {
                validateMathArgs(mathFunc, args.size(), parser);
                return new MathFunctionExpression(mathFunc, args);
            });
        }
    }

    public static IExpression getVariable(String name) {
        Function<String, IExpression> factory = VARIABLE_REGISTRY.get(name.toLowerCase());
        return factory != null ? factory.apply(name.toLowerCase()) : null;
    }

    public static IExpression createFunction(String name, List<IExpression> args, ExpressionParser parser) {
        IFunctionFactory factory = FUNCTION_REGISTRY.get(name.toLowerCase());
        return factory != null ? factory.create(args, parser) : null;
    }

    public static void registerVariable(String name, Function<String, IExpression> factory) {
        VARIABLE_REGISTRY.put(name.toLowerCase(), factory);
    }

    public static void registerWorldProperty(String name) {
        registerVariable(name, WorldPropertyExpression::new);
    }

    public static void registerMachineProperty(String name) {
        registerVariable(name, MachinePropertyExpression::new);
    }

    public static void registerFunction(String name, IFunctionFactory factory) {
        FUNCTION_REGISTRY.put(name.toLowerCase(), factory);
    }

    private static void validateMathArgs(String name, int argCount, ExpressionParser parser) {
        if (name.equals("pow") || name.equals("min")
            || name.equals("max")
            || name.equals("atan2")
            || name.equals("npr")
            || name.equals("ncr")
            || name.equals("perm")
            || name.equals("permu")
            || name.equals("permutation")
            || name.equals("combi")
            || name.equals("combination")) {
            if (argCount < 2) throw parser.error(name + "() requires at least 2 arguments");
        } else if (name.equals("log")) {
            if (argCount < 1 || argCount > 2) throw parser.error("log() requires 1 or 2 arguments");
        } else if (name.equals("clamp")) {
            if (argCount < 3) throw parser.error(name + "() requires at least 3 arguments");
        } else if (name.equals("random")) {
            if (argCount != 0) throw parser.error(name + "() takes no arguments");
        } else {
            if (argCount != 1) throw parser.error(name + "() requires exactly 1 argument");
        }
    }

    @FunctionalInterface
    public interface IFunctionFactory {

        IExpression create(List<IExpression> args, ExpressionParser parser);
    }
}
