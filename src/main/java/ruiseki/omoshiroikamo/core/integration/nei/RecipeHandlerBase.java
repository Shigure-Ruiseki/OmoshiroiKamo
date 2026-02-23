package ruiseki.omoshiroikamo.core.integration.nei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public abstract class RecipeHandlerBase extends TemplateRecipeHandler implements IRecipeHandlerBase {

    public final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    @Override
    public void prepare() {}

    public abstract String getRecipeID();

    public void changeToGuiTexture() {
        GuiDraw.changeTexture(this.getGuiTexture());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void addTransferRect(int x, int y, int width, int height) {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(x, y, width, height), this.getRecipeID()));
    }

    @Override
    public void drawForeground(int recipe) {
        super.drawForeground(recipe);
        this.drawFluidTanks(recipe);
        this.changeToGuiTexture();
    }

    @Override
    public void drawExtras(int recipe) {
        super.drawExtras(recipe);
        this.drawItemChance(recipe);
        this.drawFluidChance(recipe);
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("liquid") && results[0] instanceof FluidStack
            && ((FluidStack) results[0]).getFluid() != null) {
            this.loadCraftingRecipes((FluidStack) results[0]);
        } else if (outputId.equals(this.getRecipeID())) {
            this.loadAllRecipes();
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadAllRecipes() {}

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        FluidStack fluid = NEIUtils.getFluidStack(result);
        if (fluid != null && fluid.getFluid() != null) {
            this.loadCraftingRecipes(fluid);
        }
    }

    public void loadCraftingRecipes(FluidStack result) {}

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("liquid") && ingredients[0] instanceof FluidStack
            && ((FluidStack) ingredients[0]).getFluid() != null) {
            this.loadUsageRecipes((FluidStack) ingredients[0]);
        } else {
            super.loadUsageRecipes(inputId, ingredients);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        FluidStack fluid = NEIUtils.getFluidStack(ingredient);
        if (fluid != null && fluid.getFluid() != null) {
            this.loadUsageRecipes(fluid);
        }
    }

    public void loadUsageRecipes(FluidStack ingredient) {}

    @Override
    public List<String> handleTooltip(GuiRecipe<?> guiRecipe, List<String> currenttip, int recipe) {
        super.handleTooltip(guiRecipe, currenttip, recipe);
        CachedBaseRecipe crecipe = (CachedBaseRecipe) this.arecipes.get(recipe);

        if (GuiContainerManager.shouldShowTooltip(guiRecipe)) {
            Point mouse = GuiDraw.getMousePosition();
            Point offset = guiRecipe.getRecipePosition(recipe);
            Point relMouse = new Point(mouse.x - guiRecipe.guiLeft - offset.x, mouse.y - guiRecipe.guiTop - offset.y);
            currenttip = this.provideTooltip(guiRecipe, currenttip, crecipe, relMouse);
        }

        return currenttip;
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe<?> guiRecipe, ItemStack itemStack, List<String> currenttip,
        int recipe) {
        super.handleItemTooltip(guiRecipe, itemStack, currenttip, recipe);
        CachedBaseRecipe crecipe = (CachedBaseRecipe) this.arecipes.get(recipe);
        Point mouse = GuiDraw.getMousePosition();
        Point offset = guiRecipe.getRecipePosition(recipe);
        Point relMouse = new Point(mouse.x - guiRecipe.guiLeft - offset.x, mouse.y - guiRecipe.guiTop - offset.y);

        return this.provideItemTooltip(guiRecipe, itemStack, currenttip, crecipe, relMouse);
    }

    public List<String> provideTooltip(GuiRecipe<?> guiRecipe, List<String> currenttip, CachedBaseRecipe crecipe,
        Point relMouse) {
        if (crecipe.getFluidTanks() != null) {
            for (PositionedFluidTank tank : crecipe.getFluidTanks()) {
                if (tank.position.contains(relMouse)) {
                    tank.handleTooltip(currenttip);
                }
            }
        }
        return currenttip;
    }

    public List<String> provideItemTooltip(GuiRecipe<?> guiRecipe, ItemStack itemStack, List<String> currenttip,
        CachedBaseRecipe crecipe, Point relMouse) {
        for (PositionedStack stack : crecipe.getIngredients()) {
            if (stack instanceof PositionedStackAdv && ((PositionedStackAdv) stack).getRect()
                .contains(relMouse)) {
                currenttip = ((PositionedStackAdv) stack).handleTooltip(guiRecipe, currenttip);
            }
        }
        for (PositionedStack stack : crecipe.getOtherStacks()) {
            if (stack instanceof PositionedStackAdv && ((PositionedStackAdv) stack).getRect()
                .contains(relMouse)) {
                currenttip = ((PositionedStackAdv) stack).handleTooltip(guiRecipe, currenttip);
            }
        }
        PositionedStack stack = crecipe.getResult();
        if (stack instanceof PositionedStackAdv && ((PositionedStackAdv) stack).getRect()
            .contains(relMouse)) {
            currenttip = ((PositionedStackAdv) stack).handleTooltip(guiRecipe, currenttip);
        }
        return currenttip;
    }

    @Override
    public boolean keyTyped(GuiRecipe<?> gui, char keyChar, int keyCode, int recipe) {
        if (keyCode == NEIClientConfig.getKeyBinding("gui.recipe")) {
            if (this.transferFluidTank(gui, recipe, false)) {
                return true;
            }
        } else if (keyCode == NEIClientConfig.getKeyBinding("gui.usage")) {
            if (this.transferFluidTank(gui, recipe, true)) {
                return true;
            }
        }
        return super.keyTyped(gui, keyChar, keyCode, recipe);
    }

    @Override
    public boolean mouseClicked(GuiRecipe<?> gui, int button, int recipe) {
        if (button == 0) {
            if (this.transferFluidTank(gui, recipe, false)) {
                return true;
            }
        } else if (button == 1) {
            if (this.transferFluidTank(gui, recipe, true)) {
                return true;
            }
        }
        return super.mouseClicked(gui, button, recipe);
    }

    protected boolean transferFluidTank(GuiRecipe<?> guiRecipe, int recipe, boolean usage) {
        CachedBaseRecipe crecipe = (CachedBaseRecipe) this.arecipes.get(recipe);
        Point mouse = GuiDraw.getMousePosition();
        Point offset = guiRecipe.getRecipePosition(recipe);
        Point relMouse = new Point(mouse.x - guiRecipe.guiLeft - offset.x, mouse.y - guiRecipe.guiTop - offset.y);

        if (crecipe.getFluidTanks() != null) {
            for (PositionedFluidTank tank : crecipe.getFluidTanks()) {
                if (tank.position.contains(relMouse)) {
                    return tank.transfer(usage);
                }
            }
        }

        return false;
    }

    public void drawFluidTanks(int recipe) {
        CachedRecipe cachedRecipe = this.arecipes.get(recipe);
        if (cachedRecipe instanceof CachedBaseRecipe crecipe) {
            if (crecipe.getFluidTanks() != null) {
                for (PositionedFluidTank fluidTank : crecipe.getFluidTanks()) {
                    fluidTank.draw();
                }
            }
        }
    }

    public void drawItemChance(int recipeIndex) {
        CachedRecipe cachedRecipe = this.arecipes.get(recipeIndex);
        if (!(cachedRecipe instanceof CachedBaseRecipe crecipe)) {
            return;
        }

        List<PositionedStack> allStacks = new ArrayList<>();
        if (crecipe.getResult() != null) {
            allStacks.add(crecipe.getResult());
        }

        List<PositionedStack> ingredients = crecipe.getIngredients();
        if (ingredients != null) {
            allStacks.addAll(ingredients);
        }

        List<PositionedStack> others = crecipe.getOtherStacks();
        if (others != null) {
            allStacks.addAll(others);
        }

        for (PositionedStack stack : allStacks) {
            if (stack instanceof PositionedStackAdv advStack) {
                advStack.drawChance();
                advStack.drawLabel();
            }
        }
    }

    public void drawFluidChance(int recipeIndex) {
        CachedRecipe cachedRecipe = this.arecipes.get(recipeIndex);
        if (!(cachedRecipe instanceof CachedBaseRecipe crecipe)) {
            return;
        }

        List<PositionedFluidTank> tanks = crecipe.getFluidTanks();
        if (tanks == null || tanks.isEmpty()) {
            return;
        }

        for (PositionedFluidTank tank : tanks) {
            if (tank != null) {
                tank.drawChance();
            }
        }
    }

    @Override
    public String getOverlayIdentifier() {
        return this.getRecipeID();
    }

    public static void drawItemSlot(int x, int y) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(new ResourceLocation(LibResources.GUI_SLOT));
        GuiDraw.drawTexturedModalRect(x, y, 18, 0, 18, 18);
    }

    public void drawFluidSlot(int x, int y) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(new ResourceLocation(LibResources.GUI_SLOT));
        GuiDraw.drawTexturedModalRect(x, y, 0, 0, 18, 18);
    }

    public void drawStretchedItemSlot(int x, int y, int totalWidth, int totalHeight) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(new ResourceLocation(LibResources.GUI_SLOT));

        int texX = 18;
        int texY = 0;
        int pieceW = 6;
        int pieceH = 6;

        int innerW = totalWidth - 2 * pieceW;
        int innerH = totalHeight - 2 * pieceH;

        GuiDraw.drawTexturedModalRect(x, y, texX, texY, pieceW, pieceH); // Top-left
        GuiDraw.drawTexturedModalRect(x + totalWidth - pieceW, y, texX + 12, texY, pieceW, pieceH); // Top-right
        GuiDraw.drawTexturedModalRect(x, y + totalHeight - pieceH, texX, texY + 12, pieceW, pieceH); // Bottom-left
        GuiDraw.drawTexturedModalRect(
            x + totalWidth - pieceW,
            y + totalHeight - pieceH,
            texX + 12,
            texY + 12,
            pieceW,
            pieceH); // Bottom-right

        for (int i = 0; i < innerW; i += pieceW) {
            int w = Math.min(pieceW, innerW - i);
            GuiDraw.drawTexturedModalRect(x + pieceW + i, y, texX + 6, texY, w, pieceH); // Top
            GuiDraw.drawTexturedModalRect(x + pieceW + i, y + totalHeight - pieceH, texX + 6, texY + 12, w, pieceH); // Bottom
        }

        for (int j = 0; j < innerH; j += pieceH) {
            int h = Math.min(pieceH, innerH - j);
            GuiDraw.drawTexturedModalRect(x, y + pieceH + j, texX, texY + 6, pieceW, h); // Left
            GuiDraw.drawTexturedModalRect(x + totalWidth - pieceW, y + pieceH + j, texX + 12, texY + 6, pieceW, h); // Right
        }

        for (int i = 0; i < innerW; i += pieceW) {
            int w = Math.min(pieceW, innerW - i);
            for (int j = 0; j < innerH; j += pieceH) {
                int h = Math.min(pieceH, innerH - j);
                GuiDraw.drawTexturedModalRect(x + pieceW + i, y + pieceH + j, texX + 6, texY + 6, w, h); // Center
            }
        }
    }

    public void drawStretchedFluidSlot(int x, int y, int totalWidth, int totalHeight) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(new ResourceLocation(LibResources.GUI_SLOT));

        int texX = 0;
        int texY = 0;
        int pieceW = 6;
        int pieceH = 6;

        int innerW = totalWidth - 2 * pieceW;
        int innerH = totalHeight - 2 * pieceH;

        GuiDraw.drawTexturedModalRect(x, y, texX, texY, pieceW, pieceH); // Top-left
        GuiDraw.drawTexturedModalRect(x + totalWidth - pieceW, y, texX + 12, texY, pieceW, pieceH); // Top-right
        GuiDraw.drawTexturedModalRect(x, y + totalHeight - pieceH, texX, texY + 12, pieceW, pieceH); // Bottom-left
        GuiDraw.drawTexturedModalRect(
            x + totalWidth - pieceW,
            y + totalHeight - pieceH,
            texX + 12,
            texY + 12,
            pieceW,
            pieceH);

        for (int i = 0; i < innerW; i += pieceW) {
            int w = Math.min(pieceW, innerW - i);
            GuiDraw.drawTexturedModalRect(x + pieceW + i, y, texX + 6, texY, w, pieceH);
            GuiDraw.drawTexturedModalRect(x + pieceW + i, y + totalHeight - pieceH, texX + 6, texY + 12, w, pieceH);
        }

        for (int j = 0; j < innerH; j += pieceH) {
            int h = Math.min(pieceH, innerH - j);
            GuiDraw.drawTexturedModalRect(x, y + pieceH + j, texX, texY + 6, pieceW, h);
            GuiDraw.drawTexturedModalRect(x + totalWidth - pieceW, y + pieceH + j, texX + 12, texY + 6, pieceW, h);
        }

        for (int i = 0; i < innerW; i += pieceW) {
            int w = Math.min(pieceW, innerW - i);
            for (int j = 0; j < innerH; j += pieceH) {
                int h = Math.min(pieceH, innerH - j);
                GuiDraw.drawTexturedModalRect(x + pieceW + i, y + pieceH + j, texX + 6, texY + 6, w, h); // Center
            }
        }
    }

    public abstract class CachedBaseRecipe extends CachedRecipe {

        public List<PositionedFluidTank> getFluidTanks() {
            List<PositionedFluidTank> tanks = new ArrayList<>();
            PositionedFluidTank tank = this.getFluidTank();
            if (tank != null) {
                tanks.add(tank);
            }
            return tanks;
        }

        public PositionedFluidTank getFluidTank() {
            return null;
        }
    }
}
