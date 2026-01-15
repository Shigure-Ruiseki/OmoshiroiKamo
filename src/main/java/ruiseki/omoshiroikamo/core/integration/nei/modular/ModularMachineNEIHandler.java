package ruiseki.omoshiroikamo.core.integration.nei.modular;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import blockrenderer6343.integration.nei.MultiblockHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.item.ItemMachineBlueprint;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * NEI handler for ModularMachine structure previews.
 * Extends MultiblockHandler to integrate with BlockRenderer6343's preview
 * system.
 * Each custom structure is displayed as a separate page in NEI.
 */
public class ModularMachineNEIHandler extends MultiblockHandler {

    private static final ModularMachineGuiHandler guiHandler = new ModularMachineGuiHandler();

    // Cached constructables for each structure
    private static final List<CustomStructureConstructable> constructableList = new ArrayList<>();
    private static boolean initialized = false;

    public ModularMachineNEIHandler() {
        super(guiHandler);
    }

    /**
     * Initialize the handler with all registered custom structures.
     * Called from NEI config after CustomStructureRegistry is populated.
     */
    public static void initialize() {
        if (initialized) return;

        constructableList.clear();

        for (String name : CustomStructureRegistry.getRegisteredNames()) {
            IStructureDefinition<TEMachineController> def = CustomStructureRegistry.getDefinition(name);
            if (def == null) continue;

            StructureEntry entry = StructureManager.getInstance()
                .getCustomStructure(name);
            int[] offset = (entry != null && entry.controllerOffset != null) ? entry.controllerOffset
                : new int[] { 0, 0, 0 };

            CustomStructureConstructable constructable = new CustomStructureConstructable(name, def, offset);
            constructableList.add(constructable);

            Logger.info("[ModularMachineNEIHandler] Registered structure: " + name);
        }

        initialized = true;
        Logger.info("[ModularMachineNEIHandler] Initialized with " + constructableList.size() + " structure(s)");
    }

    /**
     * Reinitialize after structure registry changes (e.g., reload).
     */
    public static void reinitialize() {
        initialized = false;
        initialize();
    }

    @Override
    public @NotNull ItemStack getConstructableStack(IConstructable multiblock) {
        // Return the controller block for all structures
        return new ItemStack(MachineryBlocks.MACHINE_CONTROLLER.getBlock());
    }

    @Override
    protected @NotNull ObjectSet<IConstructable> tryLoadingMultiblocks(ItemStack candidate) {
        if (!initialized || constructableList.isEmpty()) {
            initialize();
        }

        if (candidate == null || candidate.getItem() == null) {
            return ObjectSets.emptySet();
        }

        // Check if it's the Machine Controller block
        Item controllerItem = Item.getItemFromBlock(MachineryBlocks.MACHINE_CONTROLLER.getBlock());
        if (candidate.getItem() == controllerItem) {
            // Return all structures for the controller
            return new ObjectOpenHashSet<>(constructableList);
        }

        // Check if it's a Blueprint item
        if (candidate.getItem() instanceof ItemMachineBlueprint) {
            String structureName = ItemMachineBlueprint.getStructureName(candidate);
            if (structureName != null && !structureName.isEmpty()) {
                // Find the specific structure
                for (CustomStructureConstructable c : constructableList) {
                    if (c.getStructureName()
                        .equals(structureName)) {
                        ObjectSet<IConstructable> result = new ObjectOpenHashSet<>();
                        result.add(c);
                        return result;
                    }
                }
            }
        }

        return ObjectSets.emptySet();
    }

    @Override
    public void drawBackground(int recipe) {
        // Set the current structure on the GUI handler before rendering
        if (currentMultiblocks != null && recipe >= 0 && recipe < currentMultiblocks.length) {
            IConstructable current = currentMultiblocks[recipe];
            if (current instanceof CustomStructureConstructable csc) {
                guiHandler.setCurrentStructure(csc);
            }
        }
        super.drawBackground(recipe);
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new ModularMachineNEIHandler();
    }

    @Override
    public String getRecipeName() {
        // Show the current structure name
        if (currentMultiblocks != null && oldRecipe >= 0 && oldRecipe < currentMultiblocks.length) {
            IConstructable current = currentMultiblocks[oldRecipe];
            if (current instanceof CustomStructureConstructable csc) {
                return "Modular: " + csc.getStructureName();
            }
        }
        return "Modular Machine";
    }

    @Override
    public String getRecipeTabName() {
        return "Modular Machine";
    }

    @Override
    public String getGuiTexture() {
        return "blockrenderer6343:textures/void.png";
    }
}
