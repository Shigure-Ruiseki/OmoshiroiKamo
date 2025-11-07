package ruiseki.omoshiroikamo.plugin.nei.recipe.chicken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public class ChickenBreedingRecipeHandler extends RecipeHandlerBase {

    public static final String UID = "chickens.Breeding";

    @Override
    public String getRecipeID() {
        return UID;
    }

    @Override
    public String getGuiTexture() {
        return LibResources.GUI_CHICKEN_BREEDING;
    }

    @Override
    public String getRecipeName() {
        return "Chicken Breeding";
    }

    @Override
    public void loadTransferRects() {
        this.addTransferRect(77, 14, 8, 7);
    }

    @Override
    public void loadAllRecipes() {
        Set<ChickensRegistryItem> added = new HashSet<>();
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            if (chicken.getParent1() != null || chicken.getParent2() != null) {
                if (added.add(chicken)) {
                    arecipes.add(new CachedChickensRecipe(chicken));
                }
            }
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        super.loadCraftingRecipes(result);
        Set<ChickensRegistryItem> added = new HashSet<>();
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            ItemStack egg = ModItems.CHICKEN_SPAWN_EGG.newItemStack(1, chicken.getId());
            if (egg.isItemEqual(result) && (chicken.getParent1() != null || chicken.getParent2() != null)) {
                if (added.add(chicken)) {
                    arecipes.add(new CachedChickensRecipe(chicken));
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        super.loadUsageRecipes(ingredient);
        Set<ChickensRegistryItem> added = new HashSet<>();
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {

            ChickensRegistryItem p1 = chicken.getParent1();
            ChickensRegistryItem p2 = chicken.getParent2();

            if (p1 == null || p2 == null) {
                continue;
            }

            ItemStack egg1 = ModItems.CHICKEN_SPAWN_EGG.newItemStack(1, p1.getId());
            ItemStack egg2 = ModItems.CHICKEN_SPAWN_EGG.newItemStack(1, p2.getId());

            if (egg1.isItemEqual(ingredient) || egg2.isItemEqual(ingredient)) {
                if (added.add(chicken)) {
                    arecipes.add(new CachedChickensRecipe(chicken));
                }
            }
        }
    }

    @Override
    public void drawExtras(int recipe) {
        super.drawExtras(recipe);
        drawProgressBar(77, 14, 82, 0, 8, 7, 200, 0);
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(40, 10, 0, 0, 82, 54);
    }

    public class CachedChickensRecipe extends CachedBaseRecipe {

        private final ChickensRegistryItem chicken;

        public CachedChickensRecipe(ChickensRegistryItem chicken) {

            this.chicken = chicken;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            ArrayList<PositionedStack> stacks = new ArrayList<>();
            if (chicken.getParent1() != null && chicken.getParent2() != null) {

                ChickensRegistryItem parent1 = chicken.getParent1();
                ItemStack egg1 = ModItems.CHICKEN_SPAWN_EGG.newItemStack(1, parent1.getId());
                PositionedStack parent1Egg = new PositionedStack(egg1, 50, 26);
                stacks.add(parent1Egg);

                ChickensRegistryItem parent2 = chicken.getParent2();
                ItemStack egg2 = ModItems.CHICKEN_SPAWN_EGG.newItemStack(1, parent2.getId());
                PositionedStack parent2Egg = new PositionedStack(egg2, 95, 26);
                stacks.add(parent2Egg);
            }
            return stacks;
        }

        @Override
        public PositionedStack getResult() {
            return new PositionedStack(ModItems.CHICKEN_SPAWN_EGG.newItemStack(1, chicken.getId()), 74, 40);
        }

        public ChickensRegistryItem getChicken() {
            return chicken;
        }
    }
}
