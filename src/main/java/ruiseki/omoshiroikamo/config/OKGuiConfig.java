package ruiseki.omoshiroikamo.config;

import net.minecraft.client.gui.GuiScreen;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;

import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class OKGuiConfig extends SimpleGuiConfig {

    public OKGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, LibMisc.MOD_ID, LibMisc.MOD_NAME, GeneralConfig.class);
    }

}
