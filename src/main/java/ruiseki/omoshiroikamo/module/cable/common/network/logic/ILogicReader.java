package ruiseki.omoshiroikamo.module.cable.common.network.logic;

import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.key.LogicKey;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;

public interface ILogicReader {

    List<LogicKey> getLogics();

    ILogicValue read(LogicKey key);
}
