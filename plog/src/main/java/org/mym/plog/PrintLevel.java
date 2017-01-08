package org.mym.plog;

import android.support.annotation.IntDef;
import android.util.Log;

/**
 * A utility annotation for log level.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
@IntDef({Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR, Log.ASSERT})
public @interface PrintLevel {

}
