package org.mym.plog.printer;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mym.plog.printer.FilePrinter.DIR_EXT_FILES;
import static org.mym.plog.printer.FilePrinter.DIR_EXT_ROOT;
import static org.mym.plog.printer.FilePrinter.DIR_INT_FILES;

/**
 * A helper class for file printer to resolve file dir, providing extra grammar sugar.
 * Created by muyangmin on Apr 25, 2017.
 */
/*package*/ final class FilePrinterHelper {

    /**
     * Check whether argument path matches predefined storage path.
     * If true, replace the placeholder by the actual path.
     * For example, "${EXT_STORE}/app/crash" may be replaced by "/storage/emulated/0/app/crash".
     *
     * @return the actual path, or original argument if it not match any pre-supported dirs.
     * @throws IllegalStateException If try to use external storage but external storage
     *                               is unavailable.
     */
    static String parseActualPath(Context context, String path) throws IllegalStateException {
        String[] supportedPath = new String[]{DIR_EXT_ROOT, DIR_EXT_FILES, DIR_INT_FILES};
        for (String s : supportedPath) {
            // match only line start, e.g."^${EXT_STORE}"
            // must process escape char for regex
            String regex = "^" + s.replace("$", "\\$").replace("{", "\\{")
                    .replace("}", "\\}");

            Matcher matcher = Pattern.compile(regex).matcher(path);
            if (!matcher.find()) {
                continue;
            }
            String param = matcher.group();
            String actualPath = null;
            switch (param) {
                case DIR_EXT_ROOT:
                    File root = Environment.getExternalStorageDirectory();
                    if ((!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
                            || root == null) {
                        throw new IllegalStateException("External storage is not available!");
                    }
                    actualPath = matcher.replaceFirst(root.getAbsolutePath());
                    break;

                case DIR_EXT_FILES:
                    //This method requires no permission
                    File filesDir = context.getExternalFilesDir(null);
                    if (filesDir == null) {
                        throw new IllegalStateException("External storage is not available!");
                    }
                    actualPath = matcher.replaceFirst(filesDir.getAbsolutePath());
                    break;

                case DIR_INT_FILES:
                    actualPath = matcher.replaceFirst(context.getFilesDir().getAbsolutePath());
                    break;
            }
            return actualPath;
        }
        return path;
    }

    /**
     * Resolve and check for log path.
     *
     * @return A readable and writable path. If path does not exist, create it automatically.
     * @throws IllegalArgumentException If path is empty, or path is not a directory, or path
     *                                  cannot be readable or writable.
     */
    static File resolveDirOrCreate(String path) throws IllegalArgumentException {
        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Path cannot be null!");
        }
        File file = new File(path);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Path is not a directory!");
        }
        if (!file.canRead() || !file.canWrite()) {
            throw new IllegalArgumentException("Path cannot be read/write!");
        }
        return file;
    }
}
