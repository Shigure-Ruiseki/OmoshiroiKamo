package ruiseki.omoshiroikamo.module.ids.common.part;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.ids.part.ICableBusContainer;
import ruiseki.omoshiroikamo.api.ids.part.SelectedPart;

public class NullCableBusContainer implements ICableBusContainer {

    @Override
    public int isProvidingStrongPower(final ForgeDirection opposite) {
        return 0;
    }

    @Override
    public int isProvidingWeakPower(final ForgeDirection opposite) {
        return 0;
    }

    @Override
    public boolean canConnectRedstone(final EnumSet<ForgeDirection> of) {
        return false;
    }

    @Override
    public void onEntityCollision(final Entity e) {}

    @Override
    public boolean activate(final EntityPlayer player, final Vec3 vecFromPool) {
        return false;
    }

    @Override
    public void onNeighborChanged() {}

    @Override
    public boolean isSolidOnSide(final ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public SelectedPart selectPart(final Vec3 v3) {
        return new SelectedPart();
    }

    @Override
    public boolean isLadder(final EntityLivingBase entity) {
        return false;
    }

    @Override
    public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random r) {}

    @Override
    public int getLightValue() {
        return 0;
    }
}
