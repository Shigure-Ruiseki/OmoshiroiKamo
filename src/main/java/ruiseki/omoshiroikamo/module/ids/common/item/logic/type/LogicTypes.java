package ruiseki.omoshiroikamo.module.ids.common.item.logic.type;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.datastructure.BlockStack;
import ruiseki.omoshiroikamo.core.lib.LibResources;

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
    public static LogicType<NBTTagCompound> NBT;
    public static LogicType<List<String>> LIST;

    public static void preInit() {
        NULL = LogicTypeRegistry.register(new LogicType<>("null"));

        BOOLEAN = LogicTypeRegistry.register(new LogicType<>("boolean") {

            @Override
            public void registerIcons(IIconRegister register) {
                IconRegistry.addIcon(
                    "valuetype.boolean",
                    register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/boolean"));
            }
        });

        INT = LogicTypeRegistry.register(new LogicType<>("int") {

            @Override
            public void registerIcons(IIconRegister register) {
                IconRegistry
                    .addIcon("valuetype.int", register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/int"));
            }

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
            public void registerIcons(IIconRegister register) {
                IconRegistry.addIcon(
                    "valuetype.double",
                    register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/double"));
            }

            @Override
            public boolean isNumeric() {
                return true;
            }

        });

        STRING = LogicTypeRegistry.register(new LogicType<>("string") {

            @Override
            public void registerIcons(IIconRegister register) {
                IconRegistry.addIcon(
                    "valuetype.string",
                    register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/string"));
            }
        });

        LIST = LogicTypeRegistry.register(new LogicType<>("list") {

            @Override
            public void registerIcons(IIconRegister register) {
                IconRegistry
                    .addIcon("valuetype.list", register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/list"));
            }
        });

        BLOCK = LogicTypeRegistry.register(new LogicType<>("block"));

        ITEM = LogicTypeRegistry.register(new LogicType<>("item"));

        FLUID = LogicTypeRegistry.register(new LogicType<>("fluid"));

        NBT = LogicTypeRegistry.register(new LogicType<>("nbt") {

            @Override
            public void registerIcons(IIconRegister register) {
                IconRegistry
                    .addIcon("valuetype.nbt", register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/nbt"));
            }
        });
    }
}
