package ruiseki.omoshiroikamo.api.structure.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Container for shape data with dynamic mappings.
 * Replaces legacy StructureJsonLoader.ShapeWithMappings.
 */
public class StructureShapeWithMappings {

    public final String[][] shape;
    public final Map<Character, Object> dynamicMappings;

    public StructureShapeWithMappings(String[][] shape, Map<Character, Object> dynamicMappings) {
        this.shape = shape;
        this.dynamicMappings = dynamicMappings != null ? dynamicMappings : new HashMap<>();
    }
}
