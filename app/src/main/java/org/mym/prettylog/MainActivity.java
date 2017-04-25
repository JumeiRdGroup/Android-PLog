package org.mym.prettylog;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.mym.plog.Category;
import org.mym.plog.DebugPrinter;
import org.mym.plog.Interceptor;
import org.mym.plog.PLog;
import org.mym.plog.PrintLevel;
import org.mym.plog.config.PLogConfig;
import org.mym.plog.printer.FilePrinter;
import org.mym.plog.timing.TimingLogger;
import org.mym.prettylog.data.JSONEntity;
import org.mym.prettylog.data.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.DebouncingOnClickListener;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private static final int LOG_BASIC = 758;
    private static final int LOG_LONG = LOG_BASIC + 1;
    private static final int LOG_TO_FILE = LOG_LONG + 1;
    private static final int LOG_JSON = LOG_TO_FILE + 1;
    private static final int LOG_THROWABLE = LOG_JSON + 1;
    private static final int LOG_POJO = LOG_THROWABLE + 1;
    private static final int LOG_TIMING = LOG_POJO + 1;
    private static final int LOG_CRASH = LOG_TIMING + 1;


    @BindView(R.id.main_switch_line_info)
    Switch mSwitchLineInfo;

    @BindView(R.id.main_switch_auto_tag)
    Switch mSwitchAutoTag;

    @BindView(R.id.main_switch_concat_tag)
    Switch mSwitchConcatTag;

    @BindView(R.id.main_switch_thread_info)
    Switch mSwitchThreadInfo;

    @BindView(R.id.main_edt_global_tag)
    EditText mEdtGlobalTag;

    @BindView(R.id.main_recycler_usage)
    RecyclerView mRecyclerView;

    @BindView(R.id.main_tv_printer)
    TextView mTvPrinter;

    @BindView(R.id.main_edt_global_intercept)
    EditText mEdtInterceptWord;

    @BindView(R.id.main_edt_category)
    EditText mEdtCategory;

    @BindView(R.id.main_edt_log_content)
    EditText mEdtLogContent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                showAboutDialog();
                return true;
        }
        return false;
    }

    private void showAboutDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).setView(R.layout.dialog_about)
                .show();
        TextView tvDescription = ButterKnife.findById(dialog, R.id.about_tv_description);
        tvDescription.setText(Html.fromHtml(getString(R.string.about_library_description)));

        TextView tvVersion = ButterKnife.findById(dialog, R.id.about_tv_version);
        tvVersion.setText(BuildConfig.VERSION_NAME);

        TextView tvHome = ButterKnife.findById(dialog, R.id.about_tv_library_home);
        tvHome.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.about_value_library_link)));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(new UsageAdapter(new int[]{
                LOG_BASIC, LOG_LONG, LOG_TO_FILE,
                LOG_JSON, LOG_POJO, LOG_THROWABLE,
                LOG_TIMING, LOG_CRASH
        }, getResources().getStringArray(R.array.usage_cases)));

//        PLog.prepare(new DebugPrinter(true), new TextViewPrinter(mTvPrinter));
        showDefaultConfig();
        preparePrinters();
    }

    private void showDefaultConfig() {
        Resources res = getResources();
        mSwitchAutoTag.setChecked(res.getBoolean(R.bool.cfg_auto_tag));
        mSwitchConcatTag.setChecked(res.getBoolean(R.bool.cfg_concat_tag));
        mSwitchLineInfo.setChecked(res.getBoolean(R.bool.cfg_line_info));
        mSwitchThreadInfo.setChecked(res.getBoolean(R.bool.cfg_thread_info));
        mEdtGlobalTag.setText(res.getString(R.string.cfg_global_tag));
    }

    public void preparePrinters() {
        PLog.prepare(new DebugPrinter(BuildConfig.DEBUG),
                new TextViewPrinter(mTvPrinter));
//        initCrashPrinter();
        MainActivityPermissionsDispatcher.initCrashPrinterWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void initCrashPrinter() {
        PLog.appendPrinter(CrashPrinter.getInstance(this));
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForExternalStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.msg_storage_rationale)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDenyMessage() {
        toastMsg(R.string.msg_storage_permission_denied);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode, grantResults);
    }

    private void toastMsg(@StringRes int msg, Object... params) {
        Toast.makeText(this, getString(msg, params), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.main_btn_apply_config)
    void applyConfig() {

        String newGlobalTag = mEdtGlobalTag.getText().toString().trim();

        if (TextUtils.isEmpty(newGlobalTag)) {
            newGlobalTag = getString(R.string.cfg_global_tag);
        }

        Interceptor interceptor = null;
        final String keyword = mEdtInterceptWord.getText().toString().trim();
        if (!TextUtils.isEmpty(keyword)) {
            interceptor = new Interceptor() {
                @Override
                public boolean onIntercept(@PrintLevel int level, @NonNull String tag, @Nullable
                        Category category, @NonNull String msg) {
                    boolean intercepted = msg.contains(keyword)
                            || (category != null && category.getName().contains(keyword));
                    if (intercepted) {
                        toastMsg(R.string.msg_log_was_intercepted, keyword);
                    }
                    return intercepted;
                }
            };
        }

        PLogConfig config = PLogConfig.newBuilder(PLog.getCurrentConfig())
                .keepLineNumber(mSwitchLineInfo.isChecked())
                .keepThreadInfo(mSwitchThreadInfo.isChecked())
                .useAutoTag(mSwitchAutoTag.isChecked())
                .forceConcatGlobalTag(mSwitchConcatTag.isChecked())
                .globalTag(newGlobalTag)
                .globalInterceptor(interceptor)
                .build();
        PLog.init(config);

        hideSoftInput();

        toastMsg(R.string.main_msg_config_applied);
    }

    @OnClick(R.id.main_btn_print_log)
    void printLog() {
        String category = mEdtCategory.getText().toString().trim();
        String content = mEdtLogContent.getText().toString().trim();

        if (!TextUtils.isEmpty(content)) {
            PLog.category(category).msg(content).execute();
        } else {
            PLog.empty();
        }

        hideSoftInput();
    }

    private void hideSoftInput() {
        InputMethodManager imm = ((InputMethodManager) getSystemService(Context
                .INPUT_METHOD_SERVICE));
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void performClick(@UsageCase int action) {
        switch (action) {
            case LOG_BASIC:
                logBasic();
                break;
            case LOG_JSON:
                logJSON();
                break;
            case LOG_LONG:
                logLong();
                break;
            case LOG_POJO:
                logObjects();
                break;
            case LOG_THROWABLE:
                logThrowable();
                break;
            case LOG_TIMING:
                logTiming();
                break;
            case LOG_TO_FILE:
                logToFile();
                break;
            case LOG_CRASH:
//                logCrash();
                MainActivityPermissionsDispatcher.logCrashWithCheck(this);
                break;
        }
    }

    void logBasic() {
        PLog.empty();

        PLog.v("This is a verbose log.");
        PLog.d("This is a debug log. param is %d, %.2f and %s", 1, 2.413221, "Great");
        PLog.i("InfoTag", "This is an info log using specified tag.");
        PLog.w("This is a warn log.");
        PLog.e("This is an error log.");

        new Thread("BackgroundThread") {
            @Override
            public void run() {
                PLog.level(Log.INFO).msg("This is a log in another thread.").execute();
            }
        }.start();
    }

    void logLong() {
        PLog.i("Here is one line of text that is going to be wrapped after 20 columns.Here is one" +
                " line of text that is going to be wrapped after 20 columns.Here is one line of " +
                "text that is going to be wrapped after 20 columns.");

        StringBuilder sb = new StringBuilder("无限长字符串测试");
        for (int i = 0; i < 100; i++) {
            sb.append("[").append(i).append("]");
        }
        PLog.d(sb.toString());
    }

    void logObjects() {
        /**
         * User class is a data class without toString() method overridden.
         */
        User.Cat tom = new User.Cat("Tom", "Blue");
        User.Cat jerry = new User.Cat("Jerry", "brown");

        List<User.Cat> cats = new ArrayList<>();
        Collections.addAll(cats, tom, jerry);

        User user = new User("Alice", 23, cats);

        PLog.objects(cats);
        PLog.objects(user);

        //Log multi objects.
        PLog.objects("RxJava", "RxAndroid", "RxBinding", "RxBus");

    }

    void logThrowable() {
        NullPointerException e = new NullPointerException("This is a sample exception!");
        //PLog can log exceptions in all levels, WARN and ERROR is recommended.
        PLog.throwable(e);

        //force using error level
        PLog.level(Log.ERROR).throwable(e);

        //Using crash category
        PLog.level(Log.WARN).category(CrashPrinter.CAT_CRASH)
                .params(e).execute();
    }

    void logJSON() {
        //Sample 1: for JSON objects
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JSONEntity.DATA);
            PLog.json(jsonObject);
        } catch (JSONException e) {
            PLog.throwable(e);
        }

        //Sample 2: for String arguments
        PLog.objects(JSONEntity.DATA1);
        PLog.objects(JSONEntity.DATA2);

        //Sample 3: formatting json string with message tips
        PLog.d("I have a json string like this: %s", JSONEntity.DATA1);
    }

    /**
     * To use built-in {@link FilePrinter} class, plog-printer dependency is required.
     * See wiki for more details.
     */
    void logToFile() {
        FilePrinter printer = new FilePrinter(this);
        //ONLY printers changed
//        PLog.prepare(new DebugPrinter(BuildConfig.DEBUG), printer);

        //APPEND printer
        PLog.appendPrinter(printer);

        PLog.i("This is the first line of file log.");

        PLog.v("file length can be very long");

        PLog.i("This is the last line of file log.");

        Toast.makeText(this, getString(R.string.file_logger_tip, printer.getLogFilePath()), Toast
                .LENGTH_SHORT).show();

        //Close File printer. If all your logs are written to file, this step is optional.
        printer.close();

        //RESET printers
        preparePrinters();
    }

    /**
     * To use built-in {@link TimingLogger} class, plog-timing dependency is required.
     * See wiki for more details.
     * <p>
     * The usage is totally same as {@link android.util.TimingLogger}; all you should remember is
     * just using right class reference.
     * </p>
     */
    void logTiming() {
        TimingLogger logger = new TimingLogger("TimingTag", "TimingLabel");

        logger.addSplit("Operation Step1");
        emulateTimeOperation();

        logger.addSplit("Operation Step2");
        emulateTimeOperation();

        logger.addSplit("Operation FINISHED");

        logger.dumpToLog();
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void logCrash() {
        PLog.level(Log.ERROR).category(CrashPrinter.CAT_CRASH).params(new NullPointerException())
                .execute();
        String path = CrashPrinter.getInstance(this).getLogFilePath();
        toastMsg(R.string.msg_crash_saved, path);
    }

    private void emulateTimeOperation() {
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(200));
        } catch (InterruptedException ignored) {

        }
    }

    @IntDef({LOG_BASIC, LOG_LONG, LOG_TO_FILE,
            LOG_JSON, LOG_POJO, LOG_THROWABLE,
            LOG_TIMING, LOG_CRASH})
    private @interface UsageCase {

    }

    private class UsageAdapter extends RecyclerView.Adapter<UsageHolder> {

        @ColorRes
        private int[] mItemColorPalette;

        private String[] mTexts;
        @UsageCase
        private int[] mActions;

        private int mItemCount;

        public UsageAdapter(int[] actions, String[] texts) {
            if (actions == null || texts == null || actions.length != texts.length) {
                throw new IllegalArgumentException("Wrong array!");
            }
            this.mActions = actions;
            this.mTexts = texts;
            this.mItemColorPalette = new int[]{
                    R.color.md_blue_300, R.color.md_light_blue_500, R.color.md_blue_500,
                    R.color.md_blue_800, R.color.md_light_blue_A100
//                    R.color.colorPrimary, R.color.md_blue_300,
//                    R.color.md_blue_A100, R.color.colorPrimaryDark
            };
            mItemCount = actions.length;
        }

        @Override
        public UsageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_usage_case, parent, false);
            return new UsageHolder(view);
        }

        @Override
        public void onBindViewHolder(final UsageHolder holder, final int position) {
            holder.displayData(mTexts[position]);
            holder.itemView.setBackgroundResource(mItemColorPalette[position % mItemColorPalette
                    .length]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PLog.empty();
                    performClick(mActions[holder.getAdapterPosition()]);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }
    }

    private class UsageHolder extends RecyclerView.ViewHolder {

        private TextView tvContent;

        public UsageHolder(View itemView) {
            super(itemView);
            tvContent = ButterKnife.findById(itemView, R.id.item_main_content);
        }

        public void displayData(String text) {
            tvContent.setText(text);
        }
    }
}
