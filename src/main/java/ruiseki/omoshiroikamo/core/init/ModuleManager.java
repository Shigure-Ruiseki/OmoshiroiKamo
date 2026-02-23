package ruiseki.omoshiroikamo.core.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommand;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;

public class ModuleManager {

    private final ModBase mod;

    public ModuleManager(ModBase mod) {
        this.mod = mod;
    }

    private final List<ModModuleBase> modules = new ArrayList<>();

    public void register(ModModuleBase module) {
        modules.add(module);
    }

    public void preInit(FMLPreInitializationEvent event) {
        for (ModModuleBase module : modules) {
            if (!module.isEnable()) continue;
            module.preInit(event);
        }
    }

    public void proxyPreInit() {
        for (ModModuleBase module : modules) {
            if (!module.isEnable()) continue;
            ICommonProxy proxy = module.getModuleProxy();
            if (proxy != null) {
                proxy.registerEventHooks();
            }
        }
    }

    public void init(FMLInitializationEvent event) {
        for (ModModuleBase module : modules) {
            if (!module.isEnable()) continue;
            module.init(event);
        }
    }

    public void proxyInit() {
        for (ModModuleBase module : modules) {
            if (!module.isEnable()) continue;
            ICommonProxy proxy = module.getModuleProxy();
            if (proxy != null) {
                proxy.registerRenderers();
                proxy.registerKeyBindings(mod.getKeyRegistry());
                proxy.registerPacketHandlers(mod.getPacketHandler());
                proxy.registerTickHandlers();
            }
        }
    }

    public void postInit(FMLPostInitializationEvent event) {
        for (ModModuleBase module : modules) {
            if (!module.isEnable()) continue;
            module.postInit(event);
        }
    }

    public void onServerStarting(FMLServerStartingEvent event) {
        for (ModModuleBase module : modules) {
            if (!module.isEnable()) continue;
            module.onServerStarting(event);
        }
    }

    public void onAboutToStartEvent(FMLServerAboutToStartEvent event) {
        for (ModModuleBase module : modules) {
            if (!module.isEnable()) continue;
            module.onAboutToStartEvent(event);
        }
    }

    public void onServerStarted(FMLServerStartedEvent event) {
        for (ModModuleBase module : modules) {
            if (!module.isEnable()) continue;
            module.onServerStarted(event);
        }
    }

    public void onServerStopping(FMLServerStoppingEvent event) {
        for (ModModuleBase module : modules) {
            if (!module.isEnable()) continue;
            module.onServerStopping(event);
        }
    }

    public void registerSubCommand(Map<String, ICommand> subcommand) {
        for (ModModuleBase module : modules) {
            if (!module.isEnable()) continue;
            module.registerSubCommand(subcommand);
        }
    }
}
