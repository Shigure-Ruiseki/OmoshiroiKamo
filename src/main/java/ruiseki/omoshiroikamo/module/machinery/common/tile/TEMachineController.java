package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.structurelib.alignment.IAlignment;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.block.CraftingState;
import ruiseki.omoshiroikamo.api.block.RedstoneMode;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.ISidedTexture;
import ruiseki.omoshiroikamo.api.modular.recipe.ErrorReason;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.Properties;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineController;
import ruiseki.omoshiroikamo.module.machinery.common.item.ItemMachineBlueprint;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.ProcessAgent;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;

/**
 * Corresponds to the 'Q' symbol in structure definitions.
 * The controller reads the structure name from the inserted blueprint
 * and validates the surrounding blocks against that structure definition.
 * TODO: Improve holo indicator
 */
public class TEMachineController extends AbstractMBModifierTE
    implements IAlignment, IGuiHolder<PosGuiData>, ISidedTexture {

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

    // Structure management
    private final StructureAgent structureAgent = new StructureAgent(this);

    // Recipe processing
    private final ProcessAgent processAgent = new ProcessAgent();
    // Recipe group - obtained from custom structure or GUI
    private String recipeGroup = "default";
    // Look-ahead: next recipe cached during current processing
    private transient ModularRecipe nextRecipe = null;
    // Recipe version at the time nextRecipe was cached (for invalidation on reload)
    private transient int cachedRecipeVersion = -1;

    // GUI management
    private final GuiManager guiManager = new GuiManager(this);

    private ExtendedFacing extendedFacing = ExtendedFacing.DEFAULT;

    // Redstone Control - Inherited from AbstractTE

    // Transient flag to trigger tint packet resend on load
    private boolean needsTintResend = false;

    public TEMachineController() {
        super();
    }

    // ========== Structure Definition ==========

    @Override
    public IStructureDefinition<TEMachineController> getStructureDefinition() {
        return structureAgent.getStructureDefinition();
    }

    @Override
    public int[][] getOffSet() {
        return structureAgent.getOffSet();
    }

    @Override
    public String getStructurePieceName() {
        return structureAgent.getStructurePieceName();
    }

    @Override
    public int getTier() {
        // TODO: recognize tier by block type, structure change, or controller
        return 1;
    }

    // ========== Structure Parts Tracking ==========

    @Override
    protected void clearStructureParts() {
        structureAgent.resetStructure();
    }

    @Override
    protected boolean addToMachine(Block block, int meta, int x, int y, int z) {
        return structureAgent.addToMachine(block, meta, x, y, z);
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
        // Also add the port's position to structure block positions
        if (port instanceof TileEntity) {
            TileEntity te = (TileEntity) port;
            structureAgent.addStructurePosition(te.xCoord, te.yCoord, te.zCoord);
        }
    }

    /**
     * Called by BlockResolver to track all structure blocks (casings, etc.)
     * for tinting purposes.
     */
    public void trackStructureBlock(int x, int y, int z) {
        structureAgent.addStructurePosition(x, y, z);
    }

    // ========== Crafting Configuration ==========
    // We disable AbstractMachineTE's built-in crafting logic to rely solely on
    // ProcessAgent.

    @Override
    public boolean canStartCrafting() {
        return false;
    }

    @Override
    protected boolean canContinueCrafting() {
        return false;
    }

    @Override
    protected CraftingState updateCraftingState() {
        return CraftingState.IDLE;
    }

    // Last process error reason for GUI display
    private ErrorReason lastProcessErrorReason = ErrorReason.NONE;
    private String lastProcessErrorDetail = null;

    public String getLastProcessErrorDetail() {
        return lastProcessErrorDetail;
    }

    @Override
    protected boolean structureCheck(String piece, int ox, int oy, int oz) {
        return structureAgent.structureCheck(piece, ox, oy, oz);
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
        structureAgent.onFormed();
    }

    /**
     * Main update loop.
     * Blueprint is required for the controller to operate.
     * Uses StructureLib for structure validation when blueprint is set.
     */
    @Override
    public void doUpdate() {
        // Resend tint packet on load/restore
        if (needsTintResend && worldObj != null && !worldObj.isRemote) {
            structureAgent.sendTintPacket();
            needsTintResend = false;
        }

        // Sync customStructureName from blueprint if blueprint is present in GUI
        String blueprintName = getStructureNameFromBlueprint();
        String currentName = structureAgent.getCustomStructureName();

        // Update if blueprint changed (including removal)
        if (!Objects.equals(blueprintName, currentName)) {
            structureAgent.setCustomStructureName(blueprintName);
            setFormed(false);
            clearStructureParts();
            processAgent.abort();
            markDirty();

            if (blueprintName != null && !blueprintName.isEmpty()) {
                updateRecipeGroupFromStructure();
            }
        }

        // Blueprint required - no operation without it
        if (structureAgent.getCustomStructureName() == null || structureAgent.getCustomStructureName()
            .isEmpty()) {
            lastProcessErrorReason = ErrorReason.MISSING_BLUEPRINT;
            return;
        }

        // Use StructureLib-based structure checking (calls super.doUpdate())
        // Super handles IC2 registration, energy sync, and structure validation
        super.doUpdate();

        if (worldObj.isRemote) {
            return;
        }
        // Check Redstone signal for suspension
        if (!isRedstoneActive()) {
            lastProcessErrorReason = ErrorReason.PAUSED;
            return;
        }

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
            ProcessAgent.TickResult result = processAgent.tick(getInputPorts(), getOutputPorts());

            if (result == ProcessAgent.TickResult.NO_ENERGY) {
                lastProcessErrorReason = ErrorReason.NO_ENERGY;
            } else if (result == ProcessAgent.TickResult.NO_MANA) {
                lastProcessErrorReason = ErrorReason.NO_MANA;
            } else if (result == ProcessAgent.TickResult.NO_INPUT) {
                lastProcessErrorReason = ErrorReason.INPUT_MISSING;
            } else if (result == ProcessAgent.TickResult.PAUSED) {
                lastProcessErrorReason = ErrorReason.PAUSED;
            } else if (result == ProcessAgent.TickResult.OUTPUT_FULL) {
                lastProcessErrorReason = ErrorReason.OUTPUT_FULL;
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
            // Check if output fits before starting
            IPortType.Type insufficientType = recipe.checkOutputCapacity(getOutputPorts());
            if (insufficientType != null) {
                setProcessError(
                    ErrorReason.OUTPUT_CAPACITY_INSUFFICIENT,
                    LibMisc.LANG.localize("gui.port_type." + insufficientType.name()));
                return;
            }

            if (!recipe.canOutput(getOutputPorts())) {
                setProcessError(ErrorReason.OUTPUT_FULL);
                return;
            }

            List<IModularPort> energyPorts = getInputPorts(IPortType.Type.ENERGY);
            processAgent.start(recipe, getInputPorts(), energyPorts);
            lastProcessErrorReason = ErrorReason.NONE;
        } else {
            lastProcessErrorReason = ErrorReason.NO_MATCHING_RECIPE;

            // Check if it's actually NO_INPUT
            if (processAgent.diagnoseIdle(getInputPorts()) == ProcessAgent.TickResult.NO_INPUT) {
                lastProcessErrorReason = ErrorReason.INPUT_MISSING;
            }
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
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(LibMisc.MOD_ID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return guiManager.buildUI(data, syncManager, settings);
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
        if (!Objects.equals(newName, structureAgent.getCustomStructureName())) {
            structureAgent.setCustomStructureName(newName);
            updateRecipeGroupFromStructure();
            setFormed(false);
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
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setString("recipeGroup", recipeGroup);
        structureAgent.writeToNBT(nbt);
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
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        recipeGroup = nbt.getString("recipeGroup");
        if (recipeGroup.isEmpty()) recipeGroup = "default";
        // Load blueprint inventory
        if (nbt.hasKey("inventory")) {
            inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
        }
        if (nbt.hasKey("inventory")) {
            inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
        }

        // Load structure data and positions
        if (structureAgent.readFromNBT(nbt)) {
            needsTintResend = true;
        }

        // Update structure from loaded blueprint
        updateStructureFromBlueprint();
        // Load process agent
        if (nbt.hasKey("processAgent")) {
            processAgent.readFromNBT(nbt.getCompoundTag("processAgent"));
        }
        // Load ExtendedFacing
        ExtendedFacing previousFacing = this.extendedFacing;
        if (nbt.hasKey("extendedFacing")) {
            int ordinal = nbt.getByte("extendedFacing") & 0xFF;
            if (ordinal < ExtendedFacing.VALUES.length) {
                extendedFacing = ExtendedFacing.VALUES[ordinal];
            }
        }

        if (worldObj != null && worldObj.isRemote && !Objects.equals(previousFacing, extendedFacing)) {
            worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        }
    }

    // ========== Redstone Control ==========

    public boolean isRedstonePowered() {
        return this.redstonePowered;
    }

    public void setRedstonePowered(boolean powered) {
        this.redstonePowered = powered;
    }

    @Override
    public boolean isRedstoneActive() {
        return RedstoneMode.isActive(redstoneMode, redstonePowered);
    }

    // ========== CustomStructure ==========

    /**
     * Set the custom structure name. Also updates recipeGroup from structure.
     */
    public void setCustomStructureName(String name) {
        structureAgent.setCustomStructureName(name);
        updateRecipeGroupFromStructure();

        // Reset structure state
        setFormed(false);
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
        return structureAgent.getCustomStructureName();
    }

    /**
     * Get the custom structure display name.
     */
    public String getCustomStructureDisplayName() {
        String name = getCustomStructureName();
        if (name != null && !name.isEmpty()) {
            StructureEntry entry = StructureManager.getInstance()
                .getCustomStructure(name);
            if (entry != null && entry.displayName != null && !entry.displayName.isEmpty()) {
                return entry.displayName;
            }
        }
        return name;
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
        if (structureAgent.getCustomStructureName() == null || structureAgent.getCustomStructureName()
            .isEmpty()) {
            return;
        }
        StructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(structureAgent.getCustomStructureName());
        if (entry != null && entry.recipeGroup != null && !entry.recipeGroup.isEmpty()) {
            this.recipeGroup = entry.recipeGroup;
        }
    }

    /**
     * Get the custom structure properties, or null if not using custom structure.
     */
    public Properties getCustomProperties() {
        return structureAgent.getCustomProperties();
    }

    // ========== Structure Tinting ==========

    /**
     * Get cached structure tint color.
     * Used by BlockMachineController's colorMultiplier.
     *
     * @return ARGB color value, or null if no color defined
     */
    public Integer getCachedStructureTintColor() {
        return structureAgent.getCachedStructureTintColor();
    }

    /**
     * Get the tint color from this controller's structure properties.
     * Returns null if not formed or no color is defined.
     *
     * @return ARGB color value, or null if no color defined
     */
    public Integer getStructureTintColor() {
        return structureAgent.getStructureTintColor();
    }

    // ========== IAlignment Implementation ==========
    // TODO: load rotation limits from config.

    @Override
    public ExtendedFacing getExtendedFacing() {
        return extendedFacing;
    }

    @Override
    public void setExtendedFacing(ExtendedFacing facing) {
        if (facing == null) facing = ExtendedFacing.DEFAULT;
        this.extendedFacing = facing;

        // Reset structure state when facing changes
        setFormed(false);
        clearStructureParts();

        markDirty();
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public IAlignmentLimits getAlignmentLimits() {
        // Allow all directions
        return IAlignmentLimits.UNLIMITED;
    }

    // ========== Package Accessors for Managers ==========

    public PortManager getPortManager() {
        return portManager;
    }

    public ProcessAgent getProcessAgent() {
        return processAgent;
    }

    public ErrorReason getLastProcessErrorReason() {
        return lastProcessErrorReason;
    }

    public void setLastProcessErrorReason(ErrorReason reason) {
        this.lastProcessErrorReason = reason;
        this.lastProcessErrorDetail = null; // Reset detail when setting reason directly
    }

    public void setLastProcessErrorDetail(String detail) {
        this.lastProcessErrorDetail = detail;
    }

    public void setProcessError(ErrorReason reason) {
        setProcessError(reason, null);
    }

    public void setProcessError(ErrorReason reason, String detail) {
        this.lastProcessErrorReason = reason;
        this.lastProcessErrorDetail = detail;
    }

    public boolean isFormed() {
        return isFormed;
    }

    public void setFormed(boolean formed) {
        this.isFormed = formed;
    }

    public String getLastValidationError() {
        return structureAgent.getLastValidationError();
    }

    // ========== ISidedTexture Implementation ==========

    @Override
    public IIcon getTexture(ForgeDirection side, int pass) {
        if (pass == 1) { // Overlay pass
            if (side != extendedFacing.getDirection()) {
                return null;
            }
            Block block = getBlockType();
            if (block instanceof BlockMachineController) {
                BlockMachineController controllerBlock = (BlockMachineController) block;
                return controllerBlock.getOverlayIcon();
            }
        }
        return null; // Base pass handled by ISBRH using standard block render
    }

    @Override
    public void invalidate() {
        super.invalidate();
        structureAgent.resetStructure();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        structureAgent.resetStructure();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
        if (!world.isRemote) {
            boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
            if (this.redstonePowered != powered) {
                this.redstonePowered = powered;
                this.redstoneStateDirty = true;
                this.forceClientUpdate = true;
            }
        }
    }

}
