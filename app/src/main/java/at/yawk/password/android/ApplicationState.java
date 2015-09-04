package at.yawk.password.android;

import android.content.Context;
import at.yawk.password.MultiFileLocalStorageProvider;
import java.io.File;

/**
 * @author yawkat
 */
public class ApplicationState {
    private static volatile PasswordDatabase DATABASE;

    public static PasswordDatabase getDatabase(Context context) {
        if (DATABASE == null) {
            synchronized (ApplicationState.class) {
                if (DATABASE == null) {
                    File dbDir = new File(context.getFilesDir(), "db");
                    //noinspection ResultOfMethodCallIgnored
                    dbDir.mkdirs();
                    DATABASE = new PasswordDatabase(new MultiFileLocalStorageProvider(dbDir));
                }
            }
        }
        return DATABASE;
    }
}
