package ruiseki.omoshiroikamo.module.dml.common.item.deepLearner;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class ItemDeepLearner extends ItemOK implements IGuiHolder<PlayerInventoryGuiData> {

    public ItemDeepLearner() {
        super(ModObject.itemDeepLearner.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("dml/deep_learner");
    }

    @Override
    public ModularScreen createScreen(PlayerInventoryGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(LibMisc.MOD_ID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        EntityPlayer player = data.getPlayer();
        ItemStack stack = data.getUsedItemStack();
        DeepLearnerHandler handler = new DeepLearnerHandler(stack.copy());
        ModularPanel panel = DeepLearnerPanel.defaultPanel(syncManager, settings, player, handler, data.getSlotIndex());

        syncManager.onCommonTick(() -> {
            ItemStack used = data.getUsedItemStack();
            if (used != null) {
                used.setTagCompound(
                    handler.getDeepLearner()
                        .getTagCompound());
            }
        });

        syncManager.addCloseListener(player1 -> {
            if (!(player1 instanceof EntityPlayerMP)) {
                return;
            }

            ItemStack used = data.getUsedItemStack();
            NBTTagCompound tag = handler.getDeepLearner()
                .getTagCompound();

            if (used != null) {
                used.setTagCompound(tag);
            } else {
                ItemStack slotStack = player1.inventory.getStackInSlot(data.getSlotIndex());
                if (slotStack != null) {
                    slotStack.setTagCompound(tag);
                }
            }
        });

        return panel;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
        super.onUpdate(stack, world, entity, slot, isHeld);
        if (!stack.hasTagCompound()) {
            DeepLearnerHandler cap = new DeepLearnerHandler(stack.copy());
            cap.writeToItem();
            stack.setTagCompound(
                cap.getDeepLearner()
                    .getTagCompound());
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);
        if (!stack.hasTagCompound()) {
            DeepLearnerHandler cap = new DeepLearnerHandler(stack.copy());
            cap.writeToItem();
            stack.setTagCompound(
                cap.getDeepLearner()
                    .getTagCompound());
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && stack != null && stack.getTagCompound() != null) {
            GuiFactories.playerInventory()
                .openFromMainHand(player);
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        // NonNullList<ItemStack> internalDataModels = DataModel.getValidFromList(getContainedItems(stack));
        //
        // list.add(new ChatComponentText("Will display a §bHUD§7 when in mainhand or offhand"));
        // list.add(new ChatComponentText("and populated with data models"));
        //
        // if(internalDataModels.size() > 0) {
        // if(!KeyboardHelper.isHoldingShift()) {
        // list.add(new ChatComponentText(I18n.format("deepmoblearning.holdshift")));
        // } else {
        // list.add(new ChatComponentText("Contains the following models"));
        // for (ItemStack dataModel : internalDataModels) {
        // if (dataModel.getItem() instanceof ItemDataModel) {
        // list.add(new ChatComponentText(DataModel.getTierName(dataModel, false) + " " + dataModel.getDisplayName()));
        // }
        // }
        // }
        // }
    }
}
