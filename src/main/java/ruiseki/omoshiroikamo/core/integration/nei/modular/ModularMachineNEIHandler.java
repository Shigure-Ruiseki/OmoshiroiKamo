package ruiseki.omoshiroikamo.core.integration.nei.modular;

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
 * NEI handler for a single ModularMachine structure preview.
 * Each structure gets its own handler instance and tab in NEI.
 * This avoids the page navigation issues with shared static state.
 */
public class ModularMachineNEIHandler extends MultiblockHandler {

    // Each handler has its own GuiHandler instance to avoid state conflicts
    private final ModularMachineGuiHandler guiHandler;

    // The specific structure this handler displays
    private final String structureName;
    private final CustomStructureConstructable constructable;

    /**
     * Create a handler for a specific structure.
     * 
     * @param structureName Name of the structure
     */
    public ModularMachineNEIHandler(String structureName) {
        // Create a dedicated GuiHandler for this structure and pass to parent
        super(new ModularMachineGuiHandler());
        this.guiHandler = (ModularMachineGuiHandler) super.getGuiHandler();
        this.structureName = structureName;

        // Create the constructable for this structure
        IStructureDefinition<TEMachineController> def = CustomStructureRegistry.getDefinition(structureName);
        StructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(structureName);
        int[] offset = (entry != null && entry.controllerOffset != null) ? entry.controllerOffset
            : new int[] { 0, 0, 0 };

        this.constructable = new CustomStructureConstructable(structureName, def, offset);
        this.guiHandler.setCurrentStructure(this.constructable);

        Logger.info("[ModularMachineNEIHandler] Created handler for structure: " + structureName);
    }

    /**
     * Private constructor for newInstance().
     */
    private ModularMachineNEIHandler(String structureName, ModularMachineGuiHandler guiHandler,
        CustomStructureConstructable constructable) {
        super(guiHandler);
        this.guiHandler = guiHandler;
        this.structureName = structureName;
        this.constructable = constructable;
        this.guiHandler.setCurrentStructure(this.constructable);
    }

    /**
     * Get the GuiHandler for parent class.
     */
    @Override
    public ModularMachineGuiHandler getGuiHandler() {
        return guiHandler;
    }

    @Override
    public @NotNull ItemStack getConstructableStack(IConstructable multiblock) {
        return new ItemStack(MachineryBlocks.MACHINE_CONTROLLER.getBlock());
    }

    @Override
    protected @NotNull ObjectSet<IConstructable> tryLoadingMultiblocks(ItemStack candidate) {
        if (candidate == null || candidate.getItem() == null) {
            return ObjectSets.emptySet();
        }

        // Check if it's the Machine Controller block
        Item controllerItem = Item.getItemFromBlock(MachineryBlocks.MACHINE_CONTROLLER.getBlock());
        if (candidate.getItem() == controllerItem) {
            // Return only THIS structure
            ObjectSet<IConstructable> result = new ObjectOpenHashSet<>();
            result.add(constructable);
            return result;
        }

        // Check if it's a Blueprint item matching this structure
        if (candidate.getItem() instanceof ItemMachineBlueprint) {
            String blueprintStructure = ItemMachineBlueprint.getStructureName(candidate);
            if (structureName.equals(blueprintStructure)) {
                ObjectSet<IConstructable> result = new ObjectOpenHashSet<>();
                result.add(constructable);
                return result;
            }
        }

        return ObjectSets.emptySet();
    }

    @Override
    public void drawBackground(int recipe) {
        // Ensure GUI handler has the correct structure
        guiHandler.setCurrentStructure(constructable);
        // Only force rebuild when switching FROM a different structure's tab
        // This allows layer changes to work while viewing the same tab
        // Access lastRenderingController via guiHandler (inherited static from
        // GuiMultiblockHandler)
        if (guiHandler.getLastRenderingController() != constructable) {
            oldRecipe = -1;
        }
        super.drawBackground(recipe);
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        // Create a new instance with the same structure
        return new ModularMachineNEIHandler(structureName, new ModularMachineGuiHandler(), constructable);
    }

    @Override
    public String getRecipeName() {
        return structureName;
    }

    @Override
    public String getRecipeTabName() {
        return structureName;
    }

    @Override
    public String getGuiTexture() {
        return "blockrenderer6343:textures/void.png";
    }

    public String getStructureName() {
        return structureName;
    }
}
