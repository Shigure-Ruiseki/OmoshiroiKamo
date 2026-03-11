package ruiseki.omoshiroikamo.api.structure.io;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.gson.JsonObject;

/**
 * Registry for structure requirements.
 * Allows dynamic registration of new requirement types.
 */
public class RequirementRegistry {

    private static final Map<String, BiFunction<String, JsonObject, IStructureRequirement>> parsers = new HashMap<>();

    static {
        register("itemInput", ItemRequirement::fromJson);
        register("itemOutput", ItemRequirement::fromJson);
        register("fluidInput", FluidRequirement::fromJson);
        register("fluidOutput", FluidRequirement::fromJson);
        register("energyInput", EnergyRequirement::fromJson);
        register("energyOutput", EnergyRequirement::fromJson);
        register("manaInput", ManaRequirement::fromJson);
        register("manaOutput", ManaRequirement::fromJson);
        register("gasInput", GasRequirement::fromJson);
        register("gasOutput", GasRequirement::fromJson);
        register("essentiaInput", EssentiaRequirement::fromJson);
        register("essentiaOutput", EssentiaRequirement::fromJson);
        register("visInput", VisRequirement::fromJson);
        register("visOutput", VisRequirement::fromJson);
    }

    /**
     * Register a parser for a requirement type.
     * 
     * @param type   The type identifier (e.g., "itemInput").
     * @param parser Function that creates IStructureRequirement from type and
     *               JsonObject.
     */
    public static void register(String type, BiFunction<String, JsonObject, IStructureRequirement> parser) {
        parsers.put(type, parser);
    }

    /**
     * Parse a JSON object into an IStructureRequirement.
     * 
     * @param type The type of requirement.
     * @param data The JSON data for the requirement.
     * @return The parsed requirement, or null if type is unknown.
     */
    public static IStructureRequirement parse(String type, JsonObject data) {
        BiFunction<String, JsonObject, IStructureRequirement> parser = parsers.get(type);
        if (parser != null) {
            return parser.apply(type, data);
        }
        return null;
    }
}
