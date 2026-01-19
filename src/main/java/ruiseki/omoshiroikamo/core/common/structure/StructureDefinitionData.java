package ruiseki.omoshiroikamo.core.common.structure;

import java.util.List;
import java.util.Map;

/**
 * Data classes representing the JSON structure definitions.
 * TODO: Custom model renderer if we can
 */
public class StructureDefinitionData {

    /**
     * Represents the entire JSON file (array form).
     */
    public static class StructureFile {

        public List<StructureEntry> entries;
    }

    /**
     * One structure entry (either "default" or a concrete structure).
     */
    public static class StructureEntry {

        /** Structure name ("default" or e.g. "oreExtractorTier1"). */
        public String name;

        /** Display name for UI (CustomStructure only). */
        public String displayName;

        /** Recipe group to use (CustomStructure only). */
        public String recipeGroup;

        /** Layer array (null for default entries). */
        public List<Layer> layers;

        /** Block mappings. */
        public Map<String, Object> mappings;

        // Port requirements (CustomStructure only).
        // TODO: Custom IO Block (tanks and storages from other mods)
        public Requirements requirements;

        /** Machine properties (CustomStructure only). */
        public Properties properties;

        /**
         * Controller offset for hologram display [x, y, z]. Calculated from 'Q'
         * position.
         */
        public int[] controllerOffset;
    }

    /**
     * Port requirements for CustomStructure.
     */
    public static class Requirements {

        public PortRequirement itemInput;
        public PortRequirement itemOutput;
        public PortRequirement fluidInput;
        public PortRequirement fluidOutput;
        public PortRequirement energyInput;
        public PortRequirement energyOutput;
        public PortRequirement manaInput;
        public PortRequirement manaOutput;
        public PortRequirement gasInput;
        public PortRequirement gasOutput;
        public PortRequirement essentiaInput;
        public PortRequirement essentiaOutput;
        public PortRequirement visInput;
        public PortRequirement visOutput;
    }

    /**
     * Min/max requirement for a port type.
     */
    public static class PortRequirement {

        public Integer min;
        public Integer max;
    }

    /**
     * Machine properties for CustomStructure.
     */
    public static class Properties {

        /** Speed multiplier (default 1.0). */
        public float speedMultiplier = 1.0f;

        /** Energy cost multiplier (default 1.0). */
        public float energyMultiplier = 1.0f;

        /** Minimum batch size (default 1). */
        public int batchMin = 1;

        /** Maximum batch size (default 1). */
        public int batchMax = 1;
    }

    /**
     * A named layer within a structure.
     */
    public static class Layer {

        /** Layer name (e.g., "controller", "core", "base"). */
        public String name;

        /** Rows within the layer (each row is a string). */
        public List<String> rows;
    }

    /**
     * A block mapping entry (single block or multiple candidates).
     */
    public static class BlockMapping {

        /** ID when mapping to a single block. */
        public String block;

        /** Candidate list when multiple blocks are allowed. */
        public List<BlockEntry> blocks;

        /** Minimum count across the symbol (optional). */
        public Integer min;

        /** Maximum count across the symbol (optional). */
        public Integer max;
    }

    /**
     * An individual block entry (one candidate).
     */
    public static class BlockEntry {

        /** Block ID in mod:block:meta form (* allows any meta). */
        public String id;

        /** Minimum count for this block. */
        public Integer min;

        /** Maximum count for this block. */
        public Integer max;
    }
}
