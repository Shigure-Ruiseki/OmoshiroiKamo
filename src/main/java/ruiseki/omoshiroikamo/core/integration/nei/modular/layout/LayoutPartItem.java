package ruiseki.omoshiroikamo.core.integration.nei.modular.layout;

import java.awt.Point;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import ruiseki.omoshiroikamo.core.integration.nei.RecipeHandlerBase;

public class LayoutPartItem extends RecipeLayoutPart<ItemStack> {

    public static final int ITEM_SIZE = 18;

    private final PositionedStack stack;

    public LayoutPartItem(List<ItemStack> items) {
        super(0, 0);
        this.stack = new PositionedStack(items, 0, 0); // Position is updated later
    }

    public LayoutPartItem(ItemStack item) {
        super(0, 0);
        this.stack = new PositionedStack(item, 0, 0); // Position is updated later
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        this.stack.relx = x;
        this.stack.rely = y;
    }

    @Override
    public int getWidth() {
        return ITEM_SIZE;
    }

    @Override
    public int getHeight() {
        return ITEM_SIZE;
    }

    @Override
    public int getMaxHorizontalCount() {
        return 3;
    }

    @Override
    public int getSortOrder() {
        return 10;
    }

    @Override
    public void draw(Minecraft mc) {
        RecipeHandlerBase.drawItemSlot(offset.x - 1, offset.y - 1);
    }

    @Override
    public void handleTooltip(Point mousePos, List<String> currenttip) {
        // NEI handles item tooltips automatically via PositionedStack
    }

    public PositionedStack getStack() {
        return this.stack;
    }
}
