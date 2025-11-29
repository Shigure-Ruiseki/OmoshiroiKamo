package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore;

import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorStructure.STRUCTURE_DEFINITION_TIER_4;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorStructure.STRUCTURE_TIER_4;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorStructure.TIER_OFFSET;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

public class TEQuantumOreExtractorT4 extends TEQuantumExtractor {

    public static EnvironmentalConfig.QuantumExtractorConfig config = EnvironmentalConfig.quantumExtractorConfig;

    public TEQuantumOreExtractorT4() {}

    @Override
    protected IStructureDefinition<TEQuantumOreExtractorT4> getStructureDefinition() {
        return STRUCTURE_DEFINITION_TIER_4;
    }

    @Override
    public int[][] getOffSet() {
        return TIER_OFFSET;
    }

    @Override
    public IFocusableRegistry getRegistry() {
        return QuantumExtractorRecipes.oreRegistry[3];
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
        return config.energyCostOreTier4;
    }

    @Override
    public int getBaseDuration() {
        return config.tickOreTier4;
    }

    @Override
    public int getMinDuration() {
        return config.tickOreTier4;
    }

    @Override
    public int getMaxDuration() {
        return config.tickOreTier4;
    }
}
