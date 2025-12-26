package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;

/**
 * Machine Controller TileEntity - manages a Modular Machinery multiblock.
 * Corresponds to the 'Q' symbol in structure definitions.
 * 
 * Responsibilities:
 * - Structure validation and formation
 * - IO port position tracking
 * - Machine state management
 * - Recipe processing coordination
 */
public class TEMachineController extends TileEntity {

    public enum MachineState {
        IDLE,
        WORKING,
        PAUSED,
        ERROR
    }

    private MachineState state = MachineState.IDLE;
    private boolean isFormed = false;

    // IO port positions tracked during structure formation
    private final List<BlockPos> itemInputPorts = new ArrayList<>();
    private final List<BlockPos> itemOutputPorts = new ArrayList<>();
    private final List<BlockPos> energyInputPorts = new ArrayList<>();

    // Processing
    private int currentProgress = 0;
    private int maxProgress = 0;

    public TEMachineController() {}

    /**
     * Called when a player right-clicks the controller block.
     */
    public void onRightClick(EntityPlayer player) {
        if (isFormed) {
            // Already formed - show status
            player.addChatComponentMessage(
                new ChatComponentText(
                    "[Machine] Status: " + state.name()
                        + " | Item Inputs: "
                        + itemInputPorts.size()
                        + " | Item Outputs: "
                        + itemOutputPorts.size()
                        + " | Energy Inputs: "
                        + energyInputPorts.size()));
        } else {
            // Try to form structure
            boolean success = tryFormStructure();
            if (success) {
                player.addChatComponentMessage(new ChatComponentText("[Machine] Structure formed successfully!"));
            } else {
                player.addChatComponentMessage(
                    new ChatComponentText("[Machine] Invalid structure. Check block placement."));
            }
        }
    }

    /**
     * Attempt to form the multiblock structure.
     * Currently uses a simple 3x3x3 structure for testing.
     */
    private boolean tryFormStructure() {
        clearStructureParts();

        // TODO: Integrate with StructureManager for JSON-based structure validation
        // For MVP, use a simple hardcoded 3x3x3 structure check

        // Check for machine casings in a 3x3x3 pattern (excluding controller position)
        int casingCount = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue; // Skip controller position
                    }

                    int checkX = xCoord + dx;
                    int checkY = yCoord + dy;
                    int checkZ = zCoord + dz;
                    Block block = worldObj.getBlock(checkX, checkY, checkZ);

                    if (block == MachineryBlocks.MACHINE_CASING) {
                        casingCount++;
                    } else if (block == MachineryBlocks.ITEM_INPUT_PORT) {
                        addIOPort(block, 0, checkX, checkY, checkZ);
                        casingCount++;
                    } else if (block == MachineryBlocks.ITEM_OUTPUT_PORT) {
                        addIOPort(block, 0, checkX, checkY, checkZ);
                        casingCount++;
                    } else if (block == MachineryBlocks.ENERGY_INPUT_PORT) {
                        addIOPort(block, 0, checkX, checkY, checkZ);
                        casingCount++;
                    }
                }
            }
        }

        // Need at least 26 blocks (3x3x3 - 1 for controller)
        if (casingCount >= 26) {
            isFormed = true;
            state = MachineState.IDLE;
            return true;
        }

        return false;
    }

    /**
     * Called during structure validation to register IO port positions.
     * This will be called by StructureLib via ofBlockAdderWithPos.
     */
    public boolean addIOPort(Block block, int meta, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z, worldObj);

        if (block == MachineryBlocks.ITEM_INPUT_PORT) {
            if (!itemInputPorts.contains(pos)) {
                itemInputPorts.add(pos);
            }
            return true;
        } else if (block == MachineryBlocks.ITEM_OUTPUT_PORT) {
            if (!itemOutputPorts.contains(pos)) {
                itemOutputPorts.add(pos);
            }
            return true;
        } else if (block == MachineryBlocks.ENERGY_INPUT_PORT) {
            if (!energyInputPorts.contains(pos)) {
                energyInputPorts.add(pos);
            }
            return true;
        }

        return false;
    }

    /**
     * Clear all tracked structure parts.
     * Called before structure re-validation.
     */
    protected void clearStructureParts() {
        itemInputPorts.clear();
        itemOutputPorts.clear();
        energyInputPorts.clear();
        isFormed = false;
        state = MachineState.IDLE;
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote || !isFormed) {
            return;
        }

        // TODO: Implement recipe processing logic
        // 1. Check for valid recipe
        // 2. Check energy availability
        // 3. Progress processing
        // 4. Output results
    }

    // Getters
    public boolean isFormed() {
        return isFormed;
    }

    public MachineState getState() {
        return state;
    }

    public List<BlockPos> getItemInputPorts() {
        return itemInputPorts;
    }

    public List<BlockPos> getItemOutputPorts() {
        return itemOutputPorts;
    }

    public List<BlockPos> getEnergyInputPorts() {
        return energyInputPorts;
    }

    // NBT
    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("isFormed", isFormed);
        nbt.setInteger("state", state.ordinal());
        nbt.setInteger("currentProgress", currentProgress);
        nbt.setInteger("maxProgress", maxProgress);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        isFormed = nbt.getBoolean("isFormed");
        state = MachineState.values()[nbt.getInteger("state")];
        currentProgress = nbt.getInteger("currentProgress");
        maxProgress = nbt.getInteger("maxProgress");
    }
}
