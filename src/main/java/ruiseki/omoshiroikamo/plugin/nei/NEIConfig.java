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

    public static final GuiContainerWrapperStackPositioner stackPositioner = new GuiContainerWrapperStackPositioner();
    public static final GuiContainerWrapperOverlayHandler overlayHandler = new GuiContainerWrapperOverlayHandler();

    @Override
    public void loadConfig() {
        API.registerRecipeHandler(new ElectrolyzerRecipeHandler());
        API.registerUsageHandler(new ElectrolyzerRecipeHandler());

        API.registerRecipeHandler(new AnvilRecipeHandler());
        API.registerUsageHandler(new AnvilRecipeHandler());

        API.registerRecipeHandler(new MaterialPropertiesHandler());
        API.registerUsageHandler(new MaterialPropertiesHandler());

        API.registerRecipeHandler(new VoidOreRecipeHandler());
        API.registerUsageHandler(new VoidOreRecipeHandler());

        API.registerRecipeHandler(new VoidResRecipeHandler());
        API.registerUsageHandler(new VoidResRecipeHandler());
        Logger.info("Loaded NeiConfig");
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
