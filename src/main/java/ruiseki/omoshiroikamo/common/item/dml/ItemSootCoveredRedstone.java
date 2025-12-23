package ruiseki.omoshiroikamo.common.item.dml;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.KeyboardUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

@EventBusSubscriber
public class ItemSootCoveredRedstone extends ItemOK {

    public ItemSootCoveredRedstone() {
        super(ModObject.itemSootCoveredRedstone.unlocalisedName);
        setMaxStackSize(64);
        setTextureName("soot_covered_redstone");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        String redstone = ChatFormatting.RED + LibMisc.LANG.localize("item.redstone.name") + ChatFormatting.GRAY;
        String coal = ChatFormatting.RESET + LibMisc.LANG.localize("tile.blockCoal.name") + ChatFormatting.GRAY;
        String leftClick = KeyboardUtils.getAttackKeyName();
        list.add(LibMisc.LANG.localize("tooltip.soot_covered_redstone", redstone, coal, leftClick));
    }

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return BackportConfigs.useDML;
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            return;
        }

        ItemStack stack = event.entityPlayer.getHeldItem();
        if (stack == null) return;

        Item heldItem = stack.getItem();
        if (heldItem != Items.redstone) return;

        Block clickedBlock = event.world.getBlock(event.x, event.y, event.z);
        if (clickedBlock != Blocks.coal_block) return;

        Vec3 hitVec = Vec3.createVectorHelper(event.x + 0.5, event.y + 0.5, event.z + 0.5);

        if (!event.world.isRemote) {
            ItemStack output = ModItems.SOOT_COVERED_REDSTONE.newItemStack();

            EntityItem entityItem = new EntityItem(event.world, hitVec.xCoord, hitVec.yCoord, hitVec.zCoord, output);

            event.world.spawnEntityInWorld(entityItem);

            stack.stackSize--;
            if (stack.stackSize <= 0) {
                event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, null);
            }
        }

        event.setCanceled(true);
    }

}
