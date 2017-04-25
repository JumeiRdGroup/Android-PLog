package org.mym.plog;

import android.support.annotation.NonNull;

/**
 * The simplest category implementation: it just return the value passed in the constructor.
 * Created by muyangmin on Jan 17, 2017.
 *
 * @since 2.0.0
 */
public class SimpleCategory implements Category {

    private String mName;

    public SimpleCategory(@NonNull String name) {
        this.mName = name;
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
