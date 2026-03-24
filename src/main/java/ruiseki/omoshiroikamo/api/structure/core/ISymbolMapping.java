package ruiseki.omoshiroikamo.api.structure.core;

import ruiseki.omoshiroikamo.api.structure.io.IStructureSerializable;

/**
 * Defines the mapping between a symbol (char) and its physical/functional
 * representation.
 */
public interface ISymbolMapping extends IStructureSerializable {

    /**
     * Get the symbol character.
     */
    char getSymbol();

    /**
     * Get the assigned port index for this mapping.
     * 
     * @return Specified index, or -1 if not specified.
     */
    default int getPortIndex() {
        return -1;
    }
}
