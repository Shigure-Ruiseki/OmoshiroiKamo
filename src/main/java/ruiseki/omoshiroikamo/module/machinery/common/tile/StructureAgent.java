package ruiseki.omoshiroikamo.module.machinery.common.tile;

import static ruiseki.omoshiroikamo.CommonProxy.NETWORK;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.Properties;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketStructureTint;

/**
 * Handles structure-related logic for {@link TEMachineController}.
 */
public class StructureAgent {

    private static IStructureDefinition<TEMachineController> STRUCTURE_DEFINITION;

    private final TEMachineController controller;

    private String customStructureName = null;
    private Integer structureTintColor = null;
    private final Set<ChunkCoordinates> structureBlockPositions = new HashSet<>();
    private String lastValidationError = "";

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
        // Fallback to default structure
        return STRUCTURE_DEFINITION;
    }

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
    }

    private void sendClearPacket(Collection<ChunkCoordinates> positions) {
        if (positions == null || positions.isEmpty()) return;

        StructureTintCache.clearAll(controller.getWorldObj(), positions);

        // Include controller position in clear list
        ArrayList<ChunkCoordinates> allPositions = new ArrayList<>(positions);
        allPositions.add(new ChunkCoordinates(controller.xCoord, controller.yCoord, controller.zCoord));

        PacketStructureTint clearPacket = PacketStructureTint
            .createClear(controller.getWorldObj().provider.dimensionId, allPositions);
        NETWORK.sendToAllAround(clearPacket, controller);

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

        IPortType.Direction direction = port.getPortDirection();

        return switch (direction) {
            case INPUT -> {
                controller.getPortManager()
                    .addPort(port, true);
                yield true;
            }
            case OUTPUT -> {
                controller.getPortManager()
                    .addPort(port, false);
                yield true;
            }
            case BOTH -> {
                controller.getPortManager()
                    .addPort(port, true);
                controller.getPortManager()
                    .addPort(port, false);
                yield true;
            }
            default -> false;
        };
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

        boolean valid = getStructureDefinition().check(
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

        if (valid && !controller.isFormed()) {
            controller.setFormed(true);
            onFormed();
        } else if (!valid && controller.isFormed()) {
            controller.setFormed(false);
            structureTintColor = null;
            // Full reset if became invalid
            if (!controller.getWorldObj().isRemote) {
                sendClearPacket(oldPositions);
                controller.getWorldObj()
                    .markBlockForUpdate(controller.xCoord, controller.yCoord, controller.zCoord);
            }
        }

        if (valid && controller.isFormed()) {
            // Perform additional requirements check for CustomStructure
            if (!checkRequirements()) {
                lastValidationError = LibMisc.LANG.localize("gui.status.requirements_not_met");
                controller.setFormed(false);
                if (!controller.getWorldObj().isRemote) {
                    // Clear both old and current
                    Set<ChunkCoordinates> allToClear = new HashSet<>(oldPositions);
                    allToClear.addAll(structureBlockPositions);
                    sendClearPacket(allToClear);
                }
                clearInternalData();
                return false;
            }

            // Send tint packet AFTER all callbacks have completed.
            // Pass oldPositions to calculate diff.
            sendTintPacket(oldPositions);
        } else if (!valid) {
            // If check failed, we don't know exactly why
            // but usually it means blocks don't match.
            // Ensure old parts are cleared.
            if (!controller.getWorldObj().isRemote && !oldPositions.isEmpty()) {
                sendClearPacket(oldPositions);
            }

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

        StructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(customStructureName);
        if (entry == null || entry.layers == null) return LibMisc.LANG.localize("gui.status.invalid_definition");

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

        StructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(customStructureName);
        return controller.getPortManager()
            .checkRequirements(entry);
    }

    public void onFormed() {
        // Load structure tint color
        structureTintColor = getStructureTintColor();

        // StructureBlockPositions may be empty here because callbacks run after check()
        // The actual tint packet will be sent by sendTintPacket() called after all
        // callbacks complete
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
            NETWORK.sendToAllAround(colorPacket, controller);
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

    public Properties getCustomProperties() {
        if (customStructureName == null || customStructureName.isEmpty()) return null;
        StructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(customStructureName);
        return entry != null ? entry.properties : null;
    }

    // ========== Structure Tinting ==========

    public Integer getCachedStructureTintColor() {
        return structureTintColor;
    }

    public Integer getStructureTintColor() {
        if (!controller.isFormed() || customStructureName == null) {
            return null;
        }

        Properties props = getCustomProperties();

        if (props != null && props.tintColor != null) {
            try {
                String hex = props.tintColor.replace("#", "");
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

    public void writeToNBT(NBTTagCompound nbt) {
        if (customStructureName != null) {
            nbt.setString("customStructureName", customStructureName);
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
    }

    public boolean readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("customStructureName")) {
            customStructureName = nbt.getString("customStructureName");
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

        // Restore color cache from loaded structure name
        if (loadedBlocks && customStructureName != null) {
            structureTintColor = getStructureTintColor();
        }

        return loadedBlocks;
    }

}
