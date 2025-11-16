package ruiseki.omoshiroikamo.plugin.nei.recipe.voidMiner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.WeightedStackBase;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.BlockColoredLens;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.common.util.ItemUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.nei.PositionedStackAdv;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public class QuantumOreExtractorRecipeHandler extends RecipeHandlerBase {

    @Override
    public String getRecipeName() {
        return "Void Ore Miner";
    }

    @Override
    public String getRecipeID() {
        return ModObject.blockQuantumOreExtractor.getRegistryName();
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
        Set<WeightedStackBase> added = new HashSet<>();
        IFocusableRegistry registry = QuantumExtractorRecipes.quantumOreExtractorRegistry;
        for (WeightedStackBase ws : registry.getUnFocusedList()) {
            ItemStack output = ws.getMainStack();
            if (output != null) {
                if (added.add(ws)) {
                    arecipes.add(new CachedVoidOreRecipe(ws, null));
                }
            }
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack item) {
        super.loadCraftingRecipes(item);
        Set<WeightedStackBase> added = new HashSet<>();
        IFocusableRegistry registry = QuantumExtractorRecipes.quantumOreExtractorRegistry;

        for (WeightedStackBase ws : registry.getUnFocusedList()) {
            ItemStack output = ws.getMainStack();
            if (output != null && ItemUtils.areStacksEqual(output, item)) {
                if (added.add(ws)) {
                    arecipes.add(new CachedVoidOreRecipe(ws, null));
                }
            }
        }

        EnumDye color = registry.getPrioritizedLens(item);
        if (color != null) {
            for (WeightedStackBase ws : registry.getFocusedList(color, 1.0f)) {
                ItemStack output = ws.getMainStack();
                if (output != null && ItemUtils.areStacksEqual(output, item)) {
                    if (added.add(ws)) {
                        arecipes.add(new CachedVoidOreRecipe(ws, color));
                    }
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        super.loadUsageRecipes(ingredient);
        Set<WeightedStackBase> added = new HashSet<>();
        IFocusableRegistry registry = QuantumExtractorRecipes.quantumOreExtractorRegistry;

        Item item = ingredient.getItem();
        Item coloredLend = ModBlocks.COLORED_LENS.getItem();
        Item lens = ModBlocks.LENS.getItem();
        boolean isLens = (item == lens || item == coloredLend);

        if (isLens) {
            if (ingredient.getItem() == lens) {
                for (WeightedStackBase ws : registry.getUnFocusedList()) {
                    ItemStack output = ws.getMainStack();
                    if (output != null) {
                        if (added.add(ws)) {
                            arecipes.add(new CachedVoidOreRecipe(ws, null));
                        }
                    }
                }
            } else {
                BlockColoredLens coloredLens = (BlockColoredLens) Block.getBlockFromItem(ingredient.getItem());
                EnumDye color = coloredLens.getFocusColor(ingredient.getItemDamage());
                List<WeightedStackBase> focusedList = registry.getFocusedList(color, 1.0f);

                for (WeightedStackBase ws : focusedList) {
                    ItemStack output = ws.getMainStack();
                    if (output != null && registry.getPrioritizedLens(output) == color) {
                        if (added.add(ws)) {
                            arecipes.add(new CachedVoidOreRecipe(ws, color));
                        }
                    }
                }
            }
        }
    }

    public class CachedVoidOreRecipe extends CachedBaseRecipe {

        private List<PositionedStack> input;
        private PositionedStack output;
        private EnumDye color;

        public CachedVoidOreRecipe(WeightedStackBase recipe, EnumDye color) {
            this.input = new ArrayList<>();
            List<ItemStack> miners = new ArrayList<>();
            miners.add(ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 0));
            miners.add(ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 1));
            miners.add(ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 2));
            miners.add(ModBlocks.QUANTUM_ORE_EXTRACTOR.newItemStack(1, 3));
            this.input.add(new PositionedStack(miners, 25, 16));

            this.color = color;
            if (color == null) {
                this.input.add(new PositionedStack(ModBlocks.LENS.newItemStack(1, 0), 75, 16));
            } else {
                int lens = color.ordinal();
                this.input.add(new PositionedStack(ModBlocks.COLORED_LENS.newItemStack(1, lens), 75, 16));
            }
            this.output = new PositionedStackAdv(recipe.getMainStack(), 125, 16)
                .setChance((float) recipe.getWeight() / 100);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks, input);
        }

        public EnumDye getColor() {
            return color;
        }

        @Override
        public PositionedStack getResult() {
            return output;
        }
    }
}
