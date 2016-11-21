package org.mym.plog.logger;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.util.PrintStreamPrinter;

import org.mym.plog.PLog;

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
 * This class defines a standard file logger.
 * Created by muyangmin on Nov 15, 2016.
 *
 * @author muyangmin
 * @since 1.0.0
 */
@SuppressWarnings("WeakerAccess")
public class FileLogger extends SinglePipeLogger implements Closeable {

    /**
     * File size limit.
     */
    private long mFileSizeLimit;
    /**
     * Indicate whether only print to file; if set to true, you won't see log from logcat.
     */
    private boolean mLogToFileOnly;

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
     * Create a file logger using default config.
     */
    public FileLogger(Context mContext) {
        this(getDefaultLogFilePath(mContext), new TimingFileNameGenerator(),
                1024 * 1024, false);
    }

    /**
     * Create a file logger.
     *
     * @param mFileSizeLimit     file size limit. By default a single log file would not over 1M.
     *                           If a non-positive integer is provided, log files has no size limit.
     * @param mFileNameGenerator a generator to provide filename when creating new log file.
     * @param mLogToFileOnly     Indicate whether only print to file; if set to true, you won't see
     *                           log from logcat.
     */
    public FileLogger(String mLogFilePath, FileNameGenerator mFileNameGenerator,
                      long mFileSizeLimit, boolean mLogToFileOnly) {
        this.mLogFilePath = mLogFilePath;
        this.mFileNameGenerator = mFileNameGenerator;
        this.mFileSizeLimit = mFileSizeLimit;
        this.mLogToFileOnly = mLogToFileOnly;

        HandlerThread mPrintThread = new HandlerThread("PLogFileLoggerThread",
                Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE);
        mPrintThread.start();
        mPrintHandler = new PrintHandler(mPrintThread.getLooper(), this);

        mTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US);
    }

    /**
     * Handy method defines default log file path.
     *
     * @param context the context of package.
     * @return the absolute path is [package-path]/files/plog/.
     * @see Context#getFilesDir()
     */
    public static String getDefaultLogFilePath(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separatorChar + "plog";
    }

    @Override
    protected void log(int level, String tag, String msg) {
        if (!mLogToFileOnly) {
            callDefaultLogger(level, tag, msg);
        }
        mPrintHandler.sendMessage(mPrintHandler.obtainMessage(PrintHandler.MSG_WRITE_LOG,
                createPrintText(level, tag, msg)));
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
    public String getLogFilePath() {
        return mLogFilePath;
    }

    /**
     * Resolve and check for log path defined by {@link #getLogFilePath()}.
     *
     * @return A readable and writable path. If path does not exist, create it automatically.
     * @throws IllegalArgumentException If path is empty, or path is not a directory, or path
     *                                  cannot be readable or writable.
     */
    private File resolveLogFileDir() throws IllegalArgumentException {
        String path = getLogFilePath();
        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Log file path cannot be null!");
        }
        File file = new File(path);
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Log file path is not a directory!");
        }
        if (!file.canRead() || !file.canWrite()) {
            throw new IllegalArgumentException("Log file path cannot be read/write!");
        }
        return file;
    }

    @Override
    public void close() {
        if (mPrintHandler != null) {
            mPrintHandler.sendEmptyMessage(PrintHandler.MSG_CLOSE_FILE);
        }
    }

    /**
     * The interface to generate new log file.
     */
    protected interface FileNameGenerator {
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
        private WeakReference<FileLogger> mFileLogger;

        PrintHandler(Looper looper, FileLogger logger) {
            super(looper);
            mFileLogger = new WeakReference<>(logger);
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
            FileLogger logger = mFileLogger.get();
            if (logger == null) {
                return;
            }

            //single log length (content) may over limit; but needn't to care this situation
            // because it's still safe.
            if (mCurrentFile != null
                    && (mCurrentFile.length() + content.length() > logger.mFileSizeLimit)) {
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
        private boolean createOutputStream(FileLogger logger) throws IOException {
            File path = logger.resolveLogFileDir();
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
            return true;
        }
    }

    protected static class TimingFileNameGenerator implements FileNameGenerator {

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
