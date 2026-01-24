package ruiseki.omoshiroikamo.module.cable.common.network.logic.node;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.module.cable.common.network.logic.key.LogicKey;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.key.LogicKeyRegistry;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator.ILogicOperator;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.node.operator.OperatorRegistry;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.util.LogicNBTUtils;

public class LogicNodeFactory {

    private LogicNodeFactory() {}

    public static ILogicNode fromNBT(NBTTagCompound tag) {
        if (tag == null || !tag.hasKey("Type")) {
            return null;
        }

        String type = tag.getString("Type");

        return switch (type) {
            case "READER" -> readReader(tag);
            case "LITERAL" -> readLiteral(tag);
            case "OP" -> readOperator(tag);
            default -> null;
        };
    }

    private static ILogicNode readReader(NBTTagCompound tag) {

        LogicKey key = LogicKeyRegistry.get(tag.getString("Key"));
        if (key == null) return null;

        int x = tag.getInteger("X");
        int y = tag.getInteger("Y");
        int z = tag.getInteger("Z");
        ForgeDirection side = ForgeDirection.getOrientation(tag.getByte("Side"));

        return new ReaderNode(new ReaderRef(x, y, z, side), key);
    }

    private static ILogicNode readLiteral(NBTTagCompound tag) {
        ILogicValue value = LogicNBTUtils.readLiteralValue(tag);
        return value != null ? new LiteralNode(value) : null;
    }

    private static ILogicNode readOperator(NBTTagCompound tag) {

        String opId = tag.getString("Op");
        ILogicOperator op = OperatorRegistry.get(opId);

        if (op == null) return null;

        NBTTagList list = tag.getTagList("Children", 10);
        ILogicNode[] children = new ILogicNode[list.tagCount()];

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound childTag = list.getCompoundTagAt(i);
            children[i] = fromNBT(childTag);
        }

        return new OperatorNode(op, children);
    }

}
