package ruiseki.omoshiroikamo.plugin.nei.recipe.voidMiner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.DyeColor;

import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.WeightedStackBase;
import ruiseki.omoshiroikamo.common.block.multiblock.voidMiner.lens.BlockLaserLens;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.recipe.voidMiner.VoidMinerRecipes;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.nei.PositionedStackAdv;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public class VoidOreRecipeHandler extends RecipeHandlerBase {

    @Override
    public String getRecipeName() {
        return "Void Ore Miner";
    }

    @Override
    public String getRecipeID() {
        return ModObject.blockVoidOreMiner.getRegistryName();
    }

    @Override
    public String getGuiTexture() {
        return LibResources.GUI_NEI_BLANK;
    }

    @Override
    public void loadAllRecipes() {}

    @Override
    public void loadCraftingRecipes(ItemStack item) {
        super.loadCraftingRecipes(item);
        Set<WeightedStackBase> added = new HashSet<>();
        IFocusableRegistry registry = VoidMinerRecipes.voidOreMinerRegistry;

        for (WeightedStackBase ws : registry.getUnFocusedList()) {
            ItemStack output = ws.getMainStack();
            if (output != null && ItemStack.areItemStacksEqual(output, item)) {
                if (added.add(ws)) {
                    arecipes.add(new CachedVoidOreRecipe(ws, null));
                }
            }
        }

        DyeColor color = registry.getPrioritizedLens(item);
        if (color != null) {
            for (WeightedStackBase ws : registry.getFocusedList(color, 1.0f)) {
                ItemStack output = ws.getMainStack();
                if (output != null && ItemStack.areItemStacksEqual(output, item)) {
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
        IFocusableRegistry registry = VoidMinerRecipes.voidOreMinerRegistry;

        Item item = ingredient.getItem();
        Item lensItem = ModBlocks.LASER_LENS.getItem();
        Item minerItem = ModBlocks.VOID_ORE_MINER.getItem();

        boolean isMiner = item == minerItem;
        boolean isLens = item == lensItem;

        if (isMiner) {
            for (WeightedStackBase ws : registry.getUnFocusedList()) {
                ItemStack output = ws.getMainStack();
                if (output != null) {
                    if (added.add(ws)) {
                        arecipes.add(new CachedVoidOreRecipe(ws, null));
                    }
                }
            }
        }

        if (isLens) {
            if (ingredient.getItemDamage() == 0) {
                for (WeightedStackBase ws : registry.getUnFocusedList()) {
                    ItemStack output = ws.getMainStack();
                    if (output != null) {
                        if (added.add(ws)) {
                            arecipes.add(new CachedVoidOreRecipe(ws, null));
                        }
                    }
                }
            } else {
                BlockLaserLens lens = (BlockLaserLens) Block.getBlockFromItem(ingredient.getItem());
                DyeColor color = lens.getFocusColor(ingredient.getItemDamage());
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
        private DyeColor color;

        public CachedVoidOreRecipe(WeightedStackBase recipe, DyeColor color) {
            this.input = new ArrayList<>();
            List<ItemStack> miners = new ArrayList<>();
            miners.add(ModBlocks.VOID_ORE_MINER.newItemStack(1, 0));
            miners.add(ModBlocks.VOID_ORE_MINER.newItemStack(1, 1));
            miners.add(ModBlocks.VOID_ORE_MINER.newItemStack(1, 2));
            miners.add(ModBlocks.VOID_ORE_MINER.newItemStack(1, 3));
            this.input.add(new PositionedStack(miners, 25, 16));

            this.color = color;
            if (color == null) {
                this.input.add(new PositionedStack(ModBlocks.LASER_LENS.newItemStack(1, 0), 75, 16));
            } else {
                int lens = color.ordinal() + 1;
                this.input.add(new PositionedStack(ModBlocks.LASER_LENS.newItemStack(1, lens), 75, 16));
            }
            this.output = new PositionedStackAdv(recipe.getMainStack(), 125, 16)
                .setChance((float) recipe.getWeight() / 100);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks, input);
        }

        public DyeColor getColor() {
            return color;
        }

        @Override
        public PositionedStack getResult() {
            return output;
        }
    }
}
