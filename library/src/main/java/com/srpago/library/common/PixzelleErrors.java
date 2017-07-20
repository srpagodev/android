package com.srpago.library.common;

import android.support.annotation.IntDef;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class with definitions for some codes, and for handling error messages.
 *
 * @author  Rodolfo Pena - Pixzelle Studios S. de R.L. de C.V.
 * @version 2.0
 * @since   2014-11-26
 */
public abstract class PixzelleErrors {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({OK, CREATED, NO_CONTENT, BAD_REQUEST, UNAUTHORIZED, FORBIDDEN, NOT_FOUND, UNPROCESSABLE_ENTITY, SERVER_ERROR,
            MAINTENANCE, NO_INTERNET, TIME_OUT, UNKNOWN_CODE})
    public @interface SERVER_CODES {
    }

    public final static int OK = 200;
    public final static int CREATED = 201;
    public final static int NO_CONTENT = 204;
    public final static int BAD_REQUEST = 400;
    public final static int UNAUTHORIZED = 401;
    public final static int FORBIDDEN = 403;
    public final static int NOT_FOUND = 404;
    public final static int UNPROCESSABLE_ENTITY = 422;
    public final static int SERVER_ERROR = 500;
    public final static int MAINTENANCE = 503;
    public final static int NO_INTERNET = 69;
    public final static int TIME_OUT = 70;
    public final static int UNKNOWN_CODE = 71;

    public abstract void setServerError(@SERVER_CODES int code);

    @SERVER_CODES
    public abstract int getServerError();

    /**
     * Prints the exception in the log.
     * @param ex Exception to print.
     */
    static void printException(Exception ex){
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter( writer );
        ex.printStackTrace( printWriter );
        printWriter.flush();
        String stackTrace = writer.toString();
        Log.e("SrPagoError", stackTrace);
    }

    /**
     * Returns the exception message, so the developer can handle it
     * @param ex Exception to get the message
     * @return Message of the exception.
     */
    static String getExceptionMessage(Exception ex){
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter( writer );
        ex.printStackTrace( printWriter );
        printWriter.flush();
        return writer.toString();
    }
}
