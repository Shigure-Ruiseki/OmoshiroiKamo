package ruiseki.omoshiroikamo.plugin.nei.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.api.material.MaterialEntry;
import ruiseki.omoshiroikamo.api.material.MaterialRegistry;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.nei.PositionedStackAdv;
import ruiseki.omoshiroikamo.plugin.nei.RecipeHandlerBase;

public class MaterialPropertiesHandler extends RecipeHandlerBase {

    @Override
    public String getRecipeName() {
        return "Material Properties";
    }

    @Override
    public String getRecipeID() {
        return "materialProperties";
    }

    @Override
    public String getGuiTexture() {
        return LibResources.GUI_NEI_BLANK;
    }

    @Override
    public void loadTransferRects() {
        this.addTransferRect(0, 0, 0, 0);
    }

    @Override
    public void loadUsageRecipes(ItemStack item) {
        super.loadUsageRecipes(item);
        for (MaterialEntry entry : MaterialRegistry.all()) {
            int[] metas = { entry.meta, LibResources.META1 + entry.meta, LibResources.META2 + entry.meta,
                LibResources.META3 + entry.meta, LibResources.META4 + entry.meta, LibResources.META5 + entry.meta };

            for (int meta : metas) {
                if (NEIServerUtils.areStacksSameTypeCrafting(item, ModItems.MATERIAL.newItemStack(1, meta))) {
                    arecipes.add(new CachedMaterialPropertise(entry));
                    break;
                }
            }
        }
    }

    @Override
    public void drawBackground(int recipeIndex) {
        super.drawBackground(recipeIndex);
        CachedMaterialPropertise recipe = (CachedMaterialPropertise) arecipes.get(recipeIndex);

        int itemsPerRow = 8;
        int itemSize = 18;
        int xStart = 9;
        int yStart = 9;

        // === Vẽ slot item ===
        for (int i = 0; i < recipe.materialItems.size(); i++) {
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;
            int x = xStart + col * itemSize;
            int y = yStart + row * itemSize;
            drawItemSlot(x, y);
        }

        int itemRows = (recipe.materialItems.size() + itemsPerRow - 1) / itemsPerRow;
        int textX = xStart;
        int textY = yStart + itemRows * itemSize + 6;
        int lineHeight = 11;
        int line = 0;

        MaterialEntry entry = recipe.materialEntry;

        int titleColor = 0x2222AA;
        int labelColor = 0x333333;
        int valueColor = 0x555555;

        GuiDraw.drawString(
            "§lMaterial: " + entry.getUnlocalizedName(),
            textX,
            textY + lineHeight * line++,
            titleColor,
            false);

        GuiDraw.drawString("Density:", textX, textY + lineHeight * line, labelColor, false);
        GuiDraw.drawString(
            String.format("%.0f kg/m³", entry.getDensityKgPerM3()),
            textX + 65,
            textY + lineHeight * line++,
            valueColor,
            false);

        GuiDraw.drawString("Spec. Heat:", textX, textY + lineHeight * line, labelColor, false);
        GuiDraw.drawString(
            String.format("%.2f J/kgK", entry.getSpecificHeat()),
            textX + 65,
            textY + lineHeight * line++,
            valueColor,
            false);

        GuiDraw.drawString("Conductivity:", textX, textY + lineHeight * line, labelColor, false);
        GuiDraw.drawString(
            String.format("%.2f W/mK", entry.getThermalConductivity()),
            textX + 65,
            textY + lineHeight * line++,
            valueColor,
            false);

        GuiDraw.drawString("Melting Pt:", textX, textY + lineHeight * line, labelColor, false);
        GuiDraw.drawString(
            String.format("%.0f K", entry.getMeltingPointK()),
            textX + 65,
            textY + lineHeight * line++,
            valueColor,
            false);

        GuiDraw.drawString("Pressure:", textX, textY + lineHeight * line, labelColor, false);
        GuiDraw.drawString(
            String.format("%.2f MPa", entry.getMaxPressureMPa()),
            textX + 65,
            textY + lineHeight * line++,
            valueColor,
            false);

        GuiDraw.drawString("Electric:", textX, textY + lineHeight * line, labelColor, false);
        GuiDraw.drawString(
            String.format("%.2f", entry.getElectricalConductivity()),
            textX + 65,
            textY + lineHeight * line++,
            valueColor,
            false);

        GuiDraw.drawString("Voltage:", textX, textY + lineHeight * line, labelColor, false);
        GuiDraw.drawString(
            String.format("%d V", entry.getMaxVoltage()),
            textX + 65,
            textY + lineHeight * line++,
            valueColor,
            false);

        GuiDraw.drawString("Tier:", textX, textY + lineHeight * line, labelColor, false);
        GuiDraw.drawString(
            entry.getVoltageTier()
                .getDisplayName(),
            textX + 65,
            textY + lineHeight * line++,
            valueColor,
            false);

    }

    public class CachedMaterialPropertise extends CachedBaseRecipe {

        private final List<ItemStack> materialItems = new ArrayList<>();
        private final MaterialEntry materialEntry;

        public CachedMaterialPropertise(MaterialEntry entry) {
            materialEntry = entry;
            int[] metas = { entry.meta, LibResources.META1 + entry.meta, LibResources.META2 + entry.meta,
                LibResources.META3 + entry.meta, LibResources.META4 + entry.meta, LibResources.META5 + entry.meta };
            for (int meta : metas) {
                materialItems.add(ModItems.MATERIAL.newItemStack(1, meta));
            }
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> stacks = new ArrayList<>();
            int baseX = 10;
            int baseY = 10;
            int itemSize = 18;
            int itemsPerRow = 8;

            for (int i = 0; i < materialItems.size(); i++) {
                int row = i / itemsPerRow;
                int col = i % itemsPerRow;
                int x = baseX + col * itemSize;
                int y = baseY + row * itemSize;
                stacks.add(new PositionedStackAdv(materialItems.get(i), x, y));
            }

            return stacks;
        }

        public List<ItemStack> getMaterialItems() {
            return materialItems;
        }

        public MaterialEntry getMaterialEntry() {
            return materialEntry;
        }
    }

}
