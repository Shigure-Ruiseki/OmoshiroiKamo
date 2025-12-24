package ruiseki.omoshiroikamo.module.cows.integration.nei;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.core.integration.nei.PositionedFluidTank;
import ruiseki.omoshiroikamo.core.integration.nei.RecipeHandlerBase;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsItems;

public class CowMilkingRecipeHandler extends RecipeHandlerBase {

    public static final String UID = "cows.Milking";

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
        return "Cow Milking";
    }

    @Override
    public void loadTransferRects() {
        this.addTransferRect(0, 0, 0, 0);
    }

    @Override
    public void loadAllRecipes() {
        Set<CowsRegistryItem> added = new HashSet<>();
        for (CowsRegistryItem chicken : CowsRegistry.INSTANCE.getItems()) {
            if (added.add(chicken)) {
                arecipes.add(new CachedCowsRecipe(chicken));
            }
        }
    }

    @Override
    public void loadCraftingRecipes(FluidStack result) {
        super.loadCraftingRecipes(result);
        Set<CowsRegistryItem> added = new HashSet<>();
        for (CowsRegistryItem chicken : CowsRegistry.INSTANCE.getItems()) {
            if (chicken.createMilkFluid()
                .isFluidEqual(result)) {
                if (added.add(chicken)) {
                    arecipes.add(new CachedCowsRecipe(chicken));
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        super.loadUsageRecipes(ingredient);
        Set<CowsRegistryItem> added = new HashSet<>();
        for (CowsRegistryItem chicken : CowsRegistry.INSTANCE.getItems()) {
            ItemStack egg = CowsItems.COW_SPAWN_EGG.newItemStack(1, chicken.getId());
            if (egg.isItemEqual(ingredient)) {
                if (added.add(chicken)) {
                    arecipes.add(new CachedCowsRecipe(chicken));
                }
            }
        }
    }

    @Override
    public void drawBackground(int recipe) {
        super.drawBackground(recipe);
        drawItemSlot(52, 26);
        drawFluidSlot(98, 26);
    }

    public class CachedCowsRecipe extends CachedBaseRecipe {

        private final CowsRegistryItem cow;

        public CachedCowsRecipe(CowsRegistryItem cow) {
            this.cow = cow;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        @Override
        public PositionedStack getIngredient() {
            return new PositionedStack(CowsItems.COW_SPAWN_EGG.newItemStack(1, cow.getId()), 53, 27);
        }

        @Override
        public PositionedFluidTank getFluidTank() {
            return new PositionedFluidTank(cow.createMilkFluid(), 98, 26);
        }

        public CowsRegistryItem getCow() {
            return cow;
        }
    }
}
