package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.gtnewhorizon.structurelib.alignment.IAlignment;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.core.client.gui.GuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.Properties;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.module.machinery.common.item.ItemMachineBlueprint;
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
 * Blueprint slot provides GUI-based structure selection.
 * The controller reads the structure name from the inserted blueprint
 * and validates the surrounding blocks against that structure definition.
 */
public class TEMachineController extends AbstractMBModifierTE implements IAlignment, IGuiHolder<PosGuiData> {

    // ========== Blueprint Inventory ==========
    public static final int BLUEPRINT_SLOT = 0;
    private static final int INVENTORY_SIZE = 1;

    private final ItemStackHandlerBase inventory = new ItemStackHandlerBase(INVENTORY_SIZE) {

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == BLUEPRINT_SLOT) {
                updateStructureFromBlueprint();
            }
            markDirty();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == BLUEPRINT_SLOT) {
                return stack != null && stack.getItem() instanceof ItemMachineBlueprint;
            }
            return false;
        }
    };

    // Port management (delegated to PortManager)
    private final PortManager portManager = new PortManager();

    // Recipe processing
    private final ProcessAgent processAgent = new ProcessAgent();
    // Recipe group - obtained from custom structure or GUI
    private String recipeGroup = "default";
    // CustomStructure name (automatically derived from blueprint)
    private String customStructureName = null;
    // Look-ahead: next recipe cached during current processing
    private transient ModularRecipe nextRecipe = null;
    // Recipe version at the time nextRecipe was cached (for invalidation on reload)
    private transient int cachedRecipeVersion = -1;

    // Structure definition (dynamically loaded from CustomStructureRegistry)
    private static IStructureDefinition<TEMachineController> STRUCTURE_DEFINITION;

    // Controller facing (rotation state) - used by IAlignment
    private ExtendedFacing extendedFacing = ExtendedFacing.DEFAULT;

    public TEMachineController() {
        super();
    }

    // ========== Structure Definition ==========

    @Override
    public IStructureDefinition<TEMachineController> getStructureDefinition() {
        // Check for custom structure first
        if (customStructureName != null && !customStructureName.isEmpty()) {
            IStructureDefinition<TEMachineController> customDef = CustomStructureRegistry
                .getDefinition(customStructureName);
            if (customDef != null) {
                return customDef;
            }
        }
        // Fallback to default structure (or null for simple check)
        return STRUCTURE_DEFINITION;
    }

    @Override
    public int[][] getOffSet() {
        // Get offset from custom structure definition if available
        if (customStructureName != null && !customStructureName.isEmpty()) {
            StructureEntry entry = StructureManager.getInstance()
                .getCustomStructure(customStructureName);
            if (entry != null && entry.controllerOffset != null && entry.controllerOffset.length >= 3) {
                return new int[][] { entry.controllerOffset };
            }
        }
        // Default offset (no structure)
        return new int[][] { { 0, 0, 0 } };
    }

    @Override
    public String getStructurePieceName() {
        // CustomStructureRegistry registers shapes using the structure name
        return customStructureName != null ? customStructureName : "main";
    }

    @Override
    public int getTier() {
        return 1;
    }

    // ========== Structure Parts Tracking ==========

    @Override
    protected void clearStructureParts() {
        portManager.clear();
    }

    @Override
    protected boolean addToMachine(Block block, int meta, int x, int y, int z) {
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te == null || !(te instanceof IModularPort port)) {
            return false;
        }

        IPortType.Direction direction = port.getPortDirection();

        return switch (direction) {
            case INPUT -> {
                portManager.addPort(port, true);
                yield true;
            }
            case OUTPUT -> {
                portManager.addPort(port, false);
                yield true;
            }
            case BOTH -> {
                portManager.addPort(port, true);
                portManager.addPort(port, false);
                yield true;
            }
            default -> false;
        };
    }

    /**
     * Called by StructureLib's ofTileAdder during structure check.
     * Adds a port to the appropriate list based on direction.
     *
     * @param port    The port to add
     * @param isInput True for input list, false for output list
     */
    public void addPortFromStructure(IModularPort port, boolean isInput) {
        portManager.addPort(port, isInput);
    }

    // ========== Crafting Configuration ==========
    // TODO: These methods are inherited from AbstractMBModifierTE but unused
    // Consider removing or refactoring the parent class.

    // Field to store validation error message
    private String lastValidationError = "";

    @Override
    protected boolean structureCheck(String piece, int ox, int oy, int oz) {
        // Clear previous error
        lastValidationError = "";

        boolean valid = super.structureCheck(piece, ox, oy, oz);

        if (valid && isFormed) {
            // Perform additional requirements check for CustomStructure
            if (!checkRequirements()) {
                isFormed = false;
                clearStructureParts();
                return false;
            }
        } else if (!valid) {
            // If super check failed, we don't know exactly why (StructureLib doesn't tell
            // us easily),
            // but usually it means blocks don't match.
            // We could try to give a hint if custom structure is set.
            if (customStructureName != null) {
                lastValidationError = "Block mismatch or incomplete structure.";
            }
        }

        return valid;
    }

    /**
     * Check if the formed structure meets the requirements defined in
     * CustomStructure.
     *
     * @return true if requirements are met or no requirements exist
     */
    private boolean checkRequirements() {
        if (customStructureName == null || customStructureName.isEmpty()) return true;

        StructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(customStructureName);
        return portManager.checkRequirements(entry);
    }

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
     * Main update loop.
     * Blueprint is required for the controller to operate.
     * Uses StructureLib for structure validation when blueprint is set.
     */
    @Override
    public void doUpdate() {
        // Blueprint required - no operation without it
        if (customStructureName == null || customStructureName.isEmpty()) {
            if (isFormed) {
                isFormed = false;
                clearStructureParts();
                processAgent.abort();
            }
            return;
        }

        // Use StructureLib-based structure checking (calls super.doUpdate())
        // Super handles IC2 registration, energy sync, and structure validation
        super.doUpdate();

        // Process recipes when formed
        if (isFormed) {
            processRecipe();
        }
    }

    /**
     * Process recipe using ProcessAgent.
     * Searches for the next recipe during current recipe processing,
     * enabling zero-tick delay between recipe completions.
     */
    private void processRecipe() {
        // Try to output if waiting
        if (processAgent.isWaitingForOutput()) {
            if (processAgent.tryOutput(getOutputPorts())) {
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
                    .findMatch(new String[] { recipeGroup }, getInputPorts());
                cachedRecipeVersion = RecipeLoader.getInstance()
                    .getRecipeVersion();
            }

            // If complete, immediately try to output and start next
            if (result == ProcessAgent.TickResult.READY_OUTPUT) {
                if (processAgent.tryOutput(getOutputPorts())) {
                    startNextRecipe();
                }
            }
            return;
        }

        // IDLE: Try to start new recipe
        startNextRecipe();
    }

    private void startNextRecipe() {
        // Invalidate cache if recipes were reloaded
        if (cachedRecipeVersion != RecipeLoader.getInstance()
            .getRecipeVersion()) {
            nextRecipe = null;
        }

        // Use cached recipe if available, otherwise search
        ModularRecipe recipe = nextRecipe;
        if (recipe == null) {
            recipe = RecipeLoader.getInstance()
                .findMatch(new String[] { recipeGroup }, getInputPorts());
        }
        nextRecipe = null; // Clear cache

        if (recipe != null) {
            List<IModularPort> energyPorts = getInputPorts(IPortType.Type.ENERGY);
            processAgent.start(recipe, getInputPorts(), energyPorts);
        }
    }

    /**
     * Diagnose why no recipe is currently running.
     * Returns an error message.
     */
    private String diagnoseRecipeIssue() {
        // Check if there are any recipes for this group
        List<ModularRecipe> recipes = RecipeLoader.getInstance()
            .getRecipes(recipeGroup);
        if (recipes.isEmpty()) {
            return "No recipes registered for group: " + recipeGroup;
        }

        // Check if we have required port types
        if (getInputPorts().isEmpty()) {
            return "No input ports connected";
        }
        if (getOutputPorts().isEmpty()) {
            return "No output ports connected";
        }

        // Try to find what's missing for each recipe
        for (ModularRecipe recipe : recipes) {
            StringBuilder missingInputs = new StringBuilder();
            StringBuilder missingOutputs = new StringBuilder();

            // Check inputs
            for (var input : recipe.getInputs()) {
                if (!input.process(getInputPorts(), true)) {
                    if (missingInputs.length() > 0) missingInputs.append(", ");
                    missingInputs.append(
                        input.getPortType()
                            .name());
                }
            }

            // If inputs are OK, check outputs
            if (missingInputs.length() == 0) {
                for (var output : recipe.getOutputs()) {
                    if (!output.process(getOutputPorts(), true)) {
                        if (missingOutputs.length() > 0) missingOutputs.append(", ");
                        missingOutputs.append(
                            output.getPortType()
                                .name());
                    }
                }

                if (missingOutputs.length() > 0) {
                    return "Output full or missing: " + missingOutputs;
                }

                // All inputs/outputs OK but recipe not starting - check energy (fallback)
                return "Energy required";
            }
        }

        // Generic message - inputs don't match any recipe
        return "No matching recipe found";
    }

    /**
     * Diagnose which output types are blocked when waiting for output.
     * Uses cached output types from ProcessAgent.
     */
    private String diagnoseBlockedOutputs() {
        // Try to use recipe first
        ModularRecipe recipe = processAgent.getCurrentRecipe();
        if (recipe != null) {
            StringBuilder blocked = new StringBuilder();
            for (var output : recipe.getOutputs()) {
                if (!output.process(getOutputPorts(), true)) {
                    if (blocked.length() > 0) blocked.append(", ");
                    blocked.append(
                        output.getPortType()
                            .name());
                }
            }
            return blocked.length() > 0 ? blocked.toString() : "Unknown";
        }

        // Fallback: use cached output types
        Set<IPortType.Type> cachedTypes = processAgent.getCachedOutputTypes();
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

    // ========== ModularUI GUI ==========
    // TODO: GUI enhance

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("machine_controller_gui");

        // Title
        panel.child(new TileWidget(getLocalizedName()));

        // Blueprint slot (right side)
        panel.child(
            new ItemSlot()
                .slot(
                    new ModularSlot(inventory, BLUEPRINT_SLOT)
                        .filter(stack -> stack != null && stack.getItem() instanceof ItemMachineBlueprint))
                .background(GuiTextures.EMPTY_SLOT)
                .pos(151, 8));

        // Status display
        panel.child(
            IKey.dynamic(this::getStatusText)
                .asWidget()
                .pos(8, 25));

        // Progress bar (Change is needed)
        panel.child(
            new ProgressWidget().progress(this::getProgressPercent)
                .texture(GuiTextures.SOLID_UP_ARROW_ICON, 20)
                .pos(80, 45));

        syncManager.bindPlayerInventory(data.getPlayer());
        panel.bindPlayerInventory();

        return panel;
    }

    /**
     * Get status text for GUI display.
     * Uses diagnose methods for detailed status information.
     */
    private String getStatusText() {
        if (customStructureName == null || customStructureName.isEmpty()) {
            return "Insert Blueprint";
        }
        if (!isFormed) {
            return "Structure Not Formed";
        }
        if (processAgent.isWaitingForOutput()) {
            // Use diagnoseBlockedOutputs for detailed info
            String blocked = diagnoseBlockedOutputs();
            return "Output Full: " + blocked;
        }
        if (processAgent.isRunning()) {
            int progress = processAgent.getProgress();
            int max = processAgent.getMaxProgress();
            return "Processing: " + String.format("%.1f", (float) progress / max * 100) + "%";
        }
        // IDLE - use diagnoseRecipeIssue for detailed info
        return diagnoseRecipeIssue();
    }

    /**
     * Get progress percentage for GUI progress bar.
     */
    private float getProgressPercent() {
        if (!processAgent.isRunning()) return 0f;
        int max = processAgent.getMaxProgress();
        if (max <= 0) return 0f;
        return (float) processAgent.getProgress() / max;
    }

    // ========== Blueprint Inventory Methods ==========

    /**
     * Get the blueprint inventory handler.
     */
    public ItemStackHandlerBase getInventory() {
        return inventory;
    }

    /**
     * Get the blueprint ItemStack from the inventory slot.
     */
    @Nullable
    public ItemStack getBlueprintStack() {
        return inventory.getStackInSlot(BLUEPRINT_SLOT);
    }

    /**
     * Get structure name from the blueprint in the slot.
     */
    @Nullable
    private String getStructureNameFromBlueprint() {
        ItemStack blueprint = getBlueprintStack();
        if (blueprint == null || blueprint.stackSize == 0) return null;
        if (!(blueprint.getItem() instanceof ItemMachineBlueprint)) return null;
        return ItemMachineBlueprint.getStructureName(blueprint);
    }

    /**
     * Update custom structure when blueprint changes.
     * Called automatically when inventory contents change.
     */
    private void updateStructureFromBlueprint() {
        String newName = getStructureNameFromBlueprint();
        if (!Objects.equals(newName, customStructureName)) {
            this.customStructureName = newName;
            updateRecipeGroupFromStructure();
            this.isFormed = false;
            clearStructureParts();
            processAgent.abort();
            if (worldObj != null) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    // ========== Getters ==========

    public List<IModularPort> getInputPorts() {
        return portManager.getInputPorts();
    }

    public List<IModularPort> getOutputPorts() {
        return portManager.getOutputPorts();
    }

    public List<IModularPort> getInputPorts(IPortType.Type type) {
        return portManager.getInputPorts(type);
    }

    public List<IModularPort> getOutputPorts(IPortType.Type type) {
        return portManager.getOutputPorts(type);
    }

    public <T extends IModularPort> List<T> getTypedInputPorts(IPortType.Type type, Class<T> portClass) {
        return portManager.getTypedInputPorts(type, portClass);
    }

    public <T extends IModularPort> List<T> getTypedOutputPorts(IPortType.Type type, Class<T> portClass) {
        return portManager.getTypedOutputPorts(type, portClass);
    }

    public <T extends IModularPort> List<T> validPorts(List<T> ports) {
        return portManager.validPorts(ports);
    }

    // ========== Item IO Helpers ==========

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

    public int getStoredInputEnergy() {
        int total = 0;
        for (AbstractEnergyIOPortTE port : getTypedInputPorts(IPortType.Type.ENERGY, AbstractEnergyIOPortTE.class)) {
            total += port.getEnergyStored();
        }
        return total;
    }

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
        if (customStructureName != null) {
            nbt.setString("customStructureName", customStructureName);
        }
        // Save blueprint inventory
        nbt.setTag("inventory", inventory.serializeNBT());
        // Save process agent
        NBTTagCompound agentNbt = new NBTTagCompound();
        processAgent.writeToNBT(agentNbt);
        nbt.setTag("processAgent", agentNbt);
        // Save ExtendedFacing
        nbt.setByte("extendedFacing", (byte) extendedFacing.ordinal());
    }

    @Override
    public void readCommon(NBTTagCompound nbt) {
        super.readCommon(nbt);
        recipeGroup = nbt.getString("recipeGroup");
        if (recipeGroup.isEmpty()) recipeGroup = "default";
        // Load blueprint inventory
        if (nbt.hasKey("inventory")) {
            inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
        }
        // Update structure from loaded blueprint
        updateStructureFromBlueprint();
        // Load process agent
        if (nbt.hasKey("processAgent")) {
            processAgent.readFromNBT(nbt.getCompoundTag("processAgent"));
        }
        // Load ExtendedFacing
        if (nbt.hasKey("extendedFacing")) {
            int ordinal = nbt.getByte("extendedFacing") & 0xFF;
            if (ordinal >= 0 && ordinal < ExtendedFacing.VALUES.length) {
                extendedFacing = ExtendedFacing.VALUES[ordinal];
            }
        }
    }

    // ========== CustomStructure ==========

    /**
     * Set the custom structure name. Also updates recipeGroup from structure.
     */
    public void setCustomStructureName(String name) {
        this.customStructureName = name;
        updateRecipeGroupFromStructure();

        // Reset structure state
        this.isFormed = false;
        clearStructureParts();

        // Notify client
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    /**
     * Get the custom structure name.
     */
    public String getCustomStructureName() {
        return customStructureName;
    }

    /**
     * Get the current recipe group.
     */
    public String getRecipeGroup() {
        return recipeGroup;
    }

    /**
     * Set the recipe group manually.
     */
    public void setRecipeGroup(String recipeGroup) {
        this.recipeGroup = recipeGroup;
    }

    /**
     * Update recipeGroup from custom structure definition.
     */
    private void updateRecipeGroupFromStructure() {
        if (customStructureName == null || customStructureName.isEmpty()) return;
        StructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(customStructureName);
        if (entry != null && entry.recipeGroup != null && !entry.recipeGroup.isEmpty()) {
            this.recipeGroup = entry.recipeGroup;
        }
    }

    /**
     * Get the custom structure properties, or null if not using custom structure.
     */
    public Properties getCustomProperties() {
        if (customStructureName == null || customStructureName.isEmpty()) return null;
        StructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(customStructureName);
        return entry != null ? entry.properties : null;
    }

    // ========== IAlignment Implementation ==========
    // TODO: In the future, load alignment limits from structure JSON config
    // to allow per-structure rotation restrictions.

    @Override
    public ExtendedFacing getExtendedFacing() {
        return extendedFacing;
    }

    @Override
    public void setExtendedFacing(ExtendedFacing facing) {
        if (facing == null) facing = ExtendedFacing.DEFAULT;
        this.extendedFacing = facing;

        // Reset structure state when facing changes
        this.isFormed = false;
        clearStructureParts();

        markDirty();
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public IAlignmentLimits getAlignmentLimits() {
        // Allow only horizontal directions (no up/down facing)
        // TODO: Load from structure JSON config in the future
        return IAlignmentLimits.UPRIGHT;
    }
}
