package ruiseki.omoshiroikamo.module.cable.common.conduit.geom;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.module.cable.common.conduit.IConduit;

public class Offsets {

    private static Map<OffsetKey, Offset> OFFSETS = new HashMap<OffsetKey, Offset>();

    public static Offset get(Class<? extends IConduit> type, ForgeDirection dir) {
        Offset res = OFFSETS.get(key(type, getAxisForDir(dir)));
        if (res == null) {
            res = Offset.NONE;
        }
        return res;
    }

    public static OffsetKey key(Class<? extends IConduit> type, Axis axis) {
        return new OffsetKey(type, axis);
    }

    public static Axis getAxisForDir(ForgeDirection dir) {
        if (dir == ForgeDirection.UNKNOWN) {
            return Axis.NONE;
        }
        if (dir == ForgeDirection.EAST || dir == ForgeDirection.WEST) {
            return Axis.X;
        }
        if (dir == ForgeDirection.UP || dir == ForgeDirection.DOWN) {
            return Axis.Y;
        }
        if (dir == ForgeDirection.NORTH || dir == ForgeDirection.SOUTH) {
            return Axis.Z;
        }
        return Axis.NONE;
    }

    public static enum Axis {
        NONE,
        X,
        Y,
        Z
    }

    public static class OffsetKey {

        String typeName;
        Axis axis;

        private OffsetKey(Class<? extends IConduit> type, Axis axis) {
            this.typeName = type.getCanonicalName();
            this.axis = axis;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((axis == null) ? 0 : axis.hashCode());
            result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            OffsetKey other = (OffsetKey) obj;
            if (axis != other.axis) {
                return false;
            }
            if (typeName == null) {
                if (other.typeName != null) {
                    return false;
                }
            } else if (!typeName.equals(other.typeName)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "OffsetKey [typeName=" + typeName + ", axis=" + axis + "]";
        }
    }
}
