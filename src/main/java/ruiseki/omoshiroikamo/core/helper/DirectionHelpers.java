package ruiseki.omoshiroikamo.core.helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.util.MathUtil;

public class DirectionHelpers {

    public static ForgeDirection yawToDirection6(EntityLivingBase entity) {

        float pitch = entity.rotationPitch;

        if (pitch > 60) {
            return ForgeDirection.DOWN;
        }

        if (pitch < -60) {
            return ForgeDirection.UP;
        }

        int yaw = MathHelper.floor_double((entity.rotationYaw * 4F / 360F) + 0.5D) & 3;

        return switch (yaw) {
            case 1 -> ForgeDirection.EAST;
            case 2 -> ForgeDirection.SOUTH;
            case 3 -> ForgeDirection.WEST;
            default -> ForgeDirection.NORTH;
        };
    }

    public static ForgeDirection yawToDirection4(EntityLivingBase entity) {
        return switch (Math.round(MathUtil.mod(entity.rotationYaw, 360) / 360 * 4)) {
            case 1 -> ForgeDirection.EAST;
            case 2 -> ForgeDirection.SOUTH;
            case 3 -> ForgeDirection.WEST;
            default -> ForgeDirection.NORTH;
        };
    }
}
