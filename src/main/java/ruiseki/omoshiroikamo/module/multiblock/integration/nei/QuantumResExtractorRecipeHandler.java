package ruiseki.omoshiroikamo.module.multiblock.integration.nei;

import net.minecraft.block.Block;

import codechicken.nei.recipe.TemplateRecipeHandler;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.init.QuantumExtractorRecipes;

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
    protected IFocusableRegistry getRegistryForNEI(int tierArg, int dimId) {
        if (tierArg >= 0 && tierArg < QuantumExtractorRecipes.MAX_TIER) {
            return QuantumExtractorRecipes.getResRegistryForNEI(tierArg, dimId);
        }
        return null;
    }

    @Override
    protected Block getMinerBlock() {
        return MultiBlockBlocks.QUANTUM_RES_EXTRACTOR.getBlock();
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
