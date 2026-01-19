package ruiseki.omoshiroikamo.core.client.gui.widget;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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

    public ItemStackDrawable(ItemStack item) {
        setItem(item);
    }

    public ItemStackDrawable(Item item) {
        setItem(item);
    }

    public ItemStackDrawable(Item item, int meta) {
        setItem(item, meta);
    }

    public ItemStackDrawable(Item item, int meta, int amount) {
        setItem(item, meta, amount);
    }

    public ItemStackDrawable(Item item, int meta, int amount, @Nullable NBTTagCompound nbt) {
        setItem(item, meta, amount, nbt);
    }

    public ItemStackDrawable(Block item) {
        setItem(item);
    }

    public ItemStackDrawable(Block item, int meta) {
        setItem(item, meta);
    }

    public ItemStackDrawable(Block item, int meta, int amount) {
        setItem(new ItemStack(item, amount, meta));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {

        ItemStack stack = getItem();

        if (stack == null || stack.getItem() == null || stack.stackSize <= 0) {
            return;
        }

        applyColor(widgetTheme.getColor());

        GuiDraw.drawItem(stack, x, y, width, height, context.getCurrentDrawingZ());

        GuiDraw.drawAmountText(stack.stackSize, null, x, y, width, height, Alignment.BottomRight);
    }

}
