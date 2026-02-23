package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.modular.recipe.EssentiaInput;
import ruiseki.omoshiroikamo.api.modular.recipe.EssentiaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.GasInput;
import ruiseki.omoshiroikamo.api.modular.recipe.GasOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ManaInput;
import ruiseki.omoshiroikamo.api.modular.recipe.ManaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.VisInput;
import ruiseki.omoshiroikamo.api.modular.recipe.VisOutput;
import ruiseki.omoshiroikamo.core.lib.LibMods;

public class NEIRendererFactory {

    public static INEIPositionedRenderer createGasRenderer(Object input, Object output, Rectangle rect) {
        if (LibMods.Mekanism.isLoaded()) {
            return MekanismHelper.createGasRenderer(input, output, rect);
        }
        return null;
    }

    public static INEIPositionedRenderer createAspectRenderer(Object input, Object output, Rectangle rect) {
        if (LibMods.Thaumcraft.isLoaded()) {
            return ThaumcraftHelper.createAspectRenderer(input, output, rect);
        }
        return null;
    }

    public static INEIPositionedRenderer createManaRenderer(Object input, Object output, Rectangle rect) {
        if (LibMods.Botania.isLoaded()) {
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
                thaumcraft.api.aspects.Aspect aspect;
                if (input instanceof VisInput) {
                    VisInput visIn = (VisInput) input;
                    aspect = thaumcraft.api.aspects.Aspect.getAspect(visIn.getAspectTag());
                    if (aspect != null) {
                        return new PositionedVis(aspect, visIn.getAmount(), rect);
                    }
                } else if (output instanceof VisOutput) {
                    VisOutput visOut = (VisOutput) output;
                    aspect = thaumcraft.api.aspects.Aspect.getAspect(visOut.getAspectTag());
                    if (aspect != null) {
                        return new PositionedVis(aspect, visOut.getAmountCentiVis(), rect);
                    }
                } else if (input instanceof EssentiaInput) {
                    EssentiaInput essIn = (EssentiaInput) input;
                    aspect = thaumcraft.api.aspects.Aspect.getAspect(essIn.getAspectTag());
                    if (aspect != null) {
                        return new PositionedEssentia(aspect, essIn.getAmount(), rect);
                    }
                } else if (output instanceof EssentiaOutput) {
                    EssentiaOutput essOut = (EssentiaOutput) output;
                    aspect = thaumcraft.api.aspects.Aspect.getAspect(essOut.getAspectTag());
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

    /**
     * Creates an ItemAspect ItemStack from ThaumcraftNEIPlugin for the given aspect
     * tag.
     * Returns null if ThaumcraftNEIPlugin is not loaded or the aspect is not found.
     */
    public static ItemStack createEssentiaItemStack(String aspectTag) {
        if (LibMods.ThaumcraftNEIPlugin.isLoaded()) {
            return ThaumcraftNEIPluginHelper.createItemStack(aspectTag);
        }
        return null;
    }

    private static class ThaumcraftNEIPluginHelper {

        public static ItemStack createItemStack(String aspectTag) {
            try {
                thaumcraft.api.aspects.Aspect aspect = thaumcraft.api.aspects.Aspect.getAspect(aspectTag);
                if (aspect == null) return null;
                Item item = com.djgiannuzz.thaumcraftneiplugin.ModItems.itemAspect;
                if (item == null) return null;
                ItemStack stack = new ItemStack(item, 1, 0);
                com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect.setAspect(stack, aspect);
                return stack;
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
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
