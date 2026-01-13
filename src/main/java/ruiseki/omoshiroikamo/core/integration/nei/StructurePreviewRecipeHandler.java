package ruiseki.omoshiroikamo.core.integration.nei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.core.common.structure.BlockResolver;
import ruiseki.omoshiroikamo.core.common.structure.BlockResolver.ResolvedBlock;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData;
import ruiseki.omoshiroikamo.core.common.structure.StructureJsonLoader;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.QuantumBeaconShapes;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorShapes;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes;
import ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray.SolarArrayShapes;

/**
 * NEI handler to visualize StructureLib shapes (default and custom structures).
 */
public class StructurePreviewRecipeHandler extends RecipeHandlerBase {

    private static final int CELL = 12;
    private static final int GRID_MAX_WIDTH = 150;
    private static final int BASE_X = 6;
    private static final int BASE_Y = 18;
    private static final ItemStack PLACEHOLDER = new ItemStack(Blocks.stone);

    private static final String RECIPE_ID = "ok.structure";
    private static final int MAX_TIER = 6;

    @Override
    public String getRecipeID() {
        return RECIPE_ID;
    }

    @Override
    public String getGuiTexture() {
        return LibResources.GUI_NEI_BLANK;
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    @Override
    public String getRecipeName() {
        if (!arecipes.isEmpty() && arecipes.get(0) instanceof CachedStructureRecipe first) {
            return first.getTitle();
        }
        return "Structures";
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        arecipes.clear();

        // Machine Controller → show all structures
        Item controllerItem = MachineryBlocks.MACHINE_CONTROLLER.getItem();
        if (controllerItem != null && ingredient.getItem() == controllerItem) {
            loadAllRecipes();
            return;
        }

        // Blueprint → show specific structure only
        if (ingredient.getItem() instanceof ruiseki.omoshiroikamo.module.machinery.common.item.ItemMachineBlueprint) {
            String structureName = ruiseki.omoshiroikamo.module.machinery.common.item.ItemMachineBlueprint
                .getStructureName(ingredient);
            if (structureName != null && !structureName.isEmpty()) {
                loadStructureByName(structureName);
                return;
            }
        }

        super.loadUsageRecipes(ingredient);
    }

    /**
     * Load a specific structure by name (for blueprint usage lookup).
     */
    private void loadStructureByName(String structureName) {
        StructureManager manager = StructureManager.getInstance();
        var entry = manager.getCustomStructure(structureName);
        if (entry == null || entry.layers == null || entry.layers.isEmpty()) {
            return;
        }

        // Build shape array from layers
        String[][] shape = new String[entry.layers.size()][];
        for (int i = 0; i < entry.layers.size(); i++) {
            shape[i] = entry.layers.get(i).rows.toArray(new String[0]);
        }

        Map<Character, Object> mappings = toCharKeyMap(entry.mappings);
        String title = entry.displayName != null ? entry.displayName : structureName;
        addLayerRecipes(title, structureName, shape, mappings);
    }

    @Override
    public void loadAllRecipes() {
        arecipes.clear();

        // Built-in multiblocks (tiers 1..6)
        addTieredStructures(
            "ore_miner",
            "oreExtractorTier",
            "Ore Extractor",
            QuantumOreExtractorShapes::getShapeWithMappings);
        addTieredStructures(
            "res_miner",
            "resExtractorTier",
            "Resource Extractor",
            QuantumResExtractorShapes::getShapeWithMappings);
        addTieredStructures("solar_array", "solarArrayTier", "Solar Array", SolarArrayShapes::getShapeWithMappings);
        addTieredStructures(
            "quantum_beacon",
            "beaconTier",
            "Quantum Beacon",
            QuantumBeaconShapes::getShapeWithMappings);

        // Custom structures from config/omoshiroikamo/modular/structures
        addCustomStructures();
    }

    private void addTieredStructures(String fileKey, String baseName, String displayBase, ShapeProvider shapeProvider) {
        for (int tier = 1; tier <= MAX_TIER; tier++) {
            String structureName = baseName + tier;
            StructureJsonLoader.ShapeWithMappings swm = shapeProvider.get(tier);
            String[][] shape = swm != null ? swm.shape
                : StructureManager.getInstance()
                    .getShape(fileKey, structureName);
            Map<Character, Object> mappings = swm != null ? swm.dynamicMappings : null;

            if (shape == null) {
                continue;
            }

            String title = displayBase + " T" + tier;
            addLayerRecipes(title, structureName, shape, mappings);
        }
    }

    private void addCustomStructures() {
        StructureManager manager = StructureManager.getInstance();
        for (String name : manager.getCustomStructureNames()) {
            var entry = manager.getCustomStructure(name);
            if (entry == null || entry.layers == null || entry.layers.isEmpty()) {
                continue;
            }

            // Build shape array from layers
            String[][] shape = new String[entry.layers.size()][];
            for (int i = 0; i < entry.layers.size(); i++) {
                shape[i] = entry.layers.get(i).rows.toArray(new String[0]);
            }

            Map<Character, Object> mappings = toCharKeyMap(entry.mappings);
            String title = entry.displayName != null ? entry.displayName : name;
            addLayerRecipes(title, name, shape, mappings);
        }
    }

    private static Map<Character, Object> toCharKeyMap(Map<String, Object> src) {
        if (src == null || src.isEmpty()) {
            return null;
        }
        Map<Character, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> e : src.entrySet()) {
            if (e.getKey() == null || e.getKey()
                .isEmpty()) {
                continue;
            }
            result.put(
                e.getKey()
                    .charAt(0),
                e.getValue());
        }
        return result;
    }

    private void addLayerRecipes(String title, String structureName, String[][] shape,
        Map<Character, Object> mappings) {
        for (int layer = 0; layer < shape.length; layer++) {
            arecipes.add(new CachedStructureRecipe(title, structureName, layer, shape, mappings));
        }
    }

    @Override
    public void drawExtras(int recipeIndex) {
        super.drawExtras(recipeIndex);
        if (recipeIndex < 0 || recipeIndex >= arecipes.size()) return;
        if (!(arecipes.get(recipeIndex) instanceof CachedStructureRecipe csr)) return;

        GuiDraw.drawString(csr.getTitle(), BASE_X, 4, 0x404040, false);
        GuiDraw.drawString("Layer " + (csr.layerIndex + 1) + "/" + csr.layerCount, BASE_X, 14, 0x606060, false);

        drawLayerGrid(csr);
        drawLegend(csr);
    }

    private void drawLayerGrid(CachedStructureRecipe csr) {
        for (int y = 0; y < csr.height; y++) {
            String row = csr.rows[y];
            for (int x = 0; x < row.length(); x++) {
                char symbol = row.charAt(x);
                if (symbol == ' ') continue;
                int px = csr.offsetX + x * CELL;
                int py = csr.offsetY + y * CELL;

                // draw slot outline
                GuiDraw.drawRect(px, py, CELL, CELL, 0x20FFFFFF);
                GuiDraw.drawRect(px + 1, py + 1, CELL - 2, CELL - 2, 0x30000000);

                // draw symbol label (over stack)
                String label = String.valueOf(symbol);
                fontRenderer.drawString(label, px + 3, py + 2, 0xFFFFFF);
            }
        }
    }

    private void drawLegend(CachedStructureRecipe csr) {
        int legendX = BASE_X;
        int legendY = BASE_Y + csr.height * CELL + 6;
        int lines = 0;
        for (Map.Entry<Character, SymbolInfo> entry : csr.symbolInfo.entrySet()) {
            if (lines >= 8) break; // avoid overflow
            char symbol = entry.getKey();
            SymbolInfo info = entry.getValue();
            String summary = info.summary;
            GuiDraw.drawString(symbol + ": " + summary, legendX, legendY + lines * 10, 0x505050, false);
            lines++;
        }
    }

    private static List<String> extractBlockIds(Object mapping) {
        List<String> result = new ArrayList<>();
        if (mapping instanceof String str) {
            result.add(str);
            return result;
        }
        if (mapping instanceof StructureDefinitionData.BlockMapping bm) {
            if (bm.block != null) {
                result.add(bm.block);
            }
            if (bm.blocks != null) {
                for (StructureDefinitionData.BlockEntry entry : bm.blocks) {
                    if (entry != null && entry.id != null) {
                        result.add(entry.id);
                    }
                }
            }
            return result;
        }
        if (mapping instanceof Map<?, ?>map) {
            Object block = map.get("block");
            if (block instanceof String) {
                result.add((String) block);
            }
            Object blocks = map.get("blocks");
            if (blocks instanceof List<?>list) {
                for (Object obj : list) {
                    if (obj instanceof Map<?, ?>m2) {
                        Object id = m2.get("id");
                        if (id instanceof String) {
                            result.add((String) id);
                        }
                    } else if (obj instanceof String s) {
                        result.add(s);
                    }
                }
            }
            return result;
        }
        return result;
    }

    private static List<ItemStack> toStacks(List<String> ids) {
        List<ItemStack> stacks = new ArrayList<>();
        for (String id : ids) {
            ResolvedBlock resolved = BlockResolver.resolve(id);
            if (resolved == null || resolved.isAir) {
                continue;
            }
            Block block = resolved.block;
            Item item = Item.getItemFromBlock(block);
            if (item == null) {
                continue;
            }
            int meta = resolved.anyMeta ? 0 : resolved.meta;
            stacks.add(new ItemStack(item, 1, meta));
        }
        return stacks;
    }

    /** Cached recipe representing a single layer. */
    private class CachedStructureRecipe extends CachedBaseRecipe {

        private final String title;
        private final String structureName;
        private final int layerIndex;
        private final int layerCount;
        private final String[] rows;
        private final int width;
        private final int height;
        private final int offsetX;
        private final int offsetY;
        private final List<PositionedStack> stacks = new ArrayList<>();
        private final Map<Character, SymbolInfo> symbolInfo = new HashMap<>();

        CachedStructureRecipe(String title, String structureName, int layerIndex, String[][] shape,
            Map<Character, Object> mappings) {
            this.title = title;
            this.structureName = structureName;
            this.layerIndex = layerIndex;
            this.layerCount = shape.length;
            this.rows = shape[layerIndex];
            this.height = rows.length;
            this.width = rows.length > 0 ? rows[0].length() : 0;
            this.offsetX = BASE_X + Math.max(0, (GRID_MAX_WIDTH - width * CELL) / 2);
            this.offsetY = BASE_Y;

            Map<Character, Object> effectiveMappings = mappings != null ? mappings : new HashMap<>();
            buildSymbolInfo(effectiveMappings);
            buildStacks();
        }

        private void buildSymbolInfo(Map<Character, Object> mappings) {
            for (String row : rows) {
                for (int i = 0; i < row.length(); i++) {
                    char symbol = row.charAt(i);
                    if (symbol == ' ' || symbolInfo.containsKey(symbol)) continue;
                    List<String> ids = extractBlockIds(mappings.get(symbol));
                    List<ItemStack> stacks = toStacks(ids);
                    String summary;
                    if (!ids.isEmpty()) {
                        summary = ids.size() == 1 ? ids.get(0) : ids.get(0) + " (+" + (ids.size() - 1) + ")";
                    } else {
                        summary = "(unmapped)";
                    }
                    symbolInfo.put(symbol, new SymbolInfo(symbol, ids, stacks, summary));
                }
            }
        }

        private void buildStacks() {
            for (int y = 0; y < rows.length; y++) {
                String row = rows[y];
                for (int x = 0; x < row.length(); x++) {
                    char symbol = row.charAt(x);
                    if (symbol == ' ') continue;

                    SymbolInfo info = symbolInfo.get(symbol);
                    ItemStack baseStack = (info != null && !info.stacks.isEmpty()) ? info.stacks.get(0) : PLACEHOLDER;
                    PositionedStack stack = new PositionedStack(baseStack, offsetX + x * CELL, offsetY + y * CELL);
                    if (info != null && info.stacks.size() > 1) {
                        stack.setPermutationToRender(0);
                        stack.items = info.stacks.toArray(new ItemStack[0]);
                    }
                    stacks.add(stack);
                }
            }
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return stacks;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        String getTitle() {
            return title + " [" + structureName + "]";
        }
    }

    private static class SymbolInfo {

        final char symbol;
        final List<String> ids;
        final List<ItemStack> stacks;
        final String summary;

        SymbolInfo(char symbol, List<String> ids, List<ItemStack> stacks, String summary) {
            this.symbol = symbol;
            this.ids = ids;
            this.stacks = stacks;
            this.summary = summary;
        }
    }

    @FunctionalInterface
    private interface ShapeProvider {

        StructureJsonLoader.ShapeWithMappings get(int tier);
    }
}
