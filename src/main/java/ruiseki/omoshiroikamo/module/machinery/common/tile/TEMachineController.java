package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;

/**
 * Machine Controller TileEntity - manages a Modular Machinery multiblock.
 * Extends AbstractMBModifierTE to leverage existing structure validation and
 * crafting logic.
 * Corresponds to the 'Q' symbol in structure definitions.
 *
 * TODO: This class already extends AbstractMBModifierTE which provides:
 * - CraftingState management (via AbstractMachineTE)
 * - RedstoneMode support (via AbstractEnergyTE)
 * - Energy capability system (via AbstractEnergyTE)
 * Ensure we don't duplicate these existing systems.
 */
public class TEMachineController extends AbstractMBModifierTE {

    // IO ports tracked during structure formation (TileEntity instances, not
    // positions)
    private final List<IModularPort> inputPorts = new ArrayList<>();
    private final List<IModularPort> outputPorts = new ArrayList<>();

    // Structure definition (will be loaded from JSON in Phase 1)
    private static IStructureDefinition<TEMachineController> STRUCTURE_DEFINITION;

    // Structure offsets per tier (currently only tier 1)
    private static final int[][] OFFSETS = { { 1, 1, 1 } // Tier 1 offset
    };

    public TEMachineController() {
        super();
    }

    // ========== Structure Definition ==========

    @Override
    protected IStructureDefinition<TEMachineController> getStructureDefinition() {
        // TODO: Load from JSON via StructureManager
        // For MVP, return null to use simplified check
        return STRUCTURE_DEFINITION;
    }

    @Override
    public int[][] getOffSet() {
        return OFFSETS;
    }

    @Override
    public String getStructurePieceName() {
        return "main";
    }

    @Override
    public int getTier() {
        return 1;
    }

    // ========== Structure Parts Tracking ==========

    @Override
    protected void clearStructureParts() {
        inputPorts.clear();
        outputPorts.clear();
    }

    @Override
    protected boolean addToMachine(Block block, int meta, int x, int y, int z) {
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te == null || !(te instanceof IModularPort port)) {
            return false;
        }

        IPortType.Direction direction = port.getPortDirection();

        return switch (direction) {
            case INPUT -> addIfAbsent(inputPorts, port);
            case OUTPUT -> addIfAbsent(outputPorts, port);
            case BOTH -> {
                addIfAbsent(inputPorts, port);
                addIfAbsent(outputPorts, port);
                yield true;
            }
            default -> false;
        };
    }

    /**
     * Add port to list if not already present.
     */
    private boolean addIfAbsent(List<IModularPort> list, IModularPort port) {
        if (!list.contains(port)) {
            list.add(port);
            return true;
        }
        return false;
    }

    // ========== Crafting Configuration ==========

    @Override
    public int getBaseDuration() {
        return 100; // 5 seconds base
    }

    @Override
    public int getMinDuration() {
        return 20; // 1 second minimum
    }

    @Override
    public int getMaxDuration() {
        return 1200; // 1 minute maximum
    }

    @Override
    public float getSpeedMultiplier() {
        return 1.0f; // No speed modifiers yet
    }

    @Override
    public void onFormed() {
        // Called when structure is successfully formed
    }

    /**
     * Override doUpdate to use simple structure check when STRUCTURE_DEFINITION is
     * null.
     */
    @Override
    public void doUpdate() {
        if (STRUCTURE_DEFINITION != null) {
            // Use parent's structure check
            super.doUpdate();
            return;
        }

        // Check structure every 20 ticks (1 second)
        if (!shouldDoWorkThisTick(20)) {
            return;
        }

        // Always re-scan structure to update IO ports
        boolean wasFormed = isFormed;
        boolean nowFormed = trySimpleFormStructure();

        // If structure was broken, clear parts
        if (wasFormed && !nowFormed) {
            isFormed = false;
            clearStructureParts();
        }
    }

    @Override
    protected void finishCrafting() {
        // TODO: Implement recipe output
        resetCrafting();
    }

    @Override
    public int getCraftingEnergyCost() {
        // TODO: Get from current recipe
        return 100;
    }

    // ========== Player Interaction ==========

    /**
     * Called when a player right-clicks the controller block.
     */
    public void onRightClick(EntityPlayer player) {
        if (worldObj.isRemote) {
            return;
        }

        // Always re-scan structure on click for instant feedback
        setPlayer(player);
        boolean wasFormed = isFormed;
        boolean nowFormed = trySimpleFormStructure();

        if (nowFormed) {
            String status = wasFormed ? "Status" : "Structure formed";
            player.addChatComponentMessage(
                new ChatComponentText(
                    "[Machine] " + status
                        + ": "
                        + getCraftingState().name()
                        + " | Inputs: "
                        + inputPorts.size()
                        + " | Outputs: "
                        + outputPorts.size()));

            // Display port type counts
            sendPortTypeCounts(player, "Input", inputPorts);
            sendPortTypeCounts(player, "Output", outputPorts);

            // Display port coordinates
            sendPortDetails(player, inputPorts, outputPorts);
        } else {
            player.addChatComponentMessage(
                new ChatComponentText("[Machine] Invalid structure. Need 3x3x3 blocks behind controller."));
        }
    }

    /**
     * Send port type counts to player chat.
     */
    private void sendPortTypeCounts(EntityPlayer player, String label, List<IModularPort> ports) {
        StringBuilder sb = new StringBuilder("  " + label + " Ports: ");
        for (IPortType.Type type : IPortType.Type.values()) {
            if (type == IPortType.Type.NONE) continue;
            long count = ports.stream()
                .filter(p -> p.getPortType() == type)
                .count();
            if (count > 0) {
                sb.append(type.name())
                    .append("=")
                    .append(count)
                    .append(" ");
            }
        }
        player.addChatComponentMessage(new ChatComponentText(sb.toString()));
    }

    /**
     * Send port coordinate details to player chat.
     */
    private void sendPortDetails(EntityPlayer player, List<IModularPort> inputs, List<IModularPort> outputs) {
        for (IModularPort port : inputs) {
            TileEntity te = (TileEntity) port;
            player.addChatComponentMessage(
                new ChatComponentText(
                    "  [IN] " + port.getPortType()
                        .name() + " @ (" + te.xCoord + ", " + te.yCoord + ", " + te.zCoord + ")"));
        }
        for (IModularPort port : outputs) {
            TileEntity te = (TileEntity) port;
            player.addChatComponentMessage(
                new ChatComponentText(
                    "  [OUT] " + port.getPortType()
                        .name() + " @ (" + te.xCoord + ", " + te.yCoord + ", " + te.zCoord + ")"));
        }
    }

    /**
     * Simple 3x3x3 structure check for MVP testing.
     * Controller is on the front face (Z=0), structure extends behind (Z+1 to Z+3)
     */
    private boolean trySimpleFormStructure() {
        clearStructureParts();

        int casingCount = 0;
        // Check 3x3x3 area behind the controller (Z+1 to Z+3)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = 1; dz <= 3; dz++) {
                    int checkX = xCoord + dx;
                    int checkY = yCoord + dy;
                    int checkZ = zCoord + dz;
                    Block block = worldObj.getBlock(checkX, checkY, checkZ);

                    if (block == MachineryBlocks.MACHINE_CASING.getBlock()) {
                        casingCount++;
                    } else if (block instanceof IModularBlock) {
                        addToMachine(block, 0, checkX, checkY, checkZ);
                        casingCount++;
                    }
                }
            }
        }

        if (casingCount >= 27) {
            isFormed = true;
            onFormed();
            return true;
        }

        return false;
    }

    // ========== Getters ==========

    /**
     * Get all input ports.
     */
    public List<IModularPort> getInputPorts() {
        return inputPorts;
    }

    /**
     * Get all output ports.
     */
    public List<IModularPort> getOutputPorts() {
        return outputPorts;
    }

    /**
     * Get input ports filtered by type.
     */
    public List<IModularPort> getInputPorts(IPortType.Type type) {
        return inputPorts.stream()
            .filter(p -> p.getPortType() == type)
            .collect(Collectors.toList());
    }

    /**
     * Get output ports filtered by type.
     */
    public List<IModularPort> getOutputPorts(IPortType.Type type) {
        return outputPorts.stream()
            .filter(p -> p.getPortType() == type)
            .collect(Collectors.toList());
    }

    /**
     * Get valid ports (filters out invalid TileEntities).
     */
    public <T extends IModularPort> List<T> validPorts(List<T> ports) {
        return ports.stream()
            .filter(p -> p != null && !((net.minecraft.tileentity.TileEntity) p).isInvalid())
            .collect(Collectors.toList());
    }
}
