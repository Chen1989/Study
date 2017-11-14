package com.chen.study.chenWebView;

/**
 * Created by PengChen on 2016/9/20.
 */
public class StateParams
{
    public final static String Start = "State_Start";
    public final static String WaitLong = "State_Wait_Long";
    public final static String WaitShort = "State_Wait_Short";
    public final static String Hook = "State_Hook";
    public final static String End = "State_End";

    public final static int TimeoutLong = 1;
    public final static int PageFinished = 2;
    public final static int TimeoutShort = 3;
    public final static int Notify = 4;
    public final static int Close = 5;

    public static String getTypeName(int type)
    {
        switch (type)
        {
            case TimeoutLong:
                return "TimeoutLong";
            case PageFinished:
                return "PageFinished";
            case TimeoutShort:
                return "TimeoutShort";
            case Notify:
                return "Notify";
            case Close:
                return "Close";
        }

        return "";
    }
}
