package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes.STRUCTURE_TIER_1;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.QuantumResExtractorStructure.STRUCTURE_DEFINITION_TIER_1;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

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
    public ExtractorType getExtractorType() {
        return ExtractorType.RESOURCE;
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
        return QuantumExtractorConfig.resMiner.getEnergyCost(1);
    }

    public int getBaseDuration() {
        return QuantumExtractorConfig.resMiner.getTick(1);
    }

    public int getMinDuration() {
        return QuantumExtractorConfig.resMiner.getMinTick(1);
    }

    public int getMaxDuration() {
        return QuantumExtractorConfig.resMiner.getMaxTick(1);
    }
}
