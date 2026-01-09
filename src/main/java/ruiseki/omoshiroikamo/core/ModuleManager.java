package ruiseki.omoshiroikamo.core;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.api.mod.IModuleCommon;

public final class ModuleManager {

    private static final List<IModuleCommon> COMMON = new ArrayList<>();
    private static final List<IModuleClient> CLIENT = new ArrayList<>();

    public static void register(IModuleCommon module) {
        COMMON.add(module);
    }

    public static void register(IModuleClient module) {
        CLIENT.add(module);
    }

    public static void onConstruction(FMLConstructionEvent e) {
        for (IModuleCommon m : COMMON) if (m.isEnabled()) m.onConstruction(e);
    }

    public static void preInitCommon(FMLPreInitializationEvent e) {
        for (IModuleCommon m : COMMON) if (m.isEnabled()) m.preInit(e);
    }

    public static void initCommon(FMLInitializationEvent e) {
        for (IModuleCommon m : COMMON) if (m.isEnabled()) m.init(e);
    }

    public static void postInitCommon(FMLPostInitializationEvent e) {
        for (IModuleCommon m : COMMON) if (m.isEnabled()) m.postInit(e);
    }

    public static void serverLoad(FMLServerStartingEvent e) {
        for (IModuleCommon m : COMMON) if (m.isEnabled()) m.serverLoad(e);
    }

    public static void serverStarted(FMLServerStartedEvent e) {
        for (IModuleCommon m : COMMON) if (m.isEnabled()) m.serverStarted(e);
    }

    public static void onServerAboutToStart(FMLServerAboutToStartEvent e) {
        for (IModuleCommon m : COMMON) if (m.isEnabled()) m.onServerAboutToStart(e);
    }

    public static void onServerStopped(FMLServerStoppedEvent e) {
        for (IModuleCommon m : COMMON) if (m.isEnabled()) m.onServerStopped(e);
    }

    public static void preInitClient(FMLPreInitializationEvent e) {
        for (IModuleClient m : CLIENT) if (m.isEnabled()) m.preInit(e);
    }

    public static void initClient(FMLInitializationEvent e) {
        for (IModuleClient m : CLIENT) if (m.isEnabled()) m.init(e);
    }

    public static void postInitClient(FMLPostInitializationEvent e) {
        for (IModuleClient m : CLIENT) if (m.isEnabled()) m.postInit(e);
    }

}
