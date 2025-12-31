package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

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

        // Test recipe: 64 Coal + 10000mb Water -> 1 Diamond
        if (isFormed) {
            tryTestRecipe();
        }
    }

    /**
     * Test recipe: 64 Coal + 10000mb Water -> 1 Diamond
     */
    private void tryTestRecipe() {
        // 1. Check for required coal (64 total across all input ports)
        int totalCoal = 0;
        for (ItemStack stack : getStoredInputItems()) {
            if (stack.getItem() == Items.coal) {
                totalCoal += stack.stackSize;
            }
        }
        if (totalCoal < 64) {
            return;
        }

        // 2. Check for required water (10000mb total)
        int totalWater = 0;
        for (FluidStack fluid : getStoredInputFluids()) {
            if (fluid.getFluid() == FluidRegistry.WATER) {
                totalWater += fluid.amount;
            }
        }
        if (totalWater < 10000) {
            return;
        }

        // 3. Check if output has space (simulate)
        ItemStack diamond = new ItemStack(Items.diamond, 1);
        List<ItemStack> remainder = insertOutputItems(Collections.singletonList(diamond.copy()));
        if (!remainder.isEmpty()) {
            return; // No space for output
        }

        // Diamond was already inserted, now consume inputs
        System.out.println("[TestRecipe] Consuming: 64 coal, 10000mb water -> 1 diamond");
        consumeCoal(64);
        drainWater(10000);
    }

    private void consumeCoal(int amount) {
        int remaining = amount;
        for (AbstractItemIOPortTE port : getTypedInputPorts(IPortType.Type.ITEM, AbstractItemIOPortTE.class)) {
            for (int i = 0; i < port.getSizeInventory() && remaining > 0; i++) {
                ItemStack stack = port.getStackInSlot(i);
                if (stack != null && stack.getItem() == Items.coal) {
                    int consume = Math.min(stack.stackSize, remaining);
                    stack.stackSize -= consume;
                    remaining -= consume;
                    if (stack.stackSize <= 0) {
                        port.setInventorySlotContents(i, null);
                    }
                }
            }
        }
    }

    private void drainWater(int amount) {
        int remaining = amount;
        for (AbstractFluidPortTE port : getTypedInputPorts(IPortType.Type.FLUID, AbstractFluidPortTE.class)) {
            // Use internal drain to bypass side IO checks
            FluidStack drained = port.internalDrain(remaining, true);
            if (drained != null) {
                remaining -= drained.amount;
            }
            if (remaining <= 0) break;
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
     * Get input ports filtered by type and cast to the specific port class.
     */
    @SuppressWarnings("unchecked")
    public <T extends IModularPort> List<T> getTypedInputPorts(IPortType.Type type, Class<T> portClass) {
        return inputPorts.stream()
            .filter(p -> p.getPortType() == type)
            .filter(portClass::isInstance)
            .map(p -> (T) p)
            .collect(Collectors.toList());
    }

    /**
     * Get output ports filtered by type and cast to the specific port class.
     */
    @SuppressWarnings("unchecked")
    public <T extends IModularPort> List<T> getTypedOutputPorts(IPortType.Type type, Class<T> portClass) {
        return outputPorts.stream()
            .filter(p -> p.getPortType() == type)
            .filter(portClass::isInstance)
            .map(p -> (T) p)
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

    // ========== Item IO Helpers ==========

    /**
     * Get all items stored in input item ports.
     */
    public List<ItemStack> getStoredInputItems() {
        List<ItemStack> items = new ArrayList<>();
        for (AbstractItemIOPortTE port : getTypedInputPorts(IPortType.Type.ITEM, AbstractItemIOPortTE.class)) {
            for (int i = 0; i < port.getSizeInventory(); i++) {
                ItemStack stack = port.getStackInSlot(i);
                if (stack != null) {
                    items.add(stack.copy());
                }
            }
        }
        return items;
    }

    /**
     * Insert items into output item ports.
     * 
     * @return remainder that couldn't be inserted
     */
    public List<ItemStack> insertOutputItems(List<ItemStack> items) {
        List<ItemStack> remainder = new ArrayList<>();
        for (ItemStack stack : items) {
            ItemStack remaining = stack.copy();
            for (AbstractItemIOPortTE port : getTypedOutputPorts(IPortType.Type.ITEM, AbstractItemIOPortTE.class)) {
                remaining = insertItemToPort(port, remaining);
                if (remaining == null || remaining.stackSize == 0) {
                    break;
                }
            }
            if (remaining != null && remaining.stackSize > 0) {
                remainder.add(remaining);
            }
        }
        return remainder;
    }

    private ItemStack insertItemToPort(AbstractItemIOPortTE port, ItemStack stack) {
        if (stack == null) return null;
        ItemStack remaining = stack.copy();
        for (int i = 0; i < port.getSizeInventory() && remaining.stackSize > 0; i++) {
            ItemStack existing = port.getStackInSlot(i);
            if (existing == null) {
                port.setInventorySlotContents(i, remaining.copy());
                return null;
            } else if (existing.isItemEqual(remaining) && ItemStack.areItemStackTagsEqual(existing, remaining)) {
                int space = existing.getMaxStackSize() - existing.stackSize;
                int transfer = Math.min(space, remaining.stackSize);
                existing.stackSize += transfer;
                remaining.stackSize -= transfer;
                port.setInventorySlotContents(i, existing);
            }
        }
        return remaining.stackSize > 0 ? remaining : null;
    }

    // ========== Fluid IO Helpers ==========

    /**
     * Get all fluids stored in input fluid ports.
     */
    public List<FluidStack> getStoredInputFluids() {
        List<FluidStack> fluids = new ArrayList<>();
        for (AbstractFluidPortTE port : getTypedInputPorts(IPortType.Type.FLUID, AbstractFluidPortTE.class)) {
            FluidStack stored = port.getStoredFluid();
            if (stored != null && stored.amount > 0) {
                fluids.add(stored.copy());
            }
        }
        return fluids;
    }

    /**
     * Fill fluid into output fluid ports.
     * 
     * @return amount that was actually filled
     */
    public int fillOutputFluid(FluidStack fluid) {
        if (fluid == null || fluid.amount <= 0) return 0;
        int remaining = fluid.amount;
        for (AbstractFluidPortTE port : getTypedOutputPorts(IPortType.Type.FLUID, AbstractFluidPortTE.class)) {
            FluidStack toFill = fluid.copy();
            toFill.amount = remaining;
            // Use internal fill to bypass side IO checks
            int filled = port.internalFill(toFill, true);
            remaining -= filled;
            if (remaining <= 0) break;
        }
        return fluid.amount - remaining;
    }

    // ========== Energy IO Helpers ==========

    /**
     * Get total energy stored in input energy ports.
     */
    public int getStoredInputEnergy() {
        int total = 0;
        for (AbstractEnergyIOPortTE port : getTypedInputPorts(IPortType.Type.ENERGY, AbstractEnergyIOPortTE.class)) {
            total += port.getEnergyStored();
        }
        return total;
    }

    /**
     * Extract energy from input energy ports.
     * 
     * @return amount actually extracted
     */
    public int extractInputEnergy(int amount, boolean simulate) {
        int remaining = amount;
        for (AbstractEnergyIOPortTE port : getTypedInputPorts(IPortType.Type.ENERGY, AbstractEnergyIOPortTE.class)) {
            int available = port.getEnergyStored();
            int toExtract = Math.min(available, remaining);
            if (!simulate && toExtract > 0) {
                port.extractEnergy(toExtract);
            }
            remaining -= toExtract;
            if (remaining <= 0) break;
        }
        return amount - remaining;
    }
}
