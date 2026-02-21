package ruiseki.omoshiroikamo.module.machinery;

import java.io.File;
import java.util.Map;

import net.minecraft.command.ICommand;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.module.machinery.common.command.CommandModular;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryItems;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;

public class MachineryModule extends ModModuleBase {

    private static File configDir;

    public MachineryModule() {
        super(OmoshiroiKamo.instance);
    }

    public static File getConfigDir() {
        return configDir;
    }

    @Override
    protected ICommonProxy createProxy() {
        try {
            if (MinecraftHelpers.isClientSide()) {
                return new MachineryClient();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new MachineryCommon();
    }

    @Override
    public boolean isEnable() {
        return BackportConfigs.enableMachinery;
    }

    @Override
    protected void registerSubCommand(Map<String, ICommand> subcommand) {
        super.registerSubCommand(subcommand);
        subcommand.put(CommandModular.NAME, new CommandModular(this.getMod()));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        configDir = event.getModConfigurationDirectory();
        MachineryBlocks.preInit();
        MachineryItems.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        RecipeLoader.getInstance()
            .loadAll(configDir);
    }
}
