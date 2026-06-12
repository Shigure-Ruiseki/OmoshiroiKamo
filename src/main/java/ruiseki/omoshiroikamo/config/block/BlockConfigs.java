package ruiseki.omoshiroikamo.config.block;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.omoshiroikamo.Reference;

@Config.Comment("Main Block settings")
@Config.LangKey(Reference.CONFIG + "blockConfig")
@Config(modid = Reference.MOD_ID, category = "general.blocks", configSubDirectory = Reference.MOD_ID)
public class BlockConfigs {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(BlockConfigs.class);
    }
}
