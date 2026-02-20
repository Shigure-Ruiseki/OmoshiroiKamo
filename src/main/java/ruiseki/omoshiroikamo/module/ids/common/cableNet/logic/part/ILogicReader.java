package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.part;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.core.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.key.LogicKey;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.ILogicValue;

public interface ILogicReader {

    ILogicValue read(LogicKey key);

    default void writeLogicToCard(ItemStack card, LogicKey key, ICableNode node) {
        NBTTagCompound logic = new NBTTagCompound();

        logic.setString("Type", "READER");

        logic.setString("Reader", node.getId());

        logic.setInteger("X", node.getPos().x);
        logic.setInteger("Y", node.getPos().y);
        logic.setInteger("Z", node.getPos().z);
        logic.setByte(
            "Side",
            (byte) node.getSide()
                .ordinal());

        logic.setString("Key", key.getId());
        logic.setString(
            "ValueType",
            key.getDefaultType()
                .getId());

        ItemNBTUtils.setCompound(card, "Logic", logic);
    }

}
