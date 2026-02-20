package ruiseki.omoshiroikamo.core.integration.nei.modular;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ruiseki.omoshiroikamo.api.modular.recipe.FluidInput;
import ruiseki.omoshiroikamo.api.modular.recipe.FluidOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ItemInput;
import ruiseki.omoshiroikamo.api.modular.recipe.ItemOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.core.integration.nei.PositionedFluidTank;
import ruiseki.omoshiroikamo.core.integration.nei.RecipeHandlerBase;
import ruiseki.omoshiroikamo.core.integration.nei.modular.layout.LayoutPartEnergy;
import ruiseki.omoshiroikamo.core.integration.nei.modular.layout.LayoutPartEssentia;
import ruiseki.omoshiroikamo.core.integration.nei.modular.layout.LayoutPartFactory;
import ruiseki.omoshiroikamo.core.integration.nei.modular.layout.LayoutPartFluid;
import ruiseki.omoshiroikamo.core.integration.nei.modular.layout.LayoutPartGas;
import ruiseki.omoshiroikamo.core.integration.nei.modular.layout.LayoutPartItem;
import ruiseki.omoshiroikamo.core.integration.nei.modular.layout.LayoutPartMana;
import ruiseki.omoshiroikamo.core.integration.nei.modular.layout.LayoutPartRenderer;
import ruiseki.omoshiroikamo.core.integration.nei.modular.layout.LayoutPartVis;
import ruiseki.omoshiroikamo.core.integration.nei.modular.layout.RecipeLayoutPart;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.PositionedText;
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
        // Removed central bounding box
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

        // New list for all parts
        private final List<RecipeLayoutPart<?>> allParts = new ArrayList<>();

        public CachedModularRecipe(ModularRecipe recipe) {
            this.recipe = recipe;
            layout();
        }

        private void layout() {
            clearLists();

            // 1. Collect all parts
            List<RecipeLayoutPart<?>> inputParts = new ArrayList<>();
            List<RecipeLayoutPart<?>> outputParts = new ArrayList<>();

            collectParts(recipe.getInputs(), inputParts, true);
            collectPartsErrors(recipe.getOutputs(), outputParts, false);

            // Separate inputs by type
            List<LayoutPartMana> manaParts = new ArrayList<>();
            List<LayoutPartEnergy> energyParts = new ArrayList<>();
            List<LayoutPartItem> itemParts = new ArrayList<>();
            List<LayoutPartEssentia> essentiaParts = new ArrayList<>();
            List<LayoutPartVis> visParts = new ArrayList<>();
            List<LayoutPartFluid> fluidParts = new ArrayList<>();
            List<LayoutPartGas> gasParts = new ArrayList<>();

            for (RecipeLayoutPart<?> part : inputParts) {
                if (part instanceof LayoutPartMana) manaParts.add((LayoutPartMana) part);
                else if (part instanceof LayoutPartEnergy) energyParts.add((LayoutPartEnergy) part);
                else if (part instanceof LayoutPartItem) itemParts.add((LayoutPartItem) part);
                else if (part instanceof LayoutPartEssentia) essentiaParts.add((LayoutPartEssentia) part);
                else if (part instanceof LayoutPartVis) visParts.add((LayoutPartVis) part);
                else if (part instanceof LayoutPartFluid) fluidParts.add((LayoutPartFluid) part);
                else if (part instanceof LayoutPartGas) gasParts.add((LayoutPartGas) part);
            }

            // --- LAYOUT LOGIC ---

            int mainY = 18;
            int currentX = 5;

            String recipeName = recipe.getName();
            if (recipeName != null) {
                PositionedText text = new PositionedText(
                    recipeName,
                    0x222222,
                    new Rectangle(currentX, mainY - 12, 100, 10));
                allParts.add(new LayoutPartRenderer(text));
            }

            // 1. Items (Center Grid)
            int itemStartX = currentX;
            layoutGrid(itemParts, itemStartX, mainY, 3, 18);

            // 2. Essentia (Bottom Left, below items)
            int essentiaY = mainY + (3 * 18) + 8;
            layoutGrid(essentiaParts, itemStartX, essentiaY, 4, 16);

            // 3. Vis (Right of items)
            int visStartX = itemStartX + (3 * 18) + 4;
            layoutGrid(visParts, visStartX, mainY, 2, 16);

            // 4. Energy (Right of Vis)
            int energyStartX = visStartX + (2 * 16) + 4;
            if (!energyParts.isEmpty()) {
                LayoutPartEnergy energy = energyParts.get(0);
                energy.setPosition(energyStartX, mainY);
                allParts.add(energy);
            }

            // 5. Fluids/Gas (Far Right)
            int tankStartX = energyStartX + 16 + 4;
            int currentTankX = tankStartX;

            for (LayoutPartFluid fluid : fluidParts) {
                fluid.setPosition(currentTankX, mainY);
                currentTankX += fluid.getWidth() + 2;
            }
            for (LayoutPartGas gas : gasParts) {
                gas.setPosition(currentTankX, mainY);
                allParts.add(gas);
                currentTankX += gas.getWidth() + 2;
            }

            // 6. Output Area
            int outputY = 115;
            int outputStartX = 10;
            layoutComponents(outputParts, outputStartX, outputY, false);

            // 7. Mana (Bottom Bar)
            int manaY = 80;
            if (!manaParts.isEmpty()) {
                LayoutPartMana mana = manaParts.get(0);
                mana.setPosition(5, manaY);
                allParts.add(mana);
            }

            // Populate legacy lists for NEI compatibility
            for (RecipeLayoutPart<?> part : itemParts) allParts.add(part);
            for (RecipeLayoutPart<?> part : essentiaParts) allParts.add(part);
            for (RecipeLayoutPart<?> part : visParts) allParts.add(part);

            populateLegacyLists(inputParts, true);
            populateLegacyLists(outputParts, false);
        }

        private void layoutGrid(List<? extends RecipeLayoutPart<?>> parts, int x, int y, int cols, int cellSize) {
            int col = 0;
            int row = 0;
            for (RecipeLayoutPart<?> part : parts) {
                part.setPosition(x + (col * cellSize), y + (row * cellSize));
                col++;
                if (col >= cols) {
                    col = 0;
                    row++;
                }
            }
        }

        private void collectParts(List<? extends IRecipeInput> inputs, List<RecipeLayoutPart<?>> parts,
            boolean isInput) {
            for (IRecipeInput input : inputs) {
                RecipeLayoutPart<?> part = LayoutPartFactory.create(input);
                if (part != null) {
                    parts.add(part);
                }
            }
        }

        private void collectPartsErrors(List<? extends IRecipeOutput> outputs, List<RecipeLayoutPart<?>> parts,
            boolean isInput) {
            for (IRecipeOutput output : outputs) {
                RecipeLayoutPart<?> part = LayoutPartFactory.create(output);
                if (part != null) {
                    parts.add(part);
                }
            }
        }

        /**
         * Lays out components starting from x, y.
         * Returns the maximum X used.
         */
        private int layoutComponents(List<RecipeLayoutPart<?>> parts, int startX, int startY, boolean isInput) {
            int currentX = startX;
            int currentY = startY;
            int maxYInRow = 0;
            int maxX = startX;

            RecipeLayoutPart<?> lastPart = null;
            int itemsInRow = 0;

            for (RecipeLayoutPart<?> part : parts) {
                // Check if we need a new line
                boolean newline = false;

                // If max horizontal count exceeded
                if (lastPart != null && lastPart.getClass() == part.getClass()) {
                    if (itemsInRow >= part.getMaxHorizontalCount()) {
                        newline = true;
                    }
                } else if (lastPart != null && lastPart.getClass() != part.getClass()) {
                    // Type changed, reset row count, maybe force newline if height is different?
                    itemsInRow = 0;
                }

                // Check bounds
                if (currentX + part.getWidth() > 166) {
                    newline = true;
                }

                if (newline) {
                    currentX = startX;
                    currentY += maxYInRow + 4; // Gap
                    maxYInRow = 0;
                    itemsInRow = 0;
                }

                part.setPosition(currentX, currentY);

                // Update metrics
                currentX += part.getWidth() + 2; // Gap X
                if (currentX > maxX) maxX = currentX;
                if (part.getHeight() > maxYInRow) maxYInRow = part.getHeight();

                itemsInRow++;
                lastPart = part;
            }

            return maxX;
        }

        private void populateLegacyLists(List<RecipeLayoutPart<?>> parts, boolean isInput) {
            for (RecipeLayoutPart<?> part : parts) {
                if (part instanceof LayoutPartItem) {
                    PositionedStack stack = ((LayoutPartItem) part).getStack();
                    if (isInput) inputStacks.add(stack);
                    else outputStacks.add(stack);
                } else if (part instanceof LayoutPartFluid) {
                    PositionedFluidTank tank = ((LayoutPartFluid) part).getTank();
                    fluidTanks.add(tank);
                }
            }
        }

        private void clearLists() {
            inputStacks.clear();
            outputStacks.clear();
            fluidTanks.clear();
            allParts.clear();
        }

        public void drawSlots() {
            // Draw backgrounds for all parts
            for (RecipeLayoutPart<?> part : allParts) {
                // We pass Minecraft instance to draw
                part.draw(Minecraft.getMinecraft());
            }
        }

        public void drawExtras() {}

        public void handleTooltip(Point relMouse, List<String> currenttip) {
            for (RecipeLayoutPart<?> part : allParts) {
                part.handleTooltip(relMouse, currenttip);
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
            return outputStacks.isEmpty() ? null : outputStacks.get(0);
        }

        @Override
        public List<PositionedStack> getOtherStacks() {
            if (outputStacks.size() <= 1) return Collections.emptyList();
            return outputStacks.subList(1, outputStacks.size());
        }

        @Override
        public List<PositionedFluidTank> getFluidTanks() {
            return fluidTanks;
        }
    }
}
