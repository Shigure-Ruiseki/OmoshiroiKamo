package ruiseki.omoshiroikamo.core.integration.nei.modular;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ruiseki.omoshiroikamo.api.modular.recipe.EnergyInput;
import ruiseki.omoshiroikamo.api.modular.recipe.EnergyOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.FluidInput;
import ruiseki.omoshiroikamo.api.modular.recipe.FluidOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ItemInput;
import ruiseki.omoshiroikamo.api.modular.recipe.ItemOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.core.integration.nei.PositionedFluidTank;
import ruiseki.omoshiroikamo.core.integration.nei.RecipeHandlerBase;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.INEIPositionedRenderer;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.NEIRendererFactory;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.PositionedEnergy;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;

public class ModularRecipeNEIHandler extends RecipeHandlerBase {

    private final String recipeGroup;

    public ModularRecipeNEIHandler(String recipeGroup) {
        this.recipeGroup = recipeGroup;
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new ModularRecipeNEIHandler(recipeGroup);
    }

    @Override
    public String getRecipeID() {
        return "modular_" + recipeGroup;
    }

    @Override
    public String getRecipeName() {
        return recipeGroup;
    }

    @Override
    public String getGuiTexture() {
        return "blockrenderer6343:textures/void.png"; // Use blank texture as we draw slots dynamically
    }

    @Override
    public void loadTransferRects() {
        // Dynamic transfer rect based on layout, but fixed for simplicity for now
        this.addTransferRect(76, 20, 24, 18);
    }

    @Override
    public void drawBackground(int recipe) {
        CachedModularRecipe crecipe = (CachedModularRecipe) arecipes.get(recipe);
        crecipe.drawSlots();
    }

    @Override
    public void drawForeground(int recipe) {
        super.drawForeground(recipe);
        CachedModularRecipe crecipe = (CachedModularRecipe) arecipes.get(recipe);
        crecipe.drawExtras();
    }

    @Override
    public List<String> provideTooltip(GuiRecipe<?> guiRecipe, List<String> currenttip, CachedBaseRecipe crecipe,
        Point relMouse) {
        super.provideTooltip(guiRecipe, currenttip, crecipe, relMouse);
        if (crecipe instanceof CachedModularRecipe) {
            ((CachedModularRecipe) crecipe).handleTooltip(relMouse, currenttip);
        }
        return currenttip;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getRecipeID())) {
            for (ModularRecipe recipe : RecipeLoader.getInstance()
                .getAllRecipes()) {
                if (recipe.getRecipeGroup()
                    .equals(recipeGroup)) {
                    arecipes.add(new CachedModularRecipe(recipe));
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (ModularRecipe recipe : RecipeLoader.getInstance()
            .getAllRecipes()) {
            if (!recipe.getRecipeGroup()
                .equals(recipeGroup)) continue;

            boolean match = false;
            for (IRecipeOutput output : recipe.getOutputs()) {
                if (output instanceof ItemOutput) {
                    ItemOutput itemOut = (ItemOutput) output;
                    for (ItemStack stack : itemOut.getItems()) {
                        if (NEIServerUtils.areStacksSameTypeCrafting(stack, result)) {
                            match = true;
                            break;
                        }
                    }
                }
            }
            if (match) {
                arecipes.add(new CachedModularRecipe(recipe));
            }
        }
    }

    @Override
    public void loadCraftingRecipes(FluidStack result) {
        for (ModularRecipe recipe : RecipeLoader.getInstance()
            .getAllRecipes()) {
            if (!recipe.getRecipeGroup()
                .equals(recipeGroup)) continue;

            boolean match = false;
            for (IRecipeOutput output : recipe.getOutputs()) {
                if (output instanceof FluidOutput) {
                    FluidOutput fluidOut = (FluidOutput) output;
                    if (fluidOut.getFluid()
                        .isFluidEqual(result)) {
                        match = true;
                        break;
                    }
                }
            }
            if (match) {
                arecipes.add(new CachedModularRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (ModularRecipe recipe : RecipeLoader.getInstance()
            .getAllRecipes()) {
            if (!recipe.getRecipeGroup()
                .equals(recipeGroup)) continue;

            boolean match = false;
            for (IRecipeInput input : recipe.getInputs()) {
                if (input instanceof ItemInput) {
                    ItemInput itemIn = (ItemInput) input;
                    for (ItemStack stack : itemIn.getItems()) {
                        if (NEIServerUtils.areStacksSameTypeCrafting(stack, ingredient)) {
                            match = true;
                            break;
                        }
                    }
                }
            }
            if (match) {
                arecipes.add(new CachedModularRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(FluidStack ingredient) {
        for (ModularRecipe recipe : RecipeLoader.getInstance()
            .getAllRecipes()) {
            if (!recipe.getRecipeGroup()
                .equals(recipeGroup)) continue;

            boolean match = false;
            for (IRecipeInput input : recipe.getInputs()) {
                if (input instanceof FluidInput) {
                    FluidInput fluidIn = (FluidInput) input;
                    if (fluidIn.getFluid()
                        .isFluidEqual(ingredient)) {
                        match = true;
                        break;
                    }
                }
            }
            if (match) {
                arecipes.add(new CachedModularRecipe(recipe));
            }
        }
    }

    public class CachedModularRecipe extends CachedBaseRecipe {

        private final ModularRecipe recipe;
        private final List<PositionedStack> inputStacks = new ArrayList<>();
        private final List<PositionedStack> outputStacks = new ArrayList<>();
        private final List<PositionedFluidTank> fluidTanks = new ArrayList<>();
        private final List<INEIPositionedRenderer> extraRenderers = new ArrayList<>();

        public CachedModularRecipe(ModularRecipe recipe) {
            this.recipe = recipe;
            layout();
        }

        // Dynamic layout logic
        private void layout() {
            int inputX = 4;
            int inputY = 4;
            int outputX = 100;
            int outputY = 4;
            int slotSize = 18;
            int gap = 2;

            // Process Inputs
            for (IRecipeInput input : recipe.getInputs()) {
                if (input instanceof ItemInput) {
                    ItemInput itemIn = (ItemInput) input;
                    // Assuming itemIn.getItems() returns allowed inputs
                    inputStacks.add(new PositionedStack(itemIn.getItems(), inputX, inputY));

                    // Simple grid layout for inputs
                    inputX += slotSize + gap;
                    if (inputX > 60) { // Wrap
                        inputX = 4;
                        inputY += slotSize + gap;
                    }
                } else if (input instanceof FluidInput) {
                    FluidInput fluidIn = (FluidInput) input;
                    // Place fluid tank
                    Rectangle rect = new Rectangle(inputX, inputY, 18, 50); // Tall tank
                    fluidTanks.add(new PositionedFluidTank(fluidIn.getFluid(), rect)); // Max capacity dummy
                    inputX += slotSize + gap; // Fluid takes space
                } else if (input instanceof EnergyInput) {
                    EnergyInput energyIn = (EnergyInput) input;
                    Rectangle rect = new Rectangle(inputX, inputY, 18, 50);
                    extraRenderers.add(new PositionedEnergy(energyIn.getAmount(), energyIn.isPerTick(), true, rect));
                    inputX += slotSize + gap;
                } else {
                    // Try extended renderers
                    Rectangle rect = new Rectangle(inputX, inputY, 18, 50); // Default space
                    // Check Gas
                    INEIPositionedRenderer renderer = NEIRendererFactory.createGasRenderer(input, null, rect);
                    if (renderer == null) renderer = NEIRendererFactory.createAspectRenderer(input, null, rect);
                    if (renderer == null) renderer = NEIRendererFactory.createManaRenderer(input, null, rect);

                    if (renderer != null) {
                        extraRenderers.add(renderer);
                        inputX += slotSize + gap;
                    }
                }
            }

            // Process Outputs
            for (IRecipeOutput output : recipe.getOutputs()) {
                if (output instanceof ItemOutput) {
                    ItemOutput itemOut = (ItemOutput) output;
                    // Chance handling?
                    if (!itemOut.getItems()
                        .isEmpty()) {
                        outputStacks.add(
                            new PositionedStack(
                                itemOut.getItems()
                                    .get(0),
                                outputX,
                                outputY));
                    }

                    outputX += slotSize + gap;
                    if (outputX > 160) { // Wrap
                        outputX = 100;
                        outputY += slotSize + gap;
                    }
                } else if (output instanceof FluidOutput) {
                    FluidOutput fluidOut = (FluidOutput) output;
                    Rectangle rect = new Rectangle(outputX, outputY, 18, 50);
                    fluidTanks.add(new PositionedFluidTank(fluidOut.getOutput(), rect));
                    outputX += slotSize + gap;
                } else if (output instanceof EnergyOutput) {
                    EnergyOutput energyOut = (EnergyOutput) output;
                    Rectangle rect = new Rectangle(outputX, outputY, 18, 50);
                    extraRenderers.add(new PositionedEnergy(energyOut.getAmount(), energyOut.isPerTick(), false, rect));
                    outputX += slotSize + gap;
                } else {
                    // Try extended renderers for output
                    Rectangle rect = new Rectangle(outputX, outputY, 18, 50);
                    // Check Gas
                    INEIPositionedRenderer renderer = NEIRendererFactory.createGasRenderer(null, output, rect);
                    if (renderer == null) renderer = NEIRendererFactory.createAspectRenderer(null, output, rect);
                    if (renderer == null) renderer = NEIRendererFactory.createManaRenderer(null, output, rect);

                    if (renderer != null) {
                        extraRenderers.add(renderer);
                        outputX += slotSize + gap;
                    }
                }
            }
        }

        public void drawSlots() {
            // Draw slots under items
            for (PositionedStack stack : inputStacks) {
                drawItemSlot(stack.relx, stack.rely);
            }
            for (PositionedStack stack : outputStacks) {
                drawItemSlot(stack.relx, stack.rely);
            }
            // Draw slots for fluids
            for (PositionedFluidTank tank : fluidTanks) {
                drawStretchedFluidSlot(tank.position.x, tank.position.y, tank.position.width, tank.position.height);
            }
            // Draw slots for extras
            for (INEIPositionedRenderer renderer : extraRenderers) {
                Rectangle rect = renderer.getPosition();
                drawStretchedItemSlot(rect.x, rect.y, rect.width, rect.height);
            }
        }

        public void drawExtras() {
            for (INEIPositionedRenderer renderer : extraRenderers) {
                renderer.draw();
            }
        }

        public void handleTooltip(Point relMouse, List<String> currenttip) {
            for (INEIPositionedRenderer renderer : extraRenderers) {
                if (renderer.getPosition()
                    .contains(relMouse)) {
                    renderer.handleTooltip(currenttip);
                }
            }
        }

        @Override
        public PositionedFluidTank getFluidTank() {
            return fluidTanks.isEmpty() ? null : fluidTanks.get(0);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return inputStacks;
        }

        @Override
        public PositionedStack getResult() {
            // TemplateRecipeHandler needs getResult() to return something, usually first
            // output
            return outputStacks.isEmpty() ? null : outputStacks.get(0);
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            // Return remaining outputs
            if (outputStacks.size() <= 1) return Collections.emptyList();
            return outputStacks.subList(1, outputStacks.size());
        }

        @Override
        public List<PositionedFluidTank> getFluidTanks() {
            return fluidTanks;
        }
    }
}
