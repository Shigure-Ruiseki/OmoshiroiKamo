package ruiseki.omoshiroikamo.core.block.property;

import static net.minecraftforge.common.util.ForgeDirection.UNKNOWN;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.core.InvalidPropertyTextException;

import ruiseki.omoshiroikamo.core.helper.TileHelpers;
import ruiseki.omoshiroikamo.core.tileentity.IOrientable;

public interface DirectionProperty extends BlockProperty<ForgeDirection> {

    @Override
    default Type getType() {
        return ForgeDirection.class;
    }

    @Override
    default boolean hasTrait(BlockPropertyTrait trait) {
        return switch (trait) {
            case SupportsWorld, WorldMutable, SupportsStacks, StackMutable -> true;
            default -> false;
        };
    }

    @Override
    default JsonElement serialize(ForgeDirection value) {
        return new JsonPrimitive(stringify(value));
    }

    @Override
    default ForgeDirection deserialize(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive()
            .isString() ? parse(element.getAsString()) : UNKNOWN;
    }

    @Override
    default String stringify(ForgeDirection value) {
        return value.name()
            .toLowerCase();
    }

    @Override
    default ForgeDirection parse(String text) throws InvalidPropertyTextException {
        try {
            return ForgeDirection.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPropertyTextException("Invalid ForgeDirection", e);
        }
    }

    static AbstractDirectionProperty facing() {
        return new AbstractDirectionProperty("facing") {

            @Override
            public ForgeDirection getValue(ItemStack stack) {
                return ForgeDirection.UP;
            }

            @Override
            public ForgeDirection getValue(IBlockAccess world, int x, int y, int z) {
                IOrientable orientable = TileHelpers.getSafeTile(world, x, y, z, IOrientable.class);
                return orientable != null ? orientable.getForward() : ForgeDirection.UP;
            }

            @Override
            public void setValue(World world, int x, int y, int z, ForgeDirection value) {
                IOrientable orientable = TileHelpers.getSafeTile(world, x, y, z, IOrientable.class);
                if (orientable == null) return;

                ForgeDirection up;
                if (value == ForgeDirection.UP || value == ForgeDirection.DOWN) {
                    up = ForgeDirection.NORTH;
                } else {
                    up = ForgeDirection.UP;
                }

                orientable.setOrientation(value, up);
            }
        };
    }

    abstract class AbstractDirectionProperty implements DirectionProperty {

        private String name;

        public AbstractDirectionProperty(String name) {
            this.name = name;
        }

        public AbstractDirectionProperty setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
