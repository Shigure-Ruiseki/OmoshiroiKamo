package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore;

import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorShapes.STRUCTURE_TIER_5;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorShapes.TIER_OFFSET;
import static ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.QuantumOreExtractorStructure.STRUCTURE_DEFINITION_TIER_5;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

public class TEQuantumOreExtractorT5 extends TEQuantumExtractor {

    public static EnvironmentalConfig.QuantumExtractorConfig config = EnvironmentalConfig.quantumExtractorConfig;

    public TEQuantumOreExtractorT5() {}

    @Override
    protected IStructureDefinition<TEQuantumOreExtractorT5> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_5;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
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
    public ExtractorType getExtractorType() {
        return ExtractorType.ORE;
    }

    @Override
    public int getEnergyCostPerDuration() {
        return config.energyCostOreTier5;
    }

    @Override
    public int getBaseDuration() {
        return config.tickOreTier5;
    }

    public int getMinDuration() {
        return config.minTickOreTier5;
    }

    @Override
    public int getMaxDuration() {
        return config.tickOreTier5;
    }
}
