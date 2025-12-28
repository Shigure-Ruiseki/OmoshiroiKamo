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
import net.minecraft.nbt.NBTTagCompound;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
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
        // Prefer the first recipe's dimension (if available) so tab name matches what
        // is shown
        int dimId = filterDimension;
        if (!arecipes.isEmpty() && arecipes.get(0) instanceof CachedVoidRecipe first) {
            dimId = first.getDimensionId();
        }
        String dimName = NEIDimensionConfig.getDisplayName(dimId);
        return getMinerNameBase() + " T" + (tier + 1) + " [" + dimName + "]";
    }

    // --- UI Drawing ---

    /** Rectangle for header info area */
    private static final Rectangle HEADER_RECT = new Rectangle(5, 10, 150, 12);

    @Override
    public void drawForeground(int recipe) {
        super.drawForeground(recipe);

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        GL11.glDisable(GL11.GL_LIGHTING);

        // Get the cached recipe to access dimension info
        if (recipe >= 0 && recipe < arecipes.size()) {
            CachedRecipe cached = arecipes.get(recipe);
            if (cached instanceof CachedVoidRecipe voidRecipe) {
                // Draw dimension name above catalyst icon (position X=25, Y=14 - text above at
                // Y=4)
                String dimName = NEIDimensionConfig.getDisplayName(voidRecipe.getDimensionId());
                fr.drawString(dimName, 25, 4, 0x404040);
            }
        }

        // Draw mode-specific header on first recipe only
        if (recipe == 0) {
            switch (currentViewMode) {
                case ITEM_DETAIL:
                    // // Show item name in header
                    // if (detailItem != null) {
                    // String itemName = detailItem.getDisplayName();
                    // fr.drawString(itemName, HEADER_RECT.x, HEADER_RECT.y + 2, 0x404040);
                    // }
                    break;

                case LENS_BONUS:
                    // Show lens name in header
                    if (detailItem != null) {
                        String lensName = detailItem.getDisplayName() + " Bonus";
                        fr.drawString(lensName, HEADER_RECT.x, HEADER_RECT.y + 2, 0x404040);
                    }
                    break;

                case DIMENSION:
                default:
                    // Dimension mode header is handled per-recipe above
                    break;
            }
        }
    }

    @Override
    public boolean mouseClicked(GuiRecipe<?> gui, int button, int recipe) {
        // Only handle clicks in DIMENSION mode (for dimension cycling)
        if (currentViewMode != ViewMode.DIMENSION) {
            return super.mouseClicked(gui, button, recipe);
        }

        java.awt.Point mouse = GuiDraw.getMousePosition();
        java.awt.Point offset = gui.getRecipePosition(recipe);

        if (offset != null && recipe == 0) {
            int recipeX = ((GuiContainer) gui).guiLeft + offset.x;
            int recipeY = ((GuiContainer) gui).guiTop + offset.y;
            int relX = mouse.x - recipeX;
            int relY = mouse.y - recipeY;

            if (HEADER_RECT.contains(relX, relY)) {
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
        manualDimensionChange = true;

        // Reload recipes with new dimension
        arecipes.clear();
        loadAllRecipes();

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
    public int recipiesPerPage() {
        // Lens bonus view shows compact item list, so more items per page
        if (currentViewMode == ViewMode.LENS_BONUS) {
            return 12;
        }
        return 6; // Default for detailed views
    }

    /**
     * Set a custom display name for an ItemStack via NBT.
     */
    protected static void setDisplayName(ItemStack stack, String name) {
        if (stack == null || name == null) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound display = stack.getTagCompound()
            .getCompoundTag("display");
        display.setString("Name", name);
        stack.getTagCompound()
            .setTag("display", display);
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
                // Crystal Lens
                filterDye = EnumDye.CRYSTAL;
            } else if (item == coloredLens) {
                BlockColoredLens lensBlock = (BlockColoredLens) Block.getBlockFromItem(coloredLens);
                filterDye = lensBlock.getFocusColor(meta);
            }
            // Note: Clear Lens (meta 0) keeps filterDye = null

            // Get items and sort by probability
            List<WeightedStackBase> allItems = new ArrayList<>(registry.getUnFocusedList());

            // Filter: only show items that have this lens as their bonus lens
            List<WeightedStackBase> bonusItems = new ArrayList<>();
            for (WeightedStackBase ws : allItems) {
                ItemStack output = ws.getMainStack();
                if (output != null) {
                    EnumDye preferred = registry.getPrioritizedLens(output);
                    if (filterDye == null) {
                        // Clear Lens: show all items
                        bonusItems.add(ws);
                    } else if (preferred == filterDye) {
                        // Colored/Crystal: only show items that benefit from this lens
                        bonusItems.add(ws);
                    }
                }
            }

            // Sort by probability (highest first)
            bonusItems.sort((a, b) -> Double.compare(b.realWeight, a.realWeight));

            // Group items into grids (16 items per grid: 8 columns × 2 rows)
            final int ITEMS_PER_GRID = 16;
            for (int i = 0; i < bonusItems.size(); i += ITEMS_PER_GRID) {
                int end = Math.min(i + ITEMS_PER_GRID, bonusItems.size());
                List<WeightedStackBase> gridItems = bonusItems.subList(i, end);
                arecipes.add(new CachedLensGridRecipe(new ArrayList<>(gridItems)));
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
        // For miner blocks and dimension catalysts, use our custom usage handler
        // This ensures proper dimension filtering for catalysts (instead of
        // parent's loadCraftingRecipes which ignores our dimension filter)
        if ("item".equals(inputId) && ingredients.length > 0 && ingredients[0] instanceof ItemStack stack) {
            Item minerItem = Item.getItemFromBlock(getMinerBlock());
            if (stack.getItem() == minerItem) {
                return getUsageHandler(inputId, ingredients);
            }

            // Check if this is a dimension catalyst
            int catalystDimension = NEIDimensionConfig.getDimensionForCatalyst(stack);
            if (catalystDimension != NEIDimensionConfig.DIMENSION_COMMON) {
                // Use loadUsageRecipes to apply proper dimension filtering
                VoidMinerRecipeHandler handler = createForTier(tier);
                if (handler != null) {
                    handler.loadUsageRecipes(stack);
                    return handler.numRecipes() > 0 ? handler : null;
                }
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

            // Layout positions adjusted: more centered, below header
            // Kept comfortably below the header to avoid overlap
            final int ITEM_Y = 22;

            switch (currentViewMode) {
                case LENS_BONUS:
                    // Lens view: Show only output item (simple list)
                    this.output = new PositionedStack(outputStack, 5, ITEM_Y);
                    break;

                case ITEM_DETAIL:
                    // Item detail: Show [Catalyst][Lens+%][Item][BonusLens+%]
                    // Centered layout (NEI recipe area is ~166px wide)
                    ItemStack catalystIcon = NEIDimensionConfig.getCatalystStack(dimId);
                    if (catalystIcon == null) {
                        catalystIcon = new ItemStack(getMinerBlock(), 1, tier);
                    }
                    this.input.add(new PositionedStack(catalystIcon, 25, ITEM_Y));

                    PositionedStackAdv clearLens = new PositionedStackAdv(
                        MultiBlockBlocks.LENS.newItemStack(1, 0),
                        50,
                        ITEM_Y);
                    clearLens.setChance((float) (ws.realWeight / 100.0f));
                    clearLens.setTextYOffset(10);
                    clearLens.setTextColor(0x000000);
                    this.input.add(clearLens);

                    this.output = new PositionedStack(outputStack, 80, ITEM_Y);

                    addBonusLens(ws, registry, outputStack, 110, ITEM_Y);
                    break;

                case DIMENSION:
                default:
                    // Dimension view: [Catalyst][Lens+%][Item][BonusLens+%]
                    ItemStack dimCatalyst = NEIDimensionConfig.getCatalystStack(dimId);
                    if (dimCatalyst == null) {
                        dimCatalyst = new ItemStack(getMinerBlock(), 1, tier);
                    }
                    this.input.add(new PositionedStack(dimCatalyst, 25, ITEM_Y));

                    PositionedStackAdv dimLens = new PositionedStackAdv(
                        MultiBlockBlocks.LENS.newItemStack(1, 0),
                        50,
                        ITEM_Y);
                    dimLens.setChance((float) (ws.realWeight / 100.0f));
                    dimLens.setTextYOffset(10);
                    dimLens.setTextColor(0x000000);
                    this.input.add(dimLens);

                    this.output = new PositionedStack(outputStack, 80, ITEM_Y);

                    addBonusLens(ws, registry, outputStack, 110, ITEM_Y);
                    break;
            }
        }

        private void addBonusLens(WeightedStackBase ws, IFocusableRegistry registry, ItemStack outputStack, int x,
            int y) {
            EnumDye preferred = registry.getPrioritizedLens(outputStack);
            if (preferred != null) {
                ItemStack lensStack;
                if (preferred == EnumDye.CRYSTAL) {
                    lensStack = MultiBlockBlocks.LENS.newItemStack(1, 1);
                } else {
                    lensStack = MultiBlockBlocks.COLORED_LENS.newItemStack(1, preferred.ordinal());
                }

                List<WeightedStackBase> focusedList = registry.getFocusedList(preferred, 1.0f);
                double focusedChance = 0;
                for (WeightedStackBase fws : focusedList) {
                    if (fws.isStackEqual(outputStack)) {
                        focusedChance = fws.realWeight;
                        break;
                    }
                }

                PositionedStackAdv bonusLens = new PositionedStackAdv(lensStack, x, y);
                bonusLens.setChance((float) (focusedChance / 100.0f));
                bonusLens.setTextYOffset(10);
                bonusLens.setTextColor(0x000000);
                this.input.add(bonusLens);
            }
        }

        /** Legacy constructor for backwards compatibility */
        public CachedVoidRecipe(WeightedStackBase ws, IFocusableRegistry registry, int tier) {
            this(ws, registry, tier, filterDimension);
        }

        public int getDimensionId() {
            return dimensionId;
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

    /**
     * Grid layout recipe for lens bonus view.
     * Displays multiple items in a grid format.
     */
    public class CachedLensGridRecipe extends CachedBaseRecipe {

        private List<PositionedStack> items;

        // Grid configuration: 8 items per row, 2 rows
        private static final int ITEMS_PER_ROW = 8;
        private static final int ITEM_SPACING = 18;
        private static final int START_X = 5;
        private static final int START_Y = 22;

        public CachedLensGridRecipe(List<WeightedStackBase> weightedItems) {
            this.items = new ArrayList<>();
            int x = START_X;
            int y = START_Y;
            int col = 0;

            for (WeightedStackBase ws : weightedItems) {
                ItemStack stack = ws.getMainStack();
                if (stack != null) {
                    this.items.add(new PositionedStack(stack, x, y));
                }
                col++;
                if (col >= ITEMS_PER_ROW) {
                    col = 0;
                    x = START_X;
                    y += ITEM_SPACING;
                } else {
                    x += ITEM_SPACING;
                }
            }
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return items;
        }

        @Override
        public PositionedStack getResult() {
            return null; // No single result for grid
        }
    }
}
