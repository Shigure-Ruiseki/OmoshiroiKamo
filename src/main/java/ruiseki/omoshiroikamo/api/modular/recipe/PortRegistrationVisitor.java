package ruiseki.omoshiroikamo.api.modular.recipe;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Visitor that handles the registration of ports into the controller.
 */
public class PortRegistrationVisitor implements IRecipeVisitor {

    private final TEMachineController controller;

    public PortRegistrationVisitor(TEMachineController controller) {
        this.controller = controller;
    }

    /**
     * Helper to visit a port regardless of its specific type.
     */
    public void visitPort(IModularPort port) {
        port.accept(this);
    }

    // Since IRecipeVisitor visits inputs and outputs, and ports implement IModularPort,
    // we can use the same visitor pattern for port registration if needed.
    // However, usually we just need to know if it's an INPUT, OUTPUT, or BOTH.

    public void register(IModularPort port) {
        switch (port.getPortDirection()) {
            case INPUT:
                controller.getPortManager()
                    .addPort(port, true);
                break;
            case OUTPUT:
                controller.getPortManager()
                    .addPort(port, false);
                break;
            case BOTH:
                controller.getPortManager()
                    .addPort(port, true);
                controller.getPortManager()
                    .addPort(port, false);
                break;
        }
    }
}
