package org.mym.prettylog.data;

/**
 *
 * @author muyangmin
 * @since V1.3.0
 */
public class User {

    private String mPrivateField;
    protected String mProtectedField;
    String mPackageField;
    public int mPublicField;

    public User(String mPackageField, String mPrivateField, String mProtectedField, int
            mPublicField) {
        this.mPackageField = mPackageField;
        this.mPrivateField = mPrivateField;
        this.mProtectedField = mProtectedField;
        this.mPublicField = mPublicField;
    }

    //toString method is not overridden
}
