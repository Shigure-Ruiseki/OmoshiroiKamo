package ruiseki.omoshiroikamo.module.cable.common.network.logic.type;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.block.BlockStack;

public class LogicTypes {

    public static LogicType<Void> NULL;
    public static LogicType<Boolean> BOOLEAN;
    public static LogicType<Integer> INT;
    public static LogicType<Long> LONG;
    public static LogicType<Float> FLOAT;
    public static LogicType<Double> DOUBLE;
    public static LogicType<String> STRING;
    public static LogicType<BlockStack> BLOCK;
    public static LogicType<ItemStack> ITEM;
    public static LogicType<FluidStack> FLUID;
    public static LogicType<List<String>> LIST;

    public static void preInit() {
        NULL = LogicTypeRegistry.register(new LogicType<>("null") {});

        BOOLEAN = LogicTypeRegistry.register(new LogicType<>("boolean") {});

        INT = LogicTypeRegistry.register(new LogicType<>("int") {

            @Override
            public boolean isNumeric() {
                return true;
            }

        });

        LONG = LogicTypeRegistry.register(new LogicType<>("long") {

            @Override
            public boolean isNumeric() {
                return true;
            }

        });

        FLOAT = LogicTypeRegistry.register(new LogicType<>("float") {

            @Override
            public boolean isNumeric() {
                return true;
            }

        });

        DOUBLE = LogicTypeRegistry.register(new LogicType<>("double") {

            @Override
            public boolean isNumeric() {
                return true;
            }

        });

        STRING = LogicTypeRegistry.register(new LogicType<>("string") {

        });

        LIST = LogicTypeRegistry.register(new LogicType<>("list") {

        });

        BLOCK = LogicTypeRegistry.register(new LogicType<>("block") {

        });

        ITEM = LogicTypeRegistry.register(new LogicType<>("item") {

        });

        FLUID = LogicTypeRegistry.register(new LogicType<>("fluid") {

        });
    }
}
