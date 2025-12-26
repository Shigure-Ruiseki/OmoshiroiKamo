package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorShapes.STRUCTURE_TIER_6;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorStructure.STRUCTURE_DEFINITION_TIER_6;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

public class TEQuantumOreExtractorT6 extends TEQuantumExtractor {

    public TEQuantumOreExtractorT6() {}

    @Override
    protected IStructureDefinition<TEQuantumOreExtractorT6> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_6;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
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
    public ExtractorType getExtractorType() {
        return ExtractorType.ORE;
    }

    @Override
    public int getEnergyCostPerDuration() {
        return QuantumExtractorConfig.oreMiner.getEnergyCost(6);
    }

    @Override
    public int getBaseDuration() {
        return QuantumExtractorConfig.oreMiner.getTick(6);
    }

    public int getMinDuration() {
        return QuantumExtractorConfig.oreMiner.getMinTick(6);
    }

    @Override
    public int getMaxDuration() {
        return QuantumExtractorConfig.oreMiner.getMaxTick(6);
    }
}
