package ruiseki.omoshiroikamo.core.block.property;

import java.lang.reflect.Type;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;

import ruiseki.omoshiroikamo.core.block.state.IOpenState;
import ruiseki.omoshiroikamo.core.helper.TileHelpers;

public interface BooleanProperty extends BlockProperty<Boolean> {

    @Override
    default Type getType() {
        return Boolean.class;
    }

    @Override
    default boolean hasTrait(BlockPropertyTrait trait) {
        return switch (trait) {
            case SupportsWorld, WorldMutable, SupportsStacks, StackMutable -> true;
            default -> false;
        };
    }

    static AbstractBooleanProperty create(String name) {
        return new AbstractBooleanProperty(name);
    }

    static AbstractBooleanProperty open() {
        return new AbstractBooleanProperty("open") {

            @Override
            public Boolean getValue(IBlockAccess world, int x, int y, int z) {
                IOpenState state = TileHelpers.getSafeTile(world, x, y, z, IOpenState.class);
                return state != null && state.isOpen();
            }

            @Override
            public void setValue(World world, int x, int y, int z, Boolean value) {
                IOpenState state = TileHelpers.getSafeTile(world, x, y, z, IOpenState.class);
                if (state == null) return;
                state.setOpen(value);
            }
        };
    }

    class AbstractBooleanProperty implements BooleanProperty {

        private final String name;

        protected AbstractBooleanProperty(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Boolean getValue(ItemStack stack) {
            return false;
        }

        @Override
        public Boolean getValue(IBlockAccess world, int x, int y, int z) {
            return false;
        }

        @Override
        public void setValue(World world, int x, int y, int z, Boolean value) {}
    }
}
