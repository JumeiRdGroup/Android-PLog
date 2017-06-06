package org.mym.plog;

import android.util.Log;

/**
 * Builder style API; use this class to fit complicated needs.
 * <p>
 * NOTE: APIs in {@link PLog} class is for common scenarios and this class is for advanced usage.
 * Some decor method in {@link PLog} will NOT be added into this class, e.g.
 * {@link PLog#wtf(Throwable)}.
 * </p>
 *
 * @author Muyangmin
 * @since 2.0.0
 */
public final class LogRequest {

    @PrintLevel
    private int level;
    private int stackOffset;
    private String tag;
    private Category category;
    private String msg;
    private Object[] params;
    private boolean printTraceOnly;

    public LogRequest() {
        //level must be assigned in constructor explicitly due to 0 is not belong to valid levels.
        level = Log.VERBOSE;
    }

    @PrintLevel
    public int getLevel() {
        return level;
    }

    public int getStackOffset() {
        return stackOffset;
    }

    public String getTag() {
        return tag;
    }

    public Category getCategory() {
        return category;
    }

    public String getMsg() {
        return msg;
    }

    public Object[] getParams() {
        return params;
    }

    /*package*/ boolean isPrintTraceOnly() {
        return printTraceOnly;
    }


    // ----- Decor methods; just for better usage BEGIN -----
    public LogRequest throwable(Throwable throwable) {
        return params(throwable);
    }

    public LogRequest category(String category) {
        return category(SimpleCategory.obtain(category));
    }
    // ----- Decor methods; just for better usage END -----

    // -----BUILDER STYLE CODE BEGIN -----

    public LogRequest level(@PrintLevel int level) {
        this.level = level;
        return this;
    }

    public LogRequest tag(String tag) {
        this.tag = tag;
        return this;
    }

    public LogRequest category(Category category) {
        this.category = category;
        return this;
    }

    public LogRequest msg(String msg) {
        this.msg = msg;
        return this;
    }

    public LogRequest params(Object... params) {
        this.params = params;
        return this;
    }

    public LogRequest stackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
        return this;
    }

    /*package*/ LogRequest printTraceOnly() {
        this.printTraceOnly = true;
        return this;
    }

    // -----BUILDER STYLE CODE END -----

    /**
     * @deprecated This method name may be a little ambiguous. Consider use {@link #print()}
     * instead.
     */
    @Deprecated
    public void execute() {
        //add try-catch block to avoid crash from library if any internal exception is thrown.
        try {
            LogEngine.handleLogRequest(this);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Print log to prepared printers.
     *
     * @since 2.0.0-beta5
     */
    public void print() {
        // Use same code with execute() to keep same stack level
        //add try-catch block to avoid crash from library if any internal exception is thrown.
        try {
            LogEngine.handleLogRequest(this);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
