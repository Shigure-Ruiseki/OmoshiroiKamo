package ruiseki.omoshiroikamo.module.dml.integration.nei;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.core.integration.nei.RecipeHandlerBase;
import ruiseki.omoshiroikamo.core.item.ItemUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.dml.common.block.lootFabricator.BlockLootFabricator;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLItems;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemPristineMatter;

public class LootFabricatorRecipeHandler extends RecipeHandlerBase {

    public static final String UID = "dml.LootFabricator";

    @Override
    public String getRecipeID() {
        return UID;
    }

    @Override
    public String getGuiTexture() {
        return LibResources.PREFIX_GUI + "nei/dml/loot_fabricator.png";
    }

    @Override
    public String getRecipeName() {
        return LibMisc.LANG.localize("nei.dml.loot_fabricator");
    }

    @Override
    public void loadTransferRects() {
        this.addTransferRect(64, 26, 35, 6);
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(30, 14, 0, 0, 103, 30);
    }

    @Override
    public void drawExtras(int recipe) {
        super.drawExtras(recipe);
        drawProgressBar(64, 26, 0, 30, 35, 6, 51, 0);
    }

    @Override
    public void loadAllRecipes() {
        Collection<ModelRegistryItem> list = ModelRegistry.INSTANCE.getItems();
        for (ModelRegistryItem model : list) {
            for (ItemStack lootItem : model.getLootItems()) {
                arecipes.add(
                    new CachedLootFabricatorRecipe(DMLItems.PRISTINE_MATTER.newItemStack(1, model.getId()), lootItem));
            }
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        super.loadCraftingRecipes(result);
        for (ModelRegistryItem model : ModelRegistry.INSTANCE.getItems()) {
            for (ItemStack loot : model.getLootItems()) {
                if (ItemUtils.areStacksEqual(result, loot)) {
                    ItemStack pristine = model.getPristineMatter();
                    arecipes.add(new CachedLootFabricatorRecipe(pristine, loot));
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        super.loadUsageRecipes(ingredient);

        Item item = ingredient.getItem();

        boolean isPristine = item instanceof ItemPristineMatter;
        boolean isLootFabricator = Block.getBlockFromItem(item) instanceof BlockLootFabricator;

        if (isLootFabricator) {
            loadAllRecipes();
            return;
        }

        if (isPristine) {
            ModelRegistryItem model = ModelRegistry.INSTANCE.getByType(ingredient.getItemDamage());

            if (model != null) {
                for (ItemStack lootItem : model.getLootItems()) {
                    arecipes.add(new CachedLootFabricatorRecipe(ingredient, lootItem));
                }
            }
        }
    }

    public class CachedLootFabricatorRecipe extends CachedBaseRecipe {

        private final ItemStack pristine;
        private final ItemStack lootItem;

        public CachedLootFabricatorRecipe(ItemStack pristine, ItemStack lootItem) {
            this.pristine = pristine;
            this.lootItem = lootItem;
        }

        @Override
        public PositionedStack getIngredient() {
            return new PositionedStack(pristine, 39, 21);
        }

        @Override
        public PositionedStack getResult() {
            return new PositionedStack(lootItem, 107, 21);
        }
    }
}
