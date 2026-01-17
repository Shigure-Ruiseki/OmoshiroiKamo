package ruiseki.omoshiroikamo.module.cable.common.search;

import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;

final class TooltipNode implements SearchNode {

    private final String text;

    TooltipNode(String text) {
        this.text = text;
    }

    @Override
    public boolean matches(ItemStackKey k) {
        for (String line : k.getTooltipLower()) {
            if (line.contains(text)) return true;
        }
        return false;
    }
}
