package ruiseki.omoshiroikamo.common.item.chicken;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;
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
        if (target.worldObj.isRemote || !(target instanceof EntityChickensChicken chicken)) {
            return false;
        }

        chicken.setStatsAnalyzed(true);

        ChatComponentText chickenName = new ChatComponentText(chicken.getCommandSenderName());
        chickenName.getChatStyle()
            .setBold(true)
            .setColor(EnumChatFormatting.GOLD);
        player.addChatMessage(chickenName);

        if (!chicken.isChild()) {
            int layProgress = chicken.getLayProgress();
            if (layProgress <= 0) {
                player.addChatMessage(new ChatComponentTranslation("tooltip.entity.nextEggSoon"));
            } else {
                player.addChatMessage(new ChatComponentTranslation("tooltip.entity.layProgress", layProgress));
            }
        }

        stack.damageItem(1, target);
        return true;
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "analyzer.l1"));
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "analyzer.l2"));
    }
}
