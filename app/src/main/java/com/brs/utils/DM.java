package com.brs.utils;

import android.util.Log;

import com.brs.constvalue.ConstValues;

/**
 * Created by ikban on 2015-10-27.
 */
public class DM {

    public static boolean isDebug = true;
    public static String TAG = "BRS";


    // information log _____________________________________________________________________________
    public static void i(Object o, String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.i(TAG, "[ " + o.getClass().getSimpleName() + " ] " + s);
    }

    public static void i(String Tag, String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.i(Tag, s);
    }

    public static void i(String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.i(TAG, s);
    }

    // debug log _____________________________________________________________________________
    public static void d(Object o, String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.d(TAG, "[ " + o.getClass().getSimpleName() + " ] " + s);
    }

    public static void d(String Tag, String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.d(Tag, s);
    }

    public static void d(String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.d(TAG, s);
    }

    // error log ___________________________________________________________________________________
    public static void e(Object o, String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.e(TAG, "[ " + o.getClass().getSimpleName() + " ] " + s);
    }

    public static void e(String Tag, String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.e(Tag, s);
    }

    public static void e(String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.e(TAG, s);
    }


    // verbos log __________________________________________________________________________________
    public static void v(Object o, String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.v(TAG, "[ " + o.getClass().getSimpleName() + "] " + s);
    }

    public static void v(String Tag, String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.v(Tag, s);
    }

    public static void v(String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.v(TAG, s);
    }


    // warning log _________________________________________________________________________________
    public static void w(Object o, String s) {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.w(TAG, "[ " + o.getClass().getSimpleName() + "] " + s);
    }

    public static void w(String Tag, String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.w(Tag, s);
    }

    public static void w(String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
            Log.w(TAG, s);
    }

    // custom log __________________________________________________________________________________
    public static void x(String s)
    {
        if(ConstValues.FOR_DEBUG_MSG)
        {
            Thread thread = Thread.currentThread();

            // get thread name
            // String strThreadName = thread.getName();

            // get file name and line number
            String strFileName = thread.getStackTrace()[4].getFileName();
            String strLineNumber = "";
            int nLineNumber = thread.getStackTrace()[4].getLineNumber();

            strLineNumber = Integer.toString(nLineNumber);

    		/*String strFormat = "" + s;
    		strFormat = strFormat.replaceAll("%d", "%s");
            strFormat = strFormat.replaceAll("%f", "%s");
            strFormat = strFormat.replaceAll("%c", "%s");
            strFormat = strFormat.replaceAll("%b", "%s");
            strFormat = strFormat.replaceAll("%x", "%s");
            strFormat = strFormat.replaceAll("%l", "%s");*/

            Log.i(TAG, "[" + s + "] / file:" + strFileName + " / line:" + strLineNumber);
        }
    }
}
