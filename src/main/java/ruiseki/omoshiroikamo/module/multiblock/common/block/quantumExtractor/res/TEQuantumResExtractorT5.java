package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes.STRUCTURE_TIER_5;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_DEFINITION_TIER_5;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

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
    public ExtractorType getExtractorType() {
        return ExtractorType.RESOURCE;
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
