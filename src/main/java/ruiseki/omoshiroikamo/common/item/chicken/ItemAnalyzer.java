package ruiseki.omoshiroikamo.common.item.chicken;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.api.entity.IMobStats;
import ruiseki.omoshiroikamo.api.entity.MobTrait;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemAnalyzer extends ItemOK {

    public ItemAnalyzer() {
        super(ModObject.itemAnalyzer);
        setMaxStackSize(1);
        setMaxDamage(238);
        setTextureName("analyzer");
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

        List<MobTrait> traits = stats.getTraits();
        if (traits != null && !traits.isEmpty()) {
            for (MobTrait trait : traits) {
                ChatComponentText traitMessage = new ChatComponentText("- " + trait.getName());
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
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "analyzer.l1"));
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "analyzer.l2"));
    }
}
