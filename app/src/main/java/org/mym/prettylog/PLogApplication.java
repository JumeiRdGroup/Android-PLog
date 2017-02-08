package org.mym.prettylog;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import org.mym.plog.DebugPrinter;
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
                .forceConcatGlobalTag(getResources().getBoolean(R.bool.cfg_concat_tag))
                .keepInnerClass(getResources().getBoolean(R.bool.cfg_inner_class))
                .keepLineNumber(getResources().getBoolean(R.bool.cfg_line_info))
                .useAutoTag(getResources().getBoolean(R.bool.cfg_auto_tag))
//                .maxLengthPerLine(getResources().getInteger(R.integer.cfg_max_length))
                .globalTag(getString(R.string.cfg_global_tag))
                //Auto disable log in release version
//                .controller(new EasyLogController(BuildConfig.DEBUG, BuildConfig.DEBUG))
                .build());

        PLog.prepare(new DebugPrinter(true));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            PLog.appendPrinter(CrashPrinter.getInstance(this));
        }

        CrashHandler.getInstance().init();
    }
}
