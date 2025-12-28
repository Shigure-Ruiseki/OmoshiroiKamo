package ruiseki.omoshiroikamo.module.multiblock.integration.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.IUsageHandler;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedStackBase;
import ruiseki.omoshiroikamo.core.integration.nei.PositionedStackAdv;
import ruiseki.omoshiroikamo.core.integration.nei.RecipeHandlerBase;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.BlockColoredLens;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;

public abstract class VoidMinerRecipeHandler extends RecipeHandlerBase {

    protected final int tier;

    /**
     * Current dimension filter for NEI display. Uses DIMENSION_COMMON by default.
     * Static so it persists across handler instances.
     */
    protected static int filterDimension = NEIDimensionConfig.DIMENSION_COMMON;

    /**
     * Flag indicating the dimension was manually changed by user.
     * When true, automatic dimension detection is skipped.
     */
    protected static boolean manualDimensionChange = false;

    /** View mode for different display layouts */
    public enum ViewMode {
        /** Show dimension's item list (default, Miner/Catalyst Usage) */
        DIMENSION,
        /** Show item's dimension list with probabilities (Item Crafting) */
        ITEM_DETAIL,
        /** Show lens bonus target items (Lens Usage) */
        LENS_BONUS
    }

    /** Current view mode */
    protected static ViewMode currentViewMode = ViewMode.DIMENSION;

    /** Item being displayed in ITEM_DETAIL mode */
    protected static ItemStack detailItem = null;

    /** Current text filter for ore name search */
    protected String filterText = "";

    public VoidMinerRecipeHandler(int tier) {
        this.tier = tier;
    }

    protected abstract IFocusableRegistry getRegistry();

    protected abstract IFocusableRegistry getRegistry(int tier);

    /**
     * Get registry for NEI display with dimension filter applied.
     * 
     * @param tier  The miner tier
     * @param dimId The dimension ID for filter
     * @return Registry containing ores available in that dimension with correct
     *         probability
     */
    protected abstract IFocusableRegistry getRegistryForNEI(int tier, int dimId);

    // Factory for spawning the handler of a specific tier
    // used when delegating usage requests
    protected abstract VoidMinerRecipeHandler createForTier(int tier);

    protected abstract Block getMinerBlock();

    protected abstract String getMinerNameBase();

    protected abstract String getRecipeIdBase();

    /**
     * Find the first dimension ID where this item can be mined.
     * Used for automatic dimension filter when viewing crafting recipes.
     * 
     * @param stack The item to search for
     * @return The dimension ID, or NEIDimensionConfig.DIMENSION_COMMON if not found
     */
    protected abstract int findFirstDimension(ItemStack stack);

    @Override
    public String getRecipeName() {
        // Show tiered name + dimension filter so each NEI tab is unique and clear
        String dimName = NEIDimensionConfig.getDisplayName(filterDimension);
        return getMinerNameBase() + " T" + (tier + 1) + " [" + dimName + "]";
    }

    // --- Dimension Filter UI ---

    /** Rectangle for dimension cycle button (relative to recipe area) */
    private static final Rectangle DIM_BUTTON_RECT = new Rectangle(5, 0, 60, 12);

    @Override
    public void drawForeground(int recipe) {
        super.drawForeground(recipe);
        // Only draw on the first recipe (avoid duplicate buttons per recipe slot)
        if (recipe != 0) return;

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        // Draw dimension button background
        GL11.glDisable(GL11.GL_LIGHTING);
        GuiDraw
            .drawRect(DIM_BUTTON_RECT.x, DIM_BUTTON_RECT.y, DIM_BUTTON_RECT.width, DIM_BUTTON_RECT.height, 0xFF404040);
        GuiDraw.drawRect(
            DIM_BUTTON_RECT.x + 1,
            DIM_BUTTON_RECT.y + 1,
            DIM_BUTTON_RECT.width - 2,
            DIM_BUTTON_RECT.height - 2,
            0xFF808080);

        // Draw dimension name
        String dimName = NEIDimensionConfig.getDisplayName(filterDimension);
        fr.drawString(dimName, DIM_BUTTON_RECT.x + 3, DIM_BUTTON_RECT.y + 2, 0xFFFFFF);

        // Draw catalyst icon if available
        ItemStack catalyst = NEIDimensionConfig.getCatalystStack(filterDimension);
        if (catalyst != null) {
            GuiContainerManager
                .drawItem(DIM_BUTTON_RECT.x + DIM_BUTTON_RECT.width - 14, DIM_BUTTON_RECT.y - 2, catalyst);
        }
    }

    @Override
    public boolean mouseClicked(GuiRecipe<?> gui, int button, int recipe) {
        // Check if dimension button was clicked
        java.awt.Point mouse = GuiDraw.getMousePosition();
        java.awt.Point offset = gui.getRecipePosition(recipe);

        if (offset != null && recipe == 0) {
            // Calculate relative position within the recipe area
            int recipeX = ((GuiContainer) gui).guiLeft + offset.x;
            int recipeY = ((GuiContainer) gui).guiTop + offset.y;
            int relX = mouse.x - recipeX;
            int relY = mouse.y - recipeY;

            if (DIM_BUTTON_RECT.contains(relX, relY)) {
                cycleDimension(gui, button == 0);
                return true;
            }
        }

        return super.mouseClicked(gui, button, recipe);
    }

    /**
     * Cycle to the next/previous dimension filter and reload recipes.
     */
    private void cycleDimension(GuiRecipe<?> gui, boolean forward) {
        List<Integer> dimIds = NEIDimensionConfig.getDimensionIds();
        int currentIndex = dimIds.indexOf(filterDimension);
        if (currentIndex < 0) currentIndex = 0;

        if (forward) {
            currentIndex = (currentIndex + 1) % dimIds.size();
        } else {
            currentIndex = (currentIndex - 1 + dimIds.size()) % dimIds.size();
        }

        filterDimension = dimIds.get(currentIndex);
        // Set flag to prevent automatic dimension detection from overwriting
        manualDimensionChange = true;

        // Reload recipes with new filter
        arecipes.clear();
        loadAllRecipes();

        // Reopen the NEI GUI to properly refresh the page
        GuiCraftingRecipe.openRecipeGui(getRecipeID());
    }

    @Override
    public String getRecipeID() {
        return getRecipeIdBase() + ".tier" + tier;
    }

    @Override
    public String getHandlerId() {
        // Ensure NEI's handler uniqueness check sees each tier as distinct
        return getRecipeID();
    }

    @Override
    public String getGuiTexture() {
        return LibResources.GUI_NEI_BLANK;
    }

    @Override
    public void loadTransferRects() {
        this.addTransferRect(0, 0, 0, 0);
    }

    @Override
    public void loadAllRecipes() {
        // Set dimension view mode
        currentViewMode = ViewMode.DIMENSION;
        detailItem = null;

        // Use filtered registry for NEI display
        IFocusableRegistry registry = getRegistryForNEI(tier, filterDimension);
        if (registry == null) {
            registry = getRegistry();
        }

        // Get and sort by probability (highest first)
        List<WeightedStackBase> unfocusedList = new ArrayList<>(registry.getUnFocusedList());
        unfocusedList.sort((a, b) -> Double.compare(b.realWeight, a.realWeight));

        for (WeightedStackBase ws : unfocusedList) {
            ItemStack output = ws.getMainStack();
            if (output != null && matchesTextFilter(output)) {
                arecipes.add(new CachedVoidRecipe(ws, registry, tier, filterDimension));
            }
        }
    }

    /**
     * Check if the item matches the current text filter.
     */
    protected boolean matchesTextFilter(ItemStack stack) {
        if (filterText == null || filterText.isEmpty()) {
            return true;
        }
        String displayName = stack.getDisplayName()
            .toLowerCase();
        return displayName.contains(filterText.toLowerCase());
    }

    @Override
    public void loadCraftingRecipes(ItemStack item) {
        // Set item detail view mode
        currentViewMode = ViewMode.ITEM_DETAIL;
        detailItem = item.copy();

        arecipes.clear();

        // Show all dimensions where this item can be mined
        for (int dimId : NEIDimensionConfig.getDimensionIds()) {
            IFocusableRegistry dimRegistry = getRegistryForNEI(tier, dimId);
            if (dimRegistry == null) continue;

            // Search for this item in this dimension's list
            for (WeightedStackBase ws : dimRegistry.getUnFocusedList()) {
                ItemStack output = ws.getMainStack();
                if (output != null && ItemUtils.areStacksEqual(output, item)) {
                    // Found! Add a recipe for this dimension
                    arecipes.add(new CachedVoidRecipe(ws, dimRegistry, tier, dimId));
                    break; // Only add once per dimension
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        arecipes.clear();
        super.loadUsageRecipes(ingredient);
        IFocusableRegistry registry = getRegistry();
        Item item = ingredient.getItem();
        Item coloredLens = MultiBlockBlocks.COLORED_LENS.getItem();
        Item clearLens = MultiBlockBlocks.LENS.getItem();
        boolean isLens = (item == clearLens || item == coloredLens);
        boolean isMiner = (item == Item.getItemFromBlock(getMinerBlock()));

        if (isMiner) {
            // Miner Usage: Show all items in Common dimension
            currentViewMode = ViewMode.DIMENSION;
            detailItem = null;
            filterDimension = NEIDimensionConfig.DIMENSION_COMMON;

            int inputTier = ingredient.getItemDamage();
            if (inputTier != this.tier) return;

            IFocusableRegistry tierRegistry = getRegistryForNEI(inputTier, filterDimension);
            if (tierRegistry == null) tierRegistry = getRegistry(inputTier);
            if (tierRegistry != null) {
                List<WeightedStackBase> unfocusedList = new ArrayList<>(tierRegistry.getUnFocusedList());
                unfocusedList.sort((a, b) -> Double.compare(b.realWeight, a.realWeight));
                for (WeightedStackBase ws : unfocusedList) {
                    ItemStack output = ws.getMainStack();
                    if (output != null) {
                        arecipes.add(new CachedVoidRecipe(ws, tierRegistry, inputTier, filterDimension));
                    }
                }
            }
        } else if (isLens) {
            // Lens Usage: Show items that benefit from this lens
            currentViewMode = ViewMode.LENS_BONUS;
            detailItem = ingredient.copy();

            EnumDye filterDye = null;
            int meta = ingredient.getItemDamage();
            if (item == clearLens && meta == 1) {
                filterDye = EnumDye.CRYSTAL;
            } else if (item == coloredLens) {
                BlockColoredLens lensBlock = (BlockColoredLens) Block.getBlockFromItem(coloredLens);
                filterDye = lensBlock.getFocusColor(meta);
            }

            List<WeightedStackBase> unfocusedList = registry.getUnFocusedList();
            for (WeightedStackBase ws : unfocusedList) {
                ItemStack output = ws.getMainStack();
                if (output != null) {
                    EnumDye preferred = registry.getPrioritizedLens(output);
                    boolean isMatch = (filterDye == null) || (preferred == filterDye);
                    if (isMatch) {
                        arecipes.add(new CachedVoidRecipe(ws, registry, tier, filterDimension));
                    }
                }
            }
        } else {
            // Check if this is a dimension catalyst
            int catalystDimension = NEIDimensionConfig.getDimensionForCatalyst(ingredient);
            if (catalystDimension != NEIDimensionConfig.DIMENSION_COMMON) {
                // Catalyst Usage: Show items for that dimension
                currentViewMode = ViewMode.DIMENSION;
                detailItem = null;
                filterDimension = catalystDimension;

                IFocusableRegistry dimRegistry = getRegistryForNEI(tier, catalystDimension);
                if (dimRegistry != null) {
                    List<WeightedStackBase> unfocusedList = new ArrayList<>(dimRegistry.getUnFocusedList());
                    unfocusedList.sort((a, b) -> Double.compare(b.realWeight, a.realWeight));
                    for (WeightedStackBase ws : unfocusedList) {
                        ItemStack output = ws.getMainStack();
                        if (output != null) {
                            arecipes.add(new CachedVoidRecipe(ws, dimRegistry, tier, catalystDimension));
                        }
                    }
                }
            }
        }
    }

    @Override
    public IUsageHandler getUsageHandler(String inputId, Object... ingredients) {
        if ("item".equals(inputId) && ingredients.length > 0 && ingredients[0] instanceof ItemStack stack) {
            Item minerItem = Item.getItemFromBlock(getMinerBlock());
            if (stack.getItem() == minerItem) {
                int inputTier = stack.getItemDamage();
                // Only the matching tier handler should respond
                // others return null to avoid duplicate tabs
                if (inputTier != this.tier) {
                    return null;
                }
                // Build a fresh handler instance for this tier and populate its usage recipes
                VoidMinerRecipeHandler handler = createForTier(inputTier);
                if (handler != null) {
                    handler.loadUsageRecipes(stack);
                    return handler.numRecipes() > 0 ? handler : null;
                }
            }
        }
        return super.getUsageHandler(inputId, ingredients);
    }

    @Override
    public IUsageHandler getUsageAndCatalystHandler(String inputId, Object... ingredients) {
        // For miner blocks, skip catalyst and use our tier-aware usage handler
        if ("item".equals(inputId) && ingredients.length > 0 && ingredients[0] instanceof ItemStack stack) {
            Item minerItem = Item.getItemFromBlock(getMinerBlock());
            if (stack.getItem() == minerItem) {
                return getUsageHandler(inputId, ingredients);
            }
        }
        return super.getUsageAndCatalystHandler(inputId, ingredients);
    }

    public class CachedVoidRecipe extends CachedBaseRecipe {

        private List<PositionedStack> input;
        private PositionedStack output;
        private int dimensionId;

        public CachedVoidRecipe(WeightedStackBase ws, IFocusableRegistry registry, int tier, int dimId) {
            this.input = new ArrayList<>();
            this.dimensionId = dimId;
            ItemStack outputStack = ws.getMainStack();

            // 1. Dimension Catalyst or Miner Icon (Far Left)
            ItemStack leftIcon = NEIDimensionConfig.getCatalystStack(dimId);
            if (leftIcon == null) {
                leftIcon = new ItemStack(getMinerBlock(), 1, tier);
            }
            this.input.add(new PositionedStack(leftIcon, 4, 16));

            // 2. Clear Lens (Left Center) + Probability
            PositionedStackAdv clearLensStack = new PositionedStackAdv(
                MultiBlockBlocks.LENS.newItemStack(1, 0),
                40,
                16);
            clearLensStack.setChance((float) (ws.realWeight / 100.0f));
            clearLensStack.setTextYOffset(10); // Draw below
            clearLensStack.setTextColor(0x000000);
            clearLensStack.setLabel("Clear");
            clearLensStack.setLabelColor(0x000000);
            this.input.add(clearLensStack);

            // 3. Output Item (Center) skip percentage display
            this.output = new PositionedStack(outputStack, 84, 16);

            // 4. Bonus Lens (Right) + Probability
            EnumDye preferred = registry.getPrioritizedLens(outputStack);
            if (preferred != null) {
                ItemStack lensStack;
                if (preferred == EnumDye.CRYSTAL) {
                    lensStack = MultiBlockBlocks.LENS.newItemStack(1, 1);
                } else {
                    int lensMeta = preferred.ordinal();
                    lensStack = MultiBlockBlocks.COLORED_LENS.newItemStack(1, lensMeta);
                }

                // Calculate chance
                List<WeightedStackBase> focusedList = registry.getFocusedList(preferred, 1.0f);
                double focusedChance = 0;
                for (WeightedStackBase fws : focusedList) {
                    if (fws.isStackEqual(outputStack)) {
                        focusedChance = fws.realWeight;
                        break;
                    }
                }

                PositionedStackAdv bonusLensStack = new PositionedStackAdv(lensStack, 128, 16);
                bonusLensStack.setChance((float) (focusedChance / 100.0f));
                bonusLensStack.setTextYOffset(10); // Draw below
                bonusLensStack.setTextColor(0x000000);
                // Set color label
                String colorName = preferred.getName();
                colorName = Character.toUpperCase(colorName.charAt(0)) + colorName.substring(1);
                bonusLensStack.setLabel(colorName);
                bonusLensStack.setLabelColor(0x000000);
                this.input.add(bonusLensStack);
            }
        }

        /** Legacy constructor for backwards compatibility */
        public CachedVoidRecipe(WeightedStackBase ws, IFocusableRegistry registry, int tier) {
            this(ws, registry, tier, filterDimension);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return input;
        }

        @Override
        public PositionedStack getResult() {
            return output;
        }
    }
}
