package ruiseki.omoshiroikamo.core.integration.waila;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IWailaEntityInfoProvider {

    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, Entity entity);
}
