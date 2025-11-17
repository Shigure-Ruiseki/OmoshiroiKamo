package ruiseki.omoshiroikamo.common.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.AnvilUpdateEvent;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.item.IAnvilUpgrade;
import ruiseki.omoshiroikamo.common.item.upgrade.EnergyUpgrade;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

@EventBusSubscriber
@SuppressWarnings("unused")
public class ManaAnvilRecipe {

    @Getter
    private static final List<IAnvilUpgrade> UPGRADES = new ArrayList<>();

    static {
        UPGRADES.add(EnergyUpgrade.ENERGY_TIER_ONE);
        UPGRADES.add(EnergyUpgrade.ENERGY_TIER_TWO);
        UPGRADES.add(EnergyUpgrade.ENERGY_TIER_THREE);
        UPGRADES.add(EnergyUpgrade.ENERGY_TIER_FOUR);
        UPGRADES.add(EnergyUpgrade.ENERGY_TIER_FIVE);
    }

    @SubscribeEvent
    public static void handleAnvilEvent(AnvilUpdateEvent event) {
        if (event.left == null || event.right == null) {
            return;
        }
        handleUpgrade(event);
    }

    private static void handleUpgrade(AnvilUpdateEvent event) {
        for (IAnvilUpgrade upgrade : UPGRADES) {
            if (upgrade.isUpgradeItem(event.right) && upgrade.canAddToItem(event.left)) {
                ItemStack res = new ItemStack(event.left.getItem(), 1, event.left.getItemDamage());
                if (event.left.stackTagCompound != null) {
                    res.stackTagCompound = (NBTTagCompound) event.left.stackTagCompound.copy();
                }
                upgrade.writeToItem(res);
                event.output = res;
                event.cost = upgrade.getLevelCost();
                return;
            }
        }
    }

    public static void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        List<IAnvilUpgrade> applyableUPGRADES = new ArrayList<IAnvilUpgrade>();
        for (IAnvilUpgrade upgrade : UPGRADES) {
            if (upgrade.hasUpgrade(itemstack)) {
                upgrade.addInformation(itemstack, entityplayer, list, flag);
            } else if (upgrade.canAddToItem(itemstack)) {
                applyableUPGRADES.add(upgrade);
            }
        }

        if (!applyableUPGRADES.isEmpty()) {
            list.add(EnumChatFormatting.YELLOW + LibMisc.LANG.localize("tooltip.anvilUPGRADES") + " ");
            for (IAnvilUpgrade up : applyableUPGRADES) {
                list.add(
                    EnumChatFormatting.DARK_AQUA + ""
                        + ""
                        + LibMisc.LANG.localizeExact(up.getUnlocalizedName() + ".name")
                        + ": ");
                list.add(
                    EnumChatFormatting.DARK_AQUA + ""
                        + EnumChatFormatting.ITALIC
                        + "  "
                        + up.getUpgradeItemName()
                        + " + "
                        + up.getLevelCost()
                        + " "
                        + LibMisc.LANG.localize("tooltip.lvs"));
            }
        }
    }

    public static Iterator<IAnvilUpgrade> recipeIterator() {
        return ImmutableList.copyOf(UPGRADES)
            .iterator();
    }
}
