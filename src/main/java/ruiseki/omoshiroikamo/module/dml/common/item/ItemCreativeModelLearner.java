package ruiseki.omoshiroikamo.module.dml.common.item;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.ItemOK;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;

public class ItemCreativeModelLearner extends ItemOK {

    public ItemCreativeModelLearner() {
        super(ModObject.CREATIVE_MODEL_LEARNER.name);
        setMaxStackSize(1);
        setTextureName(Reference.PREFIX_MOD + "dml/creative_model_learner");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

        if (world.isRemote) {
            if (GuiScreen.isShiftKeyDown()) {
                // DeepMobLearning.network.sendToServer(new LevelUpModelMessage(0));
            } else if (GuiScreen.isCtrlKeyDown()) {
                // DeepMobLearning.network.sendToServer(new LevelUpModelMessage(1));
            }
        }

        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if (!GuiScreen.isShiftKeyDown()) {
            list.add(LangHelpers.localize("tooltip.holdShift"));
        } else {
            list.add(LangHelpers.localize("tooltip.creative_model_learner.desc"));
            list.add(LangHelpers.localize("tooltip.creative_model_learner.shift"));
            list.add(LangHelpers.localize("tooltip.creative_model_learner.ctrl"));
        }
    }
}
