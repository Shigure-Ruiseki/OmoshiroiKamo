package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res;

import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_DEFINITION_TIER_4;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_TIER_4;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure.TIER_OFFSET;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.item.IFocusableRegistry;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.config.block.QuantumExtractorConfig;

public class TEQuantumResExtractorT4 extends TEQuantumExtractor {

    public TEQuantumResExtractorT4() {}

    @Override
    protected IStructureDefinition<TEQuantumResExtractorT4> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_4;
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
        return STRUCTURE_TIER_4;
    }

    @Override
    public int getTier() {
        return 4;
    }

    @Override
    public int getEnergyCostPerDuration() {
        return QuantumExtractorConfig.energyCostResTier4;
    }

    @Override
    public int getBaseDuration() {
        return QuantumExtractorConfig.tickResTier4;
    }

    @Override
    public int getMinDuration() {
        return QuantumExtractorConfig.tickResTier4;
    }

    @Override
    public int getMaxDuration() {
        return QuantumExtractorConfig.tickResTier4;
    }
}
