package ruiseki.omoshiroikamo.plugin.nei.recipe.cow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public class CowBreedingRecipeHandler extends RecipeHandlerBase {

    public static final String UID = "cows.Breeding";

    @Override
    public String getRecipeID() {
        return UID;
    }

    @Override
    public String getGuiTexture() {
        return LibResources.GUI_NEI_BLANK;
    }

    @Override
    public String getRecipeName() {
        return "Cow Breeding";
    }

    @Override
    public void loadTransferRects() {
        this.addTransferRect(0, 0, 0, 0);
    }

    @Override
    public void loadAllRecipes() {
        Set<CowsRegistryItem> added = new HashSet<>();
        for (CowsRegistryItem chicken : CowsRegistry.INSTANCE.getItems()) {
            if (chicken.getParent1() != null || chicken.getParent2() != null) {
                if (added.add(chicken)) {
                    arecipes.add(new CachedCowsRecipe(chicken));
                }
            }
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        super.loadCraftingRecipes(result);
        Set<CowsRegistryItem> added = new HashSet<>();
        for (CowsRegistryItem cow : CowsRegistry.INSTANCE.getItems()) {
            ItemStack egg = ModItems.COW_SPAWN_EGG.newItemStack(1, cow.getId());
            if (egg.isItemEqual(result) && (cow.getParent1() != null || cow.getParent2() != null)) {
                if (added.add(cow)) {
                    arecipes.add(new CachedCowsRecipe(cow));
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        super.loadUsageRecipes(ingredient);
        Set<CowsRegistryItem> added = new HashSet<>();
        for (CowsRegistryItem cow : CowsRegistry.INSTANCE.getItems()) {

            CowsRegistryItem p1 = cow.getParent1();
            CowsRegistryItem p2 = cow.getParent2();

            if (p1 == null || p2 == null) {
                continue;
            }

            ItemStack egg1 = ModItems.COW_SPAWN_EGG.newItemStack(1, p1.getId());
            ItemStack egg2 = ModItems.COW_SPAWN_EGG.newItemStack(1, p2.getId());

            if (egg1.isItemEqual(ingredient) || egg2.isItemEqual(ingredient)) {
                if (added.add(cow)) {
                    arecipes.add(new CachedCowsRecipe(cow));
                }
            }
        }
    }

    @Override
    public void drawBackground(int recipe) {
        super.drawBackground(recipe);
        drawItemSlot(50, 26);
        drawItemSlot(95, 26);
        drawItemSlot(74, 40);
    }

    public class CachedCowsRecipe extends CachedBaseRecipe {

        private final CowsRegistryItem cow;

        public CachedCowsRecipe(CowsRegistryItem cow) {

            this.cow = cow;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            ArrayList<PositionedStack> stacks = new ArrayList<>();
            if (cow.getParent1() != null && cow.getParent2() != null) {

                CowsRegistryItem parent1 = cow.getParent1();
                ItemStack egg1 = ModItems.COW_SPAWN_EGG.newItemStack(1, parent1.getId());
                PositionedStack parent1Egg = new PositionedStack(egg1, 50, 26);
                stacks.add(parent1Egg);

                CowsRegistryItem parent2 = cow.getParent2();
                ItemStack egg2 = ModItems.COW_SPAWN_EGG.newItemStack(1, parent2.getId());
                PositionedStack parent2Egg = new PositionedStack(egg2, 95, 26);
                stacks.add(parent2Egg);
            }
            return stacks;
        }

        @Override
        public PositionedStack getResult() {
            return new PositionedStack(ModItems.COW_SPAWN_EGG.newItemStack(1, cow.getId()), 74, 40);
        }

        public CowsRegistryItem getCow() {
            return cow;
        }
    }
}
