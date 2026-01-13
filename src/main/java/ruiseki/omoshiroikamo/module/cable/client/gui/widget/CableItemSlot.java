package ruiseki.omoshiroikamo.module.cable.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.core.mixins.early.minecraft.GuiAccessor;
import com.cleanroommc.modularui.core.mixins.early.minecraft.GuiScreenAccessor;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.SlotTheme;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.GlStateManager;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.widget.Widget;

public class CableItemSlot extends Widget<CableItemSlot> implements Interactable {

    public static final int SIZE = 18;

    private ItemStack stack;

    public CableItemSlot() {
        itemTooltip().setAutoUpdate(true);
        itemTooltip().tooltipBuilder(tooltip -> {
            ItemStack stack = getStack();
            if (stack == null || stack.getItem() == null || stack.stackSize <= 0) return;
            tooltip.addFromItem(stack);
        });
    }

    @Override
    public void onInit() {
        super.onInit();
        size(SIZE);
    }

    public ItemStack getStack() {
        return stack;
    }

    public CableItemSlot setStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public RichTooltip itemTooltip() {
        return super.tooltip();
    }

    @Override
    public void drawForeground(ModularGuiContext context) {
        RichTooltip tooltip = getTooltip();
        if (tooltip != null && isHoveringFor(tooltip.getShowUpTimer())) {
            tooltip.draw(getContext(), getStack());
        }
    }

    public void buildTooltip(ItemStack stack, RichTooltip tooltip) {
        if (stack == null) return;
        tooltip.addFromItem(stack);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        RenderHelper.enableGUIStandardItemLighting();
        drawStack();
        RenderHelper.disableStandardItemLighting();
        drawOverlay();
    }

    protected void drawOverlay() {
        if (isHovering()) {
            GlStateManager.colorMask(true, true, true, false);
            GuiDraw.drawRect(1, 1, 16, 16, getSlotHoverColor());
            GlStateManager.colorMask(true, true, true, true);
        }
    }

    public int getSlotHoverColor() {
        WidgetThemeEntry<SlotTheme> theme = getWidgetTheme(getContext().getTheme(), SlotTheme.class);
        return theme.getTheme()
            .getSlotHoverColor();
    }

    @Override
    public WidgetThemeEntry<?> getWidgetThemeInternal(ITheme theme) {
        return theme.getWidgetTheme(IThemeApi.ITEM_SLOT);
    }

    public void drawStack() {

        ItemStack stack = getStack();
        if (stack == null || stack.getItem() == null || stack.stackSize <= 0) return;

        GuiScreen guiScreen = getScreen().getScreenWrapper()
            .getGuiScreen();
        RenderItem renderItem = GuiScreenAccessor.getItemRender();

        // --- Z LEVEL isolation (same as vanilla slot)
        float z = getContext().getCurrentDrawingZ() + 100;
        ((GuiAccessor) guiScreen).setZLevel(z);
        renderItem.zLevel = z;

        GlStateManager.pushMatrix();
        Platform.setupDrawItem();

        // render item
        renderItem.renderItemAndEffectIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft()
                .getTextureManager(),
            stack,
            1,
            1);

        GuiDraw.afterRenderItemAndEffectIntoGUI(stack);
        GlStateManager.disableRescaleNormal();
        Platform.endDrawItem();

        // amount text
        GuiDraw.drawStandardSlotAmountText(stack.stackSize, null, getArea());

        // overlay (durability, etc.)
        ItemStack overlayStack = stack.copy();
        overlayStack.stackSize = 1;

        renderItem.renderItemOverlayIntoGUI(
            ((GuiScreenAccessor) guiScreen).getFontRenderer(),
            Minecraft.getMinecraft()
                .getTextureManager(),
            overlayStack,
            1,
            1,
            null);

        GlStateManager.popMatrix();

        GlStateManager.disableDepth();
        GlStateManager.disableLighting();

        // --- restore z
        ((GuiAccessor) guiScreen).setZLevel(0f);
        renderItem.zLevel = 0f;
    }
}
