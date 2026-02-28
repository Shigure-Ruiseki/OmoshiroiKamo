package ruiseki.omoshiroikamo.core.common.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;
import ruiseki.omoshiroikamo.api.structure.visitor.IStructureVisitor;
import ruiseki.omoshiroikamo.core.tileentity.AbstractMBModifierTE;

/**
 * Visitor that registers a structure with StructureLib.
 */
public class StructureRegistrationVisitor<T extends AbstractMBModifierTE> implements IStructureVisitor {

    private final StructureDefinition.Builder<T> builder = StructureDefinition.builder();
    private final Block controllerBlock;
    private final int tier;
    private IStructureDefinition<T> definition;

    public StructureRegistrationVisitor(Block controllerBlock, int tier) {
        this.controllerBlock = controllerBlock;
        this.tier = tier;
    }

    public IStructureDefinition<T> getDefinition() {
        return definition;
    }

    @Override
    public void visit(IStructureEntry entry) {
        // 1. Setup reserved symbols
        builder.addElement('Q', StructureUtility.ofBlock(controllerBlock, tier - 1));
        builder.addElement('_', StructureUtility.isAir());

        // 2. Add Mappings
        for (Map.Entry<Character, ISymbolMapping> mappingEntry : entry.getMappings()
            .entrySet()) {
            char symbol = mappingEntry.getKey();
            if (symbol == 'Q' || symbol == '_' || symbol == ' ') continue;

            IStructureElement<T> element = createElement(mappingEntry.getValue());
            if (element != null) {
                builder.addElement(symbol, element);
            }
        }

        // 3. Add Shape (Transposed for StructureLib)
        List<String[][]> layersData = new ArrayList<>();
        for (IStructureLayer layer : entry.getLayers()) {
            layersData.add(layer.toStructureLibRows());
        }

        String[][] combinedShape = combineLayers(entry.getLayers());
        builder.addShape(entry.getName(), StructureUtility.transpose(combinedShape));

        // 4. Build the final definition
        this.definition = builder.build();
    }

    @Override
    public void visit(IStructureRequirement requirement) {
        // Requirements are handled by StructureManager validation, not StructureLib
        // shape
    }

    private String[][] combineLayers(List<IStructureLayer> layers) {

        List<String> allRows = new ArrayList<>();
        for (IStructureLayer layer : layers) {
            allRows.addAll(layer.getRows());
        }

        IStructureLayer first = layers.get(0);
        String[][] result = new String[first.getRows()
            .size()][];
        for (int i = 0; i < first.getRows()
            .size(); i++) {
            result[i] = new String[] { first.getRows()
                .get(i) };
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private IStructureElement<T> createElement(ISymbolMapping mapping) {
        if (mapping instanceof BlockMapping) {
            BlockMapping bm = (BlockMapping) mapping;
            if (bm.getBlockId() != null) {
                return BlockResolver.createElement(bm.getBlockId());
            } else if (bm.getBlockIds() != null) {
                return BlockResolver.createChainElement(bm.getBlockIds());
            }
        }
        return null;
    }
}
