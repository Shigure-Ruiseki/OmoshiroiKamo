package ruiseki.omoshiroikamo.module.machinery.common.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Custom Fluid implementation that supports colors from EnumFluidMaterial.
 */
public class FluidOK extends Fluid {

    private final EnumFluidMaterial material;

    public FluidOK(EnumFluidMaterial material) {
        super(material.getName());
        this.material = material;
    }

    @Override
    public int getColor() {
        return material.getColor();
    }

    @Override
    public int getColor(FluidStack stack) {
        return material.getColor();
    }
}
