package ruiseki.omoshiroikamo.module.ids.common.cableNet;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.key.LogicKey;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.key.LogicKeyRegistry;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type.LogicTypeRegistry;
import ruiseki.omoshiroikamo.module.ids.common.util.LogicNBTUtils;

public class ItemVariableCard extends ItemOK {

    public ItemVariableCard() {
        super(ModObject.itemVariableCard.unlocalisedName);
        setMaxStackSize(1);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibResources.PREFIX_MOD + "ids/variable");

        IconRegistry.addIcon("valuetype.any", register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/any"));
        IconRegistry.addIcon("valuetype.named", register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/named"));
        IconRegistry
            .addIcon("valuetype.nullable", register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/nullable"));
        IconRegistry
            .addIcon("valuetype.number", register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/number"));
        IconRegistry
            .addIcon("valuetype.object", register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/object"));
        IconRegistry
            .addIcon("valuetype.operator", register.registerIcon(LibResources.PREFIX_MOD + "ids/valuetype/operator"));

        LogicTypeRegistry.registerIcons(register);
        LogicKeyRegistry.registerIcons(register);

    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 3;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (!ItemNBTUtils.verifyExistance(stack, "Logic")) {
            return this.itemIcon;
        }

        NBTTagCompound logic = ItemNBTUtils.getCompound(stack, "Logic", false);

        if (pass == 1) {
            return getValueTypeIcon(logic);
        }

        if (pass == 2) {
            return getKeyIcon(logic);
        }

        return this.itemIcon;
    }

    @SideOnly(Side.CLIENT)
    private IIcon getValueTypeIcon(NBTTagCompound logic) {
        if (logic == null) return this.itemIcon;

        String logicType = logic.getString("Type");

        if ("OP".equals(logicType)) {
            IIcon opIcon = IconRegistry.getIcon("valuetype.operator");
            return opIcon != null ? opIcon : this.itemIcon;
        }

        String valueType = logic.getString("ValueType");
        if (valueType == null || valueType.isEmpty()) {
            IIcon anyIcon = IconRegistry.getIcon("valuetype.any");
            return anyIcon != null ? anyIcon : this.itemIcon;
        }

        LogicType<?> type = LogicTypeRegistry.get(valueType);
        IIcon icon = type != null ? IconRegistry.getIcon("valuetype." + type.getId()) : null;

        if (icon != null) return icon;
        if (type != null && type.isNumeric()) {
            IIcon numberIcon = IconRegistry.getIcon("valuetype.number");
            return numberIcon != null ? numberIcon : this.itemIcon;
        }

        IIcon objectIcon = IconRegistry.getIcon("valuetype.object");
        return objectIcon != null ? objectIcon : this.itemIcon;
    }

    @SideOnly(Side.CLIENT)
    private IIcon getKeyIcon(NBTTagCompound logic) {
        if (logic == null) return this.itemIcon;

        if (!"READER".equals(logic.getString("Type"))) {
            return getValueTypeIcon(logic);
        }

        LogicKey key = LogicKeyRegistry.get(logic.getString("Key"));
        if (key != null) {
            IIcon icon = IconRegistry.getIcon("logickey." + key.getId());
            if (icon != null) return icon;
        }

        return getValueTypeIcon(logic);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if (ItemNBTUtils.getCompound(stack, "Logic", false) == null) {
            list.add(LibMisc.LANG.localize("tooltip.ids.variable_card.empty"));
            return;
        }

        LogicNBTUtils.addTooltip(stack, list);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);
        if (!ItemNBTUtils.verifyExistance(stack, "Logic")) {
            ItemNBTUtils.setCompound(stack, "Logic", LogicNBTUtils.booleanLiteral(true));
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int slot, boolean isHeld) {
        super.onUpdate(stack, worldIn, entityIn, slot, isHeld);
        if (!ItemNBTUtils.verifyExistance(stack, "Logic")) {
            ItemNBTUtils.setCompound(stack, "Logic", LogicNBTUtils.booleanLiteral(true));
        }
    }
}
