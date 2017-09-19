package org.mym.prettylog.data;

/**
 * This is a test case class for cycling reference sample: singleton.
 * <p>
 * Without recursive depth handling, recursively access this object will lead to
 * StackOverflowError.
 * <p>
 * Created by muyangmin on Sep 19, 2017.
 */
public class Singleton {
    private static volatile Singleton sInstance = null;

    private Singleton() {

    }

    public static Singleton getInstance() {
        //create a temp variable to improve performance for reading volatile field.
        Singleton instance = sInstance;
        if (instance == null) {
            synchronized (Singleton.class) {
                instance = sInstance;
                //double check here
                if (instance == null) {
                    instance = new Singleton();
                    sInstance = instance;
                }
            }
        }
        return instance;
    }
}
