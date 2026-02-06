package ruiseki.omoshiroikamo.module.ids.common.variable;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;
import ruiseki.omoshiroikamo.module.ids.common.util.LogicNBTUtils;

public class ItemVariableCard extends ItemOK {

    public ItemVariableCard() {
        super(ModObject.itemVariableCard.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("ids/variable");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if (ItemNBTUtils.getCompound(stack, "Logic", false) == null) {
            list.add("§7No logic data");
            return;
        }

        LogicNBTUtils.addTooltip(stack, list);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);
        if (!ItemNBTUtils.verifyExistance(stack, "Logic")) {
            ItemNBTUtils.setCompound(stack, "Logic", LogicNBTUtils.booleanLiteral(true));
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int slot, boolean isHeld) {
        super.onUpdate(stack, worldIn, entityIn, slot, isHeld);
        if (!ItemNBTUtils.verifyExistance(stack, "Logic")) {
            ItemNBTUtils.setCompound(stack, "Logic", LogicNBTUtils.booleanLiteral(true));
        }
    }
}
