package ruiseki.omoshiroikamo.plugin.nei.recipe.voidMiner;

import net.minecraft.block.Block;

import codechicken.nei.recipe.TemplateRecipeHandler;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;

public class QuantumResExtractorRecipeHandler extends VoidMinerRecipeHandler {

    public QuantumResExtractorRecipeHandler(int tier) {
        super(tier);
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return new QuantumResExtractorRecipeHandler(tier);
    }

    @Override
    protected IFocusableRegistry getRegistry() {
        return QuantumExtractorRecipes.resRegistry[tier];
    }

    @Override
    protected IFocusableRegistry getRegistry(int tierArg) {
        if (tierArg >= 0 && tierArg < QuantumExtractorRecipes.MAX_TIER) {
            return QuantumExtractorRecipes.resRegistry[tierArg];
        }
        return null;
    }

    @Override
    protected Block getMinerBlock() {
        return ModBlocks.QUANTUM_RES_EXTRACTOR.get();
    }

    @Override
    protected String getMinerNameBase() {
        return "Void Resource Miner";
    }

    @Override
    protected String getRecipeIdBase() {
        return String.valueOf(ModObject.blockQuantumResExtractor.getRegistryName());
    }

    @Override
    protected VoidMinerRecipeHandler createForTier(int tier) {
        return new QuantumResExtractorRecipeHandler(tier);
    }
}
