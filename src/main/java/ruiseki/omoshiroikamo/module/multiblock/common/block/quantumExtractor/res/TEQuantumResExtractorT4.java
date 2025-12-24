package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes.STRUCTURE_TIER_4;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_DEFINITION_TIER_4;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

public class TEQuantumResExtractorT4 extends TEQuantumExtractor {

    public static EnvironmentalConfig.QuantumExtractorConfig config = EnvironmentalConfig.quantumExtractorConfig;

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
    public ExtractorType getExtractorType() {
        return ExtractorType.RESOURCE;
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
        return config.energyCostResTier4;
    }

    @Override
    public int getBaseDuration() {
        return config.tickResTier4;
    }

    @Override
    public int getMinDuration() {
        return config.minTickResTier4;
    }

    @Override
    public int getMaxDuration() {
        return config.tickResTier4;
    }
}
