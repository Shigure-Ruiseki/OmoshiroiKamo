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
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedStackBase;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.BlockColoredLens;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.nei.PositionedStackAdv;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public class QuantumResExtractorRecipeHandler extends RecipeHandlerBase {

    @Override
    public String getRecipeName() {
        return "Void Resource Miner";
    }

    @Override
    public String getRecipeID() {
        return ModObject.blockQuantumResExtractor.getRegistryName();
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
        IFocusableRegistry[] registries = QuantumExtractorRecipes.resRegistry;
        for (int tier = 0; tier < registries.length; tier++) {
            IFocusableRegistry registry = registries[tier];
            for (WeightedStackBase ws : registry.getUnFocusedList()) {
                ItemStack output = ws.getMainStack();
                if (output != null) {
                    if (added.add(ws)) {
                        arecipes.add(new CachedVoidResRecipe(ws, null, tier));
                    }
                }
            }
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack item) {
        super.loadCraftingRecipes(item);
        Set<WeightedStackBase> added = new HashSet<>();
        IFocusableRegistry[] registries = QuantumExtractorRecipes.resRegistry;
        for (int tier = 0; tier < registries.length; tier++) {
            IFocusableRegistry registry = registries[tier];
            for (WeightedStackBase ws : registry.getUnFocusedList()) {
                ItemStack output = ws.getMainStack();
                if (output != null && ItemUtils.areStacksEqual(output, item)) {
                    if (added.add(ws)) {
                        arecipes.add(new CachedVoidResRecipe(ws, null, tier));
                    }
                }
            }

            EnumDye color = registry.getPrioritizedLens(item);
            if (color != null) {
                for (WeightedStackBase ws : registry.getFocusedList(color, 1.0f)) {
                    ItemStack output = ws.getMainStack();
                    if (output != null && ItemUtils.areStacksEqual(output, item)) {
                        if (added.add(ws)) {
                            arecipes.add(new CachedVoidResRecipe(ws, color, tier));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        super.loadUsageRecipes(ingredient);
        Set<WeightedStackBase> added = new HashSet<>();

        Item item = ingredient.getItem();
        Item coloredLend = ModBlocks.COLORED_LENS.getItem();
        Item lens = ModBlocks.LENS.getItem();
        boolean isLens = (item == lens || item == coloredLend);

        if (isLens) {
            IFocusableRegistry[] registries = QuantumExtractorRecipes.resRegistry;
            for (int tier = 0; tier < registries.length; tier++) {
                IFocusableRegistry registry = registries[tier];

                EnumDye dye = null;
                Item itemType = ingredient.getItem();
                int meta = ingredient.getItemDamage();

                if (itemType == lens) {
                    if (meta == 1) {
                        dye = EnumDye.CRYSTAL;
                    }
                } else if (itemType == coloredLend) {
                    BlockColoredLens coloredLens = (BlockColoredLens) Block.getBlockFromItem(itemType);
                    dye = coloredLens.getFocusColor(meta);
                }

                List<WeightedStackBase> listToUse = (dye != null) ? registry.getFocusedList(dye, 1.0f)
                    : registry.getUnFocusedList();

                for (WeightedStackBase ws : listToUse) {
                    ItemStack output = ws.getMainStack();
                    if (output != null) {
                        if (dye == null || registry.getPrioritizedLens(output) == dye) {
                            if (added.add(ws)) {
                                arecipes.add(new CachedVoidResRecipe(ws, dye, tier));
                            }
                        }
                    }
                }
            }
        }
    }

    public class CachedVoidResRecipe extends CachedBaseRecipe {

        private List<PositionedStack> input;
        private PositionedStack output;

        public CachedVoidResRecipe(WeightedStackBase recipe, EnumDye color, int tier) {
            this.input = new ArrayList<>();
            this.input.add(new PositionedStack(ModBlocks.QUANTUM_RES_EXTRACTOR.newItemStack(1, tier), 25, 16));

            if (color == null) {
                this.input.add(new PositionedStack(ModBlocks.LENS.newItemStack(1, 0), 75, 16));
            } else {
                int lens = color.ordinal();
                if (lens == 16) {
                    this.input.add(new PositionedStack(ModBlocks.LENS.newItemStack(1, 1), 75, 16));
                } else {
                    this.input.add(new PositionedStack(ModBlocks.COLORED_LENS.newItemStack(1, lens), 75, 16));
                }
            }
            this.output = new PositionedStackAdv(recipe.getMainStack(), 125, 16)
                .setChance((float) recipe.getWeight() / 100);
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
