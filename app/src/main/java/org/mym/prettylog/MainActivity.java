package org.mym.prettylog;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mym.plog.PLog;
import org.mym.prettylog.data.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_recycler_usage)
    RecyclerView mRecyclerView;

    private static final int LOG_BASIC = 758;
    private static final int LOG_LONG = LOG_BASIC + 1;
    private static final int LOG_TO_FILE = LOG_LONG + 1;
    private static final int LOG_JSON = LOG_TO_FILE + 1;
    private static final int LOG_THROWABLE = LOG_JSON + 1;
    private static final int LOG_POJO = LOG_THROWABLE + 1;
    private static final int LOG_TIMING = LOG_POJO + 1;

    @IntDef({LOG_BASIC, LOG_LONG, LOG_TO_FILE,
            LOG_JSON, LOG_POJO, LOG_THROWABLE,
            LOG_TIMING})
    private @interface UsageCase {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO add action button
        return super.onCreateOptionsMenu(menu);
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
                LOG_TIMING
        }, getResources().getStringArray(R.array.usage_cases)));
    }

    private class UsageAdapter extends RecyclerView.Adapter<UsageHolder> {

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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

        public void displayData(String text){
            tvContent.setText(text);
        }
    }

    private void performClick(@UsageCase int action){
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
        }
    }

    void logBasic() {
        PLog.empty();

        PLog.v("This is a verbose log.");
        PLog.d("This is a debug log. param is %d, %.2f and %s", 1, 2.413221, "Great");
        PLog.i("InfoTag", "This is an info log using specified tag.");
        PLog.w("This is a warn log.");
        PLog.e("This is an error log.");

        Cat2 tom = new Cat2("Tom", "Blue");
        Cat2 jerry = new Cat2("Jerry", "brown");

        PLog.i("I have 2 cats, %s and %s", tom, jerry);
    }

    void logLong() {
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
        User normalObject = new User("PLog", " is ", " pretty.", 8888);
        PLog.objects(normalObject);

        /**
         * Log multi objects.
         */
        PLog.d(null, (Object) "RxJava", "RxAndroid", "RxBinding", "RxBus");
        //This is equivalent to above line
        PLog.objects("RxJava", "RxAndroid", "RxBinding", "RxBus");

        List<User> list = new ArrayList<>();

        Collections.addAll(list, new User("This", "Crash", "Fixed", 2333),
                new User("This", "Issue", "Fixed", 9900),
                new User("This", "Feature", "Implemented", 4321));
        PLog.objects(list);

    }

    void logThrowable(){
        NullPointerException e = new NullPointerException("This is a sample exception!");
        //PLog can log exceptions in all levels, WARN and ERROR is recommended.
        PLog.throwable(e);
//        PLog.exceptions(e);
    }
    void logJSON(){
        //TODO Stub!
    }

    void logToFile() {
        //TODO Stub!
//        PLogConfig backup = PLog.getCurrentConfig();
//
//        final FileLogger fileLogger = new FileLogger(this);
//        PLog.init(PLogConfig.newBuilder(backup)
//                .logger(fileLogger)
//                .build());
//        PLog.i("This is the first line of file log.");
//
//        String story = getString(R.string.console_emulated_log);
//        PLog.v(story);
//
//        PLog.i("This is the last line of file log.");
//
//        Toast.makeText(MainActivity.this, getString(R.string.file_logger_tip,
//                fileLogger.getLogFilePath()), Toast.LENGTH_SHORT).show();
//
//        //Close file logger set previously to release resources
//        fileLogger.close();
//
//        PLog.init(backup);
    }

    void logTiming(){
        //TODO Stub!
//        timingLogExample();
//
//        PLogConfig backup = PLog.getCurrentConfig();
//
//        PLog.init(PLogConfig.newBuilder().timingLogger(new SinglePipeLogger() {
//            @Override
//            protected void log(int level, String tag, String msg) {
//                Log.i(tag, msg + "--------");
//            }
//        }).build());
//
//        PLog.resetTimingLogger("INFO level logger", "TimingLabel");
//        timingLogExample();
//
//        PLog.init(backup);
    }

//    private void timingLogExample() {
//        PLog.resetTimingLogger();
//        PLog.addTimingSplit("Timing operation STARTED");
//
//        emulateTimeOperation();
//        PLog.addTimingSplit("Operation Step 1");
//
//        emulateTimeOperation();
//        PLog.addTimingSplit("Operation Step 2");
//
//        PLog.dumpTimingToLog();
//
//    }

    private void emulateTimeOperation(){
        Random random = new Random();
        try{
            Thread.sleep(random.nextInt(200));
        }catch (InterruptedException ignored){

        }
    }

    protected interface Action0 {
        @SuppressWarnings("unused")
        void call();
    }

    private class Cat2 {
        String name;
        String color;

        public Cat2(String name, String color) {
            this.name = name;
            this.color = color;
        }
    }
}
