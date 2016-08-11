package org.mym.prettylog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.mym.plog.PLog;
import org.mym.plog.config.PLogConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void localMeaningLessMethod(){
        PLog.empty();
        PLog.logWithStackOffset(Log.INFO,
                1,  //Skip this meaningless method
                "MainActivity", "SampleMsg");
    }

    @Override
    protected void onResume() {
        super.onResume();

        PLog.init(new PLogConfig.Builder()
                .globalTag(null)
//                .emptyMsgLevel(Log.INFO)
                .forceConcatGlobalTag(true)
                .keepInnerClass(true)
                .keepLineNumber(true)
                .build());
        localMeaningLessMethod();

        PLog.empty();
        PLog.v("This is a verbose log.");
        PLog.d("DebugTag", "This is a debug log.");
        PLog.i("InfoTag", "This is an info log.");
        PLog.w("This is a warn log.");
        PLog.e("This is an error log.");
        new InnerClass().innerLogTest();
    }

    private class InnerClass{
        void innerLogTest(){
            PLog.i("This is a log in inner class.");
            new NestedInnerClass().test();
        }

        class NestedInnerClass{
            void test(){
                PLog.i("This is a log in nested inner class.");
            }
        }
    }
}
