package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;

import cpw.mods.fml.common.Loader;
import ruiseki.omoshiroikamo.api.modular.recipe.EssentiaInput;
import ruiseki.omoshiroikamo.api.modular.recipe.EssentiaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.GasInput;
import ruiseki.omoshiroikamo.api.modular.recipe.GasOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ManaInput;
import ruiseki.omoshiroikamo.api.modular.recipe.ManaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.VisInput;
import ruiseki.omoshiroikamo.api.modular.recipe.VisOutput;
import thaumcraft.api.aspects.Aspect;

public class NEIRendererFactory {

    public static INEIPositionedRenderer createGasRenderer(Object input, Object output, Rectangle rect) {
        if (Loader.isModLoaded("Mekanism")) {
            return MekanismHelper.createGasRenderer(input, output, rect);
        }
        return null;
    }

    public static INEIPositionedRenderer createAspectRenderer(Object input, Object output, Rectangle rect) {
        if (Loader.isModLoaded("Thaumcraft")) {
            return ThaumcraftHelper.createAspectRenderer(input, output, rect);
        }
        return null;
    }

    public static INEIPositionedRenderer createManaRenderer(Object input, Object output, Rectangle rect) {
        if (Loader.isModLoaded("Botania")) {
            return BotaniaHelper.createManaRenderer(input, output, rect);
        }
        return null;
    }

    // Helper classes to isolate mod dependencies

    private static class MekanismHelper {

        public static INEIPositionedRenderer createGasRenderer(Object input, Object output, Rectangle rect) {
            try {
                if (input instanceof GasInput) {
                    GasInput gasIn = (GasInput) input;
                    mekanism.api.gas.Gas gas = mekanism.api.gas.GasRegistry.getGas(gasIn.getGasName());
                    if (gas != null) {
                        return new PositionedGasTank(new mekanism.api.gas.GasStack(gas, gasIn.getAmount()), rect);
                    }
                } else if (output instanceof GasOutput) {
                    GasOutput gasOut = (GasOutput) output;
                    mekanism.api.gas.Gas gas = mekanism.api.gas.GasRegistry.getGas(gasOut.getGasName());
                    if (gas != null) {
                        return new PositionedGasTank(new mekanism.api.gas.GasStack(gas, gasOut.getAmount()), rect);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class ThaumcraftHelper {

        public static INEIPositionedRenderer createAspectRenderer(Object input, Object output, Rectangle rect) {
            try {
                if (input instanceof VisInput) {
                    VisInput visIn = (VisInput) input;
                    Aspect aspect = Aspect.getAspect(visIn.getAspectTag());
                    if (aspect != null) {
                        return new PositionedVis(aspect, visIn.getAmount(), rect);
                    }
                } else if (output instanceof VisOutput) {
                    VisOutput visOut = (VisOutput) output;
                    Aspect aspect = Aspect.getAspect(visOut.getAspectTag());
                    if (aspect != null) {
                        return new PositionedVis(aspect, visOut.getAmountCentiVis(), rect);
                    }
                } else if (input instanceof EssentiaInput) {
                    EssentiaInput essIn = (EssentiaInput) input;
                    Aspect aspect = Aspect.getAspect(essIn.getAspectTag());
                    if (aspect != null) {
                        return new PositionedEssentia(aspect, essIn.getAmount(), rect);
                    }
                } else if (output instanceof EssentiaOutput) {
                    EssentiaOutput essOut = (EssentiaOutput) output;
                    Aspect aspect = Aspect.getAspect(essOut.getAspectTag());
                    if (aspect != null) {
                        return new PositionedEssentia(aspect, essOut.getAmount(), rect);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class BotaniaHelper {

        public static INEIPositionedRenderer createManaRenderer(Object input, Object output, Rectangle rect) {
            try {
                if (input instanceof ManaInput) {
                    ManaInput manaIn = (ManaInput) input;
                    return new PositionedManaBar(manaIn.getAmount(), manaIn.isPerTick(), rect);
                } else if (output instanceof ManaOutput) {
                    ManaOutput manaOut = (ManaOutput) output;
                    return new PositionedManaBar(manaOut.getAmount(), manaOut.isPerTick(), rect);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
