package ruiseki.omoshiroikamo.client.gui.modularui2;

import net.minecraft.util.ResourceLocation;

import com.cleanroommc.modularui.drawable.UITexture;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public final class MGuiTextures {

    private MGuiTextures() {}

    public static final UITexture WHITELIST = UITexture.builder()
        .location(LibResources.OVERLAY_WHITELIST)
        .imageSize(16, 16)
        .build();

    public static final UITexture BLACKLIST = UITexture.builder()
        .location(LibResources.OVERLAY_BLACKLIST)
        .imageSize(16, 16)
        .build();

    public static final UITexture FULL_HUNGER = UITexture.builder()
        .location(LibResources.OVERLAY_FULL_HUNGER)
        .imageSize(16, 16)
        .build();

    public static final UITexture EXACT_HUNGER = UITexture.builder()
        .location(LibResources.OVERLAY_EXACT_HUNGER)
        .imageSize(16, 16)
        .build();

    public static final UITexture PROGRESS_BURN = UITexture.builder()
        .location(LibResources.PROGRESS_BURN)
        .imageSize(18, 36)
        .build();

    public static final UITexture ENERGY_PROGRESS = UITexture.builder()
        .location(LibResources.PROGRESS_ENERGY)
        .adaptable(1)
        .imageSize(16, 128)
        .build();

    public static final UITexture BREEDER_PROGRESS = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/widgets/progress_breeder")
        .imageSize(36, 36)
        .build();

    public static final UITexture ROOST_SLOT = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/slot/roost_slot")
        .imageSize(18, 18)
        .build();

    private static final ResourceLocation GUI_CONTROLS = new ResourceLocation(LibMisc.MOD_ID, "gui/gui_controls.png");

    private static final ResourceLocation ICON_LOCATION = new ResourceLocation(LibMisc.MOD_ID, "gui/icons");

    public static final UITexture CHECK_ICON = icon("check", 0, 0);
    public static final UITexture CROSS_ICON = icon("cross", 16, 0);

    public static final UITexture TOGGLE_DISABLE_ICON = icon("disable", 0, 128, 4, 10);
    public static final UITexture TOGGLE_ENABLE_ICON = icon("enable", 4, 128, 4, 10);

    private static UITexture icon(String name, int x, int y) {
        return icon(name, x, y, 16, 16);
    }

    private static UITexture icon(String name, int x, int y, int w, int h) {
        return UITexture.builder()
            .location(ICON_LOCATION)
            .imageSize(256, 256)
            .xy(x, y, w, h)
            .name(name)
            .build();
    }
}
