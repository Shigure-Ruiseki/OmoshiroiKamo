package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.block.BlockPos;
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

    // IO port positions tracked during structure formation
    private final List<BlockPos> itemInputPorts = new ArrayList<>();
    private final List<BlockPos> itemOutputPorts = new ArrayList<>();
    private final List<BlockPos> energyInputPorts = new ArrayList<>();
    private final List<BlockPos> energyOutputPorts = new ArrayList<>();

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
        itemInputPorts.clear();
        itemOutputPorts.clear();
        energyInputPorts.clear();
        energyOutputPorts.clear();
    }

    @Override
    protected boolean addToMachine(Block block, int meta, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z, worldObj);

        if (block == MachineryBlocks.ITEM_INPUT_PORT.getBlock()) {
            if (!itemInputPorts.contains(pos)) {
                itemInputPorts.add(pos);
            }
            return true;
        } else if (block == MachineryBlocks.ITEM_OUTPUT_PORT.getBlock()) {
            if (!itemOutputPorts.contains(pos)) {
                itemOutputPorts.add(pos);
            }
            return true;
        } else if (block == MachineryBlocks.ENERGY_INPUT_PORT.getBlock()) {
            if (!energyInputPorts.contains(pos)) {
                energyInputPorts.add(pos);
            }
            return true;
        } else if (block == MachineryBlocks.ENERGY_OUTPUT_PORT.getBlock()) {
            if (!energyOutputPorts.contains(pos)) {
                energyOutputPorts.add(pos);
            }
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
                        + " | Item Inputs: "
                        + itemInputPorts.size()
                        + " | Item Outputs: "
                        + itemOutputPorts.size()
                        + " | Energy Inputs: "
                        + energyInputPorts.size()
                        + " | Energy Outputs: "
                        + energyOutputPorts.size()));
        } else {
            player.addChatComponentMessage(
                new ChatComponentText("[Machine] Invalid structure. Need 3x3x3 blocks behind controller."));
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
                    } else if (block == MachineryBlocks.ITEM_INPUT_PORT.getBlock()
                        || block == MachineryBlocks.ITEM_OUTPUT_PORT.getBlock()
                        || block == MachineryBlocks.ENERGY_INPUT_PORT.getBlock()
                        || block == MachineryBlocks.ENERGY_OUTPUT_PORT.getBlock()) {
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

    public List<BlockPos> getItemInputPorts() {
        return itemInputPorts;
    }

    public List<BlockPos> getItemOutputPorts() {
        return itemOutputPorts;
    }

    public List<BlockPos> getEnergyInputPorts() {
        return energyInputPorts;
    }
}
