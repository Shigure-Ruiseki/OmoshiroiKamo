package ruiseki.omoshiroikamo.common.item.dml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.dml.DataModel;
import ruiseki.omoshiroikamo.api.entity.dml.DataModelExperience;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.KeyboardUtils;
import ruiseki.omoshiroikamo.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class ItemDataModel extends ItemOK {

    private final Map<Integer, IIcon> icons = new HashMap<>();

    public ItemDataModel() {
        super(ModObject.itemDataModel.unlocalisedName);
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (ModelRegistryItem model : ModelRegistry.INSTANCE.getItems()) {
            list.add(new ItemStack(this, 1, model.getId()));
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        ModelRegistryItem model = DataModel.getDataFromStack(stack);
        if (model != null) {
            NBTTagCompound tag = DataModel.createTagCompound(stack);
            stack.setTagCompound(tag);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        ModelRegistryItem model = ModelRegistry.INSTANCE.getByType(stack.getItemDamage());
        if (model == null) {
            return super.getItemStackDisplayName(stack);
        }
        return LibMisc.LANG.localize(model.getItemName()) + ": " + DataModel.getTierName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        for (ModelRegistryItem model : ModelRegistry.INSTANCE.getItems()) {
            int type = model.getId();

            String iconName = model.getTexture();
            IIcon icon = reg.registerIcon(iconName);
            icons.put(type, icon);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return icons.get(meta);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        TooltipUtils builder = TooltipUtils.builder();

        if (DataModel.hasExtraTooltip(stack)) {
            builder.add(DataModel.getExtraTooltip(stack));
        }

        if (!KeyboardUtils.isHoldingShift()) {
            builder.addLang("tooltip.holdshift");
        } else {
            int tier = DataModel.getTier(stack);
            builder.addLang("tooltip.data_model.tier", LibMisc.LANG.localize(DataModelExperience.getTierName(tier)));
            if (tier != DataModelExperience.getMaxTier()) {
                builder.addLang(
                    "tooltip.data_model.data_collected",
                    DataModel.getCurrentTierSimulationCountWithKills(stack),
                    DataModel.getTierRoof(stack));
                builder.addLang("tooltip.data_model.kill_multiplier", DataModel.getKillMultiplier(stack));
            }
            builder.addLang("tooltip.data_model.rf_cost", DataModel.getSimulationTickCost(stack));
            builder.addLang("tooltip.data_model.type", DataModel.getMatterTypeName(stack));

            if (!DataModel.canSimulate(stack)) {
                builder.addLang("tooltip.data_model.cannot_simulate");
            }
        }

        list.addAll(builder.build());
    }
}
