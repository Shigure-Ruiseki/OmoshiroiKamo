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
import ruiseki.omoshiroikamo.api.modular.recipe.EssentiaInput;
import ruiseki.omoshiroikamo.api.modular.recipe.EssentiaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.FluidInput;
import ruiseki.omoshiroikamo.api.modular.recipe.FluidOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.GasInput;
import ruiseki.omoshiroikamo.api.modular.recipe.GasOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ItemInput;
import ruiseki.omoshiroikamo.api.modular.recipe.ItemOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.VisInput;
import ruiseki.omoshiroikamo.api.modular.recipe.VisOutput;
import ruiseki.omoshiroikamo.core.integration.nei.PositionedFluidTank;
import ruiseki.omoshiroikamo.core.integration.nei.RecipeHandlerBase;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.INEIPositionedRenderer;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.NEIRendererFactory;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.PositionedEnergy;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.PositionedEssentia;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.PositionedGasTank;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.PositionedVis;
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
    public int recipiesPerPage() {
        return 1;
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
            int startX = 4;
            int startY = 4;
            int maxRowWidth = 80;
            int slotSize = 18;
            int gap = 2;

            int currentX = startX;
            int currentY = startY;
            int maxHeightInRow = 0;

            // --- Process Inputs (Main: Item, Fluid, Energy, Gas) ---
            for (IRecipeInput input : recipe.getInputs()) {
                if (input instanceof VisInput || input instanceof EssentiaInput) {
                    continue;
                }

                int width = slotSize;
                int height = slotSize;

                if (input instanceof FluidInput || input instanceof EnergyInput || input instanceof GasInput) {
                    height = 50;
                }

                if (currentX + width > startX + maxRowWidth) {
                    currentX = startX;
                    currentY += maxHeightInRow + gap;
                    maxHeightInRow = 0;
                }

                // Rect for SLOT (Outer)
                Rectangle slotRect = new Rectangle(currentX, currentY, width, height);
                // Rect for CONTENT (Inner)
                Rectangle contentRect = new Rectangle(currentX + 1, currentY + 1, width - 2, height - 2);

                if (input instanceof ItemInput) {
                    ItemInput itemIn = (ItemInput) input;
                    if (!itemIn.getItems()
                        .isEmpty()) {
                        inputStacks.add(new PositionedStack(itemIn.getItems(), contentRect.x, contentRect.y));
                    }
                } else if (input instanceof FluidInput) {
                    FluidInput fluidIn = (FluidInput) input;
                    fluidTanks.add(new PositionedFluidTank(fluidIn.getFluid(), contentRect));
                } else if (input instanceof EnergyInput) {
                    EnergyInput energyIn = (EnergyInput) input;
                    extraRenderers
                        .add(new PositionedEnergy(energyIn.getAmount(), energyIn.isPerTick(), true, slotRect));
                } else {
                    INEIPositionedRenderer renderer = NEIRendererFactory.createGasRenderer(input, null, contentRect);
                    if (renderer == null) renderer = NEIRendererFactory.createManaRenderer(input, null, slotRect);

                    if (renderer != null) {
                        extraRenderers.add(renderer);
                    }
                }

                currentX += width + gap;
                maxHeightInRow = Math.max(maxHeightInRow, height);
            }

            // --- Process Outputs (Main: Item, Fluid, Energy, Gas) ---
            int outputStartX = 100;
            int outputStartY = 4;
            int maxOutputWidth = 60;

            currentX = outputStartX;
            currentY = outputStartY;
            maxHeightInRow = 0;

            for (IRecipeOutput output : recipe.getOutputs()) {
                if (output instanceof VisOutput || output instanceof EssentiaOutput) {
                    continue;
                }

                int width = slotSize;
                int height = slotSize;

                if (output instanceof FluidOutput || output instanceof EnergyOutput || output instanceof GasOutput) {
                    height = 50;
                }

                if (currentX + width > outputStartX + maxOutputWidth) {
                    currentX = outputStartX;
                    currentY += maxHeightInRow + gap;
                    maxHeightInRow = 0;
                }

                Rectangle slotRect = new Rectangle(currentX, currentY, width, height);
                Rectangle contentRect = new Rectangle(currentX + 1, currentY + 1, width - 2, height - 2);

                if (output instanceof ItemOutput) {
                    ItemOutput itemOut = (ItemOutput) output;
                    if (!itemOut.getItems()
                        .isEmpty()) {
                        outputStacks.add(
                            new PositionedStack(
                                itemOut.getItems()
                                    .get(0),
                                contentRect.x,
                                contentRect.y));
                    }
                } else if (output instanceof FluidOutput) {
                    FluidOutput fluidOut = (FluidOutput) output;
                    fluidTanks.add(new PositionedFluidTank(fluidOut.getOutput(), contentRect));
                } else if (output instanceof EnergyOutput) {
                    EnergyOutput energyOut = (EnergyOutput) output;
                    extraRenderers
                        .add(new PositionedEnergy(energyOut.getAmount(), energyOut.isPerTick(), false, slotRect));
                } else {
                    INEIPositionedRenderer renderer = NEIRendererFactory.createGasRenderer(null, output, contentRect);
                    if (renderer == null) renderer = NEIRendererFactory.createManaRenderer(null, output, slotRect);

                    if (renderer != null) {
                        extraRenderers.add(renderer);
                    }
                }

                currentX += width + gap;
                maxHeightInRow = Math.max(maxHeightInRow, height);
            }

            // --- Aspects (Vis & Essentia) Section ---
            int aspectSectionY = 85;
            int aspectX = 4;

            // Vis Grid (Thaumcraft Primal Aspects) - Fixed 6 slots
            boolean hasVis = false;
            for (IRecipeInput i : recipe.getInputs()) if (i instanceof VisInput) hasVis = true;
            for (IRecipeOutput o : recipe.getOutputs()) if (o instanceof VisOutput) hasVis = true;

            if (hasVis) {
                String[] aspects = { "aer", "terra", "ignis", "aqua", "ordo", "perditio" };

                for (int i = 0; i < 6; i++) {
                    Rectangle r = new Rectangle(aspectX + i * 18 + 1, aspectSectionY + 1, 16, 16);
                    for (IRecipeInput input : recipe.getInputs()) {
                        if (input instanceof VisInput) {
                            VisInput vis = (VisInput) input;
                            if (vis.getAspectTag()
                                .equals(aspects[i])) {
                                extraRenderers.add(NEIRendererFactory.createAspectRenderer(input, null, r));
                            }
                        }
                    }
                    for (IRecipeOutput output : recipe.getOutputs()) {
                        if (output instanceof VisOutput) {
                            VisOutput vis = (VisOutput) output;
                            if (vis.getAspectTag()
                                .equals(aspects[i])) {
                                extraRenderers.add(NEIRendererFactory.createAspectRenderer(null, output, r));
                            }
                        }
                    }
                }
                aspectSectionY += 20;
            }

            // Essentia Row
            int essX = 4;
            for (IRecipeInput input : recipe.getInputs()) {
                if (input instanceof EssentiaInput) {
                    Rectangle r = new Rectangle(essX + 1, aspectSectionY + 1, 16, 16);
                    extraRenderers.add(NEIRendererFactory.createAspectRenderer(input, null, r));
                    essX += 18;
                }
            }
            for (IRecipeOutput output : recipe.getOutputs()) {
                if (output instanceof EssentiaOutput) {
                    Rectangle r = new Rectangle(essX + 1, aspectSectionY + 1, 16, 16);
                    extraRenderers.add(NEIRendererFactory.createAspectRenderer(null, output, r));
                    essX += 18;
                }
            }
        }

        public void drawSlots() {
            // Draw slots under items
            for (PositionedStack stack : inputStacks) {
                drawItemSlot(stack.relx - 1, stack.rely - 1);
            }
            for (PositionedStack stack : outputStacks) {
                drawItemSlot(stack.relx - 1, stack.rely - 1);
            }
            // Draw slots for fluids
            for (PositionedFluidTank tank : fluidTanks) {
                drawStretchedFluidSlot(
                    tank.position.x - 1,
                    tank.position.y - 1,
                    tank.position.width + 2,
                    tank.position.height + 2);
            }
            // Draw slots for extras
            for (INEIPositionedRenderer renderer : extraRenderers) {
                Rectangle rect = renderer.getPosition();
                if (renderer instanceof PositionedEnergy) {
                    // Handled internally or skip
                } else if (renderer instanceof PositionedVis) {
                    // Drawn by grid below
                } else if (renderer instanceof PositionedEssentia) {
                    drawItemSlot(rect.x - 1, rect.y - 1);
                } else if (renderer instanceof PositionedGasTank) {
                    drawStretchedFluidSlot(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2);
                } else {
                    if (rect.height > 18) {
                        drawStretchedItemSlot(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2);
                    } else {
                        drawItemSlot(rect.x - 1, rect.y - 1);
                    }
                }
            }
        }

        public void drawExtras() {
            // Draw Vis Grid Background if needed
            boolean hasVis = false;
            if (recipe != null) {
                for (IRecipeInput i : recipe.getInputs()) if (i instanceof VisInput) hasVis = true;
                for (IRecipeOutput o : recipe.getOutputs()) if (o instanceof VisOutput) hasVis = true;
            }

            if (hasVis) {
                int aspectY = 85;
                int aspectX = 4;
                for (int i = 0; i < 6; i++) {
                    drawItemSlot(aspectX + i * 18, aspectY);
                }
            }

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
