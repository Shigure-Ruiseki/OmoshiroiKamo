package ruiseki.omoshiroikamo.core.init;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.ICommand;
import net.minecraft.creativetab.CreativeTabs;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import ruiseki.omoshiroikamo.core.client.icon.IconProvider;
import ruiseki.omoshiroikamo.core.client.key.IKeyRegistry;
import ruiseki.omoshiroikamo.core.client.key.KeyRegistry;
import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.helper.LoggerHelper;
import ruiseki.omoshiroikamo.core.network.PacketHandler;
import ruiseki.omoshiroikamo.core.persist.world.WorldStorage;
import ruiseki.omoshiroikamo.core.proxy.ClientProxyComponent;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;

/**
 * Base class for mods which adds a few convenience methods.
 * Dont forget to call the supers for the init events.
 *
 * @author rubensworks
 */
@Data
public abstract class ModBase {

    public static final EnumReferenceKey<String> REFKEY_MOD_VERSION = EnumReferenceKey
        .create("mod_version", String.class);
    public static final EnumReferenceKey<String> REFKEY_TEXTURE_PATH_GUI = EnumReferenceKey
        .create("texture_path_gui", String.class);
    public static final EnumReferenceKey<String> REFKEY_TEXTURE_PATH_MODELS = EnumReferenceKey
        .create("texture_path_models", String.class);
    public static final EnumReferenceKey<String> REFKEY_TEXTURE_PATH_SKINS = EnumReferenceKey
        .create("texture_path_skins", String.class);
    public static final EnumReferenceKey<Boolean> REFKEY_RETROGEN = EnumReferenceKey.create("retrogen", Boolean.class);
    public static final EnumReferenceKey<Boolean> REFKEY_DEBUGCONFIG = EnumReferenceKey
        .create("debug_config", Boolean.class);
    public static final EnumReferenceKey<Boolean> REFKEY_CRASH_ON_INVALID_RECIPE = EnumReferenceKey
        .create("crash_on_invalid_recipe", Boolean.class);
    public static final EnumReferenceKey<Boolean> REFKEY_CRASH_ON_MODCOMPAT_CRASH = EnumReferenceKey
        .create("crash_on_modcompat_crash", Boolean.class);

    private final String modId, modName;
    private final LoggerHelper loggerHelper;
    private final Set<IInitListener> initListeners;
    private final Map<EnumReferenceKey, Object> genericReference = Maps.newHashMap();
    private final List<WorldStorage> worldStorages = Lists.newLinkedList();
    private CommandMod baseCommand;

    private final RegistryManager registryManager;
    // private final RecipeHandler recipeHandler;
    private final IKeyRegistry keyRegistry;
    private final PacketHandler packetHandler;
    private final ModuleManager moduleManager;

    private CreativeTabs defaultCreativeTab = null;
    private File configFolder = null;

    public ModBase(String modId, String modName) {
        this.modId = modId;
        this.modName = modName;
        this.loggerHelper = constructLoggerHelper();
        this.initListeners = Sets.newHashSet();
        this.registryManager = constructRegistryManager();
        // this.recipeHandler = constructRecipeHandler();
        this.keyRegistry = new KeyRegistry();
        this.packetHandler = constructPacketHandler();
        this.moduleManager = constructModuleManager();

        populateDefaultGenericReferences();
    }

    protected LoggerHelper constructLoggerHelper() {
        return new LoggerHelper(this.modName);
    }

    protected RegistryManager constructRegistryManager() {
        return new RegistryManager();
    }

    // protected abstract RecipeHandler constructRecipeHandler();

    protected PacketHandler constructPacketHandler() {
        return new PacketHandler(this);
    }

    protected CommandMod constructBaseCommand() {
        return new CommandMod(this, Maps.<String, ICommand>newHashMap());
    }

    protected ModuleManager constructModuleManager() {
        return new ModuleManager(this);
    }

    /**
     * @return The icon provider that was constructed in {@link ClientProxyComponent}.
     */
    @SideOnly(Side.CLIENT)
    public IconProvider getIconProvider() {
        return ((ClientProxyComponent) getProxy()).getIconProvider();
    }

    /**
     * Save a mod value.
     *
     * @param key   The key.
     * @param value The value.
     * @param <T>   The value type.
     */
    public <T> void putGenericReference(EnumReferenceKey<T> key, T value) {
        genericReference.put(key, value);
    }

    private void populateDefaultGenericReferences() {
        putGenericReference(REFKEY_TEXTURE_PATH_GUI, "textures/gui/");
        putGenericReference(REFKEY_TEXTURE_PATH_MODELS, "textures/models/");
        putGenericReference(REFKEY_TEXTURE_PATH_SKINS, "textures/skins/");
        putGenericReference(REFKEY_RETROGEN, false);
        putGenericReference(REFKEY_DEBUGCONFIG, false);
        putGenericReference(REFKEY_CRASH_ON_INVALID_RECIPE, false);
        putGenericReference(REFKEY_CRASH_ON_MODCOMPAT_CRASH, false);
    }

    /**
     * Get the value for a generic reference key.
     * The default keys can be found in {@link ModBase}.
     *
     * @param key The key of a value.
     * @param <T> The type of value.
     * @return The value for the given key.
     */
    @SuppressWarnings("unchecked")
    public <T> T getReferenceValue(EnumReferenceKey<T> key) {
        if (!genericReference.containsKey(key))
            throw new IllegalArgumentException("Could not find " + key + " as generic reference item.");
        return (T) genericReference.get(key);
    }

    /**
     * Log a new info message for this mod.
     *
     * @param message The message to show.
     */
    public void log(String message) {
        log(Level.INFO, message);
    }

    /**
     * Log a new message of the given level for this mod.
     *
     * @param level   The level in which the message must be shown.
     * @param message The message to show.
     */
    public void log(Level level, String message) {
        loggerHelper.log(level, message);
    }

    /**
     * Register a new mod Module.
     */
    public void registerModule(ModModuleBase module) {
        synchronized (moduleManager) {
            moduleManager.register(module);
        }
    }

    public void registerSubCommand(Map<String, ICommand> subcommand) {

    }

    /**
     * Register a new init listener.
     *
     * @param initListener The init listener.
     */
    public void addInitListeners(IInitListener initListener) {
        synchronized (initListeners) {
            initListeners.add(initListener);
        }
    }

    /**
     * Get the init-listeners on a thread-safe way;
     *
     * @return A copy of the init listeners list.
     */
    private Set<IInitListener> getSafeInitListeners() {
        Set<IInitListener> clonedInitListeners;
        synchronized (initListeners) {
            clonedInitListeners = Sets.newHashSet(initListeners);
        }
        return clonedInitListeners;
    }

    /**
     * Call the init-listeners for the given step.
     *
     * @param step The step of initialization.
     */
    protected void callInitStepListeners(IInitListener.Step step) {
        for (IInitListener initListener : getSafeInitListeners()) {
            initListener.onInit(step);
        }
    }

    /**
     * Override this, call super and annotate with {@link cpw.mods.fml.common.Mod.EventHandler}.
     *
     * @param event The pre-init event.
     */
    public void preInit(FMLPreInitializationEvent event) {
        log(Level.TRACE, "preInit()");
        moduleManager.preInit(event);

        // Call init listeners
        callInitStepListeners(IInitListener.Step.PREINIT);

        // Register events
        ICommonProxy proxy = getProxy();
        if (proxy != null) {
            proxy.registerEventHooks();
        }
        moduleManager.proxyPreInit();
    }

    /**
     * Override this, call super and annotate with {@link cpw.mods.fml.common.Mod.EventHandler}.
     *
     * @param event The init event.
     */
    public void init(FMLInitializationEvent event) {
        log(Level.TRACE, "init()");

        // Initialize the creative tab
        getDefaultCreativeTab();
        moduleManager.init(event);

        // Call init listeners
        callInitStepListeners(IInitListener.Step.INIT);

        // Register proxy related things.
        ICommonProxy proxy = getProxy();
        if (proxy != null) {
            proxy.registerRenderers();
            proxy.registerKeyBindings(getKeyRegistry());
            getPacketHandler().init();
            proxy.registerPacketHandlers(getPacketHandler());
            proxy.registerTickHandlers();
        }
        moduleManager.proxyInit();

        // Register recipes
        // RecipeHandler recipeHandler = getRecipeHandler();
        // if(recipeHandler != null) {
        // recipeHandler.registerRecipes(getConfigFolder());
        // }
    }

    /**
     * Override this, call super and annotate with {@link cpw.mods.fml.common.Mod.EventHandler}.
     *
     * @param event The post-init event.
     */
    public void postInit(FMLPostInitializationEvent event) {
        log(Level.TRACE, "postInit()");
        moduleManager.postInit(event);

        // Call init listeners
        callInitStepListeners(IInitListener.Step.POSTINIT);
    }

    /**
     * Override this, call super and annotate with {@link cpw.mods.fml.common.Mod.EventHandler}.
     * Register the things that are related to when the server is starting.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        moduleManager.onServerStarting(event);

        this.baseCommand = constructBaseCommand();
        registerSubCommand(baseCommand.getSubcommands());
        moduleManager.registerSubCommand(baseCommand.getSubcommands());
        event.registerServerCommand(baseCommand);
    }

    /**
     * Override this, call super and annotate with {@link cpw.mods.fml.common.Mod.EventHandler}.
     * Register the things that are related to server starting.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        moduleManager.onServerStarted(event);
        for (WorldStorage worldStorage : worldStorages) {
            worldStorage.onStartedEvent(event);
        }
    }

    /**
     * Override this, call super and annotate with {@link cpw.mods.fml.common.Mod.EventHandler}.
     * Register the things that are related to server stopping, like persistent storage.
     *
     * @param event The Forge event required for this.
     */
    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        moduleManager.onServerStopping(event);
        for (WorldStorage worldStorage : worldStorages) {
            worldStorage.onStoppingEvent(event);
        }
    }

    /**
     * Register a new world storage type.
     * Make sure to call this at least before the event
     * {@link FMLServerStartedEvent} is called.
     *
     * @param worldStorage The world storage to register.
     */
    public void registerWorldStorage(WorldStorage worldStorage) {
        worldStorages.add(worldStorage);
    }

    /**
     * Construct a creative tab, will only be called once during the init event.
     *
     * @return The default creative tab for items and blocks.
     */
    public abstract CreativeTabs constructDefaultCreativeTab();

    /**
     * @return The default creative tab for items and blocks.
     */
    public final CreativeTabs getDefaultCreativeTab() {
        if (defaultCreativeTab == null) {
            defaultCreativeTab = constructDefaultCreativeTab();
        }
        return defaultCreativeTab;
    }

    /**
     * @return The proxy for this mod, can be null if not required.
     */
    public abstract ICommonProxy getProxy();

    @Override
    public String toString() {
        return getModId();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Unique references to values that can be registered inside a mod.
     *
     * @param <T> The type of value.
     */
    @Data
    public static class EnumReferenceKey<T> {

        private final String key;
        private final Class<T> type;

        private EnumReferenceKey(String key, Class<T> type) {
            this.key = key;
            this.type = type;
        }

        public static <T> EnumReferenceKey<T> create(String key, Class<T> type) {
            return new EnumReferenceKey<>(key, type);
        }

    }

}
