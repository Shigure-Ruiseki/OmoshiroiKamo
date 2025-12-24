package ruiseki.omoshiroikamo.core.common.structure;

import java.util.List;
import java.util.Map;

/**
 * Data classes representing the JSON structure definitions.
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

        /** Layer array (null for default entries). */
        public List<Layer> layers;

        /** Block mappings. */
        public Map<String, Object> mappings;
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
