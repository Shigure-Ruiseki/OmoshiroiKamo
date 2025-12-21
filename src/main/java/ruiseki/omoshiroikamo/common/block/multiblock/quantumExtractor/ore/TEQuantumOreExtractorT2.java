package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore;

import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorShapes.STRUCTURE_TIER_2;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorStructure.STRUCTURE_DEFINITION_TIER_2;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

public class TEQuantumOreExtractorT2 extends TEQuantumExtractor {

    public static EnvironmentalConfig.QuantumExtractorConfig config = EnvironmentalConfig.quantumExtractorConfig;

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
    public int getEnergyCostPerDuration() {
        return config.energyCostOreTier2;
    }

    @Override
    public int getBaseDuration() {
        return config.tickOreTier2;
    }

    public int getMinDuration() {
        return config.minTickOreTier2;
    }

    @Override
    public int getMaxDuration() {
        return config.tickOreTier2;
    }
}
