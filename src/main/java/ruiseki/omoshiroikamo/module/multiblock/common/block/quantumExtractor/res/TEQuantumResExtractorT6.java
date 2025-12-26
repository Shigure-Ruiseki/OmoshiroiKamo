package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes.STRUCTURE_TIER_6;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_DEFINITION_TIER_6;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.config.backport.muliblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

public class TEQuantumResExtractorT6 extends TEQuantumExtractor {

    public TEQuantumResExtractorT6() {}

    @Override
    protected IStructureDefinition<TEQuantumResExtractorT6> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_6;
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
        return STRUCTURE_TIER_6;
    }

    @Override
    public int getTier() {
        return 6;
    }

    @Override
    public int getEnergyCostPerDuration() {
        return QuantumExtractorConfig.energyCostResTier6;
    }

    @Override
    public int getBaseDuration() {
        return QuantumExtractorConfig.tickResTier6;
    }

    @Override
    public int getMinDuration() {
        return QuantumExtractorConfig.minTickResTier6;
    }

    @Override
    public int getMaxDuration() {
        return QuantumExtractorConfig.tickResTier6;
    }
}
