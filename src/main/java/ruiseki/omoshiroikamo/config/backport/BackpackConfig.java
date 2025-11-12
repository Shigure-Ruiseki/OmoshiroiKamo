package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@Config.Comment("Main Backpack Settings")
@Config.LangKey(LibResources.CONFIG + "backpackConfig")
@Config(modid = LibMisc.MOD_ID, category = "general.backports.backpack", configSubDirectory = LibMisc.MOD_ID)
public class BackpackConfig {

    @Config.Comment("Main FeedingC Settings")
    public static final FeedingConfig feedingConfig = new FeedingConfig();

    @Config.Comment("Main Magnet Settings")
    public static final MagnetConfig magnetConfig = new MagnetConfig();

    @Config.LangKey(LibResources.CONFIG + "feedingConfig")
    public static class FeedingConfig {

        @Config.DefaultBoolean(true)
        public boolean feedingAllowInMainInventory;

        @Config.DefaultBoolean(true)
        public boolean feedingAllowInBaublesSlot;
    }

    @Config.LangKey(LibResources.CONFIG + "magnetConfig")
    public static class MagnetConfig {

        @Config.DefaultInt(5)
        public int magnetRange;

        @Config.DefaultInt(20)
        public int magnetMaxItems;

        @Config.DefaultBoolean(true)
        public boolean magnetAllowInMainInventory;

        @Config.DefaultBoolean(true)
        public boolean magnetAllowInBaublesSlot;
    }

}
