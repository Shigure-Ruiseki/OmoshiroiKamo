package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class LogicValues {

    public static final ILogicValue NULL = new ILogicValue() {

        @Override
        public LogicType<?> getType() {
            return LogicTypes.NULL;
        }

        @Override
        public Object raw() {
            return null;
        }

        @Override
        public boolean asBoolean() {
            return false;
        }

        @Override
        public int asInt() {
            return 0;
        }

        @Override
        public double asDouble() {
            return 0;
        }

        @Override
        public String asString() {
            return "";
        }

        @Override
        public String toString() {
            return "LogicValue[NULL]";
        }
    };

    public static ILogicValue of(boolean value) {
        return new BooleanValue(value);
    }

    public static ILogicValue of(int value) {
        return new IntValue(value);
    }

    public static ILogicValue of(String value) {
        return new StringValue(value);
    }

    private LogicValues() {}
}
