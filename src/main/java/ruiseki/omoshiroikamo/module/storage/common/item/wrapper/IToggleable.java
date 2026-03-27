package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

public interface IToggleable {

    String ENABLED_TAG = "Enabled";

    boolean isEnabled();

    void setEnabled(boolean enabled);

    default void toggle() {
        setEnabled(!isEnabled());
    }
}
