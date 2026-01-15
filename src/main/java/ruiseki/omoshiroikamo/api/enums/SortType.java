package ruiseki.omoshiroikamo.api.enums;

public enum SortType {

    BY_NAME,
    BY_MOD_ID,
    BY_COUNT,
    BY_ORE_DICT;

    public static SortType fromIndex(int index) {
        return SortType.values()[index];
    }
}
