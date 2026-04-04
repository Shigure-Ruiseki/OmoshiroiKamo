package ruiseki.omoshiroikamo.api.storage.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.core.datastructure.BlockPos;

public interface ITickable {

    boolean tick(EntityPlayer player);

    boolean tick(World world, BlockPos pos);
}
