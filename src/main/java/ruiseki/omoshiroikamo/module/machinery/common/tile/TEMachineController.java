package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
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
import ruiseki.omoshiroikamo.api.modular.recipe.ErrorReason;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
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

/**
 * Machine Controller TileEntity - manages a Modular Machinery multiblock.
 * Extends AbstractMBModifierTE to leverage existing structure validation and
 * crafting logic.
 * Corresponds to the 'Q' symbol in structure definitions.
 *
 * Blueprint slot provides GUI-based structure selection.
 * The controller reads the structure name from the inserted blueprint
 * and validates the surrounding blocks against that structure definition.
 * TODO: Do not consume blueprint when auto-construct
 * TODO: Prevent NBT pickup on controller middle-click
 * TODO: Support shift-click
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

    // Tint color for structure blocks (loaded from structure properties)
    private Integer structureTintColor = null;

    // Structure block positions tracking (for tint cache management)
    private final Set<ChunkCoordinates> structureBlockPositions = new HashSet<>();

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
        // Clear cache
        StructureTintCache.clearAll(worldObj, structureBlockPositions);

        // Trigger block updates (to reset colors)
        for (ChunkCoordinates pos : structureBlockPositions) {
            if (worldObj != null) {
                worldObj.markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
            }
        }

        structureBlockPositions.clear();
        portManager.clear();
    }

    @Override
    protected boolean addToMachine(Block block, int meta, int x, int y, int z) {
        // Track all structure block positions
        structureBlockPositions.add(new ChunkCoordinates(x, y, z));

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

    // Last process error reason for GUI display
    private ErrorReason lastProcessErrorReason = ErrorReason.NONE;

    @Override
    protected boolean structureCheck(String piece, int ox, int oy, int oz) {
        // Clear previous error
        lastValidationError = "";

        // Clear structure parts before checking
        clearStructureParts();

        // Use controller's facing for rotation support
        boolean valid = getStructureDefinition()
            .check(this, piece, worldObj, getExtendedFacing(), xCoord, yCoord, zCoord, ox, oy, oz, false);

        if (valid && !isFormed) {
            isFormed = true;
            onFormed();
        } else if (!valid && isFormed) {
            isFormed = false;
            structureTintColor = null;
            updateStructureBlocksRendering();
        }

        if (valid && isFormed) {
            // Perform additional requirements check for CustomStructure
            if (!checkRequirements()) {
                lastValidationError = "Structure requirements not met.";
                isFormed = false;
                clearStructureParts();
                return false;
            }
        } else if (!valid) {
            // If check failed, we don't know exactly why
            // but usually it means blocks don't match.
            if (customStructureName != null) {
                lastValidationError = "Block mismatch or incomplete structure.";
            }
        }

        return isFormed;
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
        // Load structure tint color
        structureTintColor = getStructureTintColor();

        // Cache tint color for all structure blocks
        if (structureTintColor != null) {
            for (ChunkCoordinates pos : structureBlockPositions) {
                StructureTintCache.put(worldObj, pos.posX, pos.posY, pos.posZ, structureTintColor);
            }
        }

        // Add controller itself to cache
        if (structureTintColor != null) {
            StructureTintCache.put(worldObj, xCoord, yCoord, zCoord, structureTintColor);
        }

        // Trigger block updates
        updateStructureBlocksRendering();
    }

    /**
     * Main update loop.
     * Blueprint is required for the controller to operate.
     * Uses StructureLib for structure validation when blueprint is set.
     */
    @Override
    public void doUpdate() {
        // Sync customStructureName from blueprint if blueprint is present in GUI
        // Only update if blueprint has a structure name
        String blueprintName = getStructureNameFromBlueprint();
        if (blueprintName != null && !blueprintName.isEmpty() && !Objects.equals(blueprintName, customStructureName)) {
            this.customStructureName = blueprintName;
            updateRecipeGroupFromStructure();
            this.isFormed = false;
            clearStructureParts();
            processAgent.abort();
            markDirty();
        }

        // Blueprint required - no operation without it
        if (customStructureName == null || customStructureName.isEmpty()) return;

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
            lastProcessErrorReason = ErrorReason.WAITING_OUTPUT;
            if (processAgent.tryOutput(getOutputPorts())) {
                lastProcessErrorReason = ErrorReason.NONE;
                // Output succeeded, try to start next recipe immediately
                startNextRecipe();
            }
            return;
        }

        // If running, tick and look-ahead search for next recipe
        if (processAgent.isRunning()) {
            List<IModularPort> energyPorts = getInputPorts(IPortType.Type.ENERGY);
            ProcessAgent.TickResult result = processAgent.tick(energyPorts);

            if (result == ProcessAgent.TickResult.NO_ENERGY) {
                lastProcessErrorReason = ErrorReason.NO_ENERGY;
            } else {
                lastProcessErrorReason = ErrorReason.NONE;
            }

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
                    lastProcessErrorReason = ErrorReason.NONE;
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
            lastProcessErrorReason = ErrorReason.NONE;
        } else {
            lastProcessErrorReason = ErrorReason.NO_MATCHING_RECIPE;
        }
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
                .background(OKGuiTextures.EMPTY_SLOT)
                .pos(151, 8));

        // Status display
        panel.child(
            IKey.dynamic(this::getStatusText)
                .asWidget()
                .pos(8, 25));

        // Error display (validation)
        panel.child(
            IKey.dynamic(this::getErrorText)
                .asWidget()
                .pos(8, 35));

        IntSyncValue progressSyncer = new IntSyncValue(processAgent::getProgress, processAgent::setProgress);
        IntSyncValue maxProgressSyncer = new IntSyncValue(processAgent::getMaxProgress, processAgent::setMaxProgress);
        BooleanSyncValue runningSyncer = new BooleanSyncValue(processAgent::isRunning, processAgent::setRunning);
        BooleanSyncValue waitingSyncer = new BooleanSyncValue(
            processAgent::isWaitingForOutput,
            processAgent::setWaitingForOutput);

        syncManager.syncValue("processProgressSyncer", progressSyncer);
        syncManager.syncValue("processMaxSyncer", maxProgressSyncer);
        syncManager.syncValue("processRunningSyncer", runningSyncer);
        syncManager.syncValue("processWaitingSyncer", waitingSyncer);

        // Progress bar (Change is needed)
        panel.child(
            new ProgressWidget().value(new DoubleSyncValue(() -> (double) processAgent.getProgressPercent()))
                .texture(OKGuiTextures.SOLID_UP_ARROW_ICON, 20)
                .pos(80, 45));

        syncManager.bindPlayerInventory(data.getPlayer());
        panel.bindPlayerInventory();

        return panel;
    }

    /**
     * Get status text for GUI display.
     * Delegates to ProcessAgent for detailed status information.
     */
    private String getStatusText() {
        if (customStructureName == null || customStructureName.isEmpty()) {
            return "Insert Blueprint";
        }
        if (!isFormed) {
            return "Structure Not Formed";
        }
        if (processAgent.isRunning() || processAgent.isWaitingForOutput()) {
            if (lastProcessErrorReason == ErrorReason.NO_ENERGY) {
                return ErrorReason.NO_ENERGY.getMessage();
            }
            return processAgent.getStatusMessage(getOutputPorts());
        }

        ErrorReason reason = getIdleErrorReason();
        if (reason == ErrorReason.NONE) {
            return "Idle";
        }
        return reason.getMessage();
    }

    private ErrorReason getIdleErrorReason() {
        if (getInputPorts().isEmpty()) return ErrorReason.NO_INPUT_PORTS;
        if (getOutputPorts().isEmpty()) return ErrorReason.NO_OUTPUT_PORTS;
        if (RecipeLoader.getInstance()
            .getRecipes(recipeGroup)
            .isEmpty()) {
            return ErrorReason.NO_RECIPES;
        }
        if (lastProcessErrorReason == ErrorReason.NO_MATCHING_RECIPE) {
            return ErrorReason.NO_MATCHING_RECIPE;
        }
        if (lastProcessErrorReason == ErrorReason.NO_ENERGY) {
            return ErrorReason.NO_ENERGY;
        }
        return ErrorReason.IDLE;
    }

    /**
     * Get validation error text for GUI display.
     */
    private String getErrorText() {
        if (customStructureName == null || customStructureName.isEmpty()) return "";
        if (isFormed) return "";
        if (lastValidationError == null || lastValidationError.isEmpty()) return "";
        return lastValidationError;
    }

    /**
     * Get progress percentage for GUI progress bar.
     */
    private float getProgressPercent() {
        return processAgent.getProgressPercent();
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
     * Only processes on server side to ensure proper sync.
     */
    private void updateStructureFromBlueprint() {
        // Skip if on client side
        if (worldObj != null && worldObj.isRemote) return;

        String newName = getStructureNameFromBlueprint();
        if (!Objects.equals(newName, customStructureName)) {
            this.customStructureName = newName;
            updateRecipeGroupFromStructure();
            this.isFormed = false;
            clearStructureParts();
            processAgent.abort();
            lastProcessErrorReason = ErrorReason.NONE;
            markDirty();
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
        lastProcessErrorReason = ErrorReason.NONE;

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

    // ========== Structure Tinting ==========

    /**
     * Get cached structure tint color.
     * Used by BlockMachineController's colorMultiplier.
     * 
     * @return ARGB color value, or null if no color defined
     */
    public Integer getCachedStructureTintColor() {
        return structureTintColor;
    }

    /**
     * Get the tint color from this controller's structure properties.
     * Returns null if not formed or no color is defined.
     * 
     * @return ARGB color value, or null if no color defined
     */
    public Integer getStructureTintColor() {
        if (!isFormed || customStructureName == null) {
            return null;
        }

        Properties props = getCustomProperties();

        if (props != null && props.tintColor != null) {
            try {
                String hex = props.tintColor.replace("#", "");
                int color = (int) Long.parseLong(hex, 16) | 0xFF000000;
                return color;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * Update rendering for all blocks in the structure.
     * Called when structure is formed or unformed to apply/remove tint.
     */
    private void updateStructureBlocksRendering() {
        if (worldObj == null || worldObj.isRemote) return;

        // Update all structure blocks
        for (ChunkCoordinates pos : structureBlockPositions) {
            worldObj.markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
        }

        // Update controller itself
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        // Allow all directions
        // TODO: Load from structure JSON config in the future
        return IAlignmentLimits.UNLIMITED;
    }
}
