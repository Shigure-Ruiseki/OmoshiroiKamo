package ruiseki.omoshiroikamo.core;

import java.util.Map;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModModuleBase;
import ruiseki.okcore.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.command.CommandReload;
import ruiseki.omoshiroikamo.core.command.multiblock.CommandMultiblock;
import ruiseki.omoshiroikamo.core.init.CoreItems;
import ruiseki.omoshiroikamo.core.init.CoreOreDict;
import ruiseki.omoshiroikamo.core.init.CoreRecipes;
import ruiseki.omoshiroikamo.core.structure.StructureManager;

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
            .initialize(event.getModConfigurationDirectory());
    }

    @Override
    protected void registerSubCommand(Map<String, ICommand> subcommand) {
        super.registerSubCommand(subcommand);
        subcommand.put(CommandMultiblock.NAME, new CommandMultiblock(this.getMod()));
        subcommand.put(CommandReload.NAME, new CommandReload(this.getMod()));
    }

    @Override
    public void reload(ICommandSender sender) throws Exception {
        StructureManager.getInstance()
            .reload();
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "  [Core] Structures reloaded."));
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
