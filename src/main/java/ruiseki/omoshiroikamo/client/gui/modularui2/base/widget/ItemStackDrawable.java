package ruiseki.omoshiroikamo.client.gui.modularui2.base.widget;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Alignment;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStackDrawable extends ItemDrawable {

    public ItemStackDrawable() {}

    public ItemStackDrawable(@NotNull ItemStack item) {
        setItem(item);
    }

    public ItemStackDrawable(@NotNull Item item) {
        setItem(item);
    }

    public ItemStackDrawable(@NotNull Item item, int meta) {
        setItem(item, meta);
    }

    public ItemStackDrawable(@NotNull Item item, int meta, int amount) {
        setItem(item, meta, amount);
    }

    public ItemStackDrawable(@NotNull Item item, int meta, int amount, @Nullable NBTTagCompound nbt) {
        setItem(item, meta, amount, nbt);
    }

    public ItemStackDrawable(@NotNull Block item) {
        setItem(item);
    }

    public ItemStackDrawable(@NotNull Block item, int meta) {
        setItem(item, meta);
    }

    public ItemStackDrawable(@NotNull Block item, int meta, int amount) {
        setItem(new ItemStack(item, amount, meta));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        applyColor(widgetTheme.getColor());
        if (getItem() != null) {
            GuiDraw.drawItem(this.getItem(), x, y, width, height, context.getCurrentDrawingZ());
            GuiDraw.drawAmountText(this.getItem().stackSize, null, x, y, width, height, Alignment.BottomRight);
        }
    }

}
