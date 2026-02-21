package ruiseki.omoshiroikamo.core.entity;

import java.util.regex.Pattern;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class EntityDoppleganger extends EntityCreature {

    public EntityDoppleganger(World par1World) {
        super(par1World);
        setSize(0.6F, 1.8F);
        getNavigator().setCanSwim(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, Float.MAX_VALUE));
        isImmuneToFire = true;
        experienceValue = 825;
    }

    private static final Pattern FAKE_PLAYER_PATTERN = Pattern.compile("^(?:\\[.*\\])|(?:ComputerCraft)$");

    public static boolean isTruePlayer(Entity e) {
        if (!(e instanceof EntityPlayer)) return false;

        EntityPlayer player = (EntityPlayer) e;

        String name = player.getCommandSenderName();
        return !(player instanceof FakePlayer || FAKE_PLAYER_PATTERN.matcher(name)
            .matches());
    }

}
