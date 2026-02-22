package ruiseki.omoshiroikamo.module.ids.common.util;

import java.util.Objects;

import ruiseki.omoshiroikamo.module.ids.common.item.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.value.ILogicValue;

public class LogicOperationUtils {

    private LogicOperationUtils() {}

    public static NumericRank commonRank(ILogicValue a, ILogicValue b) {
        NumericRank ra = NumericRank.of(a.getType());
        NumericRank rb = NumericRank.of(b.getType());

        if (ra == null || rb == null) return null;
        return ra.rank >= rb.rank ? ra : rb;
    }

    public static Integer compare(ILogicValue a, ILogicValue b) {
        NumericRank rank = commonRank(a, b);
        if (rank == null) return null;

        return switch (rank) {
            case INT -> Integer.compare(a.asInt(), b.asInt());
            case LONG -> Long.compare(a.asLong(), b.asLong());
            case FLOAT -> Float.compare(a.asFloat(), b.asFloat());
            case DOUBLE -> Double.compare(a.asDouble(), b.asDouble());
        };
    }

    public static boolean equals(ILogicValue a, ILogicValue b) {
        if (a.getType()
            .isNumeric()
            && b.getType()
                .isNumeric()) {
            Integer cmp = compare(a, b);
            return cmp != null && cmp == 0;
        }

        if (a.getType() == b.getType()) {
            return Objects.equals(a.raw(), b.raw());
        }

        return false;
    }

    public enum NumericRank {

        INT(1),
        LONG(2),
        FLOAT(3),
        DOUBLE(4);

        private final int rank;

        NumericRank(int rank) {
            this.rank = rank;
        }

        public static NumericRank of(LogicType<?> type) {
            if (type == null) return null;

            return switch (type.getId()) {
                case "int" -> INT;
                case "long" -> LONG;
                case "float" -> FLOAT;
                case "double" -> DOUBLE;
                default -> null;
            };
        }
    }
}
