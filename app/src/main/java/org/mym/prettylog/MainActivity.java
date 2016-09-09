package org.mym.prettylog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.mym.plog.PLog;
import org.mym.plog.config.PLogConfig;
import org.mym.plog.util.SinglePipeLogger;
import org.mym.prettylog.data.JSONEntity;
import org.mym.prettylog.data.User;
import org.mym.prettylog.wrapper.LogWrapper;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_tv_virtual_console)
    TextView mTvConsole;
    private TextViewLogger mTextViewLogger = new TextViewLogger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        TextView tvDescription = ButterKnife.findById(this, R.id.main_tv_description);
        //noinspection deprecation
        tvDescription.setText(Html.fromHtml(getString(R.string.plog_description)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextViewLogger.attach(mTvConsole);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTextViewLogger.detach();
    }

    @OnClick(R.id.btn_basic_usage)
    void basicUsage() {
        PLog.v("This is a verbose log.");
        PLog.d("DebugTag", "This is a debug log. param is %d, %.2f and %s", 1, 2.413221, "Great");
        PLog.i("InfoTag", "This is an info log.");
        PLog.w("This is a warn log.");
        PLog.e("This is an error log.");

        PLogConfig backup = PLog.getCurrentConfig();
        //Use wrapper would override globalOffset setting, so DO NOT USE AS MIXED!
        LogWrapper.init(this);
        LogWrapper.empty();
        PLog.init(backup);
    }

    @OnClick(R.id.btn_log_empty)
    void logEmpty() {
        PLog.empty();
    }

    @OnClick(R.id.btn_log_without_tag)
    void logWithoutTag() {
        PLog.i("I'm printing log without tag. Please check autoTag option.");
    }

    @OnClick(R.id.btn_log_long)
    void logVeryLong() {
        StringBuilder sb = new StringBuilder("无限长字符串测试");
        for (int i = 0; i < 100; i++) {
            sb.append("[").append(i).append("]");
        }
        PLog.d(sb.toString());
    }

    @OnClick(R.id.btn_log_objects)
    void logObjects() {
        /**
         * User class is a data class without toString() method overridden.
         */
        User normalObject = new User("PLog", " is ", " pretty.", 8888);
        PLog.objects(normalObject);

        /**
         * Log multi objects.
         */
        PLog.d(null, (Object) "RxJava", "RxAndroid", "RxBinding", "RxBus");
        //This is equivalent to above line
        PLog.objects("RxJava", "RxAndroid", "RxBinding", "RxBus");
    }

    @OnClick(R.id.btn_log_throwable)
    void logThrowable(){
        NullPointerException e = new NullPointerException("This is a sample exception!");
        PLog.throwable(Log.ERROR,
                "PLog can log exceptions in all levels, WARN and ERROR is recommended.",
                e);
//        PLog.exceptions(e);
    }

    @OnClick(R.id.btn_log_json)
    void logJSON(){
        PLog.json(JSONEntity.DATA);
    }

    @OnClick(R.id.btn_timing_logger)
    void logTiming(){
        PLog.resetTimingLogger();
        PLog.addTimingSplit("Timing operation STARTED");

        emulateTimeOperation();
        PLog.addTimingSplit("Operation Step 1");

        emulateTimeOperation();
        PLog.addTimingSplit("Operation Step 2");

        PLog.dumpTimingToLog();
    }

    private void emulateTimeOperation(){
        Random random = new Random();
        try{
            Thread.sleep(random.nextInt(200));
        }catch (InterruptedException ignored){

        }
    }

    @OnClick(R.id.btn_auto_tag)
    void logWithAutoTag() {
        PLogConfig backup = PLog.getCurrentConfig();

        PLog.init(PLogConfig.newBuilder(backup)
                .useAutoTag(true)
                .build());
        PLog.empty();

        PLog.init(backup);
    }

    @OnClick(R.id.btn_tag_anonymous)
    void logInAnonymousClass() {
        PLogConfig backup = PLog.getCurrentConfig();

        PLog.init(PLogConfig.newBuilder(backup)
                .useAutoTag(true)
                .build());
        new Action0() {
            @Override
            public void call() {
                PLog.i("This is a log in anonymous class.");
            }
        }.call();

        PLog.init(backup);
    }

    @OnClick(R.id.btn_tag_nested)
    void logInNestedClass() {
        PLogConfig backup = PLog.getCurrentConfig();

        PLog.init(PLogConfig.newBuilder(backup)
                .useAutoTag(true)
                .build());

        new Action0() {
            @Override
            public void call() {
                new NamedNestedClass().call();
            }

            class NamedNestedClass implements Action0 {
                @Override
                public void call() {
                    PLog.i("This is a log in nested class in anonymous class.");
                }
            }
        }.call();

        PLog.init(backup);
    }

    @OnClick(R.id.btn_customize_logger)
    void logWithCustomizeLogger() {
        final PLogConfig backup = PLog.getCurrentConfig();

        PLog.init(PLogConfig.newBuilder(backup)
                .useAutoTag(true)
                .keepLineNumber(false)
                .keepInnerClass(false)
                .maxLengthPerLine(64)
                .logger(mTextViewLogger)
                .build());
        mTextViewLogger.clear();
        mTvConsole.setVisibility(View.VISIBLE);

        String story = getString(R.string.console_emulated_log);
        String[] pieces = story.split("\n");
        int delay = 0;
        for (final String line : pieces){
            mTvConsole.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PLog.i(line);
                }
            }, delay);
            delay += 500;
        }

        Toast.makeText(this, R.string.console_close_tip, Toast.LENGTH_SHORT).show();
        mTvConsole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvConsole.setVisibility(View.INVISIBLE);
                PLog.init(backup);
                PLog.i("Customized logger has been disabled, and now backup to default.");
            }
        });
    }

    @OnClick(R.id.btn_stack_offset)
    void logStackOffset() {
        methodToBeIgnored();
    }

    void methodToBeIgnored() {
        //Normal call
        PLog.empty();
        //With Stack offset
        PLog.logWithStackOffset(Log.INFO, 1, getClass().getSimpleName(), "This is a log testing " +
                "stack offset.");
    }

    protected interface Action0 {
        @SuppressWarnings("unused")
        void call();
    }

    public static class TextViewLogger extends SinglePipeLogger {

        private TextView mTextView;

        @Override
        protected void log(int level, String tag, String msg) {
            if (mTextView != null) {
                mTextView.append(msg);
                mTextView.append("\n");
            }
        }

        public void clear() {
            mTextView.setText("");
        }

        public void attach(TextView textView) {
            mTextView = textView;
        }

        public void detach() {
            mTextView = null;
        }
    }
}
