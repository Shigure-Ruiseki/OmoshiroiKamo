package ruiseki.omoshiroikamo.api.structure.core;

import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;
import ruiseki.omoshiroikamo.api.structure.io.IStructureSerializable;
import ruiseki.omoshiroikamo.api.structure.visitor.IStructureVisitor;

/**
 * The root interface for a multiblock structure definition.
 */
public interface IStructureEntry extends IStructureSerializable {

    /**
     * Get the internal unique name.
     */
    String getName();

    /**
     * Get the human-readable display name.
     */
    String getDisplayName();

    /**
     * Get all structure layers.
     */
    List<IStructureLayer> getLayers();

    /**
     * Get the symbol-to-representation mappings.
     */
    Map<Character, ISymbolMapping> getMappings();

    /**
     * Get the required elements for formation.
     */
    List<IStructureRequirement> getRequirements();

    /**
     * Get the recipe group(s) this structure belongs to.
     */
    List<String> getRecipeGroup();

    /**
     * Get the controller offset [x, y, z].
     */
    int[] getControllerOffset();

    /**
     * Get the hex tint color (e.g. #FFFFFF).
     */
    String getTintColor();

    /**
     * Get the machine speed multiplier.
     */
    float getSpeedMultiplier();

    /**
     * Get the machine energy multiplier.
     */
    float getEnergyMultiplier();

    /**
     * Get the minimum batch size.
     */
    int getBatchMin();

    /**
     * Get the maximum batch size.
     */
    int getBatchMax();

    /**
     * Get the machine tier.
     */
    int getTier();

    /**
     * Get the default structure facing (e.g. SOUTH).
     */
    String getDefaultFacing();

    /**
     * Accepts a visitor to perform operations on this structure.
     */
    void accept(IStructureVisitor visitor);
}
