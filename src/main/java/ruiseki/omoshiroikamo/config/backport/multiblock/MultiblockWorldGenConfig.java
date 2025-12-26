package ruiseki.omoshiroikamo.config.backport.multiblock;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Main MultiBlock WorldGen Settings")
@Config.LangKey(LibResources.CONFIG + "eTWorldGenConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "worldGen",
    configSubDirectory = LibMisc.MOD_ID + "/multiblock",
    filename = "worldgen")
public class MultiblockWorldGenConfig {

    @Config.Comment("Hardened Stone generation settings")
    @Config.LangKey(LibResources.CONFIG + "hardenedStoneGen")
    public static final StoneGenSettings hardenedStone = new StoneGenSettings(true, 30, 12, 0, 12);

    @Config.Comment("Alabaster generation settings")
    @Config.LangKey(LibResources.CONFIG + "alabasterGen")
    public static final StoneGenSettings alabaster = new StoneGenSettings(true, 30, 22, 40, 200);

    @Config.Comment("Basalt generation settings")
    @Config.LangKey(LibResources.CONFIG + "basaltGen")
    public static final StoneGenSettings basalt = new StoneGenSettings(true, 28, 14, 8, 32);

    public static class StoneGenSettings {

        @Config.Comment("Enable generation")
        @Config.DefaultBoolean(true)
        public boolean enable;

        @Config.Comment("Size of each node (blocks)")
        @Config.DefaultInt(30)
        @Config.RangeInt(min = 0)
        public int nodeSize;

        @Config.Comment("Number of nodes per chunk")
        @Config.DefaultInt(12)
        @Config.RangeInt(min = 0)
        public int nodes;

        @Config.Comment("Minimum generation height")
        @Config.DefaultInt(0)
        @Config.RangeInt(min = 0)
        public int minHeight;

        @Config.Comment("Maximum generation height")
        @Config.DefaultInt(64)
        @Config.RangeInt(min = 0)
        public int maxHeight;

        public StoneGenSettings() {
            this(true, 30, 12, 0, 64);
        }

        public StoneGenSettings(boolean enable, int nodeSize, int nodes, int minHeight, int maxHeight) {
            this.enable = enable;
            this.nodeSize = nodeSize;
            this.nodes = nodes;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }
    }
}
