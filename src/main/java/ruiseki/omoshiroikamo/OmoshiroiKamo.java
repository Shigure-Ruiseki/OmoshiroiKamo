package ruiseki.omoshiroikamo;

import java.util.Map;

import net.minecraft.command.ICommand;
import net.minecraft.creativetab.CreativeTabs;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;
import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;
import com.gtnewhorizon.gtnhlib.config.ConfigException;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ruiseki.omoshiroikamo.config.GeneralConfig;
import ruiseki.omoshiroikamo.core.CoreModule;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityManager;
import ruiseki.omoshiroikamo.core.client.util.TextureLoader;
import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.command.CommandOK;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.integration.nei.NEICompat;
import ruiseki.omoshiroikamo.core.integration.structureLib.StructureCompat;
import ruiseki.omoshiroikamo.core.integration.waila.WailaCompat;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.modcompat.ModCompatLoader;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.module.backpack.BackpackModule;
import ruiseki.omoshiroikamo.module.chickens.ChickensModule;
import ruiseki.omoshiroikamo.module.cows.CowsModule;
import ruiseki.omoshiroikamo.module.dml.DMLModule;
import ruiseki.omoshiroikamo.module.ids.IDsModule;
import ruiseki.omoshiroikamo.module.machinery.MachineryModule;
import ruiseki.omoshiroikamo.module.multiblock.MultiBlockModule;

@Mod(
    modid = LibMisc.MOD_ID,
    name = LibMisc.MOD_NAME,
    version = LibMisc.VERSION,
    dependencies = LibMisc.DEPENDENCIES,
    guiFactory = LibMisc.GUI_FACTORY)
public class OmoshiroiKamo extends ModBase {

    static {
        try {
            GeneralConfig.registerConfig();
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    @SidedProxy(serverSide = LibMisc.PROXY_COMMON, clientSide = LibMisc.PROXY_CLIENT)
    public static ICommonProxy proxy;

    @Instance(LibMisc.MOD_ID)
    public static OmoshiroiKamo instance;

    public OmoshiroiKamo() {
        super(LibMisc.MOD_ID, LibMisc.MOD_NAME);
        putGenericReference(REFKEY_MOD_VERSION, LibMisc.VERSION);
    }

    @EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        CapabilityManager.INSTANCE.injectCapabilities(event.getASMHarvestedData());
        registerModule(new CoreModule());
        registerModule(new ChickensModule());
        registerModule(new CowsModule());
        registerModule(new DMLModule());
        registerModule(new BackpackModule());
        registerModule(new IDsModule());
        registerModule(new MachineryModule());
        registerModule(new MultiBlockModule());
    }

    @Override
    protected void loadModCompats(ModCompatLoader modCompatLoader) {
        // TODO: add version checker
        // modCompatLoader.addModCompat(new VersionCheckerModCompat());
    }

    @Override
    protected ICommand constructBaseCommand() {
        Map<String, ICommand> commands = Maps.newHashMap();
        CommandMod command = new CommandMod(this, commands);
        command.addAlias("ok");
        return command;
    }

    @Mod.EventHandler
    @Override
    public final void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        if (MinecraftHelpers.isClientSide()) {
            ModelRegistry.registerModid(LibMisc.MOD_ID);
        }
        WailaCompat.init();
        NEICompat.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        StructureCompat.postInit();
        if (MinecraftHelpers.isClientSide()) {
            TextureLoader.loadFromConfig(LibMisc.MOD_ID, LibMisc.MOD_NAME + " Runtime Textures", OmoshiroiKamo.class);
        }
    }

    @Mod.EventHandler
    @Override
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
        event.registerServerCommand(new CommandOK());
    }

    @Override
    public CreativeTabs constructDefaultCreativeTab() {
        return null;
    }

    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        super.onServerStarted(event);
    }

    @EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
    }

    @EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {}

    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {}

    @Override
    public ICommonProxy getProxy() {
        return proxy;
    }

    /**
     * Log a new info message for this mod.
     *
     * @param message The message to show.
     */
    public static void okLog(String message) {
        OmoshiroiKamo.instance.log(Level.INFO, message);
    }

    /**
     * Log a new message of the given level for this mod.
     *
     * @param level   The level in which the message must be shown.
     * @param message The message to show.
     */
    public static void okLog(Level level, String message) {
        OmoshiroiKamo.instance.log(level, message);
    }
}
