package ruiseki.omoshiroikamo.module.backpack.common.item.wrapper;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;

public class VoidUpgradeWrapper extends BasicUpgradeWrapper implements IVoidUpgrade {

    public VoidUpgradeWrapper(ItemStack upgrade) {
        super(upgrade);
    }

    @Override
    public boolean canVoid(ItemStack stack) {
        return checkFilter(stack);
    }

    @Override
    public VoidType getVoidType() {
        int ordinal = ItemNBTUtils.getInt(upgrade, VOID_TYPE_TAG, VoidType.OVERFLOW.ordinal());
        VoidType[] types = VoidType.values();
        if (ordinal < 0 || ordinal >= types.length) {
            return VoidType.OVERFLOW;
        }
        return types[ordinal];
    }

    @Override
    public void setVoidType(VoidType type) {
        if (type == null) {
            type = VoidType.OVERFLOW;
        }
        ItemNBTUtils.setInt(upgrade, VOID_TYPE_TAG, type.ordinal());
    }

    @Override
    public VoidInput getVoidInput() {
        int ordinal = ItemNBTUtils.getInt(upgrade, VOID_INPUT_TAG, VoidInput.ALL.ordinal());
        VoidInput[] types = VoidInput.values();
        if (ordinal < 0 || ordinal >= types.length) {
            return VoidInput.ALL;
        }
        return types[ordinal];
    }

    @Override
    public void setVoidInput(VoidInput type) {
        if (type == null) {
            type = VoidInput.ALL;
        }
        ItemNBTUtils.setInt(upgrade, VOID_INPUT_TAG, type.ordinal());
    }
}
