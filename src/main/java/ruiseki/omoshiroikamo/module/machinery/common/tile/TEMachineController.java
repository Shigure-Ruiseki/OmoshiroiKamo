package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

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

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.enums.CraftingState;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.enums.RedstoneMode;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.core.ITieredMachine;
import ruiseki.omoshiroikamo.api.recipe.error.ErrorReason;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.tileentity.AbstractMBModifierTE;
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
    implements IAlignment, IGuiHolder<PosGuiData>, IRecipeContext, IModularPort, ITieredMachine {

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
    private final ProcessAgent processAgent = new ProcessAgent(this);
    // Recipe groups - obtained from custom structure or GUI
    private List<String> recipeGroups = new ArrayList<>(Collections.singletonList("default"));
    // Look-ahead: next recipe cached during current processing
    private transient IModularRecipe nextRecipe = null;
    // Recipe version at the time nextRecipe was cached (for invalidation on reload)
    private transient int cachedRecipeVersion = -1;

    // Cached port lists to avoid per-tick allocations
    private transient List<IModularPort> cachedInputPorts = null;
    private transient List<IModularPort> cachedOutputPorts = null;

    // GUI management
    private final GuiManager guiManager = new GuiManager(this);

    private ExtendedFacing extendedFacing = ExtendedFacing.DEFAULT;

    // Redstone Control - Inherited from AbstractTE

    // External Port Error Cache (Persistent state for transient proxies)
    private final Set<ChunkCoordinates> reportedErrorPorts = new HashSet<>();

    // External Port Configurations
    private final Map<ChunkCoordinates, Map<IPortType.Type, EnumIO>> externalPortConfigs = new HashMap<>();

    public Map<ChunkCoordinates, Map<IPortType.Type, EnumIO>> getExternalPortConfigs() {
        return externalPortConfigs;
    }

    public boolean registerExternalPort(int px, int py, int pz, IPortType.Type portType, EntityPlayer player) {
        if (worldObj == null || worldObj.isRemote) return false;

        String structureName = getCustomStructureName();
        if (structureName == null || structureName.isEmpty()) {
            return false;
        }

        IStructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(structureName);
        if (entry == null) {
            return false;
        }

        java.util.Set<Character> exPorts = entry.getExternalPorts();
        if (exPorts == null || exPorts.isEmpty()) return false;

        ChunkCoordinates target = new ChunkCoordinates(px, py, pz);
        boolean isValid = false;
        Character foundSymbol = null;
        for (Character c : exPorts) {
            List<ChunkCoordinates> positions = symbolPositions.get(c);
            if (positions != null) {
                if (positions.contains(target)) {
                    isValid = true;
                    foundSymbol = c;
                    break;
                }
            }
        }

        if (!isValid) {
            return false;
        }

        // Check for fixed IO locks
        Map<Character, EnumIO> fixedPorts = entry.getFixedExternalPorts();
        if (fixedPorts.containsKey(foundSymbol)) {
            player.addChatMessage(new ChatComponentTranslation("chat.omoshiroikamo.port_io_fixed"));
            return true; // Cancel action but return true to prevent other interactions
        }

        Map<IPortType.Type, EnumIO> typeMap = externalPortConfigs.computeIfAbsent(target, k -> new HashMap<>());
        EnumIO current = typeMap.get(portType);

        if (current == null || current == EnumIO.NONE) {
            typeMap.put(portType, EnumIO.INPUT);
        } else if (current == EnumIO.INPUT) {
            typeMap.put(portType, EnumIO.OUTPUT);
        } else if (current == EnumIO.OUTPUT) {
            typeMap.put(portType, EnumIO.BOTH);
        } else {
            typeMap.remove(portType);
            if (typeMap.isEmpty()) {
                externalPortConfigs.remove(target);
            }
        }

        EnumIO newIo = typeMap.getOrDefault(portType, EnumIO.NONE);
        player.addChatMessage(
            new ChatComponentTranslation("chat.omoshiroikamo.port_io_set", portType.name(), newIo.name()));

        setFormed(false);
        clearStructureParts();
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return true;
    }

    /**
     * Checks if an error should be notified for the given port position.
     * Prevents spam by only allowing one notification per coordinate until reset.
     *
     * @param pos The position of the port
     * @return true if this is the first time an error is reported for this position
     */
    public boolean shouldNotifyError(ChunkCoordinates pos) {
        if (reportedErrorPorts.contains(pos)) return false;
        reportedErrorPorts.add(pos);
        return true;
    }

    /**
     * Reset the error cache for all ports.
     */
    public void resetErrorCache() {
        reportedErrorPorts.clear();
    }

    // Transient flag to trigger tint packet resend on load
    private boolean needsTintResend = false;

    // Track previous working state for render update
    private transient boolean wasWorking = false;

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
        Map<String, Integer> tiers = structureAgent.getComponentTiers();
        if (tiers.isEmpty()) {
            return 1;
        }
        int min = Integer.MAX_VALUE;
        for (int t : tiers.values()) {
            min = Math.min(min, t);
        }
        return min == Integer.MAX_VALUE ? 1 : min;
    }

    @Override
    public void setTier(int tier) {
        // No-op: Controller tier is calculated from structure components, not set
        // directly
    }

    public int getComponentTier(String componentName) {
        return structureAgent.getComponentTier(componentName);
    }

    public StructureAgent getStructureAgent() {
        return structureAgent;
    }

    public void setComponentTiers(Map<String, Integer> tiers) {
        structureAgent.setComponentTiers(tiers);
    }

    // ========== Structure Parts Tracking ==========

    @Override
    protected void clearStructureParts() {
        structureAgent.resetStructure();
        invalidatePortCache();
    }

    private final Map<Character, List<ChunkCoordinates>> symbolPositions = new HashMap<>();
    private final Map<ChunkCoordinates, Character> posToSymbol = new HashMap<>();

    public Map<Character, List<ChunkCoordinates>> getSymbolPositionsMap() {
        return symbolPositions;
    }

    public void clearSymbolPositions() {
        symbolPositions.clear();
        posToSymbol.clear();
    }

    public void trackSymbolPosition(char symbol, int x, int y, int z) {
        ChunkCoordinates coord = new ChunkCoordinates(x, y, z);
        posToSymbol.put(coord, symbol);
    }

    public void finalizeSymbolPosition(char symbol, int x, int y, int z) {
        ChunkCoordinates coord = new ChunkCoordinates(x, y, z);
        symbolPositions.computeIfAbsent(symbol, k -> new ArrayList<>())
            .add(coord);
    }

    public Character getSymbolAt(int x, int y, int z) {
        return posToSymbol.get(new ChunkCoordinates(x, y, z));
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
        updateRecipeGroupFromStructure();
        invalidatePortCache();
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
            if (processAgent.diagnoseBlockOutputFull(getOutputPorts())) {
                lastProcessErrorReason = ErrorReason.BLOCK_OUTPUT_FULL;
            } else {
                lastProcessErrorReason = ErrorReason.WAITING_OUTPUT;
            }
            if (processAgent.tryOutput(getOutputPorts())) {
                lastProcessErrorReason = ErrorReason.NONE;
                // Output succeeded, try to start next recipe immediately
                startNextRecipe();
            }
            return;
        }

        // If running, tick and look-ahead search for next recipe
        if (processAgent.isRunning()) {
            ConditionContext context = new ConditionContext(worldObj, xCoord, yCoord, zCoord);
            ProcessAgent.TickResult result = processAgent
                .tick(getContextualInputPorts(), getContextualOutputPorts(), context);

            if (result == ProcessAgent.TickResult.NO_ENERGY) {
                lastProcessErrorReason = ErrorReason.NO_ENERGY;
            } else if (result == ProcessAgent.TickResult.NO_MANA) {
                lastProcessErrorReason = ErrorReason.NO_MANA;
            } else if (result == ProcessAgent.TickResult.NO_INPUT) {
                lastProcessErrorReason = ErrorReason.INPUT_MISSING;
                if (isOnlyNonConsumingRecipe(processAgent.getCurrentRecipe())) {
                    processAgent.abort();
                }
            } else if (result == ProcessAgent.TickResult.PAUSED) {
                lastProcessErrorReason = ErrorReason.PAUSED;
            } else if (result == ProcessAgent.TickResult.OUTPUT_FULL) {
                lastProcessErrorReason = ErrorReason.OUTPUT_FULL;
            } else if (result == ProcessAgent.TickResult.BLOCK_MISSING) {
                lastProcessErrorReason = ErrorReason.BLOCK_MISSING;
                if (isOnlyNonConsumingRecipe(processAgent.getCurrentRecipe())) {
                    processAgent.abort();
                }
            } else {
                lastProcessErrorReason = ErrorReason.NONE;
            }

            // Look-ahead: search for next recipe while processing (only once)
            if (nextRecipe == null) {
                nextRecipe = RecipeLoader.getInstance()
                    .findMatch(recipeGroups.toArray(new String[0]), getContextualInputPorts());
                cachedRecipeVersion = RecipeLoader.getInstance()
                    .getRecipeVersion();
            }

            // If complete, immediately try to output and start next
            if (result == ProcessAgent.TickResult.READY_OUTPUT) {
                if (processAgent.tryOutput(getContextualOutputPorts())) {
                    lastProcessErrorReason = ErrorReason.NONE;
                    startNextRecipe();
                }
            }
            return;
        }

        // IDLE: Try to start new recipe
        startNextRecipe();

        // Update render if working state changed
        boolean currentlyWorking = processAgent.isRunning();
        if (currentlyWorking != wasWorking) {
            wasWorking = currentlyWorking;
            if (worldObj != null && !worldObj.isRemote) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    private void startNextRecipe() {
        // Invalidate cache if recipes were reloaded
        if (cachedRecipeVersion != RecipeLoader.getInstance()
            .getRecipeVersion()) {
            nextRecipe = null;
        }

        // Use cached recipe if available, otherwise search
        IModularRecipe recipe = nextRecipe;
        if (recipe == null) {
            String[] groups = recipeGroups.toArray(new String[0]);
            List<IModularPort> inputs = getContextualInputPorts();
            recipe = RecipeLoader.getInstance()
                .findMatch(groups, inputs);
        }
        nextRecipe = null; // Clear cache

        if (recipe != null) {
            // Check if output fits before starting
            IPortType.Type insufficientType = recipe.checkOutputCapacity(getContextualOutputPorts());
            if (insufficientType != null) {
                setProcessError(
                    ErrorReason.OUTPUT_CAPACITY_INSUFFICIENT,
                    LibMisc.LANG.localize("gui.port_type." + insufficientType.name()));
                return;
            }

            if (!recipe.canOutput(getContextualOutputPorts())) {
                setProcessError(ErrorReason.OUTPUT_FULL);
                return;
            }

            if (processAgent.startRecipe(recipe, getContextualInputPorts(), getContextualOutputPorts()))
                lastProcessErrorReason = ErrorReason.NONE;
            else lastProcessErrorReason = ErrorReason.NO_INPUT;
        } else {
            lastProcessErrorReason = ErrorReason.NO_MATCHING_RECIPE;

            // Check if it's actually NO_INPUT
            if (processAgent.diagnoseIdle(getContextualInputPorts()) == ProcessAgent.TickResult.NO_INPUT) {
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

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    public List<IModularPort> getContextualInputPorts() {
        if (cachedInputPorts == null) {
            cachedInputPorts = new ArrayList<>(getInputPorts());
            cachedInputPorts.add(this);
        }
        return cachedInputPorts;
    }

    public List<IModularPort> getContextualOutputPorts() {
        if (cachedOutputPorts == null) {
            cachedOutputPorts = new ArrayList<>(getOutputPorts());
            cachedOutputPorts.add(this);
        }
        return cachedOutputPorts;
    }

    public void invalidatePortCache() {
        this.cachedInputPorts = null;
        this.cachedOutputPorts = null;
        this.nextRecipe = null;
    }

    // ========== IModularPort Implementation ==========

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.BLOCK;
    }

    @Override
    public IPortType.Direction getPortDirection() {
        return IPortType.Direction.NONE;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        // No-op for the controller itself when acting as a port
    }

    @Override
    public EnumIO getSideIO(ForgeDirection side) {
        return side == extendedFacing.getDirection() ? EnumIO.BOTH : EnumIO.NONE;
    }

    @Override
    public void setSideIO(ForgeDirection side, EnumIO state) {
        // No-op
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
    private boolean isOnlyNonConsumingRecipe(IModularRecipe recipe) {
        if (recipe == null) return false;
        List<IRecipeInput> inputs = recipe.getInputs();
        if (inputs.isEmpty()) return false;
        for (IRecipeInput input : inputs) {
            if (input.isConsume()) return false;
        }
        return true;
    }

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
        resetErrorCache();
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
    public void writeCommon(NBTTagCompound nbt) {
        super.writeCommon(nbt);

        NBTTagList groupList = new NBTTagList();
        for (String group : recipeGroups) {
            groupList.appendTag(new NBTTagString(group));
        }
        nbt.setTag("recipeGroups", groupList);
        structureAgent.writeToNBT(nbt);
        // Save blueprint inventory
        nbt.setTag("inventory", inventory.serializeNBT());
        // Save process agent
        NBTTagCompound agentNbt = new NBTTagCompound();
        processAgent.writeToNBT(agentNbt);
        nbt.setTag("processAgent", agentNbt);
        // Save ExtendedFacing
        nbt.setByte("extendedFacing", (byte) extendedFacing.ordinal());
        // Save External Port Configs
        if (!externalPortConfigs.isEmpty()) {
            NBTTagList portList = new NBTTagList();
            for (Map.Entry<ChunkCoordinates, Map<IPortType.Type, EnumIO>> entry : externalPortConfigs.entrySet()) {
                NBTTagCompound portTag = new NBTTagCompound();
                portTag.setInteger("x", entry.getKey().posX);
                portTag.setInteger("y", entry.getKey().posY);
                portTag.setInteger("z", entry.getKey().posZ);

                NBTTagList typeList = new NBTTagList();
                for (Map.Entry<IPortType.Type, EnumIO> typeEntry : entry.getValue()
                    .entrySet()) {
                    NBTTagCompound typeTag = new NBTTagCompound();
                    typeTag.setByte(
                        "type",
                        (byte) typeEntry.getKey()
                            .ordinal());
                    typeTag.setByte(
                        "io",
                        (byte) typeEntry.getValue()
                            .ordinal());
                    typeList.appendTag(typeTag);
                }
                portTag.setTag("types", typeList);
                portList.appendTag(portTag);
            }
            nbt.setTag("externalPortConfigs", portList);
        }
    }

    @Override
    public void readCommon(NBTTagCompound nbt) {
        if (nbt == null) {
            return;
        }
        super.readCommon(nbt);

        recipeGroups = new ArrayList<>();
        if (nbt.hasKey("recipeGroups", 9)) {
            NBTTagList list = nbt.getTagList("recipeGroups", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                recipeGroups.add(list.getStringTagAt(i));
            }
        } else if (nbt.hasKey("recipeGroup")) {
            String group = nbt.getString("recipeGroup");
            if (!group.isEmpty()) recipeGroups.add(group);
        }
        if (recipeGroups.isEmpty()) recipeGroups.add("default");
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
        // Force update recipe groups to apply any JSON changes to existing multiblocks
        updateRecipeGroupFromStructure();
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

        // Load External Port Configs
        externalPortConfigs.clear();
        if (nbt.hasKey("externalPortConfigs", 9)) {
            NBTTagList portList = nbt.getTagList("externalPortConfigs", 10);
            for (int i = 0; i < portList.tagCount(); i++) {
                NBTTagCompound portTag = portList.getCompoundTagAt(i);
                ChunkCoordinates coords = new ChunkCoordinates(
                    portTag.getInteger("x"),
                    portTag.getInteger("y"),
                    portTag.getInteger("z"));

                Map<IPortType.Type, EnumIO> typeMap = new HashMap<>();
                if (portTag.hasKey("types", 9)) {
                    NBTTagList typeList = portTag.getTagList("types", 10);
                    for (int j = 0; j < typeList.tagCount(); j++) {
                        NBTTagCompound typeTag = typeList.getCompoundTagAt(j);
                        int typeOrdinal = typeTag.getByte("type") & 0xFF;
                        int ioOrdinal = typeTag.getByte("io") & 0xFF;
                        if (typeOrdinal < IPortType.Type.values().length && ioOrdinal < EnumIO.values().length) {
                            typeMap.put(IPortType.Type.values()[typeOrdinal], EnumIO.values()[ioOrdinal]);
                        }
                    }
                } else if (portTag.hasKey("io")) {
                    // Legacy support
                    int ordinal = portTag.getByte("io") & 0xFF;
                    if (ordinal < EnumIO.values().length) {
                        typeMap.put(IPortType.Type.ITEM, EnumIO.values()[ordinal]);
                    }
                }

                if (!typeMap.isEmpty()) {
                    externalPortConfigs.put(coords, typeMap);
                }
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
            IStructureEntry entry = StructureManager.getInstance()
                .getCustomStructure(name);
            if (entry != null && entry.getDisplayName() != null
                && !entry.getDisplayName()
                    .isEmpty()) {
                return entry.getDisplayName();
            }
        }
        return name;
    }

    /**
     * Get the primary recipe group.
     */
    public String getRecipeGroup() {
        return recipeGroups.isEmpty() ? "default" : recipeGroups.get(0);
    }

    /**
     * Get all recipe groups.
     */
    public List<String> getRecipeGroups() {
        return recipeGroups;
    }

    /**
     * Set the recipe groups manually (single group).
     */
    public void setRecipeGroup(String recipeGroup) {
        this.recipeGroups = new ArrayList<>(Collections.singletonList(recipeGroup));
    }

    /**
     * Set the recipe groups manually.
     */
    public void setRecipeGroups(List<String> recipeGroups) {
        this.recipeGroups = new ArrayList<>(recipeGroups);
    }

    /**
     * Update recipeGroup from custom structure definition.
     */
    private void updateRecipeGroupFromStructure() {
        if (structureAgent.getCustomStructureName() == null || structureAgent.getCustomStructureName()
            .isEmpty()) {
            this.recipeGroups = new ArrayList<>(Collections.singletonList("default"));
            return;
        }
        IStructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(structureAgent.getCustomStructureName());
        if (entry != null && entry.getRecipeGroup() != null
            && !entry.getRecipeGroup()
                .isEmpty()) {
            this.recipeGroups = new ArrayList<>(entry.getRecipeGroup());
        } else {
            this.recipeGroups = new ArrayList<>(Collections.singletonList("default"));
        }
    }

    /**
     * Get the custom structure properties, or null if not using custom structure.
     */
    public IStructureEntry getCustomProperties() {
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

    public void setLastValidationError(String error) {
        structureAgent.setLastValidationError(error);
    }

    public boolean isPhysicallyValid() {
        return structureAgent.isPhysicallyValid();
    }

    public void setPhysicallyValid(boolean valid) {
        structureAgent.setPhysicallyValid(valid);
    }

    // ========== IRecipeContext Implementation ==========

    @Override
    public World getWorld() {
        return worldObj;
    }

    @Override
    public ChunkCoordinates getControllerPos() {
        return new ChunkCoordinates(xCoord, yCoord, zCoord);
    }

    @Override
    public IStructureEntry getCurrentStructure() {
        String structureName = structureAgent.getCustomStructureName();
        if (structureName == null || structureName.isEmpty()) {
            return null;
        }
        return StructureManager.getInstance()
            .getCustomStructure(structureName);
    }

    @Override
    public ForgeDirection getFacing() {
        return extendedFacing.getDirection();
    }

    @Override
    public List<ChunkCoordinates> getSymbolPositions(char symbol) {
        List<ChunkCoordinates> pos = symbolPositions.getOrDefault(symbol, new ArrayList<>());
        return pos;
    }

    @Override
    public ConditionContext getConditionContext() {
        return new ConditionContext(worldObj, xCoord, yCoord, zCoord, this);
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
                // Change overlay based on working state
                boolean isWorking = processAgent != null && processAgent.isRunning();
                return isWorking ? controllerBlock.getOverlayIconActive() : controllerBlock.getOverlayIcon();
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

            // Trigger immediate structure check on neighbor change
            if (getCustomStructureName() != null && !getCustomStructureName().isEmpty()) {
                structureAgent.forceStructureCheck();
            }
        }
    }

}
