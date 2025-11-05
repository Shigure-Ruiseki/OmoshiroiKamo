package ruiseki.omoshiroikamo.common.item.chicken;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
        super(ModObject.itemAnalyzer.unlocalisedName);
        setMaxStackSize(1);
        setMaxDamage(238);
    }

    public static ItemAnalyzer create() {
        return new ItemAnalyzer();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibResources.PREFIX_MOD + "analyzer");
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
