package org.mym.plog.printer;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.PrintStreamPrinter;

import org.mym.plog.AbsPrinter;
import org.mym.plog.PLog;
import org.mym.plog.PrintLevel;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Print logs into a file. Soft wrap is disabled by default.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FilePrinter extends AbsPrinter implements Closeable {

    /**
     * Place holder string for the internal files dir.
     *
     * @see Context#getFilesDir()
     */
    protected static final String DIR_INT_FILES = "${INT_PKG}";
    /**
     * Place holder string for the external files dir. (requires no permission)
     *
     * @see Context#getExternalFilesDir(String)
     */
    protected static final String DIR_EXT_FILES = "${EXT_PKG}";
    /**
     * Place holder string for the external storage root dir.(requires WRITE PERMISSION)
     *
     * @see Environment#getExternalStorageDirectory()
     */
    protected static final String DIR_EXT_ROOT = "${EXT_ROOT}";

    /**
     * File size limit.
     */
    private long mFileSizeLimit;

    /**
     * Define log file path.
     */
    private String mLogFilePath;

    /**
     * To decide file name when need to create new log file.
     */
    private FileNameGenerator mFileNameGenerator;

    /**
     * Actually do the print work.
     */
    private Handler mPrintHandler;

    private SimpleDateFormat mTimeFormatter;

    /**
     * Create a file printer using default path (external files dir).
     *
     * @throws IllegalStateException If try to use external storage but external storage
     *                               is unavailable.
     */
    public FilePrinter(@NonNull Context context) throws IllegalStateException {
        this(context, DIR_EXT_FILES + "/logs");
    }

    /**
     * Create a file printer using specified path.
     *
     * @param logFilePath absolute path; may use predefined placeholders in start. Currently
     *                    supported placeholders: {@link #DIR_INT_FILES}, {@link #DIR_EXT_FILES},
     *                    {@link #DIR_EXT_ROOT}.
     * @throws IllegalStateException If try to use external storage but external storage
     *                               is unavailable.
     */
    public FilePrinter(@NonNull Context context, @NonNull String logFilePath)
            throws IllegalStateException {
        this(context, logFilePath, new TimingFileNameGenerator(), 1024 * 1024);
    }

    /**
     * Create a file printer.
     *
     * @param fileSizeLimit file size limit. By default a single log file would not over 1M.
     *                      If a non-positive integer is provided, log files has no size limit.
     * @param generator     a generator to provide filename when creating new log file.
     */
    public FilePrinter(@NonNull Context context, @NonNull String filePath,
                       @NonNull FileNameGenerator generator, long fileSizeLimit)
            throws IllegalStateException {
        this.mLogFilePath = FilePrinterHelper.parseActualPath(context, filePath);
        this.mFileNameGenerator = generator;
        this.mFileSizeLimit = fileSizeLimit;

        HandlerThread mPrintThread = new HandlerThread("PLogFilePrinterThread",
                Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE);
        mPrintThread.start();
        mPrintHandler = new PrintHandler(mPrintThread.getLooper(), this);

        mTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US);
    }

    @Override
    public void print(@PrintLevel int level, @NonNull String tag, @NonNull String msg) {
        mPrintHandler.sendMessage(mPrintHandler.obtainMessage(PrintHandler.MSG_WRITE_LOG,
                createPrintText(level, tag, msg)));
    }

    /**
     * This method provides a quick method to provide file header, e.g. crash files.
     * Although you still need to extends this class, but you needn't copy whole file management
     * code.
     */
    protected void printFileHeader(PrintStreamPrinter printer) {

    }

    /**
     * Override this method to redefine your preferred output format.
     * By default the log format is similar as shown in logcat.
     *
     * @param level log level
     * @param tag   log tag
     * @param msg   log content
     * @return the text to be written to file
     */
    protected String createPrintText(final int level, final String tag, final String msg) {
        String levelChar = "V";
        switch (level) {
            case Log.VERBOSE:
                levelChar = "V";
                break;
            case Log.DEBUG:
                levelChar = "D";
                break;
            case Log.INFO:
                levelChar = "I";
                break;
            case Log.WARN:
                levelChar = "W";
                break;
            case Log.ERROR:
                levelChar = "E";
                break;
            case Log.ASSERT:
                levelChar = "A";
                break;
        }
        String currentTime = mTimeFormatter.format(System.currentTimeMillis());
        return String.format("%s %s/%s: %s", currentTime, levelChar, tag, msg);
    }

    /**
     * Get the absolute path for log files. The proposal of this method is to debug or
     * traverse log path, not setting it. <br>
     * <p>
     * Override this method is not really recommended; consider using constructor instead.
     *
     * @return the absolute path for log files; should not be null
     */
    public final String getLogFilePath() {
        return mLogFilePath;
    }

    /**
     * Close associated files, etc.
     */
    @Override
    public void close() {
        if (mPrintHandler != null) {
            mPrintHandler.sendEmptyMessage(PrintHandler.MSG_CLOSE_FILE);
        }
    }

    /**
     * The interface to generate new log file.
     */
    public interface FileNameGenerator {
        /**
         * Generate a file name
         * <p>
         * NOTE: the return value for this method should NOT be null, and should be a simple name
         * because the file path is decided by file logger instead of this interface.
         * </p>
         *
         * @return the simple name of next log file, e.g. "log.txt"
         */
        String nextFile();

        /**
         * NOTE: the return value for this method should NOT be null, and should be a simple name
         * because the file path is decided by file logger instead of this interface.
         *
         * @param dir           the directory of log files. This parameter is provided for
         *                      accessing files
         *                      if needed.
         * @param lastGenerated last returned value for {@link #nextFile()}.
         * @return the simple name of next log file, e.g. "log1.txt". Directly return a value
         * that equals lastGenerate param will cause an Exception.
         */
        String nextFileIfDuplicate(File dir, String lastGenerated);
    }

    //Using standard static handler pattern, although logger would never be GC
    private static class PrintHandler extends Handler {

        private static final int MSG_WRITE_LOG = 0x0001;
        private static final int MSG_CLOSE_FILE = 0x1000;
        private File mCurrentFile;
        private PrintStream mPrintStream;
        private PrintStreamPrinter mPrinter;
        private WeakReference<FilePrinter> mFilePrinter;

        PrintHandler(Looper looper, FilePrinter logger) {
            super(looper);
            mFilePrinter = new WeakReference<>(logger);
        }

        /**
         * For {@link #MSG_WRITE_LOG}, obj field should be a CharSequence.
         * For {@link #MSG_CLOSE_FILE}, all fields are ignored.
         *
         * @param msg should not be null
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WRITE_LOG:
                    if (!(msg.obj instanceof CharSequence)) {
                        throw new IllegalArgumentException("Msg.obj must be charsequence!");
                    }
                    try {
                        printLogToFile(((CharSequence) msg.obj));
                    } catch (IOException e) {
                        PLog.throwable(e);
                    }
                    break;
                case MSG_CLOSE_FILE:
                    resetOutputStream();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported message type " + msg.what);
            }
        }

        private void printLogToFile(CharSequence content) throws IOException {
            if (TextUtils.isEmpty(content)) {
                return;
            }
            FilePrinter logger = mFilePrinter.get();
            if (logger == null) {
                return;
            }

            //single log length (content) may over limit; but needn't to care this situation
            // because it's still safe.
            if (mCurrentFile != null
                    && (logger.mFileSizeLimit > 0 && mCurrentFile.length() + content.length() > logger.mFileSizeLimit)) {
                resetOutputStream();
            }

            //If printer or file is not ready, create it
            if (mCurrentFile == null && !createOutputStream(logger)) {
                return;
            }
            //Printer is ready, print text now
            mPrinter.println(content.toString());
        }

        /**
         * Close file object and output stream.
         */
        private void resetOutputStream() {
            if (mPrintStream != null) {
                mPrintStream.close();
                mPrinter = null;
                mCurrentFile = null;
            }
        }

        /**
         * Create log file and output stream.
         *
         * @return true if create succeed, false otherwise
         * @throws IOException if occurs
         */
        private boolean createOutputStream(FilePrinter logger) throws IOException {
            File path;
            try {
                path = FilePrinterHelper.resolveDirOrCreate(logger.getLogFilePath());
            } catch (SecurityException ignored) {
                path = null;
            }

            //Create failed, not read/writable, etc
            if (path == null) {
                return false;
            }

            String name = logger.mFileNameGenerator.nextFile();

            File file = new File(path, name);
            while (file.exists()) {
                String nextName = logger.mFileNameGenerator.nextFileIfDuplicate(path, name);
                if (TextUtils.isEmpty(nextName) || nextName.equals(name)) {
                    throw new RuntimeException("File name already duplicated!");
                }
                file = new File(path, nextName);
            }

            mCurrentFile = new File(path, name);
            //If file not found and create failed, just return
            if (!mCurrentFile.exists() && !mCurrentFile.createNewFile()) {
                return false;
            }
            mPrintStream = new PrintStream(new FileOutputStream(mCurrentFile), true, "UTF-8");
            mPrinter = new PrintStreamPrinter(mPrintStream);
            FilePrinter printer;
            if (mFilePrinter != null && ((printer = mFilePrinter.get()) != null)) {
                printer.printFileHeader(mPrinter);
            }
            return true;
        }
    }

    public static class TimingFileNameGenerator implements FileNameGenerator {

        private static final String formatStr = "yyyyMMdd-HHmm";
        private static final String FORMAT_REGEX = "\\d{8}\\-\\d{4}";
        private static final String CONCAT = "-";
        private SimpleDateFormat timingFormat = new SimpleDateFormat(formatStr, Locale.US);

        @Override
        public String nextFile() {
            return timingFormat.format(System.currentTimeMillis()) + ".log";
        }

        @Override
        public String nextFileIfDuplicate(File dir, String timestamp) {
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.matches(FORMAT_REGEX + CONCAT + "\\d+" + "\\.log");
                }
            });
            if (files == null || files.length == 0) {
//                throw new RuntimeException("File name not valid but no such file exists.");
                return formatNameWithSerialNum(timestamp, 1);
            }

            //Find last file and
            long lastModifiedTime = 0;
            File lastModifiedFile = null;
            for (File file : files) {
                if (lastModifiedFile == null || file.lastModified() > lastModifiedTime) {
                    lastModifiedTime = file.lastModified();
                    lastModifiedFile = file;
                }
            }
            //Must be not null
            //noinspection ConstantConditions
            String last = lastModifiedFile.getName();
            //Because we used FilenameFilter, so can be sure that suffix must be a number.
            int serialNum = Integer.parseInt(last.substring((formatStr + CONCAT).length()));
            return formatNameWithSerialNum(timestamp, serialNum + 1);
        }

        private String formatNameWithSerialNum(String timestamp, int serialNum) {
            return timestamp + CONCAT + serialNum + ".log";
        }
    }
}
