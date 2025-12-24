package ruiseki.omoshiroikamo.core.integration.structureLib;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment;
import com.gtnewhorizon.structurelib.structure.ICustomBlockSetting;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

public class StructureLibUtils {

    public static <T> IStructureElement<T> ofBlockAdderWithPos(IBlockAdderWithPos<T> iBlockAdderWithPos,
        Block defaultBlock, int defaultMeta) {

        if (defaultBlock instanceof ICustomBlockSetting) {
            return new IStructureElement<T>() {

                @Override
                public boolean check(T t, World world, int x, int y, int z) {
                    Block worldBlock = world.getBlock(x, y, z);
                    return iBlockAdderWithPos.apply(t, worldBlock, worldBlock.getDamageValue(world, x, y, z), x, y, z);
                }

                @Override
                public boolean couldBeValid(T t, World world, int x, int y, int z, ItemStack trigger) {
                    // calling ofBlockAdderWithPos can potentially modify external state
                    // therefore we assume this can always be valid.
                    return true;
                }

                @Override
                public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
                    ((ICustomBlockSetting) defaultBlock).setBlock(world, x, y, z, defaultMeta);
                    return true;
                }

                @Override
                public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
                    StructureLibAPI.hintParticle(world, x, y, z, defaultBlock, defaultMeta);
                    return true;
                }

                @Override
                @Deprecated
                public PlaceResult survivalPlaceBlock(T t, World world, int x, int y, int z, ItemStack trigger,
                    AutoPlaceEnvironment env) {
                    if (world.getBlock(x, y, z) == defaultBlock && world.getBlockMetadata(x, y, z) == defaultMeta) {
                        return PlaceResult.SKIP;
                    }
                    return StructureUtility.survivalPlaceBlock(
                        defaultBlock,
                        defaultMeta,
                        world,
                        x,
                        y,
                        z,
                        env.getSource(),
                        env.getActor(),
                        env.getChatter());
                }
            };
        } else {
            return new IStructureElement<T>() {

                @Override
                public boolean check(T t, World world, int x, int y, int z) {
                    Block worldBlock = world.getBlock(x, y, z);
                    return iBlockAdderWithPos.apply(t, worldBlock, worldBlock.getDamageValue(world, x, y, z), x, y, z);
                }

                @Override
                public boolean couldBeValid(T t, World world, int x, int y, int z, ItemStack trigger) {
                    // calling ofBlockAdderWithPos can potentially modify external state
                    // therefore we assume this can always be valid.
                    return true;
                }

                @Override
                public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
                    world.setBlock(x, y, z, defaultBlock, defaultMeta, 2);
                    return true;
                }

                @Override
                public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
                    StructureLibAPI.hintParticle(world, x, y, z, defaultBlock, defaultMeta);
                    return true;
                }

                @Override
                @Deprecated
                public PlaceResult survivalPlaceBlock(T t, World world, int x, int y, int z, ItemStack trigger,
                    AutoPlaceEnvironment env) {
                    if (world.getBlock(x, y, z) == defaultBlock && world.getBlockMetadata(x, y, z) == defaultMeta) {
                        return PlaceResult.SKIP;
                    }
                    return StructureUtility.survivalPlaceBlock(
                        defaultBlock,
                        defaultMeta,
                        world,
                        x,
                        y,
                        z,
                        env.getSource(),
                        env.getActor(),
                        env.getChatter());
                }
            };
        }
    }

    public static <T> IStructureElement<T> ofBlockAdderWithPos(IBlockAdderWithPos<T> iBlockAdderWithPos, int dots) {
        return ofBlockAdderWithPos(iBlockAdderWithPos, StructureLibAPI.getBlockHint(), dots - 1);
    }

    public interface IBlockAdderWithPos<T> {

        boolean apply(T t, Block block, int meta, int x, int y, int z);
    }
}
