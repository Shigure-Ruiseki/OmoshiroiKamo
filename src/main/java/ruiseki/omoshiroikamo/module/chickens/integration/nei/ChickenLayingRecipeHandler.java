package ruiseki.omoshiroikamo.module.chickens.integration.nei;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public class ChickenLayingRecipeHandler extends RecipeHandlerBase {

    public static final String UID = "chickens.Laying";

    @Override
    public String getRecipeID() {
        return UID;
    }

    @Override
    public String getGuiTexture() {
        return LibResources.GUI_CHICKEN_LAYING;
    }

    @Override
    public String getRecipeName() {
        return "Chicken Laying";
    }

    @Override
    public void loadTransferRects() {
        this.addTransferRect(80, 30, 14, 10);
    }

    @Override
    public void loadAllRecipes() {
        Set<ChickensRegistryItem> added = new HashSet<>();
        for (ChickensRegistryItem chicken : ChickensRegistry.INSTANCE.getItems()) {
            if (added.add(chicken)) {
                arecipes.add(new CachedChickensRecipe(chicken));
            }
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        super.loadCraftingRecipes(result);
        Set<ChickensRegistryItem> added = new HashSet<>();
        for (ChickensRegistryItem chicken : ChickensRegistry.INSTANCE.getItems()) {
            if (chicken.createLayItem()
                .isItemEqual(result)) {
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
        for (ChickensRegistryItem chicken : ChickensRegistry.INSTANCE.getItems()) {
            ItemStack egg = ChickensItems.CHICKEN_SPAWN_EGG.newItemStack(1, chicken.getId());
            ItemStack item = ChickensItems.CHICKEN.newItemStack(1, chicken.getId());
            if (egg.isItemEqual(ingredient) || item.isItemEqual(ingredient)) {
                if (added.add(chicken)) {
                    arecipes.add(new CachedChickensRecipe(chicken));
                }
            }
        }
    }

    @Override
    public void drawExtras(int recipe) {
        super.drawExtras(recipe);
        drawProgressBar(80, 30, 82, 0, 14, 10, 43, 0);
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
        public PositionedStack getIngredient() {
            return new PositionedStack(ChickensItems.CHICKEN_SPAWN_EGG.newItemStack(1, chicken.getId()), 53, 26);
        }

        @Override
        public PositionedStack getResult() {
            return new PositionedStack(chicken.createLayItem(), 98, 26);
        }

        public ChickensRegistryItem getChicken() {
            return chicken;
        }
    }
}
