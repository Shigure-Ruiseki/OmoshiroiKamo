package ruiseki.omoshiroikamo.client.gui.modularui2;

import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.UITexture;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public final class MGuiTextures {

    private MGuiTextures() {}

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

    public static final UITexture MATCH_NBT_ICON = icon("consider_nbt", 32, 0);
    public static final UITexture IGNORE_NBT_ICON = icon("ignore_nbt", 48, 0);

    public static final UITexture COMPLETE_HUNGER_ICON = icon("complete_hunger", 96, 0);
    public static final UITexture HALF_HUNGER_ICON = icon("half_hunger", 112, 0);
    public static final UITexture IMMEDIATE_HUNGER_ICON = icon("impose_hunger", 128, 0);

    public static final UITexture MATCH_DURABILITY_ICON = icon("consider_duration", 0, 16);
    public static final UITexture IGNORE_DURABILITY_ICON = icon("ignore_duration", 16, 16);

    public static final UITexture HALF_HEART_ICON = icon("half_heart", 96, 16);
    public static final UITexture IGNORE_HALF_HEART_ICON = icon("ignore_half_heart", 112, 16);

    public static final UITexture BY_MOD_ID_ICON = icon("by_mod_id", 32, 16);
    public static final IDrawable BY_ITEM_ICON = new ItemDrawable(Items.apple);

    public static final IDrawable IN_OUT_ICON = icon("in_out", 0, 32);
    public static final IDrawable IN_ICON = icon("in", 16, 32);
    public static final IDrawable OUT_ICON = icon("out", 32, 32);

    public static final IDrawable ADD_ICON = icon("add", 96, 32);
    public static final IDrawable REMOVE_ICON = icon("remove", 112, 32);
    public static final IDrawable BRAIN_ICON = icon("brain", 128, 32);

    public static final IDrawable BACKPACK_ICON = icon("backpack", 64, 48);
    public static final IDrawable UNLOCK_BACKPACK_ICON = icon("unlock_backpack", 176, 32);
    public static final IDrawable LOCK_BACKPACK_ICON = icon("lock_backpack", 192, 32);
    public static final IDrawable UNLOCK_SEARCH_ICON = icon("unlock_search", 224, 32);
    public static final IDrawable LOCK_SEARCH_ICON = icon("lock_search", 208, 32);
    public static final IDrawable KEEP_TAB_ICON = icon("keep_tab", 80, 80);
    public static final IDrawable NOT_KEEP_TAB_ICON = icon("not_keep_tab", 80, 96);

    public static final IDrawable ONE_IN_FOUR_SLOT_ICON = icon("one_in_four_slot", 0, 80);
    public static final IDrawable ALL_FOUR_SLOT_ICON = icon("all_in_four_slot", 16, 80);
    public static final IDrawable NO_SORT_ICON = icon("no_sort", 32, 80);
    public static final IDrawable NONE_FOUR_SLOT_ICON = icon("none_in_four_slot", 48, 80);

    public static final IDrawable SETTING_ICON = icon("setting", 16, 96);
    public static final IDrawable BACK_ICON = icon("back", 64, 80);

    public static final IDrawable EXP_MAGNET_ICON = icon("exp_magnet", 96, 48);
    public static final IDrawable IGNORE_EXP_MAGNET_ICON = icon("ignore_exp_magnet", 112, 48);
    public static final IDrawable ITEM_MAGNET_ICON = icon("exp_magnet", 128, 48);
    public static final IDrawable IGNORE_ITEM_MAGNET_ICON = icon("ignore_exp_magnet", 144, 48);

    public static final UITexture MATCH_ORE_DICT_ICON = icon("consider_ore_dict", 112, 96);
    public static final UITexture IGNORE_ORE_DICT_ICON = icon("ignore_ore_dict", 128, 96);

    public static final UITexture INTO_BACKPACK = icon("into_backpack", 32, 48);
    public static final UITexture INTO_INVENTORY = icon("small_m", 48, 48);
    public static final UITexture USED_BACKPACK = icon("into_backpack", 176, 80);
    public static final UITexture UNUSED_BACKPACK = icon("unused_backpack", 192, 80);

    public static final UITexture VOID_OVERFLOW = icon("void_overflow", 224, 16);
    public static final UITexture VOID_ANY = icon("void_any", 208, 16);
    public static final UITexture VOID_ALL = icon("void_all", 0, 48);
    public static final UITexture VOID_AUTOMATION = icon("void_automation", 16, 48);

    public static final UITexture ROTATED_RIGHT = icon("rotated_right", 0, 170);
    public static final UITexture ROTATED_LEFT = icon("rotated_left", 48, 170);
    public static final UITexture BALANCE = icon("balance", 32, 170);
    public static final UITexture SPREAD = icon("spread", 80, 170);
    public static final UITexture CLEAR = icon("clear", 16, 170);

    public static final UITexture SOLID_UP_ARROW_ICON = icon("solid_up_arrow", 0, 144, 12, 12);
    public static final UITexture SOLID_DOWN_ARROW_ICON = icon("solid_down_arrow", 12, 156, 12, 12);
    public static final UITexture DOT_DOWN_ARROW_ICON = icon("dot_down_arrow", 24, 156, 12, 12);
    public static final UITexture DOT_UP_ARROW_ICON = icon("dot_up_arrow", 36, 156, 12, 12);

    public static final UITexture TOGGLE_DISABLE_ICON = icon("disable", 0, 128, 4, 10);
    public static final UITexture TOGGLE_ENABLE_ICON = icon("enable", 4, 128, 4, 10);
    public static final UITexture SMALL_M_ICON = icon("small_m", 0, 156, 12, 12);
    public static final UITexture SMALL_A_ICON = icon("small_a", 24, 144, 12, 12);
    public static final UITexture SMALL_1_ICON = icon("small_1", 36, 144, 12, 12);
    public static final UITexture SMALL_O_ICON = icon("small_ore_dict", 64, 144, 12, 12);

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
