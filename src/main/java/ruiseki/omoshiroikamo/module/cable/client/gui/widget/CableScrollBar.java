package ruiseki.omoshiroikamo.module.cable.client.gui.widget;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.UpOrDown;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.widget.Widget;

public class CableScrollBar extends Widget<CableScrollBar> implements Interactable {

    private IntSupplier totalSupplier = () -> 0;
    private IntSupplier visibleSupplier = () -> 1;
    private IntConsumer onChange = i -> {};

    private int offset = 0;
    private int maxOffset = 0;

    private boolean dragging = false;
    private int dragOffsetY = 0;

    private int columns = 1;
    private int rows = 1;

    public CableScrollBar total(IntSupplier sup) {
        this.totalSupplier = sup;
        return this;
    }

    public CableScrollBar visible(IntSupplier sup) {
        this.visibleSupplier = sup;
        return this;
    }

    public CableScrollBar onChange(IntConsumer c) {
        this.onChange = c;
        return this;
    }

    public CableScrollBar columns(int c) {
        this.columns = c;
        return this;
    }

    public CableScrollBar rows(int r) {
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
        int my = ctx.getMouseY() - getArea().y();

        int thumbY = getThumbY();
        int thumbH = getThumbHeight();

        if (my >= thumbY && my <= thumbY + thumbH) {
            dragging = true;
            dragOffsetY = my - thumbY;
            return Result.SUCCESS;
        }

        // click on track → jump
        int newOffset = (int) ((double) my / (getArea().height - thumbH) * maxOffset);
        setOffset(newOffset);
        return Result.SUCCESS;
    }

    @Override
    public void onMouseDrag(int mouseButton, long timeSinceClick) {
        if (!dragging) return;

        ModularGuiContext ctx = getContext();
        int my = ctx.getMouseY() - getArea().y() - dragOffsetY;

        int track = getArea().height - getThumbHeight();
        if (track <= 0) return;

        int newOffset = (int) ((double) my / track * maxOffset);
        setOffset(newOffset);
    }

    @Override
    public boolean onMouseRelease(int mouseButton) {
        dragging = false;
        return false;
    }

    @Override
    public boolean onMouseScroll(UpOrDown dir, int amount) {
        setOffset(offset + (dir == UpOrDown.UP ? -1 : 1));
        return true;
    }

    @Override
    public void onUpdate() {
        recalc();
    }

    private int getThumbHeight() {
        int h = getArea().height;

        int totalItems = Math.max(1, totalSupplier.getAsInt());
        int totalRows = (totalItems + columns - 1) / columns;

        int visibleRows = rows;

        return Math.max(8, h * visibleRows / totalRows);
    }

    private int getThumbY() {
        if (maxOffset == 0) return 0;

        int track = getArea().height - getThumbHeight();
        return (int) ((double) offset / maxOffset * track);
    }
}
