package ruiseki.omoshiroikamo.module.cable.common.network.logic.type;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;

public class LogicTypes {

    public static final LogicType<Void> NULL = LogicTypeRegistry.register(new LogicType<>("null") {

        @Override
        public Void cast(ILogicValue value) {
            return null;
        }
    });

    public static final LogicType<Boolean> BOOLEAN = LogicTypeRegistry.register(new LogicType<>("boolean") {

        @Override
        public Boolean cast(ILogicValue value) {
            return value.asBoolean();
        }
    });

    public static final LogicType<Integer> INT = LogicTypeRegistry.register(new LogicType<>("int") {

        @Override
        public Integer cast(ILogicValue value) {
            return value.asInt();
        }
    });

    public static final LogicType<String> STRING = LogicTypeRegistry.register(new LogicType<>("string") {

        @Override
        public String cast(ILogicValue value) {
            return value.asString();
        }
    });
}
