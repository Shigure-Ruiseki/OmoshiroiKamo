package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore;

import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorStructure.STRUCTURE_DEFINITION_TIER_1;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorStructure.STRUCTURE_TIER_1;
import static ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorStructure.TIER_OFFSET;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;

import ruiseki.omoshiroikamo.api.item.IFocusableRegistry;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.config.block.QuantumExtractorConfig;

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
    public IFocusableRegistry getRegistry() {
        return QuantumExtractorRecipes.quantumOreExtractorRegistry;
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
        return QuantumExtractorConfig.energyCostOreTier1;
    }

    public int getBaseDuration() {
        return QuantumExtractorConfig.tickOreTier1;
    }

    public int getMinDuration() {
        return QuantumExtractorConfig.tickOreTier1;
    }

    public int getMaxDuration() {
        return QuantumExtractorConfig.tickOreTier1;
    }
}
