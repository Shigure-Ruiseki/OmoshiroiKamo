package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Registry to reconstruct IRecipeOutput from NBT.
 */
public class OutputNBTRegistry {

    private static final Map<String, Supplier<IRecipeOutput>> REGISTRY = new HashMap<>();

    static {
        register("item", () -> new ItemOutput((ItemStack) null));
        register("fluid", () -> new FluidOutput("", 0));
        register("energy", () -> new EnergyOutput(0, true));
        register("mana", () -> new ManaOutput(0, true));
        register("gas", () -> new GasOutput("", 0));
        register("essentia", () -> new EssentiaOutput("", 0));
        register("vis", () -> new VisOutput("", 0));
        // Add more integrations here as needed
    }

    public static void register(String id, Supplier<IRecipeOutput> supplier) {
        REGISTRY.put(id, supplier);
    }

    public static IRecipeOutput read(NBTTagCompound nbt) {
        String id = nbt.getString("id");
        Supplier<IRecipeOutput> supplier = REGISTRY.get(id);
        if (supplier != null) {
            IRecipeOutput output = supplier.get();
            output.readFromNBT(nbt);
            return output;
        }
        Logger.warn("Unknown output type in NBT: {}", id);
        return null;
    }
}
