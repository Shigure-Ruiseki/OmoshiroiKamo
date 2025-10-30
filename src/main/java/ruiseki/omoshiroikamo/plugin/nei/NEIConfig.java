package ruiseki.omoshiroikamo.plugin.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.plugin.nei.recipe.AnvilRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.ElectrolyzerRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.MaterialPropertiesHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.voidMiner.VoidOreRecipeHandler;
import ruiseki.omoshiroikamo.plugin.nei.recipe.voidMiner.VoidResRecipeHandler;

@SuppressWarnings("unused")
public class NEIConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        Logger.info("Loading NeiConfig: " + getName());

        registerHandler(new ElectrolyzerRecipeHandler());
        registerHandler(new AnvilRecipeHandler());
        registerHandler(new MaterialPropertiesHandler());
        registerHandler(new VoidOreRecipeHandler());
        registerHandler(new VoidResRecipeHandler());
    }

    protected static void registerHandler(IRecipeHandlerBase handler) {
        handler.prepare();
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);
    }

    @Override
    public String getName() {
        return LibMisc.MOD_NAME;
    }

    @Override
    public String getVersion() {
        return LibMisc.VERSION;
    }
}
