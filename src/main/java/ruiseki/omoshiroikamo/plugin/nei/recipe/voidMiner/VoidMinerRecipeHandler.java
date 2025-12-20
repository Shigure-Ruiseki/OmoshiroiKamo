package ruiseki.omoshiroikamo.plugin.nei.recipe.voidMiner;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IUsageHandler;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedStackBase;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.BlockColoredLens;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.nei.PositionedStackAdv;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public abstract class VoidMinerRecipeHandler extends RecipeHandlerBase {

    protected final int tier;

    public VoidMinerRecipeHandler(int tier) {
        this.tier = tier;
    }

    protected abstract IFocusableRegistry getRegistry();

    protected abstract IFocusableRegistry getRegistry(int tier);

    // Factory for spawning the handler of a specific tier (used when delegating
    // usage requests)
    protected abstract VoidMinerRecipeHandler createForTier(int tier);

    protected abstract Block getMinerBlock();

    protected abstract String getMinerNameBase();

    protected abstract String getRecipeIdBase();

    @Override
    public String getRecipeName() {
        // Show tiered name so each NEI tab is unique and clear
        return getMinerNameBase() + " Tier " + (tier + 1);
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
        IFocusableRegistry registry = getRegistry();
        List<WeightedStackBase> unfocusedList = registry.getUnFocusedList();
        for (WeightedStackBase ws : unfocusedList) {
            ItemStack output = ws.getMainStack();
            if (output != null) {
                arecipes.add(new CachedVoidRecipe(ws, registry, tier));
            }
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack item) {
        arecipes.clear();
        super.loadCraftingRecipes(item);
        IFocusableRegistry registry = getRegistry();
        List<WeightedStackBase> unfocusedList = registry.getUnFocusedList();

        for (WeightedStackBase ws : unfocusedList) {
            ItemStack output = ws.getMainStack();
            if (output != null && ItemUtils.areStacksEqual(output, item)) {
                arecipes.add(new CachedVoidRecipe(ws, registry, tier));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        arecipes.clear();
        super.loadUsageRecipes(ingredient);
        IFocusableRegistry registry = getRegistry();
        Item item = ingredient.getItem();
        Item coloredLens = ModBlocks.COLORED_LENS.getItem();
        Item clearLens = ModBlocks.LENS.getItem();
        boolean isLens = (item == clearLens || item == coloredLens);
        boolean isMiner = (item == Item.getItemFromBlock(getMinerBlock()));

        if (isMiner) {
            int inputTier = ingredient.getItemDamage();
            // Only process when the handler tier matches the item tier so the correct
            // tab/catalyst is used
            if (inputTier != this.tier) {
                return;
            }
            IFocusableRegistry tierRegistry = getRegistry(inputTier);
            if (tierRegistry != null) {
                List<WeightedStackBase> unfocusedList = tierRegistry.getUnFocusedList();
                int before = arecipes.size();
                for (WeightedStackBase ws : unfocusedList) {
                    ItemStack output = ws.getMainStack();
                    if (output != null) {
                        arecipes.add(new CachedVoidRecipe(ws, tierRegistry, inputTier));
                    }
                }
            }
        } else if (isLens) {
            // Filter by lens
            EnumDye filterDye = null; // null means clear/no lens
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
                    // Check if this recipe uses the lens
                    EnumDye preferred = registry.getPrioritizedLens(output);
                    boolean isMatch = false;

                    if (filterDye == null) {
                        // Clear lens usage - shows for ALL items since clear lens is default
                        isMatch = true;
                    } else {
                        // Colored lens usage - shows only if this item matches the lens
                        if (preferred == filterDye) {
                            isMatch = true;
                        }
                    }

                    if (isMatch) {
                        arecipes.add(new CachedVoidRecipe(ws, registry, tier));
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
                // Only the matching tier handler should respond; others return null to avoid
                // duplicate tabs
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
        // For miner blocks, skip catalyst short-circuit and use our tier-aware usage
        // handler
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

        public CachedVoidRecipe(WeightedStackBase ws, IFocusableRegistry registry, int tier) {
            this.input = new ArrayList<>();
            ItemStack outputStack = ws.getMainStack();

            // 1. Miner Icon (Far Left)
            this.input.add(new PositionedStack(new ItemStack(getMinerBlock(), 1, tier), 4, 16));

            // 2. Clear Lens (Left Center) + Probability
            PositionedStackAdv clearLensStack = new PositionedStackAdv(ModBlocks.LENS.newItemStack(1, 0), 40, 16);
            clearLensStack.setChance((float) (ws.realWeight / 100.0f));
            clearLensStack.setTextYOffset(10); // Draw below
            clearLensStack.setTextColor(0x000000);
            this.input.add(clearLensStack);

            // 3. Output Item (Center) skip percentage display
            this.output = new PositionedStack(outputStack, 84, 16);

            // 4. Bonus Lens (Right) + Probability
            EnumDye preferred = registry.getPrioritizedLens(outputStack);
            if (preferred != null) {
                ItemStack lensStack;
                if (preferred == EnumDye.CRYSTAL) {
                    lensStack = ModBlocks.LENS.newItemStack(1, 1);
                } else {
                    int lensMeta = preferred.ordinal();
                    lensStack = ModBlocks.COLORED_LENS.newItemStack(1, lensMeta);
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
                this.input.add(bonusLensStack);
            }
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
