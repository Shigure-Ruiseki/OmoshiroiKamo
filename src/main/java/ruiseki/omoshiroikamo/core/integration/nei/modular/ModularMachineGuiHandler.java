package ruiseki.omoshiroikamo.core.integration.nei.modular;

import static blockrenderer6343.client.utils.BRUtil.FAKE_PLAYER;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;

import blockrenderer6343.api.utils.CreativeItemSource;
import blockrenderer6343.client.utils.ConstructableData;
import blockrenderer6343.integration.nei.GuiMultiblockHandler;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
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
            Logger.warn("[ModularMachineGuiHandler] No structure set for preview!");
            return;
        }

        String structureName = currentStructure.getStructureName();
        IStructureDefinition<TEMachineController> def = currentStructure.getDefinition();

        if (def == null) {
            def = CustomStructureRegistry.getDefinition(structureName);
        }

        if (def == null) {
            Logger.warn("[ModularMachineGuiHandler] Structure definition not found: " + structureName);
            return;
        }

        // Place the controller block at the standard position
        Block controllerBlock = MachineryBlocks.MACHINE_CONTROLLER.getBlock();
        renderer.world.setBlock(MB_PLACE_POS.x, MB_PLACE_POS.y, MB_PLACE_POS.z, controllerBlock, 0, 3);

        // Get the TileEntity
        TileEntity tileEntity = renderer.world.getTileEntity(MB_PLACE_POS.x, MB_PLACE_POS.y, MB_PLACE_POS.z);
        if (!(tileEntity instanceof TEMachineController controller)) {
            Logger.error("[ModularMachineGuiHandler] Failed to get TEMachineController at placement position");
            return;
        }

        // Set the structure name on the controller (so it knows which structure to use)
        controller.setCustomStructureName(structureName);

        // Get offset for the structure
        int[] offset = currentStructure.getOffset();
        StructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(structureName);
        if (entry != null && entry.controllerOffset != null) {
            offset = entry.controllerOffset;
        }

        // Build the structure using the definition
        FAKE_PLAYER.setWorld(renderer.world);

        int result, iterations = 0;
        do {
            result = def.survivalBuild(
                controller,
                getBuildTriggerStack(),
                structureName,
                renderer.world,
                ExtendedFacing.DEFAULT,
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
                ExtendedFacing.DEFAULT,
                MB_PLACE_POS.x,
                MB_PLACE_POS.y,
                MB_PLACE_POS.z,
                offset[0],
                offset[1],
                offset[2],
                false);
        }

        // Update entities for proper rendering
        renderer.world.updateEntitiesForNEI();

        Logger.info("[ModularMachineGuiHandler] Placed structure: " + structureName);
    }

    @Override
    protected Object getContextObject() {
        TileEntity tile = renderer.world.getTileEntity(MB_PLACE_POS.x, MB_PLACE_POS.y, MB_PLACE_POS.z);
        return tile != null ? tile : renderingController;
    }
}
