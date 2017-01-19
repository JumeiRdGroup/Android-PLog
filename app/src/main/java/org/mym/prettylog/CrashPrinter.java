package org.mym.prettylog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.PrintStreamPrinter;

import org.mym.plog.Category;
import org.mym.plog.PLog;
import org.mym.plog.PrintLevel;
import org.mym.plog.SimpleCategory;
import org.mym.plog.printer.FilePrinter;

import java.io.File;
import java.util.Arrays;

/**
 * This class provides a default(sample) crash file utility.
 * Created by muyangmin on Jan 17, 2017.
 *
 * @since 2.0.0
 */
public class CrashPrinter extends FilePrinter {

    public static final Category CRASH = new SimpleCategory("crash");
    private static volatile CrashPrinter sInstance = null;
    private static String sExtraInfo;
    private Context mApplicationContext;

    private CrashPrinter(Context mContext) {
        //Assume not null
        //noinspection ConstantConditions
        super(getCrashFileDir(mContext).getAbsolutePath(),
                new TimingFileNameGenerator(), 1024 * 1024L);
        mApplicationContext = mContext.getApplicationContext();
    }

    //    private CrashPrinter(){
//
//    }
//
    public static CrashPrinter getInstance(Context context) {
        //create a temp variable to improve performance for reading volatile field.
        CrashPrinter instance = sInstance;
        if (instance == null) {
            synchronized (CrashPrinter.class) {
                instance = sInstance;
                //double check here
                if (instance == null) {
                    instance = new CrashPrinter(context);
                    sInstance = instance;
                }
            }
        }
        return instance;
    }

    public static void setExtraInfo(@NonNull String extraInfo) {
        sExtraInfo = extraInfo;
    }

    /**
     * Get recommended crash file directory, if not exist, it would be created automatically.
     *
     * @param context Cannot be null
     * @return null if external storage not mounted, or create directory failed.
     */
    public static File getCrashFileDir(@NonNull Context context) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File rootDir = Environment.getExternalStorageDirectory();
        File pkgDir = new File(rootDir, context.getPackageName());
        PLog.objects(pkgDir);
        //If package dir not exist and create failed, return null
        if (!pkgDir.exists() && !pkgDir.mkdir()) {
            return null;
        }
        File crashFileDir = new File(pkgDir, "crash");
        if (!crashFileDir.exists() && !crashFileDir.mkdir()) {
            return null;
        }
        return crashFileDir;
    }

    @Override
    public boolean onIntercept(@PrintLevel int level, @NonNull String tag,
                               @Nullable Category category, @NonNull String msg) {
        // accept only crash category!
        return !CRASH.equals(category);
    }

    @Override
    public void print(@PrintLevel int level, @NonNull String tag, @NonNull String msg) {
        super.print(level, tag, msg);
        //Only record one crash at a time.
        close();
    }

    @Override
    protected void printFileHeader(PrintStreamPrinter ps) {

        //Print time and thread
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss, z",
//                Locale.getDefault());
//        ps.println("CrashTime: " + format.format(timestamp));
//        ps.println("CrashThread: " + thread + ", belong to group " + thread
//                .getThreadGroup());
        if (!TextUtils.isEmpty(sExtraInfo)) {
            ps.println(sExtraInfo);
            sExtraInfo = null;
        }

        //Print device info, app info, etc.
        ps.println(createCrashHeaderStr(mApplicationContext));

        //Print throwable, the core stacktrace
//        ps.println(Log.getStackTraceString(throwable));
    }

    private String createCrashHeaderStr(Context context) {
        StringBuilder sb = new StringBuilder(1024);

        sb.append("Device Model: ").append(Build.MODEL).append("\n")
                .append("Device Brand: ").append(Build.BRAND).append("\n")
                .append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n")
                .append("OS Version: ").append(Build.VERSION.SDK_INT).append("\n")
                .append("OS Name: ").append(Build.VERSION.RELEASE).append("\n")
                .append("CPU Hardware: ").append(Build.HARDWARE).append("\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append("CPU API: ").append(Arrays.toString(Build.SUPPORTED_ABIS)).append("\n");
        }

        //NOTE: 这里虽然列出了内存和外存的freeSpace, 但在测试设备 HUAWEI FRD-AL00上两者并不相同。
        // 而外存的剩余大小和设置页里看到的存储空间数据一致。
        try {
            File intStore = context.getFilesDir();
            File extStore = context.getExternalFilesDir(null);
            sb.append("Internal Storage Free: ")
                    .append(Formatter.formatFileSize(context, intStore.getFreeSpace()))
                    .append("\n");
            if (extStore != null) {
                sb.append("External Storage Free: ")
                        .append(Formatter.formatFileSize(context, extStore.getFreeSpace()))
                        .append("\n");
            }
        } catch (SecurityException ignored) {

        }

        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ignored) {
            //Empty
        }
        sb.append("App Version: ")
                .append(packageInfo == null ? "N/A" : packageInfo.versionName)
                .append("\n");

        return sb.toString();
    }

}
