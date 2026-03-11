package ruiseki.omoshiroikamo.api.structure.core;

import java.util.List;

import ruiseki.omoshiroikamo.api.structure.io.IStructureSerializable;

/**
 * Represents a single layer of a structure.
 */
public interface IStructureLayer extends IStructureSerializable {

    /**
     * Get the name of this layer (optional).
     */
    String getName();

    /**
     * Get the rows of symbols that define this layer.
     */
    List<String> getRows();

    /**
     * Converts this layer into the format expected by StructureLib (XY plane).
     * If the internal representation is XZ (horizontal), this should perform
     * transposition.
     * 
     * @return A 2D array of strings representing the layer in XY format.
     */
    String[][] toStructureLibRows();
}
