package ruiseki.omoshiroikamo.common.item.dml;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.KeyboardUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class ItemCreativeModelLearner extends ItemOK {

    public ItemCreativeModelLearner() {
        super(ModObject.itemCreativeModelLearner.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("creative_model_learner");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

        if (world.isRemote) {
            if (KeyboardUtils.isHoldingShift()) {
                // DeepMobLearning.network.sendToServer(new LevelUpModelMessage(0));
            } else if (KeyboardUtils.isHoldingCTRL()) {
                // DeepMobLearning.network.sendToServer(new LevelUpModelMessage(1));
            }
        }

        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if (!KeyboardUtils.isHoldingShift()) {
            list.add(LibMisc.LANG.localize("tooltip.holdShift"));
        } else {
            list.add("A creative item that levels up data models inside the Deep Learner.");
            list.add("§r§oSHIFT§r§7 + §r§oRIGHT§r§7 click to increase tier.§r");
            list.add("§r§oCTRL§r§7 + §r§oRIGHT§r§7 click to simulate kills.§r");
        }
    }
}
