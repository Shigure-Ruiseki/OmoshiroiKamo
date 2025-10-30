package ruiseki.omoshiroikamo.plugin.nei.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.recipe.chance.ChanceItemStack;
import ruiseki.omoshiroikamo.common.recipe.machine.MachineRecipe;
import ruiseki.omoshiroikamo.common.recipe.machine.MachineRecipeRegistry;
import ruiseki.omoshiroikamo.common.util.OreDictUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.nei.PositionedStackAdv;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public class AnvilRecipeHandler extends RecipeHandlerBase {

    @Override
    public String getRecipeName() {
        return "Anvil";
    }

    @Override
    public String getRecipeID() {
        return ModObject.blockAnvil.getRegistryName();
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
        Set<MachineRecipe> added = new HashSet<>();
        for (MachineRecipe recipe : MachineRecipeRegistry.getRecipes(ModObject.blockAnvil.unlocalisedName)) {
            if (added.add(recipe)) {
                arecipes.add(new CachedAnvilRecipe(recipe));
            }
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack item) {
        super.loadCraftingRecipes(item);
        Set<MachineRecipe> added = new HashSet<>();
        for (MachineRecipe recipe : MachineRecipeRegistry.getRecipes(ModObject.blockAnvil.unlocalisedName)) {
            for (ChanceItemStack out : recipe.getItemOutputs()) {
                if (NEIServerUtils.areStacksSameTypeCrafting(out.stack, item)) {
                    if (added.add(recipe)) {
                        arecipes.add(new CachedAnvilRecipe(recipe));
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        super.loadUsageRecipes(ingredient);
        Set<MachineRecipe> added = new HashSet<>();
        boolean isHammer = ingredient.getItem() == ModItems.HAMMER.get();
        for (MachineRecipe recipe : MachineRecipeRegistry.getRecipes(ModObject.blockAnvil.unlocalisedName)) {
            if (isHammer) {
                if (added.add(recipe)) {
                    arecipes.add(new CachedAnvilRecipe(recipe));
                }
            } else {
                for (ChanceItemStack in : recipe.getItemInputs()) {
                    if (OreDictUtils.isOreDictMatch(in.stack, ingredient)
                        || NEIServerUtils.areStacksSameTypeCrafting(in.stack, ingredient)) {
                        if (added.add(recipe)) {
                            arecipes.add(new CachedAnvilRecipe(recipe));
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void drawBackground(int recipeIndex) {
        super.drawBackground(recipeIndex);
        drawStretchedItemSlot(15, 8, 54, 54);
        drawStretchedItemSlot(95, 8, 54, 54);
    }

    public class CachedAnvilRecipe extends CachedBaseRecipe {

        private final List<ChanceItemStack> itemInputs = new ArrayList<>();
        private final List<ChanceItemStack> itemOutputs = new ArrayList<>();

        public CachedAnvilRecipe(MachineRecipe recipe) {
            if (recipe.getItemInputs() != null) {
                itemInputs.addAll(recipe.getItemInputs());
            }
            if (recipe.getItemOutputs() != null) {
                itemOutputs.addAll(recipe.getItemOutputs());
            }
        }

        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> result = new ArrayList<>();
            for (int i = 0; i < itemInputs.size(); i++) {
                ChanceItemStack is = itemInputs.get(i);
                if (is == null) {
                    continue;
                }
                int col = i % 3;
                int row = i / 3;
                int x = 15 + col * 18;
                int y = 8 + row * 18;
                result.add(new PositionedStackAdv(is.stack, x + 1, y + 1).setChance(is.chance));
            }
            return result;
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            List<PositionedStack> result = new ArrayList<>();
            for (int i = 0; i < itemOutputs.size(); i++) {
                ChanceItemStack is = itemOutputs.get(i);
                if (is == null) {
                    continue;
                }
                int col = i % 3;
                int row = i / 3;
                int x = 95 + col * 18;
                int y = 8 + row * 18;
                result.add(new PositionedStackAdv(is.stack, x + 1, y + 1).setChance(is.chance));
            }

            result.add(new PositionedStackAdv(ModItems.HAMMER.newItemStack(), 73, 16 + 1));
            result.add(new PositionedStackAdv(ModBlocks.ANVIL.newItemStack(), 73, 34 + 1));
            return result;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }
    }
}
