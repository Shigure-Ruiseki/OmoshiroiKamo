package ruiseki.omoshiroikamo.core.common.util;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;

import ruiseki.omoshiroikamo.api.block.LookDirection;

public class PlayerUtils {

    public static boolean doesPlayerExist(World world, UUID player) {
        if (world != null && player != null) {
            if (world.playerEntities == null) {
                return false;
            } else {
                for (EntityPlayer p : world.playerEntities) {
                    if (p != null && player.equals(
                        p.getGameProfile()
                            .getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static EntityPlayer getPlayerFromWorld(World world, UUID player) {
        if (world != null && player != null) {
            for (EntityPlayer p : world.playerEntities) {
                if (p != null && player.equals(
                    p.getGameProfile()
                        .getId())) {
                    return p;
                }
            }
        }
        return null;
    }

    public static boolean doesPlayerExistClient(World world, UUID player) {
        if (player == null) {
            return false;
        } else {
            for (EntityPlayer entityPlayer : world.playerEntities) {
                if (entityPlayer.getUniqueID()
                    .compareTo(player) == 0) {
                    return true;
                }
            }

            return false;
        }
    }

    public static EntityPlayer getPlayerFromWorldClient(World world, UUID player) {
        if (player == null) {
            return null;
        } else {
            for (EntityPlayer entityPlayer : world.playerEntities) {
                if (entityPlayer.getUniqueID()
                    .compareTo(player) == 0) {
                    return entityPlayer;
                }
            }

            return null;
        }
    }

    public static NBTTagCompound proifleToNBT(GameProfile profile) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Name", profile.getName());
        UUID id = profile.getId();
        if (id != null) {
            tag.setLong("UUIDL", id.getLeastSignificantBits());
            tag.setLong("UUIDU", id.getMostSignificantBits());
        }

        return tag;
    }

    public static void writeProfileToNBT(GameProfile profile, NBTTagCompound tag) {
        tag.setString("Name", profile.getName());
        UUID id = profile.getId();
        if (id != null) {
            tag.setLong("UUIDL", id.getLeastSignificantBits());
            tag.setLong("UUIDU", id.getMostSignificantBits());
        }

    }

    public static GameProfile profileFromNBT(NBTTagCompound tag) {
        String name = tag.getString("Name");
        UUID uuid = null;
        if (tag.hasKey("UUIDL")) {
            uuid = new UUID(tag.getLong("UUIDU"), tag.getLong("UUIDL"));
        } else if (StringUtils.isBlank(name)) {
            return null;
        }

        return new GameProfile(uuid, name);
    }

    public static Vec3 getEyePosition(EntityPlayer player) {
        Vec3 v = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        if (player.worldObj.isRemote) {
            // take into account any eye changes done by mods.
            v.yCoord += player.getEyeHeight() - player.getDefaultEyeHeight();
        } else {
            v.yCoord += player.getEyeHeight();
            if (player instanceof EntityPlayerMP && player.isSneaking()) {
                v.yCoord -= 0.08;
            }
        }
        return v;
    }

    public static float getEyeOffset(final EntityPlayer player) {
        assert player.worldObj.isRemote : "Valid only on client";
        return (float) (player.posY + player.getEyeHeight() - player.getDefaultEyeHeight());
    }

    public static LookDirection getPlayerRay(final EntityPlayer player, final float eyeOffset) {
        final float f = 1.0F;
        final float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        final float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        final double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
        final double d1 = eyeOffset;
        final double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;

        final Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        final float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        final float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        final float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        final float f6 = MathHelper.sin(-f1 * 0.017453292F);
        final float f7 = f4 * f5;
        final float f8 = f3 * f5;
        double d3 = 5.0D;

        if (player instanceof EntityPlayerMP) {
            d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        }
        final Vec3 vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
        return new LookDirection(vec3, vec31);
    }
}
