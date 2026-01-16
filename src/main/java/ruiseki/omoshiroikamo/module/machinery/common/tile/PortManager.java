package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.tileentity.TileEntity;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;

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
    public boolean checkRequirements(StructureEntry entry) {
        if (entry == null || entry.requirements == null) return true;

        StructureDefinitionData.Requirements req = entry.requirements;

        // Item ports
        if (!checkPortRequirement(req.itemInput, IPortType.Type.ITEM, true)) return false;
        if (!checkPortRequirement(req.itemOutput, IPortType.Type.ITEM, false)) return false;

        // Fluid ports
        if (!checkPortRequirement(req.fluidInput, IPortType.Type.FLUID, true)) return false;
        if (!checkPortRequirement(req.fluidOutput, IPortType.Type.FLUID, false)) return false;

        // Energy ports
        if (!checkPortRequirement(req.energyInput, IPortType.Type.ENERGY, true)) return false;
        if (!checkPortRequirement(req.energyOutput, IPortType.Type.ENERGY, false)) return false;

        // Mana ports
        if (!checkPortRequirement(req.manaInput, IPortType.Type.MANA, true)) return false;
        if (!checkPortRequirement(req.manaOutput, IPortType.Type.MANA, false)) return false;

        // Gas ports
        if (!checkPortRequirement(req.gasInput, IPortType.Type.GAS, true)) return false;
        if (!checkPortRequirement(req.gasOutput, IPortType.Type.GAS, false)) return false;

        // Essentia ports
        if (!checkPortRequirement(req.essentiaInput, IPortType.Type.ESSENTIA, true)) return false;
        if (!checkPortRequirement(req.essentiaOutput, IPortType.Type.ESSENTIA, false)) return false;

        // Vis ports
        if (!checkPortRequirement(req.visInput, IPortType.Type.VIS, true)) return false;
        if (!checkPortRequirement(req.visOutput, IPortType.Type.VIS, false)) return false;

        return true;
    }

    private boolean checkPortRequirement(StructureDefinitionData.PortRequirement req, IPortType.Type type,
        boolean isInput) {
        if (req == null) return true;

        long count = countPorts(type, isInput);

        if (req.min != null && count < req.min) {
            return false;
        }
        if (req.max != null && count > req.max) {
            return false;
        }
        return true;
    }
}
