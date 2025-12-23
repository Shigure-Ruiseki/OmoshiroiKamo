package ruiseki.omoshiroikamo.common.item.deepMobLearning;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.mojang.realmsclient.gui.ChatFormatting;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.KeyboardUtils;
import ruiseki.omoshiroikamo.common.util.StringUtils;
import ruiseki.omoshiroikamo.common.util.TooltipUtils;

public class ItemTrialKey extends ItemOK {

    public ItemTrialKey() {
        super(ModObject.itemTrialKey.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("trial_key");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return StringUtils.getFormattedString(super.getItemStackDisplayName(stack), ChatFormatting.AQUA);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        TooltipUtils builder = TooltipUtils.builder();

        if (!KeyboardUtils.isHoldingShift()) {
            builder.addLang("tooltip.holdshift");
        } else {
            // AttunementData attunementData = TrialKeyHelper.getAttunement(stack).orElse(null);
            // if (attunementData == null) {
            // builder.add(StringUtils.getFormattedString(LibMisc.LANG.localize("tooltip.trial_key.tooltip.not_attuned"),
            // ChatFormatting.GRAY));
            // builder.add(StringUtils.getFormattedString(LibMisc.LANG.localize("tooltip.trial_key.tooltip.available_attunements"),
            // ChatFormatting.AQUA));
            //
            // ImmutableList<String> availableTrials = MetadataManager.getAvailableTrials();
            // for (String trial : availableTrials)
            // builder.add(StringHelper.getFormattedString(" - " + trial, ChatFormatting.WHITE));
            // } else {
            // String mobName = StringHelper.getFormattedString(attunementData.getMobDisplayName(),
            // ChatFormatting.GRAY);
            // builder.add(LibMisc.LANG.localize("tooltip.trial_key.tooltip.attunement", mobName));
            //
            // String tierName = attunementData.getTierDisplayNameFormatted();
            // builder.add(LibMisc.LANG.localize("tooltip.trial_key.tooltip.tier",tierName));
            //
            // ImmutableList<TrialAffix> affixes = TrialKeyHelper.getAffixes(stack, BlockPos.ORIGIN, worldIn);
            // if (affixes.isEmpty()) {
            // builder.add(LibMisc.LANG.localize("tooltip.trial_key.tooltip.affixes_faulty"));
            // } else {
            // StringBuilder affixList = new StringBuilder();
            // for (int i = 0; i < affixes.size(); i++) {
            // affixList.append(affixes.get(i).getAffixName()).append(i == affixes.size() - 1 ? " " : ", ");
            // }
            // builder.add(LibMisc.LANG.localize("tooltip.trial_key.tooltip.affixes", affixList.toString()));
            // builder.add(LibMisc.LANG.localize("tooltip.trial_key.tooltip.affix_info"));
            // }
            // }
        }
    }
}
