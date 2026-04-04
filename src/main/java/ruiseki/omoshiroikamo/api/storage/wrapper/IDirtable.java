package ruiseki.omoshiroikamo.api.storage.wrapper;

public interface IDirtable {

    String DIRTY_TAG = "Dirty";

    boolean isDirty();

    void markDirty();

    void markClean();

    void setDirty(boolean value);
}
