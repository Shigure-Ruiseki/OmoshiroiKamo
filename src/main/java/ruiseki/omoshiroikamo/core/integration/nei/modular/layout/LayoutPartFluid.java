package ruiseki.omoshiroikamo.core.integration.nei.modular.layout;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.core.integration.nei.PositionedFluidTank;

public class LayoutPartFluid extends RecipeLayoutPart<FluidStack> {

    private final PositionedFluidTank tank;

    public LayoutPartFluid(PositionedFluidTank tank) {
        super(0, 0);
        this.tank = tank;
        // Ensure tank position is relative for now, updated later
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        if (this.tank.position != null) {
            this.tank.position.setLocation(x, y);
        } else {
            this.tank.position = new Rectangle(x, y, getWidth(), getHeight());
        }
    }

    @Override
    public int getWidth() {
        return 18;
    }

    @Override
    public int getHeight() {
        return 60;
    }

    @Override
    public int getMaxHorizontalCount() {
        return 2;
    }

    @Override
    public int getSortOrder() {
        return 100;
    }

    @Override
    public void draw(Minecraft mc) {
        tank.draw();
    }

    @Override
    public void handleTooltip(Point mousePos, List<String> currenttip) {
        if (tank.position.contains(mousePos)) {
            tank.handleTooltip(currenttip);
        }
    }

    public PositionedFluidTank getTank() {
        return tank;
    }
}
