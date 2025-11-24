package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

public interface IUpgrade {

    String TAB_STATE_TAG = "TabState";

    void setTabOpened(boolean opened);

    boolean getTabOpened();

    String getSettingLangKey();
}
