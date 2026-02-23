package ruiseki.omoshiroikamo.module.ids.common.part;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.ids.part.ICableBusContainer;
import ruiseki.omoshiroikamo.api.ids.part.SelectedPart;
import ruiseki.omoshiroikamo.core.persist.nbt.INBTSerializable;

public class CableBusContainer implements ICableBusContainer, INBTSerializable {

    private boolean hasRedstone = false;

    boolean inWorld = false;
    public boolean requiresDynamicRender = false;

    public CableBusContainer() {}

    @Override
    public int isProvidingStrongPower(ForgeDirection opposite) {
        return 0;
    }

    @Override
    public int isProvidingWeakPower(ForgeDirection opposite) {
        return 0;
    }

    @Override
    public boolean canConnectRedstone(EnumSet<ForgeDirection> of) {
        return false;
    }

    @Override
    public void onEntityCollision(Entity e) {

    }

    @Override
    public boolean activate(EntityPlayer player, Vec3 vecFromPool) {
        return false;
    }

    @Override
    public void onNeighborChanged() {

    }

    @Override
    public boolean isSolidOnSide(ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public SelectedPart selectPart(Vec3 v3) {
        return null;
    }

    @Override
    public boolean isLadder(EntityLivingBase entity) {
        return false;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random r) {

    }

    @Override
    public int getLightValue() {
        return 0;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("hasRedstone", hasRedstone);

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        hasRedstone = tag.getBoolean("hasRedstone");

    }
}
