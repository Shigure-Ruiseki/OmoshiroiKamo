package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res;

import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorShapes.STRUCTURE_TIER_5;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_DEFINITION_TIER_5;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

public class TEQuantumResExtractorT5 extends TEQuantumExtractor {

    public static EnvironmentalConfig.QuantumExtractorConfig config = EnvironmentalConfig.quantumExtractorConfig;

    public TEQuantumResExtractorT5() {}

    @Override
    protected IStructureDefinition<TEQuantumResExtractorT5> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_5;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    @Override
    protected boolean isOreExtractor() {
        return false;
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_5;
    }

    @Override
    public int getTier() {
        return 5;
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
        return config.minTickResTier5;
    }

    @Override
    public int getMaxDuration() {
        return config.tickResTier5;
    }
}
