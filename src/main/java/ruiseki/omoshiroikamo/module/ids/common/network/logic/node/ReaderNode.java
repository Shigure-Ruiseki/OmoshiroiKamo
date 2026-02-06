package ruiseki.omoshiroikamo.module.ids.common.network.logic.node;

import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.key.LogicKey;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;

public class ReaderNode implements ILogicNode {

    private final ReaderRef ref;
    private final LogicKey key;

    public ReaderNode(ReaderRef ref, LogicKey key) {
        this.ref = ref;
        this.key = key;
    }

    public ReaderRef getRef() {
        return ref;
    }

    public LogicKey getKey() {
        return key;
    }

    @Override
    public ILogicValue evaluate(EvalContext ctx) {
        return ctx.network()
            .readAt(ref.x(), ref.y(), ref.z(), ref.side(), key);
    }
}
