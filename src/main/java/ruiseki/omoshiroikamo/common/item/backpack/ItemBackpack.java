package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PlayerInventoryGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.energy.IEnergyItem;
import ruiseki.omoshiroikamo.api.energy.PowerDisplayUtil;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.IBaubleRender;
import ruiseki.omoshiroikamo.client.render.item.backpack.BackpackRenderer;
import ruiseki.omoshiroikamo.common.entity.EntityImmortalItem;
import ruiseki.omoshiroikamo.common.item.ItemBauble;
import ruiseki.omoshiroikamo.common.item.upgrade.EnergyUpgrade;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class ItemBackpack extends ItemBauble implements IEnergyItem, IGuiHolder<PlayerInventoryGuiData>, IBaubleRender {

    @SideOnly(Side.CLIENT)
    private static BackpackRenderer model;

    public ItemBackpack() {
        super(ModObject.itemBackPack);
        setMaxStackSize(1);
        setNoRepair();
        disableRightClickEquip();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
        list.add(new ItemStack(item, 1, 4));
        list.add(new ItemStack(item, 1, 5));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        String base = super.getUnlocalizedName(stack);
        String type;
        if (meta == 1) {
            type = "Copper";
        } else if (meta == 2) {
            type = "Iron";
        } else if (meta == 3) {
            type = "Gold";
        } else if (meta == 4) {
            type = "Diamond";
        } else if (meta == 5) {
            type = LibMods.EtFuturum.isLoaded() ? "Netherite" : "Obsidian";
        } else {
            type = "Starter";
        }
        return base + "." + type;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote && !player.isSneaking()) {
            GuiFactories.playerInventory()
                .openFromMainHand(player);
        }
        return super.onItemRightClick(itemStackIn, worldIn, player);
    }

    @Override
    public ModularPanel buildUI(PlayerInventoryGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new BackpackGui(data.getPlayer(), data, syncManager, settings, this);
    }

    // Energy
    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return EnergyUpgrade.receiveEnergy(container, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return EnergyUpgrade.extractEnergy(container, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return EnergyUpgrade.getEnergyStored(container);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return EnergyUpgrade.getMaxEnergyStored(container);
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        super.addInformation(itemstack, entityplayer, list, flag);
        if (GuiScreen.isShiftKeyDown()) {
            EnergyUpgrade up = EnergyUpgrade.loadFromItem(itemstack);
            if (up != null) {
                list.add(PowerDisplayUtil.formatStoredPower(up.getEnergy(), up.getCapacity()));
            }
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        return new EntityImmortalItem(world, location, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, RenderPlayerEvent event, IBaubleRender.RenderType type) {
        if (stack == null || type != IBaubleRender.RenderType.BODY) {
            return;
        }

        if (model == null) {
            model = new BackpackRenderer();
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(0F, 0.75F, 0.3F);
        GL11.glScalef(0.85F, 0.85F, 0.85F);
        GL11.glRotatef(180f, 1f, 0f, 0f);
        model.renderModel(stack);

        GL11.glPopMatrix();
    }

    @Override
    public String[] getBaubleTypes(ItemStack itemstack) {
        return new String[] { "body" };
    }
}
