package ruiseki.omoshiroikamo.core.util.search;

import ruiseki.omoshiroikamo.core.item.ItemStackKey;

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
