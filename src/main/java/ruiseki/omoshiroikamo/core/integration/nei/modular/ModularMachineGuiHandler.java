package ruiseki.omoshiroikamo.core.integration.nei.modular;

import static blockrenderer6343.client.utils.BRUtil.FAKE_PLAYER;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;

import blockrenderer6343.api.utils.CreativeItemSource;
import blockrenderer6343.client.renderer.WorldSceneRenderer;
import blockrenderer6343.client.utils.ConstructableData;
import blockrenderer6343.integration.nei.GuiMultiblockHandler;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * GuiMultiblockHandler extension for ModularMachine structure previews.
 * Handles the actual rendering and placement of custom structures in the NEI
 * preview.
 */
public class ModularMachineGuiHandler extends GuiMultiblockHandler {

    private CustomStructureConstructable currentStructure;

    public void setCurrentStructure(CustomStructureConstructable structure) {
        this.currentStructure = structure;
    }

    /**
     * Get the static lastRenderingController for comparison.
     * This is used to detect when we're switching between tabs.
     */
    public IConstructable getLastRenderingController() {
        return lastRenderingController;
    }

    /**
     * Override loadMultiblock to force GUI reinitialization on every page switch.
     * This fixes the issue where buttons/sliders don't respond on pages 2+.
     */
    @Override
    public void loadMultiblock(IConstructable multiblock, ItemStack stackForm, @NotNull ConstructableData data) {
        // Force reinit by clearing the last controller reference
        // This ensures initGui() and loadNewMultiblock() are called every time
        lastRenderingController = null;
        super.loadMultiblock(multiblock, stackForm, data);
    }

    @Override
    protected void placeMultiblock() {
        if (currentStructure == null) {
            return;
        }

        // Clear old tint cache for NEI's fake world
        // This prevents old structure's tint data from interfering with new structure
        StructureTintCache.clearDimension(Integer.MAX_VALUE);

        String structureName = currentStructure.getStructureName();
        IStructureDefinition<TEMachineController> def = CustomStructureRegistry.getDefinition(structureName);

        if (def == null) {
            def = currentStructure.getDefinition();
        }

        if (def == null) {
            return;
        }

        // Place the controller block at the standard position
        Block controllerBlock = MachineryBlocks.MACHINE_CONTROLLER.getBlock();
        renderer.world.setBlock(MB_PLACE_POS.x, MB_PLACE_POS.y, MB_PLACE_POS.z, controllerBlock, 0, 3);

        // Get the TileEntity
        TileEntity tileEntity = renderer.world.getTileEntity(MB_PLACE_POS.x, MB_PLACE_POS.y, MB_PLACE_POS.z);
        if (!(tileEntity instanceof TEMachineController controller)) {
            return;
        }

        // Get offset for the structure
        int[] offset = currentStructure.getOffset();
        StructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(structureName);

        ExtendedFacing facing = ExtendedFacing.SOUTH_NORMAL_NONE;
        if (entry != null) {
            if (entry.controllerOffset != null) {
                offset = entry.controllerOffset;
            }
            if (entry.defaultFacing != null) {
                try {
                    String facingName = entry.defaultFacing.toUpperCase();
                    // Simple mapping attempt
                    switch (facingName) {
                        case "DOWN" -> facing = ExtendedFacing.DOWN_NORMAL_NONE;
                        case "UP" -> facing = ExtendedFacing.UP_NORMAL_NONE;
                        case "NORTH" -> facing = ExtendedFacing.NORTH_NORMAL_NONE;
                        case "SOUTH" -> facing = ExtendedFacing.SOUTH_NORMAL_NONE;
                        case "WEST" -> facing = ExtendedFacing.WEST_NORMAL_NONE;
                        case "EAST" -> facing = ExtendedFacing.EAST_NORMAL_NONE;
                        default -> {
                            // Try to valueOf full name
                            try {
                                facing = ExtendedFacing.valueOf(facingName);
                            } catch (IllegalArgumentException e) {
                                // Fallback to SOUTH if invalid
                                facing = ExtendedFacing.SOUTH_NORMAL_NONE;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Ignore invalid facing
                }
            }
        }

        // Set the structure name on the controller
        controller.setCustomStructureName(structureName);
        // Force controller facing to match defaultFacing
        controller.setExtendedFacing(facing);

        // Build the structure using the definition
        FAKE_PLAYER.setWorld(renderer.world);

        int result, iterations = 0;
        do {
            result = def.survivalBuild(
                controller,
                getBuildTriggerStack(),
                structureName,
                renderer.world,
                facing,
                MB_PLACE_POS.x,
                MB_PLACE_POS.y,
                MB_PLACE_POS.z,
                offset[0],
                offset[1],
                offset[2],
                Integer.MAX_VALUE,
                ISurvivalBuildEnvironment.create(CreativeItemSource.instance, FAKE_PLAYER),
                false);
            iterations++;
        } while (renderer.world.hasChanged() && iterations < MAX_PLACE_ROUNDS && result != -2);

        // If survivalBuild didn't work, try regular construct
        if (result == -2 || iterations >= MAX_PLACE_ROUNDS) {
            def.buildOrHints(
                controller,
                getBuildTriggerStack(),
                structureName,
                renderer.world,
                facing,
                MB_PLACE_POS.x,
                MB_PLACE_POS.y,
                MB_PLACE_POS.z,
                offset[0],
                offset[1],
                offset[2],
                false);
        }

        // Store tint color for use in beforeRender callback
        if (entry != null && entry.properties != null && entry.properties.tintColor != null) {
            try {
                String hex = entry.properties.tintColor.replace("#", "");
                currentTintColor = (int) Long.parseLong(hex, 16) | 0xFF000000;
            } catch (Exception e) {
                currentTintColor = null;
            }
        } else {
            currentTintColor = null;
        }

        // Update entities for proper rendering
        renderer.world.updateEntitiesForNEI();
    }

    @Override
    protected Object getContextObject() {
        TileEntity tile = renderer.world.getTileEntity(MB_PLACE_POS.x, MB_PLACE_POS.y, MB_PLACE_POS.z);
        return tile != null ? tile : renderingController;
    }

    // Current tint color for this structure
    private Integer currentTintColor = null;

    /**
     * Apply tint colors to all rendered blocks using renderedBlocks coordinates.
     * This ensures exact coordinate match between PUT and GET.
     */
    private void applyTintToRenderedBlocks() {
        if (currentTintColor == null || renderer == null) {
            return;
        }
        StructureTintCache.clearDimension(Integer.MAX_VALUE);

        // Apply tint to each block that will be rendered
        for (long packed : renderer.renderedBlocks) {
            int x = CoordinatePacker.unpackX(packed);
            int y = CoordinatePacker.unpackY(packed);
            int z = CoordinatePacker.unpackZ(packed);
            StructureTintCache.put(renderer.world, x, y, z, currentTintColor);
        }
        // Also add translucent blocks
        for (long packed : renderer.renderTranslucentBlocks) {
            int x = CoordinatePacker.unpackX(packed);
            int y = CoordinatePacker.unpackY(packed);
            int z = CoordinatePacker.unpackZ(packed);
            StructureTintCache.put(renderer.world, x, y, z, currentTintColor);
        }
    }

    /**
     * set up rendering callbacks after the scene is initialized.
     */
    @Override
    protected void loadNewMultiblock() {
        super.loadNewMultiblock();
        // Set beforeRender callback to apply tint right before each render
        if (renderer != null) {
            renderer.setBeforeWorldRender(r -> applyTintToRenderedBlocks());
        }
    }

    // ==========================================
    // TODO: Delete this section
    private final RenderBlocks renderBlocks = new RenderBlocks();

    @Override
    public void onRendererRender(WorldSceneRenderer renderer) {
        super.onRendererRender(renderer);
        renderAxisBlocks(renderer);
    }

    private void renderAxisBlocks(WorldSceneRenderer renderer) {
        renderBlocks.blockAccess = renderer.world;
        renderBlocks.setRenderBounds(0, 0, 0, 1, 1, 1);
        renderBlocks.renderAllFaces = true;

        // X Axis (Red) - 3 blocks
        IIcon redIcon = Blocks.stained_glass.getIcon(0, 14);
        for (int i = 1; i <= 3; i++) {
            renderBlocks.renderBlockUsingTexture(
                Blocks.stained_glass,
                MB_PLACE_POS.x + i,
                MB_PLACE_POS.y,
                MB_PLACE_POS.z,
                redIcon);
        }

        // Y Axis (Green/Lime) - 3 blocks (Lime = 5)
        IIcon greenIcon = Blocks.stained_glass.getIcon(0, 5);
        for (int i = 1; i <= 3; i++) {
            renderBlocks.renderBlockUsingTexture(
                Blocks.stained_glass,
                MB_PLACE_POS.x,
                MB_PLACE_POS.y + i,
                MB_PLACE_POS.z,
                greenIcon);
        }

        // Z Axis (Blue) - 3 blocks (Blue = 11)
        IIcon blueIcon = Blocks.stained_glass.getIcon(0, 11);
        for (int i = 1; i <= 3; i++) {
            renderBlocks.renderBlockUsingTexture(
                Blocks.stained_glass,
                MB_PLACE_POS.x,
                MB_PLACE_POS.y,
                MB_PLACE_POS.z + i,
                blueIcon);
        }
    }
}
