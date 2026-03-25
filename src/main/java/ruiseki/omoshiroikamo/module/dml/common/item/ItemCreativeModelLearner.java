package ruiseki.omoshiroikamo.module.dml.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.helper.KeyboardHelpers;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.item.ItemOK;

public class ItemCreativeModelLearner extends ItemOK {

    public ItemCreativeModelLearner() {
        super(ModObject.CREATIVE_MODEL_LEARNER.name);
        setMaxStackSize(1);
        setTextureName("dml/creative_model_learner");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

        if (world.isRemote) {
            if (KeyboardHelpers.isHoldingShift()) {
                // DeepMobLearning.network.sendToServer(new LevelUpModelMessage(0));
            } else if (KeyboardHelpers.isHoldingCTRL()) {
                // DeepMobLearning.network.sendToServer(new LevelUpModelMessage(1));
            }
        }

        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if (!KeyboardHelpers.isHoldingShift()) {
            list.add(LangHelpers.localize("tooltip.holdShift"));
        } else {
            list.add(LangHelpers.localize("tooltip.creative_model_learner.desc"));
            list.add(LangHelpers.localize("tooltip.creative_model_learner.shift"));
            list.add(LangHelpers.localize("tooltip.creative_model_learner.ctrl"));
        }
    }
}
