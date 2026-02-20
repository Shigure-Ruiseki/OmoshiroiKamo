package ruiseki.omoshiroikamo.module.dml.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.util.KeyboardUtils;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class ItemCreativeModelLearner extends ItemOK {

    public ItemCreativeModelLearner() {
        super(ModObject.itemCreativeModelLearner.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("dml/creative_model_learner");
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
            list.add(LibMisc.LANG.localize("tooltip.creative_model_learner.desc"));
            list.add(LibMisc.LANG.localize("tooltip.creative_model_learner.shift"));
            list.add(LibMisc.LANG.localize("tooltip.creative_model_learner.ctrl"));
        }
    }
}
