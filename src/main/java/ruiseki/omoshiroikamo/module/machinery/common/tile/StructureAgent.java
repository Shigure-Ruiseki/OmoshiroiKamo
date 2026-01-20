package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.Properties;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketStructureTint;

/**
 * Handles structure-related logic for {@link TEMachineController}.
 */
public class StructureAgent {

    private static IStructureDefinition<TEMachineController> STRUCTURE_DEFINITION;

    private final TEMachineController controller;

    // CustomStructure name (automatically derived from blueprint)
    private String customStructureName = null;

    // Tint color for structure blocks (loaded from structure properties)
    private Integer structureTintColor = null;

    // Structure block positions tracking (for tint cache management)
    private final Set<ChunkCoordinates> structureBlockPositions = new HashSet<>();

    // Field to store validation error message
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
        // Fallback to default structure (or null for simple check)
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

    public void clearStructureParts() {
        if (controller.getWorldObj() == null) {
            structureBlockPositions.clear();
            controller.getPortManager()
                .clear();
            return;
        }

        // Clear cache (server-side)
        StructureTintCache.clearAll(controller.getWorldObj(), structureBlockPositions);

        // Send packet to clients to clear color
        if (!controller.getWorldObj().isRemote && !structureBlockPositions.isEmpty()) {
            // Include controller position in clear list
            ArrayList<ChunkCoordinates> allPositions = new ArrayList<>(structureBlockPositions);
            allPositions.add(new ChunkCoordinates(controller.xCoord, controller.yCoord, controller.zCoord));

            PacketStructureTint clearPacket = PacketStructureTint
                .createClear(controller.getWorldObj().provider.dimensionId, allPositions);
            PacketHandler.sendToAllAround(clearPacket, controller);
        }

        // Trigger block updates (to reset colors) on server
        for (ChunkCoordinates pos : structureBlockPositions) {
            controller.getWorldObj()
                .markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
        }

        structureBlockPositions.clear();
        controller.getPortManager()
            .clear();
    }

    public boolean addToMachine(Block block, int meta, int x, int y, int z) {
        // Track all structure block positions
        structureBlockPositions.add(new ChunkCoordinates(x, y, z));

        TileEntity te = controller.getWorldObj()
            .getTileEntity(x, y, z);
        if (te == null || !(te instanceof IModularPort port)) {
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

    // ========== Structure Validation ==========

    public boolean structureCheck(String piece, int ox, int oy, int oz) {
        // Clear previous error
        lastValidationError = "";

        // Clear structure parts before checking
        clearStructureParts();

        // Use controller's facing for rotation support
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
            updateStructureBlocksRendering();
        }

        if (valid && controller.isFormed()) {
            // Perform additional requirements check for CustomStructure
            if (!checkRequirements()) {
                lastValidationError = "Structure requirements not met.";
                controller.setFormed(false);
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

        return controller.isFormed();
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

        if (controller.getWorldObj() == null) return;

        // Cache tint color for all structure blocks (server-side)
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
        if (!controller.getWorldObj().isRemote && structureTintColor != null && !structureBlockPositions.isEmpty()) {
            // Include controller position
            ArrayList<ChunkCoordinates> allPositions = new ArrayList<>(structureBlockPositions);
            allPositions.add(new ChunkCoordinates(controller.xCoord, controller.yCoord, controller.zCoord));

            PacketStructureTint colorPacket = new PacketStructureTint(
                controller.getWorldObj().provider.dimensionId,
                structureTintColor,
                allPositions);
            PacketHandler.sendToAllAround(colorPacket, controller);
        }

        // Trigger block updates
        updateStructureBlocksRendering();
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
                int color = (int) Long.parseLong(hex, 16) | 0xFF000000;
                return color;
            } catch (Exception e) {
                e.printStackTrace();
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

    // ========== NBT Persistence ==========

    public void writeToNBT(NBTTagCompound nbt) {
        if (customStructureName != null) {
            nbt.setString("customStructureName", customStructureName);
        }
    }

    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("customStructureName")) {
            customStructureName = nbt.getString("customStructureName");
        }
    }
}
