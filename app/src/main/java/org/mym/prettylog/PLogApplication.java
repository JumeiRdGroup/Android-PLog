package org.mym.prettylog;

import android.app.Application;

import org.mym.plog.PLog;
import org.mym.plog.config.PLogConfig;
import org.mym.prettylog.util.CrashHandler;

/**
 * <p>
 * This class shows how to init PLog Library.
 * </p>
 * Created by muyangmin on 9/1/16.
 *
 * @author muyangmin
 * @since V1.3.0
 */
public class PLogApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PLog.init(new PLogConfig.Builder()
//                .emptyMsgLevel(Log.INFO)
                .forceConcatGlobalTag(true)
                .keepInnerClass(true)
                .keepLineNumber(true)
                .useAutoTag(true)
                .maxLengthPerLine(160)
                .build());

        CrashHandler.getInstance().init();
    }
}
