package ruiseki.omoshiroikamo.core;

import java.util.Map;

import net.minecraft.command.ICommand;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityEnergy;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityFluidHandler;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityItemHandler;
import ruiseki.omoshiroikamo.core.command.structure.CommandStructure;
import ruiseki.omoshiroikamo.core.common.init.CoreItems;
import ruiseki.omoshiroikamo.core.common.init.CoreOreDict;
import ruiseki.omoshiroikamo.core.common.init.CoreRecipes;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;

public class CoreModule extends ModModuleBase {

    public CoreModule() {
        super(OmoshiroiKamo.instance);
    }

    @Override
    protected ICommonProxy createProxy() {
        try {
            if (MinecraftHelpers.isClientSide()) {
                return new CoreClient();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new CoreCommon();
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CoreItems.preInit();

        // Initialize the custom structure system
        StructureManager.getInstance()
            .initialize(
                event.getModConfigurationDirectory()
                    .getParentFile());

        CapabilityItemHandler.register();
        CapabilityEnergy.register();
        CapabilityFluidHandler.register();

    }

    @Override
    protected void registerSubCommand(Map<String, ICommand> subcommand) {
        super.registerSubCommand(subcommand);
        subcommand.put(CommandStructure.NAME, new CommandStructure(this.getMod()));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        CoreOreDict.init();
        CoreRecipes.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
