package ruiseki.omoshiroikamo.plugin.nei.recipe.dml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.utils.Color;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.entity.dml.ModelTierRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelTierRegistryItem;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.common.block.dml.simulationCharmber.BlockSimulationChamber;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.item.dml.ItemDataModel;
import ruiseki.omoshiroikamo.common.item.dml.ItemLivingMatter;
import ruiseki.omoshiroikamo.common.item.dml.ItemPolymerClay;
import ruiseki.omoshiroikamo.common.item.dml.ItemPristineMatter;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public class SimulationChamberRecipeHandler extends RecipeHandlerBase {

    public static final String UID = "dml.SimulationChamber";

    @Override
    public String getRecipeID() {
        return UID;
    }

    @Override
    public String getGuiTexture() {
        return LibResources.PREFIX_GUI + "nei/dml/simulation_chamber.png";
    }

    @Override
    public String getRecipeName() {
        return "Simulation Chamber";
    }

    @Override
    public void loadTransferRects() {
        this.addTransferRect(77, 17, 35, 6);
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1, 1, 1, 1);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(25, 8, 0, 0, 116, 43);
    }

    @Override
    public void drawExtras(int recipeIndex) {
        super.drawExtras(recipeIndex);
        drawProgressBar(77, 17, 0, 43, 35, 6, 301, 0);
        ModelTierRegistryItem item = ModelTierRegistry.INSTANCE.getByType(1);
        if (item != null) {
            String tierName = LibMisc.LANG.localize(item.getTierName());
            String pristineChance = item.getPristineChance() + "%";
            int textWidth = fontRenderer.getStringWidth(tierName);
            fontRenderer.drawStringWithShadow(tierName, 95 - textWidth, 38, Color.WHITE.main);
            fontRenderer.drawStringWithShadow(pristineChance, 122, 38, Color.WHITE.main);
        }

    }

    @Override
    public void loadAllRecipes() {
        Collection<ModelRegistryItem> list = ModelRegistry.INSTANCE.getItems();
        for (ModelRegistryItem model : list) {
            ItemStack pristine = model.getPristineMatter();
            ItemStack living = model.getLivingMatter();
            ItemStack modelStack = ModItems.DATA_MODEL.newItemStack(1, model.getId());
            arecipes.add(new CachedSimulationChamberRecipe(pristine, living, modelStack));
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        super.loadCraftingRecipes(result);
        Item item = result.getItem();
        boolean isPristine = item instanceof ItemPristineMatter;
        boolean isLiving = item instanceof ItemLivingMatter;

        if (!isPristine && !isLiving) {
            return;
        }

        for (ModelRegistryItem model : ModelRegistry.INSTANCE.getItems()) {
            ItemStack pristine = model.getPristineMatter();
            ItemStack living = model.getLivingMatter();

            if ((isPristine && ItemUtils.areStacksEqual(result, pristine))
                || (isLiving && ItemUtils.areStacksEqual(result, living))) {

                ItemStack modelStack = ModItems.DATA_MODEL.newItemStack(1, model.getId());

                arecipes.add(new CachedSimulationChamberRecipe(pristine, living, modelStack));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        super.loadUsageRecipes(ingredient);

        Item item = ingredient.getItem();

        boolean isLootFabricator = Block.getBlockFromItem(item) instanceof BlockSimulationChamber;

        boolean isPolymerClay = item instanceof ItemPolymerClay;
        boolean isDataModel = item instanceof ItemDataModel;

        if (isLootFabricator || isPolymerClay) {
            loadAllRecipes();
            return;
        }

        if (isDataModel) {
            ModelRegistryItem model = ModelRegistry.INSTANCE.getByType(ingredient.getItemDamage());

            if (model != null) {
                ItemStack pristine = model.getPristineMatter();
                ItemStack living = model.getLivingMatter();
                ItemStack modelStack = ModItems.DATA_MODEL.newItemStack(1, model.getId());

                arecipes.add(new CachedSimulationChamberRecipe(pristine, living, modelStack));
            }
        }
    }

    public class CachedSimulationChamberRecipe extends CachedBaseRecipe {

        private final ItemStack pristine;
        private final ItemStack living;
        private final ItemStack model;

        public CachedSimulationChamberRecipe(ItemStack pristine, ItemStack living, ItemStack model) {
            this.pristine = pristine;
            this.living = living;
            this.model = model;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            ArrayList<PositionedStack> stacks = new ArrayList<>();
            stacks.add(new PositionedStack(model, 29, 12));
            stacks.add(new PositionedStack(ModItems.POLYMER_CLAY.newItemStack(), 53, 12));
            return stacks;
        }

        @Override
        public PositionedStack getResult() {
            return new PositionedStack(living, 121, 12);
        }

        @Override
        public PositionedStack getOtherStack() {
            return new PositionedStack(pristine, 101, 34);
        }
    }
}
