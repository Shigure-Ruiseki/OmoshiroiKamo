package ruiseki.omoshiroikamo.core.common.util;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;

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
}
