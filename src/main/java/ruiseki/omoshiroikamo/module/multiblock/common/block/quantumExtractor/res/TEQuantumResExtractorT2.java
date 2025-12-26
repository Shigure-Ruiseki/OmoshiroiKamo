package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes.STRUCTURE_TIER_2;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_DEFINITION_TIER_2;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

public class TEQuantumResExtractorT2 extends TEQuantumExtractor {

    public TEQuantumResExtractorT2() {}

    @Override
    protected IStructureDefinition<TEQuantumResExtractorT2> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_2;
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
        return STRUCTURE_TIER_2;
    }

    @Override
    public int getTier() {
        return 2;
    }

    @Override
    public int getEnergyCostPerDuration() {
        return QuantumExtractorConfig.resMiner.getEnergyCost(2);
    }

    @Override
    public int getBaseDuration() {
        return QuantumExtractorConfig.resMiner.getTick(2);
    }

    @Override
    public int getMinDuration() {
        return QuantumExtractorConfig.resMiner.getMinTick(2);
    }

    @Override
    public int getMaxDuration() {
        return QuantumExtractorConfig.resMiner.getMaxTick(2);
    }
}
