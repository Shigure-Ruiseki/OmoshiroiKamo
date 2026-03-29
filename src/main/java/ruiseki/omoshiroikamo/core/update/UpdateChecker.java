package ruiseki.omoshiroikamo.core.update;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.GeneralConfig;
import ruiseki.omoshiroikamo.core.common.util.VersionComparator;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Checks for updates from CurseForge API.
 */
public class UpdateChecker {

    private static final String CFWIDGET_API = "https://api.cfwidget.com/minecraft/mc-mods/omoshiroi-kamo";
    private static final String USER_AGENT = "OmoshiroiKamo UpdateChecker/" + LibMisc.VERSION;

    private static String latestVersion = null;
    private static final AtomicBoolean hasChecked = new AtomicBoolean(false);
    private static boolean updateAvailable = false;

    /**
     * Starts an asynchronous update check if enabled in config and hasn't checked
     * yet.
     */
    public static void checkUpdates() {
        if (!GeneralConfig.enableUpdateNotification || hasChecked.getAndSet(true)) {
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(CFWIDGET_API);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                        latestVersion = parseVersionFromCFWidget(reader);
                        if (latestVersion != null) {
                            compareVersions();
                        }
                    }
                }
            } catch (Exception e) {
                OmoshiroiKamo.okLog(Level.WARN, "Failed to check for updates: " + e.getMessage());
            }
        }, "OmoshiroiKamo Update Checker");
        thread.setDaemon(true);
        thread.start();
    }

    private static String parseVersionFromCFWidget(InputStreamReader reader) {
        JsonElement element = new JsonParser().parse(reader);
        if (element == null || !element.isJsonObject()) return null;

        JsonObject response = element.getAsJsonObject();
        if (!response.has("versions")) return null;

        JsonObject versions = response.getAsJsonObject("versions");
        if (!versions.has("1.7.10")) return null;

        JsonArray files = versions.getAsJsonArray("1.7.10");
        if (files.size() == 0) return null;

        JsonObject latest = files.get(0)
            .getAsJsonObject();
        if (!latest.has("version")) return null;

        return latest.get("version")
            .getAsString();
    }

    private static void compareVersions() {
        if (latestVersion == null) return;

        String current = LibMisc.VERSION;
        if (VersionComparator.compare(current, latestVersion) < 0) {
            updateAvailable = true;
        }
    }

    public static boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }
}
