package ruiseki.omoshiroikamo.module.cable.common.variable;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;
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

        LogicNBTUtils.addTooltip(stack, list);
    }
}
