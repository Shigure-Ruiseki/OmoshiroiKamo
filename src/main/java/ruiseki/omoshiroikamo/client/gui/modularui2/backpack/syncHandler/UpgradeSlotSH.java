package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackInventoryHelper;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackPanel;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedFeedingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IAdvancedFilterable;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IBasicFilterable;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.ICraftingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IFilterUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IMagnetUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IToggleable;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IVoidUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.UpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.UpgradeWrapperFactory;

public class UpgradeSlotSH extends ItemSlotSH {

    public static final int UPDATE_UPGRADE_TAB_STATE = 6;
    public static final int UPDATE_UPGRADE_TOGGLE = 7;
    public static final int UPDATE_BASIC_FILTERABLE = 8;
    public static final int UPDATE_ADVANCED_FILTERABLE = 9;
    public static final int UPDATE_ADVANCED_FEEDING = 10;
    public static final int UPDATE_FILTER = 11;
    public static final int UPDATE_MAGNET = 12;
    public static final int UPDATE_CRAFTING = 13;
    public static final int UPDATE_VOID = 14;
    public static final int UPDATE_CRAFTING_R = 15;
    public static final int UPDATE_CRAFTING_G = 16;
    public static final int UPDATE_CRAFTING_C = 17;

    private final BackpackHandler handler;
    private final BackpackPanel panel;

    public UpgradeSlotSH(BackpackPanel panel, BackpackHandler handler, ModularSlot slot) {
        super(slot);
        this.panel = panel;
        this.handler = handler;
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {

        switch (id) {
            case UPDATE_UPGRADE_TAB_STATE:
                updateTabState(buf);
                break;
            case UPDATE_UPGRADE_TOGGLE:
                updateToggleable();
                break;
            case UPDATE_BASIC_FILTERABLE:
                updateBasicFilterable(buf);
                break;
            case UPDATE_ADVANCED_FILTERABLE:
                updateAdvancedFilterable(buf);
                break;
            case UPDATE_ADVANCED_FEEDING:
                updateAdvanceFeedingUpgrade(buf);
                break;
            case UPDATE_FILTER:
                updateFilterUpgrade(buf);
                break;
            case UPDATE_MAGNET:
                updateMagnetUpgrade(buf);
                break;
            case UPDATE_CRAFTING:
                updateCraftingUpgrade(buf);
                break;
            case UPDATE_VOID:
                updateVoidUpgrade(buf);
                break;
            case UPDATE_CRAFTING_R:
                updateRotated(buf);
                break;
            case UPDATE_CRAFTING_G:
                updateGrid(buf);
                break;
            case UPDATE_CRAFTING_C:
                updateClear(buf);
                break;

            default:
                super.readOnServer(id, buf);
                break;
        }
        handler.writeToItem();
    }

    private void updateTabState(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (wrapper == null) {
            return;
        }
        wrapper.setTabOpened(buf.readBoolean());
    }

    private void updateToggleable() {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof IToggleable toggleWrapper)) {
            return;
        }
        toggleWrapper.toggle();
    }

    private void updateBasicFilterable(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof IBasicFilterable upgradeWrapper)) {
            return;
        }
        int ordinal = buf.readInt();
        IBasicFilterable.FilterType[] types = IBasicFilterable.FilterType.values();
        upgradeWrapper.setFilterType(types[ordinal]);
    }

    private void updateAdvancedFilterable(PacketBuffer buf) throws IOException {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof IAdvancedFilterable upgradeWrapper)) {
            return;
        }

        // READ IN EXACT SAME ORDER
        int filterTypeOrdinal = buf.readInt();
        int matchTypeOrdinal = buf.readInt();
        boolean ignoreDurability = buf.readBoolean();
        boolean ignoreNBT = buf.readBoolean();

        int size = buf.readInt();

        // APPLY
        upgradeWrapper.setFilterType(IBasicFilterable.FilterType.values()[filterTypeOrdinal]);
        upgradeWrapper.setMatchType(IAdvancedFilterable.MatchType.values()[matchTypeOrdinal]);
        upgradeWrapper.setIgnoreDurability(ignoreDurability);
        upgradeWrapper.setIgnoreNBT(ignoreNBT);

        upgradeWrapper.getOreDictEntries()
            .clear();
        for (int i = 0; i < size; i++) {
            upgradeWrapper.getOreDictEntries()
                .add(buf.readStringFromBuffer(100));
        }
    }

    private void updateAdvanceFeedingUpgrade(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof AdvancedFeedingUpgradeWrapper upgradeWrapper)) {
            return;
        }
        int hungerOrdinal = buf.readInt();
        int healthOrdinal = buf.readInt();

        upgradeWrapper
            .setHungerFeedingStrategy(AdvancedFeedingUpgradeWrapper.FeedingStrategy.Hunger.values()[hungerOrdinal]);
        upgradeWrapper
            .setHealthFeedingStrategy(AdvancedFeedingUpgradeWrapper.FeedingStrategy.HEALTH.values()[healthOrdinal]);
    }

    private void updateFilterUpgrade(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof IFilterUpgrade upgradeWrapper)) {
            return;
        }
        int filterOrdinal = buf.readInt();

        upgradeWrapper.setFilterWay(IFilterUpgrade.FilterWayType.values()[filterOrdinal]);
    }

    private void updateMagnetUpgrade(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof IMagnetUpgrade upgrade)) {
            return;
        }
        boolean item = buf.readBoolean();
        boolean exp = buf.readBoolean();

        upgrade.setCollectItem(item);
        upgrade.setCollectExp(exp);
    }

    private void updateCraftingUpgrade(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof ICraftingUpgrade upgradeWrapper)) {
            return;
        }
        int ordinal = buf.readInt();
        boolean backpack = buf.readBoolean();
        ICraftingUpgrade.CraftingDestination[] types = ICraftingUpgrade.CraftingDestination.values();
        upgradeWrapper.setCraftingDes(types[ordinal]);
        upgradeWrapper.setUseBackpack(backpack);
    }

    private void updateVoidUpgrade(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof IVoidUpgrade upgradeWrapper)) {
            return;
        }
        int voidType = buf.readInt();
        IVoidUpgrade.VoidType[] voidTypes = IVoidUpgrade.VoidType.values();
        int voidInput = buf.readInt();
        IVoidUpgrade.VoidInput[] voidInputs = IVoidUpgrade.VoidInput.values();
        upgradeWrapper.setVoidType(voidTypes[voidType]);
        upgradeWrapper.setVoidInput(voidInputs[voidInput]);
    }

    public void updateRotated(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof ICraftingUpgrade upgradeWrapper)) {
            return;
        }

        boolean clockwise = buf.readBoolean();
        ItemStackHandler stackHandler = upgradeWrapper.getStorage();
        BackpackInventoryHelper.rotated(stackHandler, clockwise);
    }

    public void updateGrid(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof ICraftingUpgrade upgradeWrapper)) {
            return;
        }

        boolean balance = buf.readBoolean();
        ItemStackHandler stackHandler = upgradeWrapper.getStorage();
        if (balance) {
            BackpackInventoryHelper.balance(stackHandler);
        } else {
            BackpackInventoryHelper.spread(stackHandler);
        }
    }

    public void updateClear(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
        if (!(wrapper instanceof ICraftingUpgrade upgradeWrapper)) {
            return;
        }

        int ordinal = buf.readInt();
        BackpackInventoryHelper.clear(panel, upgradeWrapper.getStorage(), ordinal);
    }

}
