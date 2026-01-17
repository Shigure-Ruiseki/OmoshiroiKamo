package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.oredict.OreDictionary;

public final class ItemSearch {

    private ItemSearch() {}

    public static boolean matches(String search, String displayNameLower, String modId, List<String> tooltipLower,
        String creativeTab, int[] oreIds) {
        if (search == null || search.isEmpty()) return true;

        String[] orTerms = search.toLowerCase()
            .split("\\|");

        for (String group : orTerms) {
            if (!group.isEmpty()
                && matchesAll(group.trim(), displayNameLower, modId, tooltipLower, creativeTab, oreIds)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesAll(String group, String name, String modId, List<String> tooltip, String tab,
        int[] oreIds) {
        for (String term : splitTerms(group)) {
            if (term.isEmpty()) continue;

            boolean neg = term.charAt(0) == '-';
            if (neg) term = term.substring(1);

            boolean hit = matchesTerm(term, name, modId, tooltip, tab, oreIds);

            if (neg == hit) return false;
        }
        return true;
    }

    private static boolean matchesTerm(String term, String name, String modId, List<String> tooltip, String tab,
        int[] oreIds) {
        if (term.isEmpty()) return true;

        // @mod
        if (term.startsWith("@")) {
            return modId.contains(term.substring(1));
        }

        // #tooltip
        if (term.startsWith("#")) {
            String t = term.substring(1);
            for (String s : tooltip) {
                if (s.contains(t)) return true;
            }
            return false;
        }

        // $oredict
        if (term.startsWith("$")) {
            String key = term.substring(1);
            int id = OreDictionary.getOreID(key);
            if (id == -1) return false;

            for (int x : oreIds) {
                if (x == id) return true;
            }
            return false;
        }

        // %creative tab
        if (term.startsWith("%")) {
            return tab.contains(term.substring(1));
        }

        // name
        if (name.contains(term)) return true;

        // tooltip fallback
        for (String s : tooltip) {
            if (s.contains(term)) return true;
        }

        return false;
    }

    private static String[] splitTerms(String input) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean quote = false;

        for (char c : input.toCharArray()) {
            if (c == '"') {
                quote = !quote;
            } else if (c == ' ' && !quote) {
                if (sb.length() > 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }

        if (sb.length() > 0) list.add(sb.toString());
        return list.toArray(new String[0]);
    }
}
