package ruiseki.omoshiroikamo.module.chickens.common.item;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.api.entity.IMobStats;
import ruiseki.omoshiroikamo.api.entity.MobTrait;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class ItemAnalyzer extends ItemOK {

    public ItemAnalyzer() {
        super(ModObject.itemAnalyzer);
        setMaxStackSize(1);
        setMaxDamage(238);
        setTextureName("chicken/analyzer");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if (target.worldObj.isRemote || !(target instanceof IMobStats stats)) {
            return false;
        }
        stats.setStatsAnalyzed(true);

        ChatComponentText chickenName = new ChatComponentText(target.getCommandSenderName());
        chickenName.getChatStyle()
            .setBold(true)
            .setColor(EnumChatFormatting.GOLD);
        player.addChatMessage(chickenName);

        Map<MobTrait, Integer> traits = stats.getTraits();
        if (traits != null && !traits.isEmpty()) {

            for (Map.Entry<MobTrait, Integer> entry : traits.entrySet()) {
                MobTrait trait = entry.getKey();
                int level = entry.getValue();

                ChatComponentText traitMessage = new ChatComponentText("- " + trait.getName() + " Lv." + level);

                traitMessage.getChatStyle()
                    .setColor(EnumChatFormatting.AQUA);

                player.addChatMessage(traitMessage);
            }
        }

        player.addChatMessage(new ChatComponentText("Growth: " + stats.getGrowth()));
        player.addChatMessage(new ChatComponentText("Strength: " + stats.getStrength()));
        player.addChatMessage(new ChatComponentText("Gain: " + stats.getGain()));

        stack.damageItem(1, target);
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        TooltipUtils builder = TooltipUtils.builder();
        builder.addLang(LibResources.TOOLTIP + "analyzer.l1");
        builder.addLang(LibResources.TOOLTIP + "analyzer.l2");
        list.addAll(builder.build());
    }
}
