package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorShapes.STRUCTURE_TIER_1;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorStructure.STRUCTURE_DEFINITION_TIER_1;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

public class TEQuantumOreExtractorT1 extends TEQuantumExtractor {

    public TEQuantumOreExtractorT1() {}

    @Override
    protected IStructureDefinition<TEQuantumOreExtractorT1> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_1;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    @Override
    public String getStructurePieceName() {
        return STRUCTURE_TIER_1;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public ExtractorType getExtractorType() {
        return ExtractorType.ORE;
    }

    public int getEnergyCostPerDuration() {
        return QuantumExtractorConfig.oreMiner.getEnergyCost(1);
    }

    public int getBaseDuration() {
        return QuantumExtractorConfig.oreMiner.getTick(1);
    }

    public int getMinDuration() {
        return QuantumExtractorConfig.oreMiner.getMinTick(1);
    }

    public int getMaxDuration() {
        return QuantumExtractorConfig.oreMiner.getMaxTick(1);
    }
}
