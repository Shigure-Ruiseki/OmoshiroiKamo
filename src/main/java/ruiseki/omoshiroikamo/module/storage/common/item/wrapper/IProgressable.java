package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

public interface IProgressable {

    String PROGRESS_TAG = "Progress";

    float getProgress();

    void setProgress(float progress);
}
