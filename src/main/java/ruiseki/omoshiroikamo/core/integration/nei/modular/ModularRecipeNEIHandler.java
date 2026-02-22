package ruiseki.omoshiroikamo.core.integration.nei.modular;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
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
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.INEIPositionedRenderer;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.PositionedText;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
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
    public String getHandlerId() {
        return getRecipeID();
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
        if (crecipe instanceof CachedModularRecipe) {
            ((CachedModularRecipe) crecipe).handleTooltip(relMouse, currenttip);
        }
        return currenttip;
    }

    @Override
    public int getRecipeHeight(int recipe) {
        if (recipe >= 0 && recipe < arecipes.size()) {
            CachedModularRecipe crecipe = (CachedModularRecipe) arecipes.get(recipe);
            return crecipe.calculatedHeight;
        }
        return 130;
    }

    @Override
    public boolean mouseClicked(GuiRecipe<?> gui, int button, int recipe) {
        if (button == 0 || button == 1) {
            CachedModularRecipe crecipe = (CachedModularRecipe) arecipes.get(recipe);
            if (crecipe.arrowRect != null) {
                Point offset = gui.getRecipePosition(recipe);
                Point pos = GuiDraw.getMousePosition();
                Point relMouse = new Point(pos.x - gui.guiLeft - offset.x, pos.y - gui.guiTop - offset.y);
                if (crecipe.arrowRect.contains(relMouse)) {
                    if (button == 0) {
                        return GuiCraftingRecipe.openRecipeGui(getRecipeID());
                    } else {
                        return GuiUsageRecipe.openRecipeGui(getRecipeID());
                    }
                }
            }
        }
        return super.mouseClicked(gui, button, recipe);
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getRecipeID())) {
            List<ModularRecipe> allRecipes = RecipeLoader.getInstance()
                .getAllRecipes();
            for (ModularRecipe recipe : allRecipes) {
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
        if (NEIServerUtils
            .areStacksSameTypeCrafting(ingredient, new ItemStack(MachineryBlocks.MACHINE_CONTROLLER.getBlock()))) {
            for (ModularRecipe recipe : RecipeLoader.getInstance()
                .getAllRecipes()) {
                if (recipe.getRecipeGroup()
                    .equals(recipeGroup)) {
                    arecipes.add(new CachedModularRecipe(recipe));
                }
            }
        }

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

        public int calculatedHeight = 130;
        public Rectangle arrowRect;

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

            int currentX = 5;
            int currentY = 18;
            String recipeName = recipe.getName();
            if (recipeName != null) {
                PositionedText text = new PositionedText(
                    recipeName,
                    0x222222,
                    new Rectangle(4, currentY - 12, 162, 10),
                    false);
                allParts.add(new LayoutPartRenderer(text));
                // Adjust for word-wrapped text height
                int extraHeight = text.getRenderedHeight() - 10;
                if (extraHeight > 0) {
                    currentY += extraHeight;
                }
            }

            // Layout Inputs
            if (!inputParts.isEmpty()) {
                PositionedText inText = new PositionedText("Inputs", 0x444444, new Rectangle(0, currentY, 166, 10));
                allParts.add(new LayoutPartRenderer(inText));
                currentY += 12;

                currentY = layoutSection(inputParts, currentX, currentY);
            } else {
                PositionedText text = new PositionedText("No Input", 0x222222, new Rectangle(0, currentY, 166, 10));
                allParts.add(new LayoutPartRenderer(text));
            }

            // Layout Outputs
            currentY += 4;
            final int arrowY = currentY;
            final int duration = recipe.getDuration();
            this.arrowRect = new Rectangle(166 / 2 - 6, arrowY, 12, 12);
            allParts.add(new LayoutPartRenderer(new INEIPositionedRenderer() {

                @Override
                public void draw() {
                    Minecraft.getMinecraft().renderEngine
                        .bindTexture(new ResourceLocation(LibMisc.MOD_ID, "textures/gui/icons.png"));
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    Gui.func_146110_a(166 / 2 - 6, arrowY, 12, 156, 12, 12, 256.0f, 256.0f);
                }

                @Override
                public Rectangle getPosition() {
                    return arrowRect;
                }

                @Override
                public void handleTooltip(List<String> currenttip) {
                    currenttip.add(NEIClientUtils.translate("recipe.tooltip"));
                }
            }));

            float timeInSeconds = duration / 20.0f;
            String timeString = String.format("%.2f", timeInSeconds) + "seconds";

            PositionedText timeText = new PositionedText(
                timeString,
                0x444444,
                new Rectangle(166 / 2 + 10, arrowY + 2, 100, 10),
                false);
            allParts.add(new LayoutPartRenderer(timeText));
            currentY += 16;

            if (!outputParts.isEmpty()) {
                currentY += 10;
                PositionedText outText = new PositionedText("Outputs", 0x444444, new Rectangle(0, currentY, 166, 10));
                allParts.add(new LayoutPartRenderer(outText));
                currentY += 12;

                currentY = layoutSection(outputParts, currentX, currentY);
            } else {
                PositionedText text = new PositionedText("No Output", 0x222222, new Rectangle(0, currentY, 166, 10));
                allParts.add(new LayoutPartRenderer(text));
            }

            this.calculatedHeight = currentY + 10;

            populateLegacyLists(inputParts, true);
            populateLegacyLists(outputParts, false);
        }

        private int layoutSection(List<RecipeLayoutPart<?>> parts, int startX, int startY) {
            List<LayoutPartMana> manaParts = new ArrayList<>();
            List<LayoutPartEnergy> energyParts = new ArrayList<>();
            List<LayoutPartItem> itemParts = new ArrayList<>();
            List<LayoutPartEssentia> essentiaParts = new ArrayList<>();
            List<LayoutPartVis> visParts = new ArrayList<>();
            List<LayoutPartFluid> fluidParts = new ArrayList<>();
            List<LayoutPartGas> gasParts = new ArrayList<>();

            for (RecipeLayoutPart<?> part : parts) {
                if (part instanceof LayoutPartMana) manaParts.add((LayoutPartMana) part);
                else if (part instanceof LayoutPartEnergy) energyParts.add((LayoutPartEnergy) part);
                else if (part instanceof LayoutPartItem) itemParts.add((LayoutPartItem) part);
                else if (part instanceof LayoutPartEssentia) essentiaParts.add((LayoutPartEssentia) part);
                else if (part instanceof LayoutPartVis) visParts.add((LayoutPartVis) part);
                else if (part instanceof LayoutPartFluid) fluidParts.add((LayoutPartFluid) part);
                else if (part instanceof LayoutPartGas) gasParts.add((LayoutPartGas) part);
            }

            int currentX = startX;
            int maxY = startY;

            // 1. Items (Grid)
            if (!itemParts.isEmpty()) {
                int cols = Math.min(itemParts.size(), 3); // Max 3 columns
                int rows = (int) Math.ceil((double) itemParts.size() / cols);
                layoutGrid(itemParts, currentX, startY, cols, 18);
                currentX += (cols * 18) + 4;
                maxY = Math.max(maxY, startY + (rows * 18));
            }

            // 2. Essentia (Grid)
            if (!essentiaParts.isEmpty()) {
                int cols = Math.min(essentiaParts.size(), 2);
                int rows = (int) Math.ceil((double) essentiaParts.size() / cols);
                layoutGrid(essentiaParts, currentX, startY, cols, 18);
                currentX += (cols * 18) + 4;
                maxY = Math.max(maxY, startY + (rows * 18));
            }

            // 3. Vis (Grid)
            if (!visParts.isEmpty()) {
                int cols = Math.min(visParts.size(), 2);
                int rows = (int) Math.ceil((double) visParts.size() / cols);
                layoutGrid(visParts, currentX, startY, cols, 18);
                currentX += (cols * 18) + 4;
                maxY = Math.max(maxY, startY + (rows * 18));
            }

            // 4. Energy
            if (!energyParts.isEmpty()) {
                LayoutPartEnergy energy = energyParts.get(0);
                energy.setPosition(currentX, startY);
                allParts.add(energy);
                currentX += energy.getWidth() + 4;
                maxY = Math.max(maxY, startY + energy.getHeight());
            }

            // 5. Fluids (Grid)
            if (!fluidParts.isEmpty()) {
                int cols = Math.min(fluidParts.size(), 2);
                int rows = (int) Math.ceil((double) fluidParts.size() / cols);
                layoutGrid(fluidParts, currentX, startY, cols, 18);
                currentX += (cols * 18) + 4;
                maxY = Math.max(maxY, startY + (rows * 18));
            }

            // 6. Gas (Grid)
            if (!gasParts.isEmpty()) {
                int cols = Math.min(gasParts.size(), 2);
                int rows = (int) Math.ceil((double) gasParts.size() / cols);
                layoutGrid(gasParts, currentX, startY, cols, 18);
                currentX += (cols * 18) + 4;
                maxY = Math.max(maxY, startY + (rows * 18));
            }

            // 7. Mana (Bottom Bar)
            if (!manaParts.isEmpty()) {
                LayoutPartMana mana = manaParts.get(0);
                mana.setPosition(startX, maxY + 8);
                allParts.add(mana);
                maxY += mana.getHeight() + 12;
            }

            for (RecipeLayoutPart<?> part : itemParts) allParts.add(part);
            for (RecipeLayoutPart<?> part : essentiaParts) allParts.add(part);
            for (RecipeLayoutPart<?> part : visParts) allParts.add(part);
            for (RecipeLayoutPart<?> part : fluidParts) allParts.add(part);
            for (RecipeLayoutPart<?> part : gasParts) allParts.add(part);

            return maxY;
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
