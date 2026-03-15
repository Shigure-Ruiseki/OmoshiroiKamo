package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.PortRegistrationVisitor;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.core.TieredBlockMapping;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketStructureTint;

/**
 * Handles structure-related logic for {@link TEMachineController}.
 */
public class StructureAgent {

    private final TEMachineController controller;

    private String customStructureName = null;
    private Integer structureTintColor = null;
    private final Map<String, Integer> componentTiers = new HashMap<>();
    private final Set<ChunkCoordinates> structureBlockPositions = new HashSet<>();
    private String lastValidationError = "";
    private boolean isPhysicallyValid = false;

    public StructureAgent(TEMachineController controller) {
        this.controller = Objects.requireNonNull(controller, "controller");
    }

    // ========== Structure Definition ==========

    public IStructureDefinition<TEMachineController> getStructureDefinition() {
        // Check for custom structure first
        if (customStructureName != null && !customStructureName.isEmpty()) {
            IStructureDefinition<TEMachineController> customDef = CustomStructureRegistry
                .getDefinition(customStructureName);
            if (customDef != null) {
                return customDef;
            }
        }
        return null;
    }

    public int[][] getOffSet() {
        // Get offset from custom structure registry
        if (customStructureName != null && !customStructureName.isEmpty()) {
            int[] offset = CustomStructureRegistry.getControllerOffset(customStructureName);
            if (offset != null) {
                return new int[][] { offset };
            }
        }
        // Default offset (no structure)
        return new int[][] { { 0, 0, 0 } };
    }

    public String getStructurePieceName() {
        // CustomStructureRegistry registers shapes using the structure name
        return customStructureName != null ? customStructureName : "main";
    }

    // ========== Structure Parts Tracking ==========

    public void resetStructure() {
        if (controller.getWorldObj() == null) {
            clearInternalData();
            return;
        }

        // Clear cache ONLY on server-side
        if (!controller.getWorldObj().isRemote) {
            // Send packet to clients to clear color
            if (!structureBlockPositions.isEmpty()) {
                sendClearPacket(new ArrayList<>(structureBlockPositions));
            }
        }

        clearInternalData();
    }

    private void clearInternalData() {
        structureBlockPositions.clear();
        controller.getPortManager()
            .clear();
        controller.clearSymbolPositions();
    }

    private void sendClearPacket(Collection<ChunkCoordinates> positions) {
        if (positions == null || positions.isEmpty()) return;

        StructureTintCache.clearAll(controller.getWorldObj(), positions);

        // Include controller position in clear list
        ArrayList<ChunkCoordinates> allPositions = new ArrayList<>(positions);
        allPositions.add(new ChunkCoordinates(controller.xCoord, controller.yCoord, controller.zCoord));

        PacketStructureTint clearPacket = PacketStructureTint
            .createClear(controller.getWorldObj().provider.dimensionId, allPositions);
        OmoshiroiKamo.instance.getPacketHandler()
            .sendToAllAround(clearPacket, controller);

        // Trigger block updates on server
        for (ChunkCoordinates pos : positions) {
            controller.getWorldObj()
                .markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
        }
    }

    public boolean addToMachine(Block block, int meta, int x, int y, int z) {
        // Track all structure block positions
        structureBlockPositions.add(new ChunkCoordinates(x, y, z));

        TileEntity te = controller.getWorldObj()
            .getTileEntity(x, y, z);
        if (!(te instanceof IModularPort port)) {
            return false;
        }

        PortRegistrationVisitor visitor = new PortRegistrationVisitor(controller);
        visitor.register(port);
        return true;
    }

    /**
     * Add a position to the structure block positions list.
     * Used by addPortFromStructure to track port positions.
     */
    public void addStructurePosition(int x, int y, int z) {
        structureBlockPositions.add(new ChunkCoordinates(x, y, z));
    }

    public void setStructureBlockPositions(List<ChunkCoordinates> positions) {
        structureBlockPositions.clear();
        structureBlockPositions.addAll(positions);
    }

    // ========== Structure Validation ==========

    public boolean structureCheck(String piece, int ox, int oy, int oz) {
        lastValidationError = "";

        Set<ChunkCoordinates> oldPositions = new HashSet<>(structureBlockPositions);

        clearInternalData();

        IStructureDefinition<TEMachineController> def = getStructureDefinition();
        if (def == null) {
            Logger.error("StructureAgent: Structure definition is null for piece '{}'!", piece);
            return false;
        }

        boolean valid = def.check(
            controller,
            piece,
            controller.getWorldObj(),
            controller.getExtendedFacing(),
            controller.xCoord,
            controller.yCoord,
            controller.zCoord,
            ox,
            oy,
            oz,
            false);

        if (valid) {
            // Physical structure matches!
            isPhysicallyValid = true;

            // Invalidate port caches to ensure we pick up any changes
            controller.invalidatePortCache();

            // Update visual and internal tracking data even if requirements are not met
            onFormed(); // Load tint, component tiers, etc.

            // Perform additional requirements check (IO ports count, etc.)
            if (!checkRequirements()) {
                lastValidationError = LibMisc.LANG.localize("gui.status.requirements_not_met");
                controller.setFormed(false);
            } else {
                controller.setFormed(true);
            }

            // Send tint packet to update client rendering
            sendTintPacket(oldPositions);

        } else {
            // Physical structure does NOT match.
            isPhysicallyValid = false;
            if (controller.isFormed()) {
                controller.setFormed(false);
                structureTintColor = null;
                // Full reset if it was formed but now invalid
                if (!controller.getWorldObj().isRemote) {
                    sendClearPacket(oldPositions);
                    controller.getWorldObj()
                        .markBlockForUpdate(controller.xCoord, controller.yCoord, controller.zCoord);
                }
            } else {
                // Not formed and still invalid - ensure bits are cleared if it was partially
                // matched before
                if (!controller.getWorldObj().isRemote && !oldPositions.isEmpty()) {
                    sendClearPacket(oldPositions);
                }
            }

            clearInternalData();

            if (customStructureName != null) {
                // Perform detailed scan to find the first error
                lastValidationError = checkStructureDetails(ox, oy, oz);
                if (lastValidationError.isEmpty()) {
                    lastValidationError = LibMisc.LANG.localize("gui.status.block_mismatch");
                }
            }
        }

        return controller.isFormed();
    }

    private String checkStructureDetails(int ox, int oy, int oz) {
        if (customStructureName == null) return "";

        IStructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(customStructureName);
        if (entry == null || entry.getLayers() == null) return LibMisc.LANG.localize("gui.status.invalid_definition");

        // Retrieve definition to get mapped elements
        // This is a bit redundant but we need the exact elements used in validation
        IStructureDefinition<TEMachineController> def = getStructureDefinition();
        if (def == null) return LibMisc.LANG.localize("gui.status.missing_definition");

        return LibMisc.LANG.localize("gui.status.block_mismatch");
    }

    /**
     * Force a structure check immediately, bypassing the periodic check.
     */
    public void forceStructureCheck() {
        if (controller.getWorldObj() == null || controller.getWorldObj().isRemote) return;

        // Calculate offsets
        int[][] offsets = getOffSet();
        int ox = 0, oy = 0, oz = 0;
        if (offsets != null && offsets.length > 0) {
            ox = offsets[0][0];
            oy = offsets[0][1];
            oz = offsets[0][2];
        }

        structureCheck(getStructurePieceName(), ox, oy, oz);
    }

    /**
     * Check if the formed structure meets the requirements defined in
     * CustomStructure.
     *
     * @return true if requirements are met or no requirements exist
     */
    private boolean checkRequirements() {
        if (customStructureName == null || customStructureName.isEmpty()) return true;

        IStructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(customStructureName);
        return controller.getPortManager()
            .checkRequirements(entry);
    }

    public void onFormed() {
        // Load structure tint color
        structureTintColor = getStructureTintColor();
        updateComponentTiers();
        applyFixedPortConfigs();
    }

    private void applyFixedPortConfigs() {
        IStructureEntry entry = getCustomProperties();
        if (entry == null) return;

        Map<Character, EnumIO> fixedPorts = entry.getFixedExternalPorts();
        if (fixedPorts.isEmpty()) return;

        for (Map.Entry<Character, EnumIO> fixedEntry : fixedPorts.entrySet()) {
            char symbol = fixedEntry.getKey();
            EnumIO fixedIo = fixedEntry.getValue();

            List<ChunkCoordinates> positions = controller.getSymbolPositionsMap()
                .get(symbol);
            if (positions != null) {
                for (ChunkCoordinates pos : positions) {
                    Map<IPortType.Type, EnumIO> typeMap = controller.getExternalPortConfigs()
                        .computeIfAbsent(pos, k -> new HashMap<>());
                    for (IPortType.Type type : IPortType.Type.values()) {
                        typeMap.put(type, fixedIo);
                    }
                }
            }
        }
    }

    private void updateComponentTiers() {
        if (customStructureName == null || customStructureName.isEmpty()) {
            componentTiers.clear();
            return;
        }

        IStructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(customStructureName);
        updateComponentTiers(entry);
    }

    /**
     * Updates component tiers based on the provided structure entry.
     * Package-private for testing purposes.
     */
    void updateComponentTiers(IStructureEntry entry) {
        componentTiers.clear();
        if (entry == null) return;

        Map<Character, ISymbolMapping> mappings = entry.getMappings();
        for (Map.Entry<Character, List<ChunkCoordinates>> symbolEntry : getSymbolPositionsMap().entrySet()) {
            char symbol = symbolEntry.getKey();
            ISymbolMapping mapping = mappings.get(symbol);

            if (mapping instanceof TieredBlockMapping tiered) {
                String componentName = tiered.getComponentName();
                int minTierForSymbol = Integer.MAX_VALUE;

                for (ChunkCoordinates pos : symbolEntry.getValue()) {
                    Block block = getWorldObj().getBlock(pos.posX, pos.posY, pos.posZ);
                    int meta = getWorldObj().getBlockMetadata(pos.posX, pos.posY, pos.posZ);
                    String blockId = Block.blockRegistry.getNameForObject(block) + ":" + meta;

                    int tier = tiered.getTier(blockId);
                    if (tier == 0) {
                        tier = tiered.getTier(Block.blockRegistry.getNameForObject(block) + ":*");
                    }

                    minTierForSymbol = Math.min(minTierForSymbol, tier);
                }

                if (minTierForSymbol != Integer.MAX_VALUE) {
                    componentTiers.put(
                        componentName,
                        Math.min(componentTiers.getOrDefault(componentName, Integer.MAX_VALUE), minTierForSymbol));
                }
            }
        }
    }

    public int getComponentTier(String componentName) {
        return componentTiers.getOrDefault(componentName, 0);
    }

    public Map<String, Integer> getComponentTiers() {
        return Collections.unmodifiableMap(componentTiers);
    }

    public void setComponentTiers(Map<String, Integer> tiers) {
        this.componentTiers.clear();
        if (tiers != null) {
            this.componentTiers.putAll(tiers);
        }
    }

    /**
     * Send tint color to clients. Called after structure check is complete
     * and all callbacks have finished.
     */
    public void sendTintPacket(Set<ChunkCoordinates> oldPositions) {
        if (controller.getWorldObj() == null) return;

        // Only server-side handles cache and packet sending
        // Client cache is managed by packets received from server
        if (controller.getWorldObj().isRemote) return;

        // 1. Calculate removed blocks
        Set<ChunkCoordinates> removedPositions = new HashSet<>(oldPositions);
        removedPositions.removeAll(structureBlockPositions);

        // 2. Clear removed blocks
        if (!removedPositions.isEmpty()) {
            sendClearPacket(removedPositions);
        }

        // 3. Update new blocks with tint

        // Cache tint color for all structure blocks
        if (structureTintColor != null) {
            for (ChunkCoordinates pos : structureBlockPositions) {
                StructureTintCache.put(controller.getWorldObj(), pos.posX, pos.posY, pos.posZ, structureTintColor);
            }
            // Add controller itself to cache
            StructureTintCache.put(
                controller.getWorldObj(),
                controller.xCoord,
                controller.yCoord,
                controller.zCoord,
                structureTintColor);
        }

        // Send packet to clients to set color
        if (structureTintColor != null && !structureBlockPositions.isEmpty()) {
            // Include controller position
            ArrayList<ChunkCoordinates> allPositions = new ArrayList<>(structureBlockPositions);
            allPositions.add(new ChunkCoordinates(controller.xCoord, controller.yCoord, controller.zCoord));

            PacketStructureTint colorPacket = new PacketStructureTint(
                controller.getWorldObj().provider.dimensionId,
                structureTintColor,
                allPositions);
            OmoshiroiKamo.instance.getPacketHandler()
                .sendToAllAround(colorPacket, controller);
        }

        // Trigger block updates
        updateStructureBlocksRendering();
    }

    // Overload for backward compatibility / external calls if any
    public void sendTintPacket() {
        sendTintPacket(new HashSet<>());
    }

    // ========== CustomStructure ==========

    public void setCustomStructureName(String name) {
        this.customStructureName = name;
    }

    public String getCustomStructureName() {
        return customStructureName;
    }

    public IStructureEntry getCustomProperties() {
        if (customStructureName == null || customStructureName.isEmpty()) return null;
        return StructureManager.getInstance()
            .getStructureEntry(customStructureName);
    }

    // ========== Structure Tinting ==========

    public Integer getCachedStructureTintColor() {
        return structureTintColor;
    }

    public Integer getStructureTintColor() {
        if (!controller.isFormed() || customStructureName == null) {
            return null;
        }

        IStructureEntry props = getCustomProperties();

        if (props != null && props.getTintColor() != null) {
            try {
                String hex = props.getTintColor()
                    .replace("#", "");
                return (int) Long.parseLong(hex, 16) | 0xFF000000;
            } catch (Exception e) {
                Logger.error(e.getMessage());
                return null;
            }
        }
        return null;
    }

    public void updateStructureBlocksRendering() {
        if (controller.getWorldObj() == null || controller.getWorldObj().isRemote) return;

        // Update all structure blocks
        for (ChunkCoordinates pos : structureBlockPositions) {
            controller.getWorldObj()
                .markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
        }

        // Update controller itself
        controller.getWorldObj()
            .markBlockForUpdate(controller.xCoord, controller.yCoord, controller.zCoord);
    }

    public String getLastValidationError() {
        return lastValidationError;
    }

    public void setLastValidationError(String error) {
        this.lastValidationError = error;
    }

    public boolean isPhysicallyValid() {
        return isPhysicallyValid;
    }

    public void setPhysicallyValid(boolean valid) {
        this.isPhysicallyValid = valid;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        if (customStructureName != null) {
            nbt.setString("customStructureName", customStructureName);
        }
        if (structureTintColor != null) {
            nbt.setInteger("structureTintColor", structureTintColor);
        }
        if (!componentTiers.isEmpty()) {
            NBTTagCompound tiersTag = new NBTTagCompound();
            for (Map.Entry<String, Integer> entry : componentTiers.entrySet()) {
                tiersTag.setInteger(entry.getKey(), entry.getValue());
            }
            nbt.setTag("componentTiers", tiersTag);
        }

        NBTTagList list = new NBTTagList();
        for (ChunkCoordinates pos : structureBlockPositions) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("x", pos.posX);
            tag.setInteger("y", pos.posY);
            tag.setInteger("z", pos.posZ);
            list.appendTag(tag);
        }
        nbt.setTag("structureBlocks", list);

        NBTTagList symbolsList = new NBTTagList();
        for (Map.Entry<Character, List<ChunkCoordinates>> entry : controller.getSymbolPositionsMap()
            .entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("sym", String.valueOf(entry.getKey()));
            NBTTagList posList = new NBTTagList();
            for (ChunkCoordinates pos : entry.getValue()) {
                NBTTagCompound ptag = new NBTTagCompound();
                ptag.setInteger("x", pos.posX);
                ptag.setInteger("y", pos.posY);
                ptag.setInteger("z", pos.posZ);
                posList.appendTag(ptag);
            }
            tag.setTag("posList", posList);
            symbolsList.appendTag(tag);
        }
        nbt.setTag("symbolPositions", symbolsList);
    }

    public boolean readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("customStructureName")) {
            customStructureName = nbt.getString("customStructureName");
        }
        if (nbt.hasKey("structureTintColor")) {
            structureTintColor = nbt.getInteger("structureTintColor");
        }
        if (nbt.hasKey("componentTiers")) {
            componentTiers.clear();
            NBTTagCompound tiersTag = nbt.getCompoundTag("componentTiers");
            for (Object keyObj : tiersTag.func_150296_c()) {
                String key = (String) keyObj;
                componentTiers.put(key, tiersTag.getInteger(key));
            }
        }

        structureBlockPositions.clear();

        boolean loadedBlocks = false;
        if (nbt.hasKey("structureBlocks")) {
            NBTTagList list = nbt.getTagList("structureBlocks", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                structureBlockPositions
                    .add(new ChunkCoordinates(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")));
            }
            loadedBlocks = !structureBlockPositions.isEmpty();
        }

        controller.clearSymbolPositions();
        if (nbt.hasKey("symbolPositions")) {
            NBTTagList symbolsList = nbt.getTagList("symbolPositions", 10);
            for (int i = 0; i < symbolsList.tagCount(); i++) {
                NBTTagCompound tag = symbolsList.getCompoundTagAt(i);
                char sym = tag.getString("sym")
                    .charAt(0);
                NBTTagList posList = tag.getTagList("posList", 10);
                for (int j = 0; j < posList.tagCount(); j++) {
                    NBTTagCompound ptag = posList.getCompoundTagAt(j);
                    controller
                        .trackSymbolPosition(sym, ptag.getInteger("x"), ptag.getInteger("y"), ptag.getInteger("z"));
                }
            }
        }

        // Restore color cache from loaded structure name
        if (loadedBlocks && customStructureName != null) {
            structureTintColor = getStructureTintColor();
        }

        return loadedBlocks;
    }

    // ========== Internal Hooks for Testing ==========

    public Map<Character, List<ChunkCoordinates>> getSymbolPositionsMap() {
        return controller.getSymbolPositionsMap();
    }

    public World getWorldObj() {
        return controller.getWorldObj();
    }

}
