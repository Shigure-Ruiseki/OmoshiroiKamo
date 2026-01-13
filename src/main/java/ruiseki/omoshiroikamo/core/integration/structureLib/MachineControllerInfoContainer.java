package ruiseki.omoshiroikamo.core.integration.structureLib;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;

import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.module.machinery.common.item.ItemMachineBlueprint;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * StructureLib info container for the modular Machine Controller.
 * Supports both the controller's current structure definition and
 * blueprint-triggered structure display.
 */
public class MachineControllerInfoContainer implements IMultiblockInfoContainer<TEMachineController> {

    @Override
    public void construct(ItemStack triggerStack, boolean hintsOnly, TEMachineController ctx, ExtendedFacing facing) {
        // Get structure name from blueprint or controller
        String structureName = getStructureName(triggerStack, ctx);

        IStructureDefinition<TEMachineController> def = getStructureDefinition(structureName, ctx);
        if (def == null) {
            return;
        }

        int[] offset = getOffset(structureName, ctx);
        String pieceName = structureName != null ? structureName : ctx.getStructurePieceName();

        def.buildOrHints(
            ctx,
            triggerStack,
            pieceName,
            ctx.getWorldObj(),
            ExtendedFacing.DEFAULT,
            ctx.xCoord,
            ctx.yCoord,
            ctx.zCoord,
            offset[0],
            offset[1],
            offset[2],
            hintsOnly);
    }

    @Override
    public int survivalConstruct(ItemStack triggerStack, int elementBudget, ISurvivalBuildEnvironment env,
        TEMachineController ctx, ExtendedFacing facing) {
        String structureName = getStructureName(triggerStack, ctx);

        IStructureDefinition<TEMachineController> def = getStructureDefinition(structureName, ctx);
        if (def == null) return 0;

        int[] offset = getOffset(structureName, ctx);

        return def.survivalBuild(
            ctx,
            triggerStack,
            structureName != null ? structureName : ctx.getStructurePieceName(),
            ctx.getWorldObj(),
            ExtendedFacing.DEFAULT,
            ctx.xCoord,
            ctx.yCoord,
            ctx.zCoord,
            offset[0],
            offset[1],
            offset[2],
            elementBudget,
            env,
            false);
    }

    @Override
    public String[] getDescription(ItemStack stack) {
        String structureName = ItemMachineBlueprint.getStructureName(stack);
        if (structureName != null) {
            return new String[] { "Structure: " + structureName };
        }
        return new String[] { "Modular Machine Controller" };
    }

    /**
     * Get the structure name from trigger stack (blueprint) or controller.
     */
    private String getStructureName(ItemStack triggerStack, TEMachineController ctx) {
        // Priority 1: Blueprint item
        if (triggerStack != null && triggerStack.getItem() instanceof ItemMachineBlueprint) {
            String name = ItemMachineBlueprint.getStructureName(triggerStack);
            if (name != null && !name.isEmpty()) {
                return name;
            }
        }

        // Priority 2: Controller's stored structure
        String controllerStructure = ctx.getCustomStructureName();
        if (controllerStructure != null && !controllerStructure.isEmpty()) {
            return controllerStructure;
        }

        return null;
    }

    /**
     * Get the structure definition for the given structure name.
     */
    private IStructureDefinition<TEMachineController> getStructureDefinition(String structureName,
        TEMachineController ctx) {
        if (structureName != null && !structureName.isEmpty()) {
            IStructureDefinition<TEMachineController> def = CustomStructureRegistry.getDefinition(structureName);
            if (def != null) {
                return def;
            }
        }
        // Fallback to controller's default structure
        return ctx.getStructureDefinition();
    }

    /**
     * Get the offset for the structure.
     * Custom structures calculate offset from the 'Q' controller position.
     */
    private int[] getOffset(String structureName, TEMachineController ctx) {
        if (structureName != null && !structureName.isEmpty()) {
            StructureEntry entry = StructureManager.getInstance()
                .getCustomStructure(structureName);
            if (entry != null && entry.controllerOffset != null) {
                return entry.controllerOffset;
            }
            // Default offset for custom structures
            return new int[] { 0, 0, 1 };
        }
        // Use controller's default offset
        int[][] offsets = ctx.getOffSet();
        return offsets[0];
    }
}
