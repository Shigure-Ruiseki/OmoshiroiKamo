package ruiseki.omoshiroikamo.core.client.gui.widget;

import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.widget.ParentWidget;

import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class ExpandedWidget extends ParentWidget<ExpandedWidget> {

    public static final UITexture RIGHT_BG = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(238, 0, 18, 256)
        .adaptable(4)
        .name("hover_scroll_btn")
        .build();

    public static final UITexture LEFT_BG = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(128, 0, 18, 256)
        .adaptable(4)
        .name("hover_scroll_btn")
        .build();

    public static final UITexture TOP_BG = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(128, 0, 128, 18)
        .adaptable(4)
        .name("hover_scroll_btn")
        .build();

    public static final UITexture DOWN_BG = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(128, 238, 128, 18)
        .adaptable(4)
        .name("hover_scroll_btn")
        .build();

    public ExpandedWidget(Side side) {
        background(side.texture);
    }

    public static ExpandedWidget left() {
        return new ExpandedWidget(Side.LEFT);
    }

    public static ExpandedWidget right() {
        return new ExpandedWidget(Side.RIGHT);
    }

    public static ExpandedWidget top() {
        return new ExpandedWidget(Side.TOP);
    }

    public static ExpandedWidget down() {
        return new ExpandedWidget(Side.DOWN);
    }

    public enum Side {

        LEFT(LEFT_BG),
        RIGHT(RIGHT_BG),
        TOP(TOP_BG),
        DOWN(DOWN_BG);

        final UITexture texture;

        Side(UITexture texture) {
            this.texture = texture;
        }
    }

}
