package ruiseki.omoshiroikamo.module.machinery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.structure.BlockResolver;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.event.MemoryEventHandler;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.item.ItemFluidCanister;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.core.network.packet.PacketReloadNEI;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.module.machinery.common.command.CommandModular;
import ruiseki.omoshiroikamo.module.machinery.common.fluid.EnumFluidMaterial;
import ruiseki.omoshiroikamo.module.machinery.common.fluid.ModFluidGases;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryItems;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryOreDict;
import ruiseki.omoshiroikamo.module.machinery.common.integration.MachineryIntegration;
import ruiseki.omoshiroikamo.module.machinery.common.integration.structurelib.MachineControllerInfoContainer;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;
import ruiseki.omoshiroikamo.module.machinery.common.tier.TierConfigLoader;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

public class MachineryModule extends ModModuleBase {

    private static File configDir;
    private static List<String> cachedGroupNames = new ArrayList<>();

    public MachineryModule() {
        super(OmoshiroiKamo.instance);
    }

    public static File getConfigDir() {
        return configDir;
    }

    /**
     * Get cached recipe group names, scanned during preInit.
     * Available before RecipeLoader.loadAll() completes.
     */
    public static List<String> getCachedGroupNames() {
        return cachedGroupNames;
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
        ModFluidGases.preInit();
        MachineryIntegration.preInit();
        MachineryBlocks.preInit();
        BlockResolver.registerHintBlock(MachineryBlocks.MACHINE_CASING.getBlock());
        CustomStructureRegistry.registerControllerBlock(MachineryBlocks.MACHINE_CONTROLLER.getBlock());
        MachineryItems.preInit();
        MachineryOreDict.init();

        for (EnumFluidMaterial mat : EnumFluidMaterial.values()) {
            ItemFluidCanister.registerFluidColor(mat.getName(), mat.getColor());
        }
        MemoryEventHandler.registerOnWorldUnload(world -> StructureTintCache.clearDimension(world));
        MemoryEventHandler.registerOnClientDisconnect(StructureTintCache::clearAll);

        // Pre-scan recipe group names so NEI can register handlers
        // before RecipeLoader.loadAll() runs in postInit
        cachedGroupNames = RecipeLoader.scanGroupNames(configDir);
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        TierConfigLoader.INSTANCE.load(configDir);
        RecipeLoader.getInstance()
            .loadAll(configDir);
    }

    /**
     * Register StructureLib's IMultiblockInfoContainer after CustomStructureRegistry.registerAll()
     * so that structures are available when StructureLib queries them for NEI display.
     * Called from OmoshiroiKamo.postInit() after StructureCompat.postInit().
     */
    public static void postInitStructures() {
        IMultiblockInfoContainer.registerTileClass(TEMachineController.class, new MachineControllerInfoContainer());
    }

    @Override
    public void reload(ICommandSender sender) throws Exception {
        boolean hasErrors = false;

        try {
            StructureManager.getInstance()
                .reload();
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "  [Modular] Structures reloaded"));
        } catch (Exception e) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "  [Modular] Structures failed: " + e.getMessage()));
            hasErrors = true;
        }

        try {
            TierConfigLoader.INSTANCE.reload();
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "  [Modular] Tier config reloaded"));
        } catch (Exception e) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "  [Modular] Tier config failed: " + e.getMessage()));
            hasErrors = true;
        }

        try {
            RecipeLoader.getInstance()
                .reload(configDir);
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "  [Modular] Recipes reloaded"));
            OmoshiroiKamo.instance.getPacketHandler()
                .sendToAll(new PacketReloadNEI());
        } catch (Exception e) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "  [Modular] Recipes failed: " + e.getMessage()));
            hasErrors = true;
        }

        if (hasErrors || JsonErrorCollector.getInstance()
            .hasErrors()) {
            JsonErrorCollector.getInstance()
                .writeToFile();
            JsonErrorCollector.getInstance()
                .reportToChat(sender);
        }
    }
}
