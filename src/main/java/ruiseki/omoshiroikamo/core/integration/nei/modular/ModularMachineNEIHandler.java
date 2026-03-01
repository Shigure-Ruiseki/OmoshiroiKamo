package ruiseki.omoshiroikamo.core.integration.nei.modular;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import blockrenderer6343.integration.nei.MultiblockHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.item.ItemMachineBlueprint;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * NEI handler for a single ModularMachine structure preview.
 * Each structure gets its own handler instance and tab in NEI.
 * Only shows on Usage lookup (not Recipe lookup for controller).
 * TODO: Fix blueprints appear briefly in left tab
 * The loadTransferRects implementation might need adjustment for proper NEI
 * integration.
 */
public class ModularMachineNEIHandler extends MultiblockHandler {

    // Each handler has its own GuiHandler instance to avoid state conflicts
    private final ModularMachineGuiHandler guiHandler;

    // The specific structure this handler displays
    private final String structureName;
    private final CustomStructureConstructable constructable;

    // Cached set of blocks used in this structure (for Usage lookup)
    private Set<Block> componentBlocks;

    /**
     * Create a handler for a specific structure.
     */
    public ModularMachineNEIHandler(String structureName) {
        super(new ModularMachineGuiHandler());
        this.guiHandler = (ModularMachineGuiHandler) super.getGuiHandler();
        this.structureName = structureName;

        IStructureDefinition<TEMachineController> def = CustomStructureRegistry.getDefinition(structureName);
        IStructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(structureName);
        int[] offset = (entry != null && entry.getControllerOffset() != null) ? entry.getControllerOffset()
            : new int[] { 0, 0, 0 };

        this.constructable = new CustomStructureConstructable(structureName, def, offset);
        this.guiHandler.setCurrentStructure(this.constructable);

        // Component blocks will be lazily initialized on first access
        this.componentBlocks = null;
    }

    /**
     * Private constructor for newInstance().
     */
    private ModularMachineNEIHandler(String structureName, ModularMachineGuiHandler guiHandler,
        CustomStructureConstructable constructable, Set<Block> componentBlocks) {
        super(guiHandler);
        this.guiHandler = guiHandler;
        this.structureName = structureName;
        this.constructable = constructable;
        this.componentBlocks = componentBlocks;
        this.guiHandler.setCurrentStructure(this.constructable);
    }

    /**
     * Build the set of blocks used in this structure.
     */
    private Set<Block> buildComponentBlockSet() {
        Set<Block> blocks = new HashSet<>();
        IStructureEntry entry = StructureManager.getInstance()
            .getCustomStructure(structureName);
        if (entry != null && entry.getMappings() != null) {
            for (ISymbolMapping value : entry.getMappings()
                .values()) {
                extractBlocksFromMapping(value, blocks);
            }
        }
        // Always include the controller block
        blocks.add(MachineryBlocks.MACHINE_CONTROLLER.getBlock());
        return blocks;
    }

    /**
     * Get component blocks with lazy initialization.
     */
    private Set<Block> getComponentBlocks() {
        if (componentBlocks == null) {
            componentBlocks = buildComponentBlockSet();
        }
        return componentBlocks;
    }

    /**
     * Extract block IDs from a mapping value.
     */
    private void extractBlocksFromMapping(ISymbolMapping value, Set<Block> blocks) {
        if (value instanceof BlockMapping mapping) {
            // Single block
            if (mapping.getBlockId() != null) {
                addBlockFromId(mapping.getBlockId(), blocks);
            }
            // Multiple blocks
            if (mapping.getBlockIds() != null) {
                for (String entryId : mapping.getBlockIds()) {
                    if (entryId != null) {
                        addBlockFromId(entryId, blocks);
                    }
                }
            }
        }
    }

    /**
     * Parse a block ID string and add the block to the set.
     */
    private void addBlockFromId(String blockId, Set<Block> blocks) {
        if (blockId == null || blockId.isEmpty()) return;

        // Remove meta suffix (:* or :0 etc)
        String cleanId = blockId;
        int lastColon = blockId.lastIndexOf(':');
        if (lastColon > 0) {
            String suffix = blockId.substring(lastColon + 1);
            if (suffix.equals("*") || suffix.matches("\\d+")) {
                cleanId = blockId.substring(0, lastColon);
            }
        }

        Block block = Block.getBlockFromName(cleanId);
        if (block != null) {
            blocks.add(block);
        }
    }

    @Override
    public @NotNull ModularMachineGuiHandler getGuiHandler() {
        return guiHandler;
    }

    @Override
    public @NotNull ItemStack getConstructableStack(IConstructable multiblock) {
        return new ItemStack(MachineryBlocks.MACHINE_CONTROLLER.getBlock());
    }

    /**
     * Override loadCraftingRecipes to NOT respond to any Recipe lookups.
     * Structure preview should only show on Usage lookup, not Recipe.
     */
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        // Do nothing - structure preview only shows on Usage (U key)
        // Recipe lookup (R key) should not show structure preview
    }

    /**
     * Override loadUsageRecipes to respond to controller, blueprints, and component
     * blocks.
     */
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient == null || ingredient.getItem() == null) {
            return;
        }

        ObjectSet<IConstructable> multiblocks = tryLoadingMultiblocks(ingredient);
        if (!multiblocks.isEmpty()) {
            // Call parent's loadUsageRecipes which properly sets lastStack
            super.loadUsageRecipes(ingredient);
        }
    }

    @Override
    protected @NotNull ObjectSet<IConstructable> tryLoadingMultiblocks(ItemStack candidate) {
        if (candidate == null || candidate.getItem() == null) {
            return ObjectSets.emptySet();
        }

        // Check if it's the Machine Controller block
        Item controllerItem = Item.getItemFromBlock(MachineryBlocks.MACHINE_CONTROLLER.getBlock());
        if (candidate.getItem() == controllerItem) {
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

        // Check if it's a component block of this structure
        if (candidate.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) candidate.getItem()).field_150939_a;
            if (getComponentBlocks().contains(block)) {
                ObjectSet<IConstructable> result = new ObjectOpenHashSet<>();
                result.add(constructable);
                return result;
            }
        }

        return ObjectSets.emptySet();
    }

    @Override
    public void drawBackground(int recipe) {
        guiHandler.setCurrentStructure(constructable);
        if (guiHandler.getLastRenderingController() != constructable) {
            oldRecipe = -1;
        }
        super.drawBackground(recipe);
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new ModularMachineNEIHandler(
            structureName,
            new ModularMachineGuiHandler(),
            constructable,
            getComponentBlocks());
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
    public String getHandlerId() {
        return getOverlayIdentifier();
    }

    /**
     * Override to use structure name as the unique handler ID.
     * This is critical for catalyst registration.
     */
    @Override
    public String getOverlayIdentifier() {
        return "modular_structure_" + structureName;
    }

    @Override
    public String getGuiTexture() {
        return "blockrenderer6343:textures/void.png";
    }

    public String getStructureName() {
        return structureName;
    }

    /**
     * Required for NEI catalyst registration to work.
     */
    @Override
    public void loadTransferRects() {
        // This method is required for NEI to properly link catalysts to this handler.
        // We don't need transfer rectangles for structure preview, but the method must
        // exist.
    }
}
