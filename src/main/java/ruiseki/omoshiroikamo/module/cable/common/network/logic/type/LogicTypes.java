package ruiseki.omoshiroikamo.module.cable.common.network.logic.type;

public class LogicTypes {

    public static final LogicType<Void> NULL = LogicTypeRegistry.register(new LogicType<>("null") {

    });

    public static final LogicType<Boolean> BOOLEAN = LogicTypeRegistry.register(new LogicType<>("boolean") {

    });

    public static final LogicType<Integer> INT = LogicTypeRegistry.register(new LogicType<>("int") {

        @Override
        public boolean isNumeric() {
            return true;
        }

    });

    public static final LogicType<Long> LONG = LogicTypeRegistry.register(new LogicType<>("long") {

        @Override
        public boolean isNumeric() {
            return true;
        }

    });

    public static final LogicType<Float> FLOAT = LogicTypeRegistry.register(new LogicType<>("float") {

        @Override
        public boolean isNumeric() {
            return true;
        }

    });

    public static final LogicType<Double> DOUBLE = LogicTypeRegistry.register(new LogicType<>("double") {

        @Override
        public boolean isNumeric() {
            return true;
        }

    });

    public static final LogicType<String> STRING = LogicTypeRegistry.register(new LogicType<>("string") {

    });
}
