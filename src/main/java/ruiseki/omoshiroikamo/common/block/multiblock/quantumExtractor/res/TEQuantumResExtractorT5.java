package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res;

import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_DEFINITION_TIER_4;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_TIER_4;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure.TIER_OFFSET;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

public class TEQuantumResExtractorT5 extends TEQuantumExtractor {

    public static EnvironmentalConfig.QuantumExtractorConfig config = EnvironmentalConfig.quantumExtractorConfig;

    public TEQuantumResExtractorT5() {}

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
        return QuantumExtractorRecipes.resRegistry[4];
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
        return config.energyCostResTier5;
    }

    @Override
    public int getBaseDuration() {
        return config.tickResTier5;
    }

    @Override
    public int getMinDuration() {
        return config.tickResTier5;
    }

    @Override
    public int getMaxDuration() {
        return config.tickResTier5;
    }
}
