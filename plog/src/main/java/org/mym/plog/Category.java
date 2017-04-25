package org.mym.plog;

import android.support.annotation.NonNull;

/**
 * This interface defines a dimension of interception, usually useful for large teams.
 *
 * @author Muyangmin
 * @since 2.0.0
 */
public interface Category {
    @NonNull
    String getName();

    /**
     * A help method to compare with another category.
     *
     * @param name another category name, must be not null; often pass by {cat.getName()}.
     * @return true if this category is compatible with param category.
     */
    boolean isSameAs(@NonNull String name);
}
