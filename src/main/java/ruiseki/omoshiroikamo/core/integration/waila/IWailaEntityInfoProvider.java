package ruiseki.omoshiroikamo.core.integration.waila;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IWailaEntityInfoProvider {

    public static final int BIT_BASIC = 0x1;
    public static final int BIT_COMMON = 0x2;
    public static final int BIT_DETAILED = 0x4;
    public static final int ALL_BITS = BIT_BASIC | BIT_COMMON | BIT_DETAILED;

    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, Entity entity);
}
