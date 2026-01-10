package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.ProcessAgent;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;
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
 * TODO: PortManager refactoring
 * TODO: GUI implimentation
 * TODO: NEI integration
 * TODO: Other port types
 */
public class TEMachineController extends AbstractMBModifierTE {

    // IO ports tracked during structure formation
    private final List<IModularPort> inputPorts = new ArrayList<>();
    private final List<IModularPort> outputPorts = new ArrayList<>();

    // Recipe processing
    private final ProcessAgent processAgent = new ProcessAgent();
    // TODO: recipeGroup will be obtained from GUI blueprint in future
    private String recipeGroup = "default";
    // Look-ahead: next recipe cached during current processing
    private transient ModularRecipe nextRecipe = null;

    // Structure definition (will be loaded from JSON)
    private static IStructureDefinition<TEMachineController> STRUCTURE_DEFINITION;

    // Structure offsets per tier
    private static final int[][] OFFSETS = { { 1, 1, 1 } };

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
    // TODO: These methods are inherited from AbstractMBModifierTE but unused
    // Consider removing or refactoring the parent class.

    @Override
    public int getBaseDuration() {
        return 100;
    }

    @Override
    public int getMinDuration() {
        return 20;
    }

    @Override
    public int getMaxDuration() {
        return 1200;
    }

    @Override
    public float getSpeedMultiplier() {
        return 1.0f;
    }

    @Override
    public void onFormed() {
        // Called when structure is successfully formed
    }

    /**
     * Override doUpdate to use structure check when STRUCTURE_DEFINITION is null.
     */
    @Override
    public void doUpdate() {
        if (STRUCTURE_DEFINITION != null) {
            super.doUpdate();
            return;
        }

        // Check structure every 20 ticks
        if (!shouldDoWorkThisTick(20)) {
            // Still process recipe every tick if running
            if (isFormed && processAgent.isRunning()) {
                processRecipe();
            }
            return;
        }

        // Re-scan structure
        boolean wasFormed = isFormed;
        boolean nowFormed = trySimpleFormStructure();

        if (wasFormed && !nowFormed) {
            isFormed = false;
            clearStructureParts();
            processAgent.abort();
        }

        // Process recipes when formed
        if (isFormed) {
            processRecipe();
        }
    }

    /**
     * Process recipe using ProcessAgent with look-ahead optimization.
     * Searches for the next recipe during current recipe processing,
     * enabling zero-tick delay between recipe completions.
     */
    private void processRecipe() {
        // Try to output if waiting
        if (processAgent.isWaitingForOutput()) {
            if (processAgent.tryOutput(outputPorts)) {
                // Output succeeded, try to start next recipe immediately
                startNextRecipe();
            }
            return;
        }

        // If running, tick and look-ahead search for next recipe
        if (processAgent.isRunning()) {
            List<IModularPort> energyPorts = getInputPorts(IPortType.Type.ENERGY);
            ProcessAgent.TickResult result = processAgent.tick(energyPorts);

            // Look-ahead: search for next recipe while processing (only once)
            if (nextRecipe == null) {
                nextRecipe = RecipeLoader.getInstance()
                    .findMatch(new String[] { recipeGroup }, inputPorts);
            }

            // If complete, immediately try to output and start next
            if (result == ProcessAgent.TickResult.READY_OUTPUT) {
                if (processAgent.tryOutput(outputPorts)) {
                    startNextRecipe();
                }
            }
            return;
        }

        // IDLE: Try to start new recipe
        startNextRecipe();
    }

    /**
     * Start the next recipe, using cached look-ahead result if available.
     */
    private void startNextRecipe() {
        // Use cached recipe if available, otherwise search
        ModularRecipe recipe = nextRecipe;
        if (recipe == null) {
            recipe = RecipeLoader.getInstance()
                .findMatch(new String[] { recipeGroup }, inputPorts);
        }
        nextRecipe = null; // Clear cache

        if (recipe != null) {
            List<IModularPort> energyPorts = getInputPorts(IPortType.Type.ENERGY);
            processAgent.start(recipe, inputPorts, energyPorts);
        }
    }

    /**
     * Diagnose why no recipe is currently running.
     * Returns an error message if an issue is found, or null if everything is OK.
     */
    private String diagnoseRecipeIssue() {
        // Check if there are any recipes for this group
        List<ModularRecipe> recipes = RecipeLoader.getInstance()
            .getRecipes(recipeGroup);
        if (recipes.isEmpty()) {
            return "No recipes registered for group: " + recipeGroup;
        }

        // Check if we have required port types
        if (inputPorts.isEmpty()) {
            return "No input ports connected";
        }
        if (outputPorts.isEmpty()) {
            return "No output ports connected";
        }

        // Try to find what's missing for each recipe
        for (ModularRecipe recipe : recipes) {
            StringBuilder missingInputs = new StringBuilder();
            StringBuilder missingOutputs = new StringBuilder();

            // Check inputs
            for (var input : recipe.getInputs()) {
                if (!input.process(inputPorts, true)) {
                    if (missingInputs.length() > 0) missingInputs.append(", ");
                    missingInputs.append(
                        input.getPortType()
                            .name());
                }
            }

            // If inputs are OK, check outputs
            if (missingInputs.length() == 0) {
                for (var output : recipe.getOutputs()) {
                    if (!output.process(outputPorts, true)) {
                        if (missingOutputs.length() > 0) missingOutputs.append(", ");
                        missingOutputs.append(
                            output.getPortType()
                                .name());
                    }
                }

                if (missingOutputs.length() > 0) {
                    return "Output full or missing: " + missingOutputs;
                }

                // All inputs/outputs OK but recipe not starting - check energy
                return "Energy required";
            }
        }

        // Generic message - inputs don't match any recipe
        return "No matching recipe found";
    }

    /**
     * Diagnose which output types are blocked when waiting for output.
     * Uses cached output types from ProcessAgent (works after relog).
     */
    private String diagnoseBlockedOutputs() {
        // Try to use recipe first
        ModularRecipe recipe = processAgent.getCurrentRecipe();
        if (recipe != null) {
            StringBuilder blocked = new StringBuilder();
            for (var output : recipe.getOutputs()) {
                if (!output.process(outputPorts, true)) {
                    if (blocked.length() > 0) blocked.append(", ");
                    blocked.append(
                        output.getPortType()
                            .name());
                }
            }
            return blocked.length() > 0 ? blocked.toString() : "Unknown";
        }

        // Fallback: use cached output types (works after relog)
        java.util.Set<IPortType.Type> cachedTypes = processAgent.getCachedOutputTypes();
        if (cachedTypes.isEmpty()) {
            return "Unknown (no cached outputs)";
        }

        StringBuilder sb = new StringBuilder();
        for (IPortType.Type type : cachedTypes) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(type.name());
        }
        return sb.toString() + " (from cache)";
    }

    @Override
    protected void finishCrafting() {
        resetCrafting();
    }

    @Override
    public int getCraftingEnergyCost() {
        return 0; // Energy is managed by ProcessAgent
    }

    /**
     * Called when a player right-clicks the controller block.
     */
    public void onRightClick(EntityPlayer player) {
        if (worldObj.isRemote) return;

        setPlayer(player);
        boolean wasFormed = isFormed;
        boolean nowFormed = trySimpleFormStructure();

        if (nowFormed) {
            String status = wasFormed ? "Status" : "Structure formed";

            // Display progress info - check waitingForOutput FIRST (it can be true while
            // running)
            if (processAgent.isWaitingForOutput()) {
                // Diagnose which outputs are blocked
                String blockedOutputs = diagnoseBlockedOutputs();
                player.addChatComponentMessage(
                    new ChatComponentText(
                        EnumChatFormatting.YELLOW + "[Machine] Complete (100%) - Waiting for output space: "
                            + blockedOutputs));
            } else if (processAgent.isRunning()) {
                int progress = processAgent.getProgress();
                int max = processAgent.getMaxProgress();
                int percent = (int) (processAgent.getProgressPercent() * 100);
                player.addChatComponentMessage(
                    new ChatComponentText(
                        EnumChatFormatting.GREEN + "[Machine] Processing: "
                            + progress
                            + "/"
                            + max
                            + " ("
                            + percent
                            + "%)"));
            } else {
                // IDLE - try to diagnose why no recipe is running
                String diagnosisMsg = diagnoseRecipeIssue();
                if (diagnosisMsg != null) {
                    player.addChatComponentMessage(
                        new ChatComponentText(EnumChatFormatting.YELLOW + "[Machine] " + diagnosisMsg));
                } else {
                    player.addChatComponentMessage(
                        new ChatComponentText(
                            "[Machine] " + status
                                + " | Inputs: "
                                + inputPorts.size()
                                + " | Outputs: "
                                + outputPorts.size()));
                }
            }

            sendPortTypeCounts(player, "Input", inputPorts);
            sendPortTypeCounts(player, "Output", outputPorts);
        } else {
            player.addChatComponentMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "[Machine] Invalid structure. Need 3x3x3 blocks behind controller."));
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

    // ========== NBT Persistence ==========

    @Override
    public void writeCommon(NBTTagCompound nbt) {
        super.writeCommon(nbt);
        nbt.setString("recipeGroup", recipeGroup);
        NBTTagCompound agentNbt = new NBTTagCompound();
        processAgent.writeToNBT(agentNbt);
        nbt.setTag("processAgent", agentNbt);
    }

    @Override
    public void readCommon(NBTTagCompound nbt) {
        super.readCommon(nbt);
        recipeGroup = nbt.getString("recipeGroup");
        if (recipeGroup.isEmpty()) recipeGroup = "default";
        if (nbt.hasKey("processAgent")) {
            processAgent.readFromNBT(nbt.getCompoundTag("processAgent"));
        }
    }
}
