package ruiseki.omoshiroikamo.module.cable.common.variable;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypeRegistry;
import ruiseki.omoshiroikamo.module.cable.common.util.LogicNBTUtils;

public class ItemVariableCard extends ItemOK {

    public ItemVariableCard() {
        super(ModObject.itemVariableCard.unlocalisedName);
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {

        if (ItemNBTUtils.getCompound(stack, "Logic", false) == null) {
            list.add("§7No logic data");
            return;
        }

        NBTTagCompound logic = ItemNBTUtils.getCompound(stack, "Logic", false);

        String type = logic.getString("Type");
        list.add("§6Logic Type: §f" + type);

        switch (type) {
            case "READER" -> addReaderInfo(logic, list);
            case "OP" -> addOperatorInfo(logic, list);
            case "LITERAL" -> LogicNBTUtils.addLiteralTooltip(logic, list);

            default -> list.add("§cUnknown logic");
        }
    }

    private void addReaderInfo(NBTTagCompound tag, List<String> list) {

        list.add("§7Reader:");

        list.add(" §fKey: §a" + tag.getString("Key"));

        int x = tag.getInteger("X");
        int y = tag.getInteger("Y");
        int z = tag.getInteger("Z");
        list.add(" §fPos: §b" + x + ", " + y + ", " + z);

        int side = tag.getByte("Side");
        list.add(" §fSide: §e" + ForgeDirection.getOrientation(side));
        String value = tag.getString("Value");
        list.add(
            " §fValue: §e" + LogicTypeRegistry.get(value)
                .toString());

    }

    private void addOperatorInfo(NBTTagCompound tag, List<String> list) {

        list.add("§7Operator:");

        list.add(" §fOp: §a" + tag.getString("Op"));

        if (tag.hasKey("Children")) {
            int count = tag.getTagList("Children", 10)
                .tagCount();
            list.add(" §fInputs: §b" + count);
        }
    }
}
