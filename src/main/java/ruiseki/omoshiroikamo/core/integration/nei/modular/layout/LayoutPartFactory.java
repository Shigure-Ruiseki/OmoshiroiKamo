package ruiseki.omoshiroikamo.core.integration.nei.modular.layout;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.modular.recipe.EnergyInput;
import ruiseki.omoshiroikamo.api.modular.recipe.EnergyOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.EssentiaInput;
import ruiseki.omoshiroikamo.api.modular.recipe.EssentiaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.FluidInput;
import ruiseki.omoshiroikamo.api.modular.recipe.FluidOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.GasInput;
import ruiseki.omoshiroikamo.api.modular.recipe.GasOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ItemInput;
import ruiseki.omoshiroikamo.api.modular.recipe.ItemOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ManaInput;
import ruiseki.omoshiroikamo.api.modular.recipe.ManaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.VisInput;
import ruiseki.omoshiroikamo.api.modular.recipe.VisOutput;
import ruiseki.omoshiroikamo.core.integration.nei.PositionedFluidTank;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.INEIPositionedRenderer;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.NEIRendererFactory;
import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.PositionedEnergy;

public class LayoutPartFactory {

    private static final Map<Class<?>, Function<Object, RecipeLayoutPart<?>>> REGISTRY = new HashMap<>();

    static {
        // --- Items ---
        register(ItemInput.class, in -> {
            ItemInput itemIn = (ItemInput) in;
            return itemIn.getItems()
                .isEmpty() ? null : new LayoutPartItem(itemIn.getItems());
        });
        register(ItemOutput.class, out -> {
            ItemOutput itemOut = (ItemOutput) out;
            return itemOut.getItems()
                .isEmpty() ? null : new LayoutPartItem(itemOut.getItems());
        });

        // --- Fluids ---
        register(FluidInput.class, in -> {
            FluidInput fluidIn = (FluidInput) in;
            return new LayoutPartFluid(new PositionedFluidTank(fluidIn.getFluid(), new Rectangle(0, 0, 16, 16)));
        });
        register(FluidOutput.class, out -> {
            FluidOutput fluidOut = (FluidOutput) out;
            return new LayoutPartFluid(new PositionedFluidTank(fluidOut.getFluid(), new Rectangle(0, 0, 16, 16)));
        });

        // --- Energy ---
        register(EnergyInput.class, in -> {
            EnergyInput energyIn = (EnergyInput) in;
            return new LayoutPartEnergy(
                new PositionedEnergy(energyIn.getAmount(), energyIn.isPerTick(), true, new Rectangle(0, 0, 16, 64)));
        });
        register(EnergyOutput.class, out -> {
            EnergyOutput energyOut = (EnergyOutput) out;
            return new LayoutPartEnergy(
                new PositionedEnergy(energyOut.getAmount(), energyOut.isPerTick(), false, new Rectangle(0, 0, 16, 64)));
        });

        // --- Mana ---
        register(ManaInput.class, in -> {
            INEIPositionedRenderer r = NEIRendererFactory.createManaRenderer(in, null, new Rectangle(0, 0, 100, 8));
            return r != null ? new LayoutPartMana(r) : null;
        });
        register(ManaOutput.class, out -> {
            INEIPositionedRenderer r = NEIRendererFactory.createManaRenderer(null, out, new Rectangle(0, 0, 100, 8));
            return r != null ? new LayoutPartMana(r) : null;
        });

        // --- Gas ---
        register(GasInput.class, in -> {
            INEIPositionedRenderer r = NEIRendererFactory.createGasRenderer(in, null, new Rectangle(0, 0, 16, 16));
            return r != null ? new LayoutPartGas(r) : null;
        });
        register(GasOutput.class, out -> {
            INEIPositionedRenderer r = NEIRendererFactory.createGasRenderer(null, out, new Rectangle(0, 0, 16, 16));
            return r != null ? new LayoutPartGas(r) : null;
        });

        // --- Vis ---
        register(VisInput.class, in -> {
            INEIPositionedRenderer r = NEIRendererFactory.createAspectRenderer(in, null, new Rectangle(0, 0, 16, 16));
            return r != null ? new LayoutPartVis(r) : null;
        });
        register(VisOutput.class, out -> {
            INEIPositionedRenderer r = NEIRendererFactory.createAspectRenderer(null, out, new Rectangle(0, 0, 16, 16));
            return r != null ? new LayoutPartVis(r) : null;
        });

        // --- Essentia ---
        register(EssentiaInput.class, in -> {
            EssentiaInput essIn = (EssentiaInput) in;
            ItemStack aspectStack = NEIRendererFactory.createEssentiaItemStack(essIn.getAspectTag());
            if (aspectStack != null) {
                aspectStack.stackSize = essIn.getAmount();
                return new LayoutPartItem(aspectStack);
            }
            INEIPositionedRenderer r = NEIRendererFactory.createAspectRenderer(in, null, new Rectangle(0, 0, 16, 16));
            return r != null ? new LayoutPartEssentia(r) : null;
        });
        register(EssentiaOutput.class, out -> {
            EssentiaOutput essOut = (EssentiaOutput) out;
            ItemStack aspectStack = NEIRendererFactory.createEssentiaItemStack(essOut.getAspectTag());
            if (aspectStack != null) {
                aspectStack.stackSize = essOut.getAmount();
                return new LayoutPartItem(aspectStack);
            }
            INEIPositionedRenderer r = NEIRendererFactory.createAspectRenderer(null, out, new Rectangle(0, 0, 16, 16));
            return r != null ? new LayoutPartEssentia(r) : null;
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> void register(Class<T> clazz, Function<T, RecipeLayoutPart<?>> creator) {
        REGISTRY.put(clazz, (Function<Object, RecipeLayoutPart<?>>) creator);
    }

    public static RecipeLayoutPart<?> create(Object inputOrOutput) {
        if (inputOrOutput == null) return null;
        Class<?> clazz = inputOrOutput.getClass();

        // Fast exact match lookup
        Function<Object, RecipeLayoutPart<?>> creator = REGISTRY.get(clazz);
        if (creator != null) {
            return creator.apply(inputOrOutput);
        }

        // Fallback for subclasses
        for (Map.Entry<Class<?>, Function<Object, RecipeLayoutPart<?>>> entry : REGISTRY.entrySet()) {
            if (entry.getKey()
                .isAssignableFrom(clazz)) {
                return entry.getValue()
                    .apply(inputOrOutput);
            }
        }

        return null; // Unsupported type
    }
}
