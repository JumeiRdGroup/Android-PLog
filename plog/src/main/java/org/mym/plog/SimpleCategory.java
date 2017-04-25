package org.mym.plog;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The simplest category implementation: it just return the value passed in the constructor.
 * Created by muyangmin on Jan 17, 2017.
 *
 * @since 2.0.0
 */
public class SimpleCategory implements Category {

    /**
     * Use a static Map to cache same categories and avoid frequently object allocating.
     */
    private static Map<String, SimpleCategory> sCachedMap = new HashMap<>();
    private String mName;

    private SimpleCategory(@NonNull String name) {
        this.mName = name;
    }

    //This class may be used by outer call
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static SimpleCategory obtain(@NonNull String name) {
        SimpleCategory category = sCachedMap.get(name);
        if (category == null) {
            category = new SimpleCategory(name);
            sCachedMap.put(name, category);
        }
        return category;
    }

    @NonNull
    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isSameAs(@NonNull String name) {
        return mName.equals(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Category) {
            return mName.equalsIgnoreCase(((Category) obj).getName());
        }
        return super.equals(obj);
    }
}
