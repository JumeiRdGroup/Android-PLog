package org.mym.prettylog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.mym.plog.PLog;
import org.mym.plog.config.PLogConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        PLog.init(new PLogConfig.Builder()
                .globalTag("GlobalTag")
                .forceConcatGlobalTag(true)
                .keepLineNumber(true)
                .build());
        PLog.empty();
        PLog.v("This is a verbose log.");
        PLog.d("DebugTag", "This is a debug log.");
        PLog.i("InfoTag", "This is a info log.");
        PLog.w("This is a warn log.");
        PLog.e("This is a error log.");
        new InnerClass().innerLogTest();
    }

    private class InnerClass{
        void innerLogTest(){
            PLog.i("This is a log in inner class.");
        }
    }
}
