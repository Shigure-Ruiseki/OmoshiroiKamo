package ruiseki.omoshiroikamo.config;

import net.minecraft.client.gui.GuiScreen;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;

import ruiseki.omoshiroikamo.Reference;

public class OKGuiConfig extends SimpleGuiConfig {

    public OKGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, Reference.MOD_ID, Reference.MOD_NAME);
    }
}
