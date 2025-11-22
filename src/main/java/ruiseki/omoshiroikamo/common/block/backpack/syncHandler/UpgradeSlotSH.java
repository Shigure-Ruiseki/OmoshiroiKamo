package ruiseki.omoshiroikamo.common.block.backpack.syncHandler;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.gtnewhorizon.gtnhlib.capability.Capabilities;

import ruiseki.omoshiroikamo.common.block.backpack.capabilities.IToggleable;
import ruiseki.omoshiroikamo.common.block.backpack.capabilities.IUpgrade;

public class UpgradeSlotSH extends ItemSlotSH {

    public static final int UPDATE_UPGRADE_TAB_STATE = 6;
    public static final int UPDATE_UPGRADE_TOGGLE = 7;
    public static final int UPDATE_BASIC_FILTERABLE = 8;
    public static final int UPDATE_ADVANCED_FILTERABLE = 9;
    public static final int UPDATE_ADVANCED_FEEDING = 10;
    public static final int UPDATE_FILTER_WAY = 11;

    public UpgradeSlotSH(ModularSlot slot) {
        super(slot);
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {
        super.readOnServer(id, buf);

        switch (id) {
            case UPDATE_UPGRADE_TAB_STATE:
                updateTabState(buf);
                break;
            case UPDATE_UPGRADE_TOGGLE:
                updateToggleable();
                break;
            case UPDATE_BASIC_FILTERABLE:
                // updateBasicFilterable(buf);
                break;
            case UPDATE_ADVANCED_FILTERABLE:
                // updateAdvancedFilterable(buf);
                break;
            case UPDATE_ADVANCED_FEEDING:
                // updateAdvanceFeedingUpgrade(buf);
                break;
            case UPDATE_FILTER_WAY:
                // updateFilterUpgrade(buf);
                break;
        }
    }

    private void updateTabState(PacketBuffer buf) {
        ItemStack stack = getSlot().getStack();
        var wrapper = Capabilities.getCapability(stack, IUpgrade.class);
        if (wrapper == null) {
            return;
        }
        wrapper.setTabOpened(stack, buf.readBoolean());
    }

    private void updateToggleable() {
        ItemStack stack = getSlot().getStack();
        var wrapper = Capabilities.getCapability(stack, IToggleable.class);
        if (wrapper == null) {
            return;
        }
        wrapper.toggle(stack);
    }

    //
    // private void updateBasicFilterable(PacketBuffer buf) {
    // var wrapper = slot.stack.getCapability(Capabilities.BASIC_FILTERABLE_CAPABILITY, null).orElse(null);
    // if (wrapper == null) {
    // return;
    // }
    // IBasicFilterable.FilterType type = buf.readEnumValue(IBasicFilterable.FilterType.class);
    // wrapper.setFilterType(type);
    // }
    //
    // private void updateAdvancedFilterable(PacketBuffer buf) {
    // var wrapper = slot.stack.getCapability(Capabilities.ADVANCED_FILTERABLE_CAPABILITY, null).orElse(null);
    // if (wrapper == null) {
    // return;
    // }
    //
    // wrapper.setFilterType(buf.readEnumValue(IBasicFilterable.FilterType.class));
    // wrapper.setMatchType(buf.readEnumValue(IAdvancedFilterable.MatchType.class));
    // wrapper.setIgnoreDurability(buf.readBoolean());
    // wrapper.setIgnoreNBT(buf.readBoolean());
    //
    // int size = buf.readInt();
    // wrapper.getOreDictEntries().clear();
    // for (int i = 0; i < size; i++) {
    // wrapper.getOreDictEntries().add(buf.readString(100));
    // }
    // }
    //
    // private void updateAdvanceFeedingUpgrade(PacketBuffer buf) {
    // var wrapper = slot.stack.getCapability(Capabilities.ADVANCED_FEEDING_UPGRADE_CAPABILITY, null).orElse(null);
    // if (wrapper == null) {
    // return;
    // }
    //
    // wrapper.setHungerFeedingStrategy(buf.readEnumValue(AdvancedFeedingUpgradeWrapper.FeedingStrategy.Hunger.class));
    // wrapper.setHealthFeedingStrategy(buf.readEnumValue(AdvancedFeedingUpgradeWrapper.FeedingStrategy.Health.class));
    // }
    //
    // private void updateFilterUpgrade(PacketBuffer buf) {
    // var wrapper = slot.stack.getCapability(Capabilities.IFILTER_UPGRADE_CAPABILITY, null).orElse(null);
    // if (wrapper == null) {
    // return;
    // }
    //
    // wrapper.setFilterWay(buf.readEnumValue(IFilterUpgrade.FilterWayType.class));
    // }

}
