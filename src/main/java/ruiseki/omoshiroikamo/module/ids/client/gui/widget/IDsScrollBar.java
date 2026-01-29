package ruiseki.omoshiroikamo.module.ids.client.gui.widget;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;

import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class IDsScrollBar extends Widget<IDsScrollBar> implements Interactable {

    private static final int THUMB_W = 12;
    private static final int THUMB_H = 15;
    private static final int TRACK_PADDING = 1;

    public static final UITexture SCROLL = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(29, 131, 12, 15)
        .adaptable(1)
        .tiled()
        .name("scroll_btn")
        .build();

    public static final UITexture DISABLE_SCROLL = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(41, 131, 12, 15)
        .adaptable(1)
        .tiled()
        .name("disable_scroll_btn")
        .build();

    public static final UITexture SCROLL_BG = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(96, 146, 14, 56)
        .adaptable(1)
        .name("hover_scroll_btn")
        .build();

    private IntSupplier totalSupplier = () -> 0;
    private IntConsumer onChange = i -> {};

    private int offset = 0;
    private int maxOffset = 0;

    private boolean dragging = false;
    private int dragOffsetY = 0;

    private int columns = 1;
    private int rows = 1;

    public IDsScrollBar() {
        background(SCROLL_BG);
        disableHoverBackground();
    }

    public IDsScrollBar total(IntSupplier sup) {
        this.totalSupplier = sup;
        return this;
    }

    public IDsScrollBar onChange(IntConsumer c) {
        this.onChange = c;
        return this;
    }

    public IDsScrollBar columns(int c) {
        this.columns = c;
        return this;
    }

    public IDsScrollBar rows(int r) {
        this.rows = r;
        return this;
    }

    private void recalc() {
        int totalItems = totalSupplier.getAsInt();
        int totalRows = (totalItems + columns - 1) / columns;

        int visibleRows = rows;

        maxOffset = Math.max(0, totalRows - visibleRows);

        if (offset > maxOffset) {
            offset = maxOffset;
            onChange.accept(offset);
        }
    }

    private void setOffset(int v) {
        int clamped = Math.max(0, Math.min(maxOffset, v));
        if (clamped != offset) {
            offset = clamped;
            onChange.accept(offset);
        }
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        if (mouseButton != 0) return Result.IGNORE;

        ModularGuiContext ctx = getContext();
        int mouseY = ctx.getMouseY();
        int thumbY = getThumbY();

        if (mouseY >= thumbY && mouseY <= thumbY + THUMB_H) {
            dragging = true;
            dragOffsetY = mouseY - thumbY;
            return Result.SUCCESS;
        }

        int track = getArea().height - THUMB_H - TRACK_PADDING * 2;
        int clickY = mouseY - TRACK_PADDING - THUMB_H / 2;

        int newOffset = (int) ((double) clickY / track * maxOffset);
        setOffset(newOffset);
        return Result.SUCCESS;
    }

    @Override
    public void onMouseDrag(int mouseButton, long timeSinceClick) {
        if (!dragging) return;

        ModularGuiContext ctx = getContext();
        int my = ctx.getMouseY();

        int track = getArea().height - THUMB_H - TRACK_PADDING * 2;
        if (track <= 0) return;

        int dragY = my - dragOffsetY - TRACK_PADDING;
        int newOffset = (int) ((double) dragY / track * maxOffset);
        setOffset(newOffset);
    }

    @Override
    public boolean onMouseRelease(int mouseButton) {
        dragging = false;
        return true;
    }

    @Override
    public boolean onMouseScroll(UpOrDown dir, int amount) {
        setOffset(offset + (dir == UpOrDown.UP ? -1 : 1));
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        recalc();
    }

    private int getThumbY() {
        if (maxOffset == 0) return 0;

        int track = getArea().height - THUMB_H - 2;
        return 1 + (int) ((double) offset / maxOffset * track);
    }

    @Override
    public void draw(ModularGuiContext ctx, WidgetThemeEntry<?> theme) {
        int thumbY = getThumbY();

        int mouseY = ctx.getMouseY() - getArea().y();

        UITexture tex = maxOffset != 0 ? SCROLL : DISABLE_SCROLL;

        tex.draw(ctx, 1, thumbY, THUMB_W, THUMB_H, theme.getTheme());
    }
}
