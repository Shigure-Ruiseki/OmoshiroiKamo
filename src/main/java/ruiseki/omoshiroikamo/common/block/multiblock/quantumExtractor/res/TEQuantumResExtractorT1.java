package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res;

import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_DEFINITION_TIER_1;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_TIER_1;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure.TIER_OFFSET;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.item.IFocusableRegistry;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.config.block.QuantumExtractorConfig;

public class TEQuantumResExtractorT1 extends TEQuantumExtractor {

    public TEQuantumResExtractorT1() {}

    @Override
    protected IStructureDefinition<TEQuantumResExtractorT1> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_1;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    @Override
    public IFocusableRegistry getRegistry() {
        return QuantumExtractorRecipes.quantumResExtractorRegistry;
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_1;
    }

    @Override
    public int getTier() {
        return 1;
    }

    public int getEnergyCostPerDuration() {
        return QuantumExtractorConfig.energyCostResTier1;
    }

    public int getBaseDuration() {
        return QuantumExtractorConfig.tickResTier1;
    }

    public int getMinDuration() {
        return QuantumExtractorConfig.tickResTier1;
    }

    public int getMaxDuration() {
        return QuantumExtractorConfig.tickResTier1;
    }
}
