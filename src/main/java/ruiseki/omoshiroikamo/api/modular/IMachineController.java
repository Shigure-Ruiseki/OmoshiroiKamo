package ruiseki.omoshiroikamo.api.modular;

/**
 * Abstraction for a machine controller.
 * Implemented by TEMachineController; used by IExternalPortProxy and PortRegistrationVisitor
 * to avoid a direct api → machinery dependency.
 */
public interface IMachineController {

    /**
     * Add a port to this controller's port manager.
     *
     * @param port    The port to add
     * @param isInput true for input side, false for output side
     */
    void addPort(IModularPort port, boolean isInput);
}
