package org.mym.prettylog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.mym.plog.PLog;
import org.mym.plog.config.PLogConfig;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void localMeaningLessMethod(){
        PLog.empty();
        StringBuilder sb = new StringBuilder("无限长字符串测试");
        for (int i=0; i<100; i++){
            sb.append("[").append(i).append("]");
        }
        PLog.d(sb.toString());
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
//                .forceConcatGlobalTag(true)
                .keepInnerClass(true)
                .keepLineNumber(true)
                .useAutoTag(true)
                .maxLengthPerLine(160)
                .build());
        localMeaningLessMethod();

        PLog.empty();
        PLog.v("This is a verbose log.");
        PLog.d("DebugTag", "This is a debug log.");
        PLog.i("InfoTag", "This is an info log.");
        PLog.w("This is a warn log.");
        PLog.e("This is an error log.");
        new InnerClass().innerLogTest();

        PLog.d(null, (Object)"RxJava", "RxAndroid", "RxBinding", "RxBus");
        //This is equivalent to above line
        PLog.objects("RxJava", "RxAndroid", "RxBinding", "RxBus");

        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, "UniversalImageLoader", "Picasso", "Glide", "Fresco");
        PLog.d("", list);
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
