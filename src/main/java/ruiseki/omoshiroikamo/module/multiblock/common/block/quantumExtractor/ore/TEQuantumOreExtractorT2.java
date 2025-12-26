package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorShapes.STRUCTURE_TIER_2;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorStructure.STRUCTURE_DEFINITION_TIER_2;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

public class TEQuantumOreExtractorT2 extends TEQuantumExtractor {

    public TEQuantumOreExtractorT2() {}

    @Override
    protected IStructureDefinition<TEQuantumOreExtractorT2> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_2;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
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
    public ExtractorType getExtractorType() {
        return ExtractorType.ORE;
    }

    @Override
    public int getEnergyCostPerDuration() {
        return QuantumExtractorConfig.oreMiner.getEnergyCost(2);
    }

    @Override
    public int getBaseDuration() {
        return QuantumExtractorConfig.oreMiner.getTick(2);
    }

    public int getMinDuration() {
        return QuantumExtractorConfig.oreMiner.getMinTick(2);
    }

    @Override
    public int getMaxDuration() {
        return QuantumExtractorConfig.oreMiner.getMaxTick(2);
    }
}
