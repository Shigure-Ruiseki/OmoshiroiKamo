package ruiseki.omoshiroikamo.api.modular;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

import ruiseki.omoshiroikamo.api.enums.EnumIO;

/**
 * Abstraction for a machine controller.
 * Implemented by TEMachineController; used by BlockResolver, IExternalPortProxy,
 * and PortRegistrationVisitor to avoid a direct api/core → machinery dependency.
 */
public interface IMachineController {

    // ---- Port management (PortRegistrationVisitor) ----

    void addPort(IModularPort port, boolean isInput);

    // ---- Port management (BlockResolver) ----

    /**
     * Add a port that was discovered during structure scanning.
     * In addition to registering the port, implementations should track the
     * port's world position so it can be invalidated when the structure breaks.
     */
    void addPortFromStructure(IModularPort port, boolean isInput);

    /**
     * Returns the external port configuration map (coords → type → IO direction).
     * Used by BlockResolver to create proxy ports for non-modular external blocks.
     */
    Map<ChunkCoordinates, Map<IPortType.Type, EnumIO>> getExternalPortConfigs();

    // ---- Structure scanning (BlockResolver) ----

    /**
     * Returns the structure symbol character at the given world position, or null
     * if the position is not part of the structure.
     */
    Character getSymbolAt(int x, int y, int z);

    /**
     * Returns the name of the structure piece this controller is currently formed as.
     */
    String getStructurePieceName();

    /**
     * Record a world position as belonging to this controller's formed structure.
     * Called during structure checking so the controller can track which blocks it owns.
     */
    void trackStructureBlock(int x, int y, int z);

    // ---- External port registration (ItemWrench) ----

    /**
     * Register an external block at (px, py, pz) as a port of the given type.
     * Returns true if registration succeeded.
     */
    boolean registerExternalPort(int px, int py, int pz, IPortType.Type portType, EntityPlayer player);
}
