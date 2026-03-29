package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

public interface ISmeltingUpgrade extends IProgressable, ITickable, IStorageUpgrade {

    String BURN_TIME_TAG = "BurnTime";
    String BURN_TIME_TOTAL_TAG = "BurnTimeTotal";
    String COOK_TIME_TAG = "CookTime";
    String COOK_TIME_TOTAL_TAG = "CookTimeTotal";

    int getCookTime();

    int getCookTimeTotal();

    int getBurnTime();

    int getBurnTimeTotal();

    void setBurnTime(int value);

    void setBurnTimeTotal(int value);

    void setCookTime(int value);

    void setCookTimeTotal(int value);

    default float getBurnProgress() {
        int burn = getBurnTime();
        int total = getBurnTimeTotal();
        if (total == 0) return 0f;
        return (float) burn / total;
    }
}
