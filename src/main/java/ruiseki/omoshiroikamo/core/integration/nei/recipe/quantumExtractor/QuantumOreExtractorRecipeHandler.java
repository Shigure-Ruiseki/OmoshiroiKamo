package ruiseki.omoshiroikamo.core.integration.nei.recipe.quantumExtractor;

import net.minecraft.block.Block;

import codechicken.nei.recipe.TemplateRecipeHandler;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.init.QuantumExtractorRecipes;

public class QuantumOreExtractorRecipeHandler extends VoidMinerRecipeHandler {

    public QuantumOreExtractorRecipeHandler(int tier) {
        super(tier);
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new QuantumOreExtractorRecipeHandler(tier);
    }

    @Override
    protected IFocusableRegistry getRegistry() {
        return QuantumExtractorRecipes.oreRegistry[tier];
    }

    @Override
    protected IFocusableRegistry getRegistry(int tierArg) {
        if (tierArg >= 0 && tierArg < QuantumExtractorRecipes.MAX_TIER) {
            return QuantumExtractorRecipes.oreRegistry[tierArg];
        }
        return null;
    }

    @Override
    protected Block getMinerBlock() {
        return MultiBlockBlocks.QUANTUM_ORE_EXTRACTOR.getBlock();
    }

    @Override
    protected String getMinerNameBase() {
        return "Void Ore Miner";
    }

    @Override
    protected String getRecipeIdBase() {
        return String.valueOf(ModObject.blockQuantumOreExtractor.getRegistryName());
    }

    @Override
    protected VoidMinerRecipeHandler createForTier(int tier) {
        return new QuantumOreExtractorRecipeHandler(tier);
    }
}
