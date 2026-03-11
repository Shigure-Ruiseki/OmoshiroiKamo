package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.tileentity.TileEntity;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;

/**
 * Manages port collections for TEMachineController.
 * Handles port storage, filtering, and requirement validation.
 */
public class PortManager {

    private final List<IModularPort> inputPorts = new ArrayList<>();
    private final List<IModularPort> outputPorts = new ArrayList<>();

    // ========== Port List Management ==========

    /**
     * Clear all stored ports.
     */
    public void clear() {
        inputPorts.clear();
        outputPorts.clear();
    }

    /**
     * Add a port to the appropriate list.
     * 
     * @param port    The port to add
     * @param isInput True for input, false for output
     */
    public void addPort(IModularPort port, boolean isInput) {
        if (port == null) return;
        if (isInput) {
            addIfAbsent(inputPorts, port);
        } else {
            addIfAbsent(outputPorts, port);
        }
    }

    private void addIfAbsent(List<IModularPort> list, IModularPort port) {
        if (!list.contains(port)) {
            list.add(port);
        }
    }

    // ========== Port Getters ==========

    public List<IModularPort> getInputPorts() {
        return inputPorts;
    }

    public List<IModularPort> getOutputPorts() {
        return outputPorts;
    }

    public List<IModularPort> getInputPorts(IPortType.Type type) {
        return inputPorts.stream()
            .filter(p -> p.getPortType() == type)
            .collect(Collectors.toList());
    }

    public List<IModularPort> getOutputPorts(IPortType.Type type) {
        return outputPorts.stream()
            .filter(p -> p.getPortType() == type)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends IModularPort> List<T> getTypedInputPorts(IPortType.Type type, Class<T> portClass) {
        return inputPorts.stream()
            .filter(p -> p.getPortType() == type)
            .filter(portClass::isInstance)
            .map(p -> (T) p)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends IModularPort> List<T> getTypedOutputPorts(IPortType.Type type, Class<T> portClass) {
        return outputPorts.stream()
            .filter(p -> p.getPortType() == type)
            .filter(portClass::isInstance)
            .map(p -> (T) p)
            .collect(Collectors.toList());
    }

    /**
     * Filter out invalid (null or removed) ports from a list.
     */
    public <T extends IModularPort> List<T> validPorts(List<T> ports) {
        return ports.stream()
            .filter(p -> p != null && !((TileEntity) p).isInvalid())
            .collect(Collectors.toList());
    }

    // ========== Port Counting ==========

    public long countPorts(IPortType.Type type, boolean isInput) {
        if (isInput) {
            return inputPorts.stream()
                .filter(p -> p.getPortType() == type)
                .count();
        } else {
            return outputPorts.stream()
                .filter(p -> p.getPortType() == type)
                .count();
        }
    }

    // ========== Requirements Check ==========

    /**
     * Check if port requirements are met for the given structure.
     * 
     * @param entry Structure definition entry
     * @return true if all requirements are met
     */
    public boolean checkRequirements(IStructureEntry entry) {
        if (entry == null) return true;
        List<IStructureRequirement> requirements = entry.getRequirements();
        if (requirements.isEmpty()) return true;

        for (IStructureRequirement req : requirements) {
            String type = req.getType();
            boolean isInput = type.toLowerCase()
                .contains("input");
            IPortType.Type portType = determinePortType(type);

            if (portType != null) {
                long count = countPorts(portType, isInput);
                if (count < req.getMinCount() || count > req.getMaxCount()) {
                    return false;
                }
            }
        }

        return true;
    }

    private IPortType.Type determinePortType(String type) {
        String lower = type.toLowerCase();
        if (lower.contains("item")) return IPortType.Type.ITEM;
        if (lower.contains("fluid")) return IPortType.Type.FLUID;
        if (lower.contains("energy")) return IPortType.Type.ENERGY;
        if (lower.contains("mana")) return IPortType.Type.MANA;
        if (lower.contains("gas")) return IPortType.Type.GAS;
        if (lower.contains("essentia")) return IPortType.Type.ESSENTIA;
        if (lower.contains("vis")) return IPortType.Type.VIS;
        return null;
    }
}
