package ruiseki.omoshiroikamo.plugin.nei.recipe.voidMiner;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedStackBase;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.BlockColoredLens;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.common.util.item.ItemUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.nei.PositionedStackAdv;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public class QuantumResExtractorRecipeHandler extends RecipeHandlerBase {

    private final int tier;

    public QuantumResExtractorRecipeHandler(int tier) {
        this.tier = tier;
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new QuantumResExtractorRecipeHandler(tier);
    }

    @Override
    public String getRecipeName() {
        return "Void Resource Miner Tier " + (tier + 1);
    }

    @Override
    public String getRecipeID() {
        return ModObject.blockQuantumResExtractor.getRegistryName() + ".tier" + tier;
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
    public void drawExtras(int recipe) {
        // No extra text drawing needed
    }

    @Override
    public void loadAllRecipes() {
        IFocusableRegistry registry = QuantumExtractorRecipes.resRegistry[tier];
        List<WeightedStackBase> unfocusedList = registry.getUnFocusedList();

        for (WeightedStackBase ws : unfocusedList) {
            ItemStack output = ws.getMainStack();
            if (output != null) {
                arecipes.add(new CachedVoidResRecipe(ws, registry, tier));
            }
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack item) {
        super.loadCraftingRecipes(item);
        IFocusableRegistry registry = QuantumExtractorRecipes.resRegistry[tier];
        List<WeightedStackBase> unfocusedList = registry.getUnFocusedList();

        for (WeightedStackBase ws : unfocusedList) {
            ItemStack output = ws.getMainStack();
            if (output != null && ItemUtils.areStacksEqual(output, item)) {
                arecipes.add(new CachedVoidResRecipe(ws, registry, tier));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        super.loadUsageRecipes(ingredient);
        IFocusableRegistry registry = QuantumExtractorRecipes.resRegistry[tier];
        Item item = ingredient.getItem();
        Item coloredLens = ModBlocks.COLORED_LENS.getItem();
        Item clearLens = ModBlocks.LENS.getItem();
        boolean isLens = (item == clearLens || item == coloredLens);
        boolean isMiner = (item == Item.getItemFromBlock(ModBlocks.QUANTUM_RES_EXTRACTOR.get()));

        if (isMiner) {
            if (ingredient.getItemDamage() == tier) {
                loadAllRecipes();
            }
        } else if (isLens) {
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
                    boolean isMatch = false;
                    if (filterDye == null) {
                        isMatch = true;
                    } else {
                        if (preferred == filterDye) {
                            isMatch = true;
                        }
                    }
                    if (isMatch) {
                        arecipes.add(new CachedVoidResRecipe(ws, registry, tier));
                    }
                }
            }
        }
    }

    public class CachedVoidResRecipe extends CachedBaseRecipe {

        private List<PositionedStack> input;
        private PositionedStack output;

        public CachedVoidResRecipe(WeightedStackBase ws, IFocusableRegistry registry, int tier) {
            this.input = new ArrayList<>();
            ItemStack outputStack = ws.getMainStack();

            this.input.add(new PositionedStack(ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, tier), 4, 16));

            PositionedStackAdv clearLensStack = new PositionedStackAdv(ModBlocks.LENS.newItemStack(1, 0), 40, 16);
            clearLensStack.setChance((float) (ws.realWeight / 100.0f));
            clearLensStack.setTextYOffset(10);
            this.input.add(clearLensStack);

            this.output = new PositionedStackAdv(outputStack, 84, 16);

            EnumDye preferred = registry.getPrioritizedLens(outputStack);
            if (preferred != null) {
                ItemStack lensStack;
                if (preferred == EnumDye.CRYSTAL) {
                    lensStack = ModBlocks.LENS.newItemStack(1, 1);
                } else {
                    int lensMeta = preferred.ordinal();
                    lensStack = ModBlocks.COLORED_LENS.newItemStack(1, lensMeta);
                }

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
                bonusLensStack.setTextYOffset(10);
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
