package ruiseki.omoshiroikamo.module.ids.common.util;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import com.google.common.collect.Lists;

import ruiseki.omoshiroikamo.api.datastructure.BlockPos;
import ruiseki.omoshiroikamo.api.datastructure.DimPos;
import ruiseki.omoshiroikamo.api.util.TileHelpers;

/**
 * Helpers related to cables.
 *
 * @author rubensworks
 */
public class CableHelpers {

    private static final List<IInterfaceRetriever> INTERFACE_RETRIEVERS = Lists.newLinkedList();
    static {
        addInterfaceRetriever(new IInterfaceRetriever() {

            @Override
            public <C> C getInterface(IBlockAccess world, BlockPos pos, Class<C> clazz) {
                Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
                if (clazz.isInstance(block)) {
                    return clazz.cast(block);
                }
                return null;
            }
        });
        addInterfaceRetriever(new IInterfaceRetriever() {

            @Override
            public <C> C getInterface(IBlockAccess world, BlockPos pos, Class<C> clazz) {
                return TileHelpers.getSafeTile(world, pos, clazz);
            }
        });
    }

    /**
     * Check for the given interface at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     * @param clazz The class to find.
     * @param <C>   The class type.
     * @return The instance or null.
     */
    public static <C> C getInterface(IBlockAccess world, BlockPos pos, Class<C> clazz) {
        C instance;
        for (IInterfaceRetriever interfaceRetriever : INTERFACE_RETRIEVERS) {
            instance = interfaceRetriever.getInterface(world, pos, clazz);
            if (instance != null) {
                return instance;
            }
        }
        return null;
    }

    /**
     * Check for the given interface at the given position.
     *
     * @param dimPos The dimensional position.
     * @param clazz  The class to find.
     * @param <C>    The class type.
     * @return The instance or null.
     */
    public static <C> C getInterface(DimPos dimPos, Class<C> clazz) {
        C instance;
        for (IInterfaceRetriever interfaceRetriever : INTERFACE_RETRIEVERS) {
            instance = interfaceRetriever.getInterface(dimPos.getWorld(), dimPos.getBlockPos(), clazz);
            if (instance != null) {
                return instance;
            }
        }
        return null;
    }

    public static void addInterfaceRetriever(IInterfaceRetriever interfaceRetriever) {
        INTERFACE_RETRIEVERS.add(interfaceRetriever);
    }

    public static interface IInterfaceRetriever {

        /**
         * Attempt to get a given interface instance.
         *
         * @param world The world.
         * @param pos   The position.
         * @param clazz The class to find.
         * @param <C>   The class type.
         * @return The instance or null.
         */
        public <C> C getInterface(IBlockAccess world, BlockPos pos, Class<C> clazz);

    }

}
