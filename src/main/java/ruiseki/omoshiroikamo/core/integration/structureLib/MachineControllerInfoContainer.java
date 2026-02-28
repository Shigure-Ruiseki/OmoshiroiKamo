package ruiseki.omoshiroikamo.core.integration.structureLib;

import java.util.NoSuchElementException;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;

import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.common.util.Logger;
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
        String structureName = getStructureName(triggerStack, ctx);

        IStructureDefinition<TEMachineController> def = getStructureDefinition(structureName, ctx);
        if (def == null) {
            notifyStructureNotFound(ctx, structureName);
            return;
        }

        int[] offset = getOffset(structureName, ctx);
        String pieceName = structureName != null ? structureName : ctx.getStructurePieceName();

        try {
            // Use controller's facing for rotation support
            def.buildOrHints(
                ctx,
                triggerStack,
                pieceName,
                ctx.getWorldObj(),
                ctx.getExtendedFacing(),
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                offset[0],
                offset[1],
                offset[2],
                hintsOnly);
        } catch (NoSuchElementException e) {
            notifyStructureNotFound(ctx, pieceName);
        }
    }

    @Override
    public int survivalConstruct(ItemStack triggerStack, int elementBudget, ISurvivalBuildEnvironment env,
        TEMachineController ctx, ExtendedFacing facing) {
        String structureName = getStructureName(triggerStack, ctx);

        IStructureDefinition<TEMachineController> def = getStructureDefinition(structureName, ctx);
        if (def == null) {
            notifyStructureNotFound(ctx, structureName);
            return 0;
        }

        int[] offset = getOffset(structureName, ctx);
        String pieceName = structureName != null ? structureName : ctx.getStructurePieceName();

        try {
            // Use controller's facing for rotation support
            return def.survivalBuild(
                ctx,
                triggerStack,
                pieceName,
                ctx.getWorldObj(),
                ctx.getExtendedFacing(),
                ctx.xCoord,
                ctx.yCoord,
                ctx.zCoord,
                offset[0],
                offset[1],
                offset[2],
                elementBudget,
                env,
                false);
        } catch (NoSuchElementException e) {
            notifyStructureNotFound(ctx, pieceName);
            return 0;
        }
    }

    /**
     * Notify that a structure was not found.
     * Logs to console and notifies any connected player.
     */
    private void notifyStructureNotFound(TEMachineController ctx, String structureName) {
        String msg = "[Machine] Structure '" + structureName + "' not found!";
        Logger.warn(msg);

        // Try to notify client (if this is being triggered by a player action)
        if (ctx != null && ctx.getWorldObj() != null && !ctx.getWorldObj().isRemote) {
            // This is server-side, we can't directly send chat here
            // The chat message will be sent by other code paths if needed
        }
    }

    @Override
    public String[] getDescription(ItemStack stack) {
        String structureName = null;
        if (stack != null) {
            if (stack.getItem() instanceof ItemMachineBlueprint) {
                structureName = ItemMachineBlueprint.getStructureName(stack);
            } else if (stack.hasTagCompound()) {
                if (stack.getTagCompound()
                    .hasKey("customStructureName")) {
                    structureName = stack.getTagCompound()
                        .getString("customStructureName");
                } else if (stack.getTagCompound()
                    .hasKey("customStructure")) {
                        structureName = stack.getTagCompound()
                            .getString("customStructure");
                    }
            }
        }

        if (structureName != null && !structureName.isEmpty()) {
            return new String[] { "Structure: " + structureName };
        }
        return null; // Don't show anything if no structure is active
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
        return null;
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
